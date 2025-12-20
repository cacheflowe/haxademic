package com.haxademic.demo.draw.cv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.OrtSession.SessionOptions;
import processing.core.PImage;
import processing.video.Movie;

public class Demo_ONNX 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected OrtEnvironment env;
	protected OrtSession session;
	protected ArrayList<Person> detectedPeople = new ArrayList<Person>();
	protected PImage inputResized;
	
	// Reusable buffers for inference (avoid allocations each frame)
	protected IntBuffer inputBuffer;
	protected String inputName;
	protected long[] inputShape;
	
	// Threading for async inference
	protected ExecutorService inferenceExecutor;
	protected AtomicBoolean inferenceRunning = new AtomicBoolean(false);
	protected ArrayList<Person> pendingResults = new ArrayList<Person>();
	protected final Object resultsLock = new Object();
	protected float lastMinConfidence = 0.3f;
	protected boolean lastFlipY = false;

	protected static final String FLIP_Y = "FLIP_Y";
	protected Movie demoVideo;

	protected void config() {
		Config.setAppSize(1280, 720);
	}
		
	protected void firstFrame () {
		// DOCs: https://onnxruntime.ai/docs/api/java/index.html
		// Download jar from: https://mvnrepository.com/artifact/com.microsoft.onnxruntime/onnxruntime
		// Download jar from: https://mvnrepository.com/artifact/com.microsoft.onnxruntime/onnxruntime_gpu
		// Download dlls from : https://github.com/microsoft/onnxruntime/releases/tag/v1.23.2
		// Add to vm args? -Donnxruntime.native.path=lib/ort
		// Copy dlls from inside ort.jar: onnxruntime4j_jni.dll, etc
		// and/or: https://developer.download.nvidia.com/compute/cudnn/redist/cudnn/windows-x86_64/
		// ---> cudnn-windows-x86_64-9.1.1.17_cuda12-archive.zip 639MB 2024-05-01 23:30
		// String ortPath = FileUtil.getPath("../lib/ort");
		// String currentPath = System.getProperty("java.library.path");
		// System.setProperty("java.library.path", ortPath + ";" + currentPath);

		// load webcam
		WebCam.instance().setDelegate(this);
		// demoVideo = new Movie(P.p, P.path("images/_sketch/les-twins-wod-2014.mp4"));
		// demoVideo.loop();
		
		// add UI
		UI.addTitle("ONNX Config");
		UI.addSlider("Min Confidence", 0.3f, 0, 1, 0.01f, false);
		UI.addToggle(FLIP_Y, false, false);

		// show controls by default
		UI.active(true);
		DebugView.active(true);

		// init ONNX session
		try {
			
			// Log available providers
			p.println("Available ONNX providers: " + OrtEnvironment.getAvailableProviders());
			env = OrtEnvironment.getEnvironment();
			
			// Add the CUDA Execution Provider
			// The order in which EPs are added determines their priority
			// Requirements:
			// - CUDA 12.2 + cuDNN 9.1.1 (exact versions matter!)
			// - Make sure Java is set to use NVIDIA card in Windows graphics settings
			SessionOptions sessionOptions = new SessionOptions();
			
			boolean useCUDA = true; // Set to true once CUDA/cuDNN versions are matched
			if (useCUDA) {
				try {
					sessionOptions.addCUDA(0); // GPU device ID 0
					p.println("CUDA execution provider added successfully");
				} catch (OrtException e) {
					p.println("CUDA not available, falling back to CPU: " + e.getMessage());
				}
			} else {
				p.println("Running on CPU (CUDA disabled)");
			}

			session = env.createSession(FileUtil.getPath("ml/movenet-multipose-lightning.onnx"), sessionOptions);
			
			// print input info
			p.println("Input info: " + session.getInputInfo());
			p.println("Output info: " + session.getOutputInfo());
			
			// Cache input name for reuse
			inputName = session.getInputNames().iterator().next();
		} catch (OrtException e) {
			e.printStackTrace();
		}
		
		// Pre-allocate input buffer (256x256x3 RGB as INT32)
		int bufferSize = 256 * 256 * 3;
		inputBuffer = ByteBuffer.allocateDirect(bufferSize * 4)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
		inputShape = new long[]{1, 256, 256, 3};
		
		inputResized = p.createImage(256, 256, ARGB);
		
		// Create single-thread executor for inference
		inferenceExecutor = Executors.newSingleThreadExecutor();
	}

	protected PImage getInputImage() {
		// return WebCam.instance().image();
		// if(demoVideo != null) {
		// 	if (demoVideo.available()) demoVideo.read();
		// 	return demoVideo;
		// }
		// check webcam
		return WebCam.instance().image();
		// return ImageCacher.get("images/_sketch/people.png");
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw input image
		PImage inputImage = getInputImage();
		
		// run inference on latest frame (async)
		if(session != null && inputImage.width > 10) {
			// Copy source image to input buffer, resizing to 256x256
			inputResized.copy(inputImage, 0, 0, inputImage.width, inputImage.height, 0, 0, 256, 256);
			p.image(inputResized, 0, 0, p.width, p.height); // why is the original image so slow?
			
			// Only submit new inference if previous one is done
			if (!inferenceRunning.get()) {
				submitInference(inputResized);
			}
			
			// Copy pending results to detected people (thread-safe)
			synchronized (resultsLock) {
				detectedPeople.clear();
				detectedPeople.addAll(pendingResults);
			}
			
			drawSkeleton(p.width, p.height);
		}
		// p.pop();

		// show input image for debugging
		PG.setDrawCorner(p);
		p.image(inputResized, 0, 0);

		// test general fps by drawing a moving box - check for smoothness
		// PG.setDrawCorner(p);
		// p.rect(p.frameCount % 200, 400, 100, 100);
	}

	////////////////////////
	// IWebCamCallback
	////////////////////////
	
	public void newFrame(PImage frame) {
		DebugView.setTexture("webcam", frame);
	}
	
	////////////////////////
	// MoveNet Logic
	////////////////////////
	
	protected void submitInference(PImage img) {
		// Copy pixel data and settings on main thread before submitting
		img.loadPixels();
		final int[] pixelsCopy = img.pixels.clone();
		final int imgWidth = img.width;
		final int imgHeight = img.height;
		lastMinConfidence = UI.value("Min Confidence");
		lastFlipY = UI.valueToggle(FLIP_Y);
		
		inferenceRunning.set(true);
		inferenceExecutor.submit(() -> {
			try {
				runMovenetAsync(pixelsCopy, imgWidth, imgHeight, lastFlipY, lastMinConfidence);
			} finally {
				inferenceRunning.set(false);
			}
		});
	}
	
	protected void runMovenetAsync(int[] pixels, int imgWidth, int imgHeight, boolean flipY, float minConfidence) {
		try {
			// Clear and reuse the pre-allocated buffer
			inputBuffer.clear();
			
			if(!flipY) {
				for (int i = 0; i < pixels.length; i++) {
					int pixel = pixels[i];
					inputBuffer.put((pixel >> 16) & 0xFF); // R
					inputBuffer.put((pixel >> 8) & 0xFF);  // G
					inputBuffer.put(pixel & 0xFF);         // B
				}
			} else {
				// Iterate in reverse row order (bottom-to-top) to match Python's
				// vertical flip
				for (int y = imgHeight - 1; y >= 0; y--) {
					for (int x = 0; x < imgWidth; x++) {
						int pixel = pixels[y * imgWidth + x];
						inputBuffer.put((pixel >> 16) & 0xFF); // R
						inputBuffer.put((pixel >> 8) & 0xFF); // G
						inputBuffer.put(pixel & 0xFF); // B
					}
				}
			}
			inputBuffer.rewind();
			
			// Create tensor from reusable buffer
			OnnxTensor tensor = OnnxTensor.createTensor(env, inputBuffer, inputShape);
			Result result = session.run(Collections.singletonMap(inputName, tensor));
			
			// parse output
			// Output shape: [1, 6, 56]
			float[][][] output = (float[][][]) result.get(0).getValue();
			float[][] people = output[0];
			
			// debug print
			// if (p.frameCount % 100 == 0) {
			// 	p.println("Input shape: " + imgWidth + "x" + imgHeight);
			// 	if (imgWidth % 32 != 0 || imgHeight % 32 != 0) {
			// 		p.println("WARNING: Image dimensions are not multiples of 32. This may affect model accuracy.");
			// 	}
			// 	p.println("Detections: " + people.length);
			// 	p.println("Person 0 confidence: " + people[0][55]);
			// 	p.println("Person 0 bbox: " + people[0][51] + ", " + people[0][52] + ", " + people[0][53] + ", " + people[0][54]);
			// }
			
			ArrayList<Person> newResults = new ArrayList<Person>();
			
			for (int i = 0; i < people.length; i++) {
				float[] personData = people[i];
				float confidence = personData[55];
				if (confidence >= minConfidence) {
					// p.println("Found person with confidence: " + confidence);
					Person person = new Person();
					person.confidence = confidence;
					// bbox
					person.bbox[0] = personData[51]; // ymin
					person.bbox[1] = personData[52]; // xmin
					person.bbox[2] = personData[53]; // ymax
					person.bbox[3] = personData[54]; // xmax
					
					// keypoints
					for (int k = 0; k < 17; k++) {
						float y = personData[k * 3];
						float x = personData[k * 3 + 1];
						float score = personData[k * 3 + 2];
						person.keypoints.add(new Keypoint(x, y, score));
					}
					newResults.add(person);
				}
			}
			
			// Update shared results (thread-safe)
			synchronized (resultsLock) {
				pendingResults.clear();
				pendingResults.addAll(newResults);
			}
			
			result.close();
			tensor.close();
			
		} catch (OrtException e) {
			e.printStackTrace();
		}
	}
	
	protected void drawSkeleton(float imgW, float imgH) {
		PG.setDrawCenter(p);
		
		for (Person person : detectedPeople) {
			p.noFill();
			p.strokeWeight(3);
			p.stroke(255);

			// draw bbox
			p.stroke(255, 255, 0);
			float ymin = (person.bbox[0] * imgH);// - imgH/2f;
			float xmin = (person.bbox[1] * imgW);// - imgW/2f;
			float ymax = (person.bbox[2] * imgH);// - imgH/2f;
			float xmax = (person.bbox[3] * imgW);// - imgW/2f;
			p.rectMode(CORNERS);
			p.rect(xmin, ymin, xmax, ymax);
			
			// draw keypoints
			p.stroke(0, 255, 0);
			p.fill(0, 255, 0);
			for (Keypoint kp : person.keypoints) {
				if (kp.score > 0.2f) {
					p.circle((kp.x * imgW), (kp.y * imgH), 10);
				}
			}
			
			// draw connections
			p.stroke(255, 0, 0);
			for (int[] conn : CONNECTIONS) {
				Keypoint k1 = person.keypoints.get(conn[0]);
				Keypoint k2 = person.keypoints.get(conn[1]);
				if (k1.score > 0.2f && k2.score > 0.2f) {
					p.line((k1.x * imgW), (k1.y * imgH), (k2.x * imgW), (k2.y * imgH));
				}
			}
		}
	}
	
	public static class Person {
		public float confidence;
		public float[] bbox = new float[4];
		public ArrayList<Keypoint> keypoints = new ArrayList<Keypoint>();
	}
	
	public static class Keypoint {
		public float x, y, score;
		public Keypoint(float x, float y, float score) {
			this.x = x;
			this.y = y;
			this.score = score;
		}
	}
	
	public static final int[][] CONNECTIONS = {
		{0, 1}, {0, 2}, {1, 3}, {2, 4}, {0, 5}, {0, 6}, {5, 7}, {7, 9},
		{6, 8}, {8, 10}, {5, 11}, {6, 12}, {11, 13}, {13, 15}, {12, 14}, {14, 16}
	};

}
