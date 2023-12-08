package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.OpticalFlow;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VertexShader_GPUParticlesLauncher_DepthSilhouette_Velocity 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;
	protected PGraphics silhouetteCropped;
	protected PGraphics silhouetteCroppedLerped;
	protected PGraphics depthCropped;

	protected PGraphics shapesLayer;
	protected ParticleLauncherGPU gpuParticles;
	protected String UI_SILHOUETTE_ALPHA = "UI_SILHOUETTE_ALPHA";
	protected String UI_RGB_CAMERA_ALPHA = "UI_RGB_CAMERA_ALPHA";
	protected String UI_SILHOUETTE_FILL_BLUR = "UI_SILHOUETTE_FILL_BLUR";
	protected String UI_SHOW_CAMERA_FLOW_DEBUG = "UI_SHOW_CAMERA_FLOW_DEBUG";

	protected String UI_PARTICLES_3_LAUNCH_ATTEMPTS = "UI_PARTICLES_3_LAUNCH_ATTEMPTS";
	protected String UI_PARTICLES_3_POINT_SIZE = "UI_PARTICLES_3_POINT_SIZE";
	protected String UI_PARTICLES_3_DECEL_INC = "UI_PARTICLES_3_DECEL_INC";
	protected String UI_PARTICLES_3_LAUNCH_SPEED_MULT = "UI_PARTICLES_3_LAUNCH_SPEED_MULT";
	protected String UI_PARTICLES_3_FLOW_SPEED_MULT = "UI_PARTICLES_3_FLOW_SPEED_MULT";
	protected String UI_PARTICLES_3_CURL_ZOOM = "UI_PARTICLES_3_CURL_ZOOM";
	protected String UI_PARTICLES_3_CURL_AMP_BASE = "UI_PARTICLES_3_CURL_AMP_BASE";
	protected String UI_PARTICLES_3_CURL_COHESION = "UI_PARTICLES_3_CURL_COHESION";
	protected String UI_PARTICLES_3_ALPHA = "UI_PARTICLES_3_ALPHA";
	protected String UI_PARTICLES_3_USE_OP_FLOW = "UI_PARTICLES_3_USE_OP_FLOW";
	protected String UI_PARTICLES_3_OP_FLOW_AMP = "UI_PARTICLES_3_OP_FLOW_AMP";

	protected int opticalFlowScaleDown = 4;
	protected OpticalFlow opticalFlowCamera;


	protected void config() {
		Config.setAppSize(1920, 1080);
		// Config.setProperty(AppSettings.FPS, 90);
	}
		
	protected void firstFrame () {
		// init depth cam
		RealSenseWrapper.setMidStreamFast();
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 6);
		depthSilhouetteSmoothed.buildUI(false);
		depthSilhouetteSmoothed.setDepthFar(3000);
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());

		// build buffer to paste silhouette into that matches the aspect ratio of the app
		float silhouetteCropScaleDown = 0.2f;
		silhouetteCropped = PG.newPG(P.round(p.width * silhouetteCropScaleDown), P.round(p.height * silhouetteCropScaleDown), false, true);
		silhouetteCroppedLerped = PG.newPG(P.round(p.width * silhouetteCropScaleDown), P.round(p.height * silhouetteCropScaleDown), false, true);
		depthCropped = PG.newPG(p.width, p.height);
		DebugView.setTexture("silhouetteCropped", silhouetteCropped);
		DebugView.setTexture("silhouetteCroppedLerped", silhouetteCroppedLerped);
		DebugView.setTexture("depthCropped", depthCropped);
		
		//////////////////////////////////////////////////////////////
		// build particles launcher
		shapesLayer = PG.newPG(p.width, p.height);
		
		PImage particle = DemoAssets.particle();
		gpuParticles = new ParticleLauncherGPU(256, "haxademic/shaders/point/particle-launcher-speed-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-vert.glsl", particle);
		DebugView.setValue("gpuParticles.vertices()", gpuParticles.numParticles());
		DebugView.setTexture("gpuParticles.positionBuffer()", gpuParticles.positionBuffer());
		DebugView.setTexture("gpuParticles.colorBuffer()", gpuParticles.colorBuffer());
		
		
		//////////////////////////////////////////////////////////////
		// UI
		UI.addTitle("GPUParticlesLauncher 3");
		UI.addSlider(UI_PARTICLES_3_LAUNCH_ATTEMPTS, 1000, 1, 5000, 10, false);
		UI.addSlider(UI_PARTICLES_3_POINT_SIZE, 10f, 0, 50, 0.1f, false);
		UI.addSlider(UI_PARTICLES_3_DECEL_INC, 0.0001f, 0.00001f, 0.001f, 0.00001f, false);
		UI.addSlider(UI_PARTICLES_3_LAUNCH_SPEED_MULT, 0.13f, 0.001f, 0.3f, 0.001f, false);
		UI.addSlider(UI_PARTICLES_3_FLOW_SPEED_MULT, 0.11f, 0.001f, 0.3f, 0.001f, false);
		UI.addSlider(UI_PARTICLES_3_CURL_ZOOM, 850, 0, 1000, 1, false);
		UI.addSlider(UI_PARTICLES_3_CURL_AMP_BASE, 40, 0, 500, 1, false);
		UI.addSlider(UI_PARTICLES_3_CURL_COHESION, 10, 0, 1000, 1, false); // larger numbers bring particles cohesion closer
		UI.addSlider(UI_PARTICLES_3_ALPHA, 1, 0, 1, 0.01f, false);
		UI.addSlider(UI_PARTICLES_3_OP_FLOW_AMP, 0.1f, 0, 1, 0.001f, false);
		UI.addToggle(UI_PARTICLES_3_USE_OP_FLOW, true, false);
		UI.addToggle(UI_SHOW_CAMERA_FLOW_DEBUG, true, false);
		UI.addTitle("Silhouette / Camera view");
		UI.addSlider(UI_SILHOUETTE_ALPHA, 0f, 0, 1, 0.01f, false);
		UI.addSlider(UI_RGB_CAMERA_ALPHA, 1f, 0, 1, 0.01f, false);
		UI.addSlider(UI_SILHOUETTE_FILL_BLUR, 10f, 0, 50, 1, false);
		
		//////////////////////////////////////////////////////////////
		buildOpticalFlow();
	}
	
	protected void buildOpticalFlow() {
		opticalFlowCamera = new OpticalFlow(silhouetteCropped.width, silhouetteCropped.height);
		opticalFlowCamera.buildUI();
//		UI.setValue(OpticalFlow.UI_uForce, 2.5f);
	}
	
	protected void drawApp() {
		// set up context
		p.background(0);
		
		//////////////////////////////////////////////////////////
		// copy silhouette to aspect-ratio-corrected copy
		// and prepare pixels data
		depthSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(depthSilhouetteSmoothed.image(), silhouetteCropped, true);
		ThresholdFilter.instance().applyTo(silhouetteCropped);
		
		// lerp silhouette for launch map
		BlendTowardsTexture.instance().setBlendLerp(0.2f);
		BlendTowardsTexture.instance().setSourceTexture(silhouetteCropped);
		BlendTowardsTexture.instance().applyTo(silhouetteCroppedLerped);
		
		// copy depth to cropped buffer
		ImageUtil.cropFillCopyImage(DepthCamera.instance().camera.getDepthImage(), depthCropped, true);
		
		
		//////////////////////////////////////////////////////////
		// draw silhouette to screen
//		p.blendMode(PBlendModes.ADD);
		p.blendMode(PBlendModes.BLEND);
		PG.setPImageAlpha(p, UI.value(UI_SILHOUETTE_ALPHA));
		p.image(silhouetteCropped, 0, 0, p.width, p.height);
		PG.resetPImageAlpha(p);
		
		p.blendMode(PBlendModes.BLEND);
		PG.setPImageAlpha(p, UI.value(UI_RGB_CAMERA_ALPHA));
		p.image(DepthCamera.instance().camera.getRgbImage(), 0, 0, p.width, p.height);
		PG.resetPImageAlpha(p);

		//////////////////////////////////////////////////////////
		// update camera optical flow
		opticalFlowCamera.update(silhouetteCropped, true); // depthCropped
		DebugView.setTexture("opticalFlowCameraResult", opticalFlowCamera.resultBuffer());
		DebugView.setTexture("opticalFlowCameraResultFlowed", opticalFlowCamera.resultFlowedBuffer());
		if(UI.valueToggle(UI_SHOW_CAMERA_FLOW_DEBUG)) {
			// generate debug texture
			opticalFlowCamera.drawDebugLines(true);
			// then draw it
			PG.setPImageAlpha(p.g, 0.5f);
			p.g.image(opticalFlowCamera.debugBuffer(), 0, 0, p.width, p.height);	
			PG.resetPImageAlpha(p.g);
		}
		
		//////////////////////////////////////////////////////////
		// launch particles! need to open & close the position buffer where we're writing new launch pixels
		// prepare pixels for launch map
		PImage launchMap = silhouetteCroppedLerped; // silhouetteCropped
		launchMap.loadPixels();
		launchFromMap(launchMap, gpuParticles, UI.valueInt(UI_PARTICLES_3_LAUNCH_ATTEMPTS));
		
		//////////////////////////////////////////////////////////
		// update particles buffers 1
		int startUpdateTime = p.millis();
		gpuParticles.simulationShader().set("decelInc", UI.valueEased(UI_PARTICLES_3_DECEL_INC));
		gpuParticles.simulationShader().set("launchSpeedMult", UI.valueEased(UI_PARTICLES_3_LAUNCH_SPEED_MULT));
		gpuParticles.simulationShader().set("flowSpeedAddMult", UI.valueEased(UI_PARTICLES_3_FLOW_SPEED_MULT));
		gpuParticles.simulationShader().set("flowMap", opticalFlowCamera.resultFlowedBuffer());
		gpuParticles.simulationShader().set("flowMode", (UI.valueToggle(UI_PARTICLES_3_USE_OP_FLOW)) ? 1 : 0);
		gpuParticles.simulationShader().set("flowAmp", UI.valueEased(UI_PARTICLES_3_OP_FLOW_AMP));
		gpuParticles.gravity(0, 0.0001f);
		gpuParticles.updateSimulation();
		DebugView.setValue("updateTime", p.millis() - startUpdateTime);
		
		// update particles color map
		ImageUtil.copyImage(ImageGradient.PASTELS(), gpuParticles.colorBuffer());
		// ImageUtil.copyImage(DepthCamera.instance().camera.getRgbImage(), gpuParticles.colorBuffer());

		// update/draw particles
//		shapesLayer.beginDraw();
//		shapesLayer.background(0,0);
		p.g.blendMode(PBlendModes.ADD);
//		p.g.blendMode(PBlendModes.BLEND);
		gpuParticles.pointSize(UI.value(UI_PARTICLES_3_POINT_SIZE));
		gpuParticles.rotateAmp(0);
		gpuParticles.renderShader().set("curlZoom", UI.value(UI_PARTICLES_3_CURL_ZOOM));
		gpuParticles.renderShader().set("curlAmpBase", UI.value(UI_PARTICLES_3_CURL_AMP_BASE));
		gpuParticles.renderShader().set("curlCohesion", UI.value(UI_PARTICLES_3_CURL_COHESION));
		gpuParticles.renderShader().set("colorMapTints", 1);
		gpuParticles.renderShader().set("baseAlpha", UI.value(UI_PARTICLES_3_ALPHA));
		gpuParticles.renderShader().set("speedSimMode", 1);
		gpuParticles.renderTo(p.g, true);
//		shapesLayer.endDraw();
	}
	
	protected void launchFromMap(PImage launchMap, ParticleLauncherGPU particles, int launchAttempts) {
		// launch! need to open & close the position buffer where we're writing new launch pixels
		int startLaunchTime = p.millis();
		particles.beginLaunch();

		// look for non-black pixels to launch from
		// since we're using a scaled-down map for efficiency, scale x/y positions back up
		float scaleLaunchMapToScreen = MathUtil.scaleToTarget(launchMap.height, p.height);
		int numLaunched = 0;
		int launches = launchAttempts;
		for (int i = 0; i < launches; i++) {
			int checkX = MathUtil.randRange(0, launchMap.width);
			int checkY = MathUtil.randRange(0, launchMap.height);
			int pixelColor = ImageUtil.getPixelColor(launchMap, checkX, checkY);
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
	
	public void keyPressed() {
		super.keyPressed();
	}
	
}
