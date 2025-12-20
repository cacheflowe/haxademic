package com.haxademic.demo.draw.cv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;

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
import processing.core.PImage;

public class Demo_ONNX 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected OrtEnvironment env;
	protected OrtSession session;
	protected ArrayList<Person> detectedPeople = new ArrayList<Person>();
	protected PImage inputResized;

	protected static final String FLIP_Y = "FLIP_Y";

	protected void config() {
		Config.setAppSize(1280, 720);
	}
		
	protected void firstFrame () {
		// load webcam
		WebCam.instance().setDelegate(this);
		
		// add UI
		UI.addTitle("ONNX Config");
		UI.addSlider("Min Confidence", 0.3f, 0, 1, 0.01f, false);
		UI.addToggle(FLIP_Y, false, false);

		// show controls by default
		UI.active(true);
		DebugView.active(true);

		// init ONNX session
		try {
			env = OrtEnvironment.getEnvironment();
			session = env.createSession(FileUtil.getPath("ml/movenet-multipose-lightning.onnx"), new OrtSession.SessionOptions());
			
			// print input info
			p.println("Input info: " + session.getInputInfo());
			p.println("Output info: " + session.getOutputInfo());
		} catch (OrtException e) {
			e.printStackTrace();
		}
		
		inputResized = p.createImage(256, 256, ARGB);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw webcam
		PImage cam = WebCam.instance().image();
		p.image(cam, 0, 0, p.width, p.height);

		// PG.setDrawCenter(p);
		// PImage people = ImageCacher.get("images/_sketch/people.png");
		// float drawX = p.width / 2f + P.sin(p.frameCount * 0.01f) * 100f;
		// float drawY = p.height / 2f + P.cos(p.frameCount * 0.007f) * 100f;
		// float rot = P.sin(p.frameCount * 0.01f) * 0.2f;
		// p.push();
		// p.translate(drawX, drawY);
		// p.rotate(rot);
		// p.scale(0.6f);
		// p.image(people, 0, 0);

		PImage inputImage = cam;

		// run inference on latest frame
		if(session != null && inputImage.width > 10) {
			// Copy source image to input buffer, resizing to 256x256
			// We use the source image directly so detections are relative to the image content,
			// regardless of how the image is transformed on screen.
			inputResized.copy(inputImage, 0, 0, inputImage.width, inputImage.height, 0, 0, 256, 256);
			runMovenet(inputResized);
			drawSkeleton(p.width, p.height);
		}
		// p.pop();

		PG.setDrawCorner(p);
		p.image(inputResized, 0, 0);
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
	
	protected void runMovenet(PImage img) {
		try {
			// prepare input tensor
			// Model expects INT32, shape [1, H, W, 3]
			long[] shape = {1, img.height, img.width, 3};
			// Use direct buffer with native byte order to ensure correct integer values are passed to C++ runtime
			IntBuffer buffer = ByteBuffer.allocateDirect(img.height * img.width * 3 * 4)
					.order(ByteOrder.nativeOrder())
					.asIntBuffer();
			
			img.loadPixels();
			
			if(UI.valueToggle(FLIP_Y) == false) {
				for (int i = 0; i < img.pixels.length; i++) {
					int pixel = img.pixels[i];
					buffer.put((pixel >> 16) & 0xFF); // R
					buffer.put((pixel >> 8) & 0xFF);  // G
					buffer.put(pixel & 0xFF);         // B
				}
			} else {
				// Iterate in reverse row order (bottom-to-top) to match Python's
				// vertical flip
				for (int y = img.height - 1; y >= 0; y--) {
					for (int x = 0; x < img.width; x++) {
						int pixel = img.pixels[y * img.width + x];
						buffer.put((pixel >> 16) & 0xFF); // R
						buffer.put((pixel >> 8) & 0xFF); // G
						buffer.put(pixel & 0xFF); // B
					}
				}
			}
			buffer.rewind();
			
			OnnxTensor tensor = OnnxTensor.createTensor(env, buffer, shape);
			Result result = session.run(Collections.singletonMap(session.getInputNames().iterator().next(), tensor));
			
			// parse output
			// Output shape: [1, 6, 56]
			float[][][] output = (float[][][]) result.get(0).getValue();
			float[][] people = output[0];
			
			// debug print
			if (p.frameCount % 100 == 0) {
				p.println("Input shape: " + img.width + "x" + img.height);
				if (img.width % 32 != 0 || img.height % 32 != 0) {
					p.println("WARNING: Image dimensions are not multiples of 32. This may affect model accuracy.");
				}
				p.println("Detections: " + people.length);
				p.println("Person 0 confidence: " + people[0][55]);
				p.println("Person 0 bbox: " + people[0][51] + ", " + people[0][52] + ", " + people[0][53] + ", " + people[0][54]);
			}
			
			detectedPeople.clear();
			float minConfidence = UI.value("Min Confidence");
			// p.println("Min Confidence: " + minConfidence);
			
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
					detectedPeople.add(person);
				}
			}
			
			result.close();
			tensor.close();
			
		} catch (OrtException e) {
			e.printStackTrace();
		}
	}
	
	protected void drawSkeleton(float imgW, float imgH) {
		PG.setDrawCenter(p);
		p.noFill();
		p.strokeWeight(3);
		p.stroke(255);
		
		for (Person person : detectedPeople) {
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
