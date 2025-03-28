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
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthSilhouetteSmoothed;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import VLCJVideo.VLCJVideo;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VertexShader_GPUParticlesLauncher_DepthSilhouette 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
	protected DepthSilhouetteSmoothed depthSilhouetteSmoothed;

	protected ParticleLauncherGPU gpuParticles;
	
	protected PGraphics silhouetteCropped;
	protected PGraphics shapesLayer;
	protected ImageGradient imageGradient;
	protected int FRAME_LAUNCH_INTERVAL = 1;
	
	protected String UI_PARTICLES_LAUNCH_ATTEMPTS = "UI_MAX_LAUNCHES_PER_FRAME";
	protected String UI_PARTICLES_POINT_SIZE = "UI_PARTICLES_POINT_SIZE";
	protected String UI_SILHOUETTE_ALPHA = "UI_SILHOUETTE_ALPHA";

	
	// TEMP REMOVE
	protected VLCJVideo videoVLC;

	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	protected void firstFrame () {
		// TEMP REMOVE
		videoVLC = new VLCJVideo(p);
		videoVLC.open(FileUtil.getPath(DemoAssets.movieFractalCubePath));
		videoVLC.play();
		videoVLC.setRepeat(true);

		
		// init depth cam
		DepthCamera.instance(DepthCameraType.Realsense);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		depthSilhouetteSmoothed = new DepthSilhouetteSmoothed(depthCamera, 6);
		depthSilhouetteSmoothed.buildUI(false);
		DebugView.setTexture("depthBuffer", depthSilhouetteSmoothed.depthBuffer());
		DebugView.setTexture("avgBuffer", depthSilhouetteSmoothed.avgBuffer());
		DebugView.setTexture("image", depthSilhouetteSmoothed.image());

		// build buffer to paste silhouette into that matches the aspect ratio of the app
		float silhouetteCropScaleDown = 0.25f;
		silhouetteCropped = PG.newPG(P.round(p.width * silhouetteCropScaleDown), P.round(p.height * silhouetteCropScaleDown), false, false);
		DebugView.setTexture("silhouetteCropped", silhouetteCropped);
		
		// build gradient
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		imageGradient.randomGradientTexture();
		
		// build particles launcher
		shapesLayer = PG.newPG(p.width, p.height);
//		gpuParticles = new ParticleLauncherGPU(256, "haxademic/shaders/point/particle-launcher-fizz-frag.glsl");
		PImage particle = DemoAssets.particle();
		gpuParticles = new ParticleLauncherGPU(512, "haxademic/shaders/point/particle-launcher-fizz-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-vert.glsl", particle);

		DebugView.setValue("gpuParticles.vertices()", gpuParticles.numParticles());
		DebugView.setTexture("gpuParticles.positionBuffer()", gpuParticles.positionBuffer());
		DebugView.setTexture("gpuParticles.colorBuffer()", gpuParticles.colorBuffer());

		// UI
		UI.addTitle("GPUParticlesLauncher");
		UI.addSlider(UI_PARTICLES_LAUNCH_ATTEMPTS, 1700, 1, 5000, 10, false);
		UI.addSlider(UI_PARTICLES_POINT_SIZE, 3, 0, 50, 0.1f, false);
		UI.addSlider(UI_SILHOUETTE_ALPHA, 0.1f, 0, 1, 0.01f, false);
	}
	
	protected void drawApp() {
		// set up context
		p.background(0);
		
		// TEMP REMOVE
		ImageUtil.drawImageCropFill(videoVLC, p.g, true);
		
		// copy silhouette to aspect-ratio-corrected copy
		// and prepare pixels data
		depthSilhouetteSmoothed.update();
		ImageUtil.cropFillCopyImage(depthSilhouetteSmoothed.image(), silhouetteCropped, true);
		silhouetteCropped.loadPixels();

		// launch! need to open & close the position buffer where we're writing new launch pixels
		int startLaunchTime = p.millis();
		gpuParticles.beginLaunch();

		// look for non-black pixels to launch from
		// since we're using a scaled-down map for efficiency, scale x/y positions back up
		PGraphics launchMap = silhouetteCropped;
		float scaleLaunchMapToScreen = MathUtil.scaleToTarget(launchMap.width, p.width);
		if(p.frameCount % FRAME_LAUNCH_INTERVAL == 0) {
			int numLaunched = 0;
			int launches = UI.valueInt(UI_PARTICLES_LAUNCH_ATTEMPTS);
			for (int i = 0; i < launches; i++) {
				int checkX = MathUtil.randRange(0, launchMap.width);
				int checkY = MathUtil.randRange(0, launchMap.height);
				int pixelColor = ImageUtil.getPixelColor(launchMap, checkX, checkY);
				float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
				if(redColor > 0.3f && numLaunched < launches) {
					gpuParticles.launch(shapesLayer, checkX * scaleLaunchMapToScreen, checkY * scaleLaunchMapToScreen);
					numLaunched++;
				}
			}
		}

		gpuParticles.endLaunch();
		DebugView.setValue("launchTime", p.millis() - startLaunchTime);
		
		// update particles buffers
		int startUpdateTime = p.millis();
		gpuParticles.updateSimulation();
		DebugView.setValue("updateTime", p.millis() - startUpdateTime);
		
		// update particles color map
		if(Mouse.xNorm < 0.5f) { 
			ImageUtil.copyImage(ImageGradient.SPARKS_FLAMES(), gpuParticles.colorBuffer());
//			ImageUtil.copyImage(ImageGradient.BLACK_HOLE(), gpuParticles.colorBuffer());
//			ImageUtil.copyImage(ImageCacher.get("../../pepsi-nitro-wall/_assets/cascade-gradient.png"), gpuParticles.colorBuffer());
		} else {
			gpuParticles.colorBuffer().beginDraw();
			gpuParticles.colorBuffer().background(255);
			gpuParticles.colorBuffer().endDraw();
		}

		// update/draw particles
		shapesLayer.beginDraw();
		shapesLayer.background(0,0);
		shapesLayer.blendMode(PBlendModes.ADD);
		gpuParticles.pointSize(UI.value(UI_PARTICLES_POINT_SIZE));
		gpuParticles.renderTo(shapesLayer, true);
		shapesLayer.endDraw();

		// draw to screen
		// silhouette
		p.blendMode(PBlendModes.BLEND);
		PG.setPImageAlpha(p, UI.value(UI_SILHOUETTE_ALPHA));
		p.image(silhouetteCropped, 0, 0, silhouetteCropped.width * scaleLaunchMapToScreen, silhouetteCropped.height * scaleLaunchMapToScreen);
		PG.resetPImageAlpha(p);
		// particles
		p.blendMode(PBlendModes.DIFFERENCE);
		p.image(shapesLayer, 0, 0);
		p.blendMode(PBlendModes.BLEND);
		
		// draw rgb camera
		PImage cameraSource = DepthCamera.instance().camera.getRgbImage();
		float camScale = 0.2f;
		float camW = cameraSource.width * camScale;
		float camH = cameraSource.height * camScale;
		p.image(cameraSource, p.width - camW - 20, 20, camW, camH);
	}
	
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') imageGradient.randomGradientTexture();
	}
	
}
