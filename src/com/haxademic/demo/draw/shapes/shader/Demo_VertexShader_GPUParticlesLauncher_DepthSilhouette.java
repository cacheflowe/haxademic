package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesLauncher_DepthSilhouette 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected PGraphics prevFrame;
	protected PGraphics curFrame;
	protected PGraphics differenceBuffer;
	protected PGraphics shapesLayer;
	protected PShader differenceShader;
	protected ImageGradient imageGradient;
	protected int FRAME_LAUNCH_INTERVAL = 1;
	protected int MAX_LAUNCHED_PER_FRAME = 500;
	
	protected ParticleLauncherGPU gpuParticles;
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 960 );
	}
		
	protected void firstFrame () {
		// capture webcam frames
		WebCam.instance().setDelegate(this).set720p();
		
		// build gradient
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		imageGradient.randomGradientTexture();
		
		// build particles launcher
		gpuParticles = new ParticleLauncherGPU(128);
		DebugView.setValue("totalVertices", gpuParticles.vertices());
	}
	
	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) {
			int cameraW = frame.width;	// frame.width (these are jacked up on OS X)
			int cameraH = frame.height;	// frame.height
			flippedCamera = p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			
			// frame buffers
			prevFrame = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			curFrame = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			differenceBuffer = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			DebugView.setTexture("differenceBuffer", differenceBuffer);
			
			// frame diff buffer/shader
			differenceShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-difference-threshold.glsl"));

			// shapes layer
			shapesLayer = PG.newPG(cameraW, cameraH);
			DebugView.setTexture("shapesLayer", shapesLayer);
//			shapesLayer.smooth(8);
		}
		// copy flipped
		flippedCamera.copy(frame, 0, 0, frame.width, frame.height, flippedCamera.width, 0, -flippedCamera.width, flippedCamera.height);
		
		// copy previous frame, and current frame to buffer
		ImageUtil.copyImage(curFrame, prevFrame);
		ImageUtil.copyImage(flippedCamera, curFrame);
		DebugView.setTexture("webcam", curFrame);

		// set difference shader textures
		differenceShader.set("tex1", curFrame);
		differenceShader.set("tex2", prevFrame);
		differenceShader.set("falloffBW", 0.5f);
		differenceShader.set("diffThresh", 0.15f);
		differenceBuffer.filter(differenceShader);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		
		if(differenceBuffer != null) {
			// draw to screen
			PG.setDrawCenter(p);
			PG.setPImageAlpha(p, 1f);
			p.blendMode(PBlendModes.BLEND);
			p.image(curFrame, p.width/2, p.height/2);
			
			// draw shapes - find launch points
			differenceBuffer.loadPixels();

			
			// launch! need to open & close the position buffer where we're writing new launch pixels
			int startLaunchTime = p.millis();
			int launchesPerFrame = 1000;
			gpuParticles.beginLaunch();

			
			if(p.frameCount % FRAME_LAUNCH_INTERVAL == 0) {
				int numLaunched = 0;
				for (int i = 0; i < launchesPerFrame; i++) {
					int checkX = MathUtil.randRange(0, differenceBuffer.width);
					int checkY = MathUtil.randRange(0, differenceBuffer.height);
					int pixelColor = ImageUtil.getPixelColor(differenceBuffer, checkX, checkY);
					float redColor = (float) ColorUtil.redFromColorInt(pixelColor) / 255f;
					if(redColor > 0.3f && numLaunched < MAX_LAUNCHED_PER_FRAME) {
						gpuParticles.launch(shapesLayer, checkX, checkY);
						numLaunched++;
					}
				}
			}

			gpuParticles.endLaunch();
			DebugView.setValue("launchTime", p.millis() - startLaunchTime);
			
			// update particles buffers
			int startUpdateTime = p.millis();
			gpuParticles.update();
			DebugView.setValue("updateTime", p.millis() - startUpdateTime);
			
			// update/draw particles
			shapesLayer.beginDraw();
			shapesLayer.background(0,0);
			shapesLayer.blendMode(PBlendModes.ADD);
			gpuParticles.renderTo(shapesLayer, true);
			shapesLayer.endDraw();

			p.image(shapesLayer, p.width/2, p.height/2);
		}
	}
	
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') imageGradient.randomGradientTexture();
	}
	
}
