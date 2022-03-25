package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BlobFinder;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import blobDetection.Blob;
import blobDetection.EdgeVertex;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.video.Movie;

public class Demo_VertexShader_GPUParticlesLauncher_DepthSilhouette_Blobs 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;
	protected PGraphics silhouetteCropped;
	protected PGraphics silhouetteFill;
	protected PGraphics silhouetteShadow;

	protected BlobFinder blobFinder;
	
	protected PGraphics shapesLayer;
	protected ParticleLauncherGPU gpuParticles1;
	protected String UI_SILHOUETTE_ALPHA = "UI_SILHOUETTE_ALPHA";
	protected String UI_SILHOUETTE_FILL_BLUR = "UI_SILHOUETTE_FILL_BLUR";
	protected String UI_OUTLINE_ALPHA = "UI_OUTLINE_ALPHA";
	protected String UI_OUTLINE_BLUR_ITERS = "UI_OUTLINE_BLUR_ITERS";
	protected String UI_OUTLINE_BLUR_SIZE = "UI_OUTLINE_BLUR_SIZE";
	protected String UI_OUTLINE_BLUR_SIGMA = "UI_OUTLINE_BLUR_SIGMA";
	protected String UI_SHOW_VIDEO = "UI_SHOW_VIDEO";
	protected String UI_VIDEO_FLOW_DEBUG = "UI_VIDEO_FLOW_DEBUG";
	protected String UI_SHOW_CAMERA_FLOW_DEBUG = "UI_SHOW_CAMERA_FLOW_DEBUG";

	protected Movie movieBg;
	protected String UI_PARTICLES_1_LAUNCH_ATTEMPTS = "UI_PARTICLES_1_LAUNCH_ATTEMPTS";
	protected String UI_PARTICLES_1_POINT_SIZE = "UI_PARTICLES_1_POINT_SIZE";
	protected String UI_PARTICLES_1_LIFESPAN_STEP = "UI_PARTICLES_1_LIFESPAN_STEP";
	protected String UI_PARTICLES_1_BASE_SPEED = "UI_PARTICLES_1_BASE_SPEED";
	protected String UI_PARTICLES_1_CURL_ZOOM = "UI_PARTICLES_1_CURL_ZOOM";
	protected String UI_PARTICLES_1_CURL_AMP_BASE = "UI_PARTICLES_1_CURL_AMP_BASE";
	protected String UI_PARTICLES_1_CURL_COHESION = "UI_PARTICLES_1_CURL_COHESION";
	protected String UI_PARTICLES_1_ALPHA = "UI_PARTICLES_1_ALPHA";
	protected String UI_PARTICLES_1_USE_OP_FLOW = "UI_PARTICLES_1_USE_OP_FLOW";
	protected String UI_PARTICLES_1_OP_FLOW_AMP = "UI_PARTICLES_1_OP_FLOW_AMP";

	protected OpticalFlow opticalFlowVideo;
	protected int opticalFlowScaleDown = 4;
	protected PGraphics opticalFlowVideoBuffer;

	protected OpticalFlow opticalFlowCamera;


	protected void config() {
		Config.setAppSize(1920, 1080);
//		Config.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	protected void firstFrame () {
		// Video Background
		movieBg = new Movie(P.p, FileUtil.getPath(DemoAssets.movieFractalCubePath));
		movieBg.play();


		// init depth cam
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 6);
		depthSilhouetteSmoothed.buildUI(false);
		UI.setValue(DepthSilhouetteSmoothed.SILHOUETTE_DEPTH_FAR, 3000);
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());

		// build buffer to paste silhouette into that matches the aspect ratio of the app
		float silhouetteCropScaleDown = 0.2f;
		silhouetteCropped = PG.newPG(P.round(p.width / 2 * silhouetteCropScaleDown), P.round(p.height * silhouetteCropScaleDown), false, true);
		silhouetteShadow = PG.newPG(silhouetteCropped.width, silhouetteCropped.height, false, true);
		silhouetteFill = PG.newPG(silhouetteCropped.width, silhouetteCropped.height, false, true);
		DebugView.setTexture("silhouetteCropped", silhouetteCropped);
		DebugView.setTexture("silhouetteShadow", silhouetteShadow);
		DebugView.setTexture("silhouetteFill", silhouetteFill);
		
		//////////////////////////////////////////////////////////////
		// build particles launcher
		shapesLayer = PG.newPG(p.width, p.height);
		
//		gpuParticles = new ParticleLauncherGPU(256, "haxademic/shaders/point/particle-launcher-fizz-frag.glsl");
		PImage particle = DemoAssets.particle();
		gpuParticles1 = new ParticleLauncherGPU(256, "haxademic/shaders/point/particle-launcher-fizz-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-vert.glsl", particle);
		DebugView.setValue("gpuParticles.vertices()", gpuParticles1.numParticles());
		DebugView.setTexture("gpuParticles.positionBuffer()", gpuParticles1.positionBuffer());
		DebugView.setTexture("gpuParticles.colorBuffer()", gpuParticles1.colorBuffer());
		
		
		//////////////////////////////////////////////////////////////
		// init blob detection
		blobFinder = new BlobFinder(silhouetteCropped, 0.4f);

		//////////////////////////////////////////////////////////////
		// UI
		UI.addTitle("GPUParticlesLauncher 1");
		UI.addSlider(UI_PARTICLES_1_LAUNCH_ATTEMPTS, 400, 1, 5000, 10, false);
		UI.addSlider(UI_PARTICLES_1_POINT_SIZE, 0.9f, 0, 50, 0.1f, false);
		UI.addSlider(UI_PARTICLES_1_LIFESPAN_STEP, 0.005f, 0.0001f, 0.05f, 0.0001f, false);
		UI.addSlider(UI_PARTICLES_1_BASE_SPEED, 0.00075f, 0, 0.004f, 0.00001f, false);
		UI.addSlider(UI_PARTICLES_1_CURL_ZOOM, 400, 0, 1000, 1, false);
		UI.addSlider(UI_PARTICLES_1_CURL_AMP_BASE, 80, 0, 500, 1, false);
		UI.addSlider(UI_PARTICLES_1_CURL_COHESION, 10, 0, 1000, 1, false); // larger numbers bring particles cohesion closer
		UI.addSlider(UI_PARTICLES_1_ALPHA, 1, 0, 1, 0.01f, false);
		UI.addSlider(UI_PARTICLES_1_OP_FLOW_AMP, 0.04f, 0, 1, 0.001f, false);
		UI.addToggle(UI_PARTICLES_1_USE_OP_FLOW, false, false);
		UI.addToggle(UI_SHOW_CAMERA_FLOW_DEBUG, false, false);
		UI.addTitle("Background video");
		UI.addToggle(UI_SHOW_VIDEO, true, false);
		UI.addToggle(UI_VIDEO_FLOW_DEBUG, false, false);
		UI.addTitle("Blob Silhouette");
		UI.addSlider(UI_SILHOUETTE_ALPHA, 0f, 0, 1, 0.01f, false);
		UI.addSlider(UI_SILHOUETTE_FILL_BLUR, 10f, 0, 50, 1, false);
		UI.addTitle("Blob Outline");
		UI.addSlider(UI_OUTLINE_ALPHA, 0f, 0, 1, 0.01f, false);
		UI.addSlider(UI_OUTLINE_BLUR_ITERS, 3, 0, 10, 1, false);
		UI.addSlider(UI_OUTLINE_BLUR_SIZE, 30, 0, 100, 1, false);
		UI.addSlider(UI_OUTLINE_BLUR_SIGMA, 10, 0, 100, 1, false);
		
		//////////////////////////////////////////////////////////////
		buildOpticalFlow();
	}
	
	protected void buildOpticalFlow() {
		// optical flow for video layer
		opticalFlowVideoBuffer = PG.newPG(p.width / opticalFlowScaleDown, p.height / opticalFlowScaleDown);
		opticalFlowVideo = new OpticalFlow(opticalFlowVideoBuffer.width, opticalFlowVideoBuffer.height);
//		opticalFlowVideo.buildUI();
		String videoConfig = "{ \"uDecayLerp\": 0.015, \"uForce\": 2.5, \"uOffset\": 9.0, \"uLambda\": 0.012, \"uThreshold\": 0.001, \"uInverseX\": -1.0, \"uInverseY\": -1.0, \"preBlurAmp\": 1.0, \"preBlurSigma\": 1.0, \"resultFlowDecayLerp\": 0.1, \"resultFlowDisplaceAmp\": 0.2, \"resultFlowDisplaceIters\": 1.0, \"resultBlurAmp\": 2.0, \"resultBlurSigma\": 4.0, \"sourceLerp\": 0.1, \"sourceDisplaceAmp\": 0.2, \"sourceDisplaceIters\": 1.0, \"showDebug\": 0.0 }";
		JSONObject jsonConfig = JsonUtil.jsonFromString(videoConfig);
		
		opticalFlowVideo.uDecayLerp(jsonConfig.getFloat("uDecayLerp"));
		opticalFlowVideo.uForce(jsonConfig.getFloat("uForce"));
		opticalFlowVideo.uOffset(jsonConfig.getFloat("uOffset"));
		opticalFlowVideo.uLambda(jsonConfig.getFloat("uLambda"));
		opticalFlowVideo.uThreshold(jsonConfig.getFloat("uThreshold"));
		opticalFlowVideo.resultFlowDecayLerp(jsonConfig.getFloat("resultFlowDecayLerp"));
			
		opticalFlowVideo.preBlurAmp(jsonConfig.getInt("preBlurAmp"));
		opticalFlowVideo.preBlurSigma(jsonConfig.getInt("preBlurSigma"));
			
		opticalFlowVideo.resultFlowDisplaceAmp(jsonConfig.getFloat("resultFlowDisplaceAmp"));
		opticalFlowVideo.resultFlowDisplaceIters(jsonConfig.getInt("resultFlowDisplaceIters"));
		opticalFlowVideo.resultBlurAmp(jsonConfig.getInt("resultBlurAmp"));
		opticalFlowVideo.resultBlurSigma(jsonConfig.getInt("resultBlurSigma"));
		
		// optical flow for camera
		opticalFlowCamera = new OpticalFlow(silhouetteCropped.width, silhouetteCropped.height);
	}
	
	protected void drawApp() {
		// set up context
		p.background(100);
		
		//////////////////////////////////////////////////////////
		// background video layer
		DebugView.setValue("movieBuffer.movie.isPlaying()", movieBg.isPlaying());
		DebugView.setTexture("movieBuffer.buffer", movieBg);
		if(movieBg.isPlaying() == false) {
			movieBg.jump(0);
			movieBg.play();
		}
		if(UI.valueToggle(UI_SHOW_VIDEO)) {
			ImageUtil.cropFillCopyImage(movieBg, p.g, false);
		}
		
		//////////////////////////////////////////////////////////
		// update video optical flow
		if(movieBg.width > 128) ImageUtil.copyImage(movieBg, opticalFlowVideoBuffer);
		opticalFlowVideo.update(opticalFlowVideoBuffer, true);
		DebugView.setTexture("opFlowResult", opticalFlowVideo.resultBuffer());
		DebugView.setTexture("opFlowResultFlowed", opticalFlowVideo.resultFlowedBuffer());
		// draw video optical flow debug
		if(UI.valueToggle(UI_VIDEO_FLOW_DEBUG)) {
			// generate debug texture
			opticalFlowVideo.drawDebugLines(true);
			// then draw it
			PG.setPImageAlpha(p.g, 0.9f);
			ImageUtil.drawImageCropFill(opticalFlowVideo.debugBuffer(), p.g, true);	
			PG.resetPImageAlpha(p.g);
		}
		
		
		//////////////////////////////////////////////////////////
		// copy silhouette to aspect-ratio-corrected copy
		// and prepare pixels data
		depthSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(depthSilhouetteSmoothed.image(), silhouetteCropped, true);
		ThresholdFilter.instance(p).applyTo(silhouetteCropped);

		//////////////////////////////////////////////////////////
		// update camer optical flow
		opticalFlowCamera.uForce(2.5f);
		opticalFlowCamera.update(silhouetteCropped, true);
		DebugView.setTexture("opticalFlowCameraResult", opticalFlowCamera.resultBuffer());
		DebugView.setTexture("opticalFlowCameraResultFlowed", opticalFlowCamera.resultFlowedBuffer());
		if(UI.valueToggle(UI_SHOW_CAMERA_FLOW_DEBUG)) {
			// generate debug texture
			opticalFlowCamera.drawDebugLines(true);
			// then draw it
			PG.setPImageAlpha(p.g, 0.5f);
			p.g.image(opticalFlowCamera.debugBuffer(), 0, 0, p.width/2, p.height);	
			PG.resetPImageAlpha(p.g);
		}


		// update blob detection
		blobFinder.blobDetection().setPosDiscrimination(true);	// true if looking for bright areas
		blobFinder.blobDetection().setThreshold(0.5f); // will detect bright areas whose luminosity > threshold
		blobFinder.update();

		//////////////////////////////////////////////////////////
		// build outline under the particles
		// draw outline
		silhouetteShadow.beginDraw();
		silhouetteShadow.background(0,0);
		silhouetteShadow.clear();
		drawBlobs(silhouetteShadow, p.color(0), true);
		silhouetteShadow.endDraw();
		
		// blur it
		BlurProcessingFilter.instance(p).setBlurSize(UI.valueInt(UI_OUTLINE_BLUR_SIZE));
		BlurProcessingFilter.instance(p).setSigma(UI.valueInt(UI_OUTLINE_BLUR_SIGMA));
		for (int i = 0; i < UI.valueInt(UI_OUTLINE_BLUR_ITERS); i++) {
			BlurProcessingFilter.instance(p).applyTo(silhouetteShadow);
		}

		//////////////////////////////////////////////////////////
		// draw fill
		silhouetteFill.beginDraw();
		silhouetteFill.background(0,0);
		silhouetteFill.clear();
		drawBlobs(silhouetteFill, p.color(0), false);
		silhouetteFill.endDraw();
		
		BlurProcessingFilter.instance(p).setBlurSize(UI.valueInt(UI_SILHOUETTE_FILL_BLUR));
		BlurProcessingFilter.instance(p).setSigma(UI.valueInt(UI_SILHOUETTE_FILL_BLUR));
		for (int i = 0; i < 1; i++) {
			BlurProcessingFilter.instance(p).applyTo(silhouetteFill);
		}
		
		//////////////////////////////////////////////////////////
		// launch particles! need to open & close the position buffer where we're writing new launch pixels
		// prepare pixels for launch map
		silhouetteCropped.loadPixels();

//		launchFromBlobs();
		launchFromMap(gpuParticles1, UI.valueInt(UI_PARTICLES_1_LAUNCH_ATTEMPTS));
		
		//////////////////////////////////////////////////////////
		// update particles buffers 1
		int startUpdateTime = p.millis();
		gpuParticles1.simulationShader().set("lifespanStep", UI.valueEased(UI_PARTICLES_1_LIFESPAN_STEP));
		gpuParticles1.simulationShader().set("baseSpeed", UI.valueEased(UI_PARTICLES_1_BASE_SPEED));
		gpuParticles1.simulationShader().set("flowMap", opticalFlowVideo.resultFlowedBuffer());
		gpuParticles1.simulationShader().set("flowMode", (UI.valueToggle(UI_PARTICLES_1_USE_OP_FLOW)) ? 1 : 0);
		gpuParticles1.simulationShader().set("flowAmp", UI.valueEased(UI_PARTICLES_1_OP_FLOW_AMP));
		gpuParticles1.gravity(0, -0.001f);
		gpuParticles1.updateSimulation();
		DebugView.setValue("updateTime", p.millis() - startUpdateTime);
		
		// update particles color map
		ImageUtil.copyImage(ImageGradient.PASTELS(), gpuParticles1.colorBuffer());

		// update/draw particles
//		shapesLayer.beginDraw();
//		shapesLayer.background(0,0);
		p.g.blendMode(PBlendModes.ADD);
//		p.g.blendMode(PBlendModes.BLEND);
		gpuParticles1.pointSize(UI.value(UI_PARTICLES_1_POINT_SIZE));
		gpuParticles1.rotateAmp(0);
		gpuParticles1.renderShader().set("curlZoom", UI.value(UI_PARTICLES_1_CURL_ZOOM));
		gpuParticles1.renderShader().set("curlAmpBase", UI.value(UI_PARTICLES_1_CURL_AMP_BASE));
		gpuParticles1.renderShader().set("curlCohesion", UI.value(UI_PARTICLES_1_CURL_COHESION));
		gpuParticles1.renderShader().set("colorMapTints", 1);
		gpuParticles1.renderShader().set("baseAlpha", UI.value(UI_PARTICLES_1_ALPHA));
		gpuParticles1.renderTo(p.g, true);
//		shapesLayer.endDraw();
		
		//////////////////////////////////////////////////////////
		// draw silhouette to screen
//		p.blendMode(PBlendModes.ADD);
		p.blendMode(PBlendModes.BLEND);
		
		PG.setPImageAlpha(p, UI.value(UI_SILHOUETTE_ALPHA));
		p.image(silhouetteFill, 0, 0, p.width / 2, p.height);
		PG.resetPImageAlpha(p);
		
		//////////////////////////////////////////////////////////
		// draw silhouette outline glow
//		p.blendMode(PBlendModes.SUBTRACT);
		PG.setPImageAlpha(p, UI.value(UI_OUTLINE_ALPHA));
		p.image(silhouetteShadow, 0, 0, p.width / 2, p.height);
		p.image(silhouetteShadow, 0, 0, p.width / 2, p.height);
		p.image(silhouetteShadow, 0, 0, p.width / 2, p.height);
		p.image(silhouetteShadow, 0, 0, p.width / 2, p.height);
		PG.resetPImageAlpha(p);
		
		
		//////////////////////////////////////////////////////////
		// debug: draw rgb camera in corner
		PImage cameraSource = DepthCamera.instance().camera.getRgbImage();
		float camScale = 0.2f;
		float camW = cameraSource.width * camScale;
		float camH = cameraSource.height * camScale;
		p.image(cameraSource, p.width - camW - 20, 20, camW, camH);
	}
	
	protected void drawBlobs(PGraphics pg, int color, boolean lines) {
		// draw edges. scale up to screen size
		float scaleUp = MathUtil.scaleToTarget(silhouetteCropped.height, pg.height);
		float blobScaleW = silhouetteCropped.width * scaleUp;
		float blobScaleH = silhouetteCropped.height * scaleUp;
		
		// loop through blobs
		if(lines) {
			pg.noFill();
			pg.strokeWeight(3f);
			pg.stroke(color);
		} else {
			pg.beginShape();
			pg.fill(color);
			pg.noStroke();
		}

		int numBlobs = blobFinder.numBlobs();
		for (int i=0 ; i < numBlobs; i++) {
			Blob blob = blobFinder.getBlob(i);
			if ( blob != null ) {
				// loop through blob segments & draw vertices
				int numBlobSegments = blob.getEdgeNb();
				if(numBlobSegments > 100) {
					
					// get average position of blob segments for center of mass
					for (int m = 0; m < numBlobSegments; m++) {
//						int safeIndex = (m * segmentsToSkip) % numBlobSegments;
						EdgeVertex eA = blob.getEdgeVertexA(m);
						EdgeVertex eB = blob.getEdgeVertexB(m);
						float segmentX = eA.x * blobScaleW;
						float segmentY = eA.y * blobScaleH;
						float segment2X = eB.x * blobScaleW;
						float segment2Y = eB.y * blobScaleH;
												
						// draw vertex
//						pg.ellipse(eA.x * blobScaleW, eA.y * blobScaleH, 5, 5);
						if(lines) {
							pg.line(segmentX, segmentY, segment2X, segment2Y);
						} else {
							pg.vertex(segmentX, segmentY);
							pg.vertex(segment2X, segment2Y);
						}
					}
				}
			}
		}
		if(!lines) pg.endShape(P.CLOSE);
	}
	
	protected void launchFromMap(ParticleLauncherGPU particles, int launchAttempts) {
		// launch! need to open & close the position buffer where we're writing new launch pixels
		int startLaunchTime = p.millis();
		particles.beginLaunch();

		// look for non-black pixels to launch from
		// since we're using a scaled-down map for efficiency, scale x/y positions back up
		float scaleLaunchMapToScreen = MathUtil.scaleToTarget(silhouetteCropped.height, p.height);
		int numLaunched = 0;
		int launches = launchAttempts;
		for (int i = 0; i < launches; i++) {
			int checkX = MathUtil.randRange(0, silhouetteCropped.width);
			int checkY = MathUtil.randRange(0, silhouetteCropped.height);
			int pixelColor = ImageUtil.getPixelColor(silhouetteCropped, checkX, checkY);
			float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
			if(redColor > 0.3f && numLaunched < launches) {
				particles.launch(shapesLayer, checkX * scaleLaunchMapToScreen, checkY * scaleLaunchMapToScreen);
				numLaunched++;
			}
		}
		DebugView.setValue("numLaunched", numLaunched);

		particles.endLaunch();
		DebugView.setValue("launchTime", p.millis() - startLaunchTime);	
	}
	
	protected void launchFromBlobs(ParticleLauncherGPU particles, int launchAttempts) {
		int startLaunchTime = p.millis();
		particles.beginLaunch();
//		float scaleLaunchMapToScreen = MathUtil.scaleToTarget(launchMap.width, p.width);

		
		// draw edges. scale up to screen size
		float scaleUp = MathUtil.scaleToTarget(silhouetteCropped.height, p.height);
		float blobScaleW = silhouetteCropped.width * scaleUp;
		float blobScaleH = silhouetteCropped.height * scaleUp;
		
		// loop through blobs
		int numBlobs = blobFinder.numBlobs();
		for (int i=0 ; i < numBlobs; i++) {
			Blob blob = blobFinder.getBlob(i);
			if ( blob != null ) {
				// loop through blob segments & draw vertices
				// make sure blobs are big enough to draw
				int numBlobSegments = blob.getEdgeNb();
				if(numBlobSegments > 100) {
					
					// TODO: need to pick a random index of all blobs & their vertices
					// this is currently not evenly distributed, or respecting the max launches concept if there are multiple blobs
					int launches = launchAttempts;
					for (int l = 0; l < launches; l++) {
						int randIndex = MathUtil.randIndex(numBlobSegments);
						EdgeVertex eA = blob.getEdgeVertexA(randIndex);
						particles.launch(shapesLayer, eA.x * blobScaleW, eA.y * blobScaleH);
					}
					
					/*
					for (int m = 0; m < numBlobSegments; m++) {
//						int safeIndex = (m * segmentsToSkip) % numBlobSegments;
						EdgeVertex eA = blob.getEdgeVertexA(m);
						
						// launch particles
						particles.launch(shapesLayer, eA.x * blobScaleW, eA.y * blobScaleH);
					}
					*/
				}
			}
		}
		
		particles.endLaunch();
		DebugView.setValue("launchTime", p.millis() - startLaunchTime);
	}
	
	
	public void keyPressed() {
		super.keyPressed();
	}
	
}
