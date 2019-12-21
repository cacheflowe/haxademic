package com.haxademic.app.partycles;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.BufferActivityMonitor;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.KinectDepthSilhouetteSmoothed;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.system.SystemUtil;

import dmxP512.DmxP512;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.serial.Serial;

public class Partycles
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// buffers
	protected PGraphics mainBuffer;
	protected PGraphics cameraBuffer;
	protected PGraphicsKeystone keystone;
	protected boolean testPattern = false;

	// kinect 
	protected BufferMotionDetectionMap motionDetectionMap;
	protected KinectDepthSilhouetteSmoothed kinectSilhouetteSmoothed;
	protected PGraphics motionBuffer;	// copy kinect silhouette to match the size of the RGB camera
	protected BufferActivityMonitor activityMonitor;

	// particles
	protected PGraphics shapesLayer;
	protected ArrayList<ShapeParticle> shapes;
	protected ImageGradient imageGradient;
	protected int FRAME_LAUNCH_INTERVAL = 2;
	protected int MAX_LAUNCHED_PER_FRAME = 5;
	protected PImage[] particleImages;
	protected int[] particleColors;

	// sponsor image
	protected PImage sponsorImg;
	
	// auto screenshots
	protected int lastScreenshotTime = 0;
	
	// dmx
	protected boolean dmxMode = true;
	protected DmxP512 dmx;
	protected String DMXPRO_PORT = "DMXPRO_PORT";
	protected String DMXPRO_BAUDRATE = "DMXPRO_BAUDRATE";
	protected String DMXPRO_UNIVERSE_SIZE = "DMXPRO_UNIVERSE_SIZE";
	protected int numLights = 7;
	protected int numColors = 3;
	protected int numChannels = numLights * numColors;
	protected boolean audioActive = false;
	protected EasingColor[] colorsDMX;
	
	// TODO:
	// - refine particles
	// - Pull particle textures from a directory to be skinnable


	protected void overridePropsFile() {
		if(P.platform != P.MACOSX) {
			p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
			p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		} else {
			p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
			p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		}
		if(dmxMode) {
			if(P.platform == P.MACOSX) {
				// mac
				p.appConfig.setProperty(DMXPRO_PORT, "/dev/tty.usbserial-EN158815");
				p.appConfig.setProperty(DMXPRO_BAUDRATE, 115000);
			} else {
				// win
				p.appConfig.setProperty(DMXPRO_PORT, "COM3");
				p.appConfig.setProperty(DMXPRO_BAUDRATE, 9600);
			}
		}
		p.appConfig.setProperty( AppSettings.FULLSCREEN, true );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
	}

	public void setupFirstFrame() {
		AudioIn.instance();
		// main buffer
		mainBuffer = p.createGraphics(1920, 1080, PRenderers.P2D);
		cameraBuffer = p.createGraphics(1920, 1080, PRenderers.P2D);
		shapesLayer = P.p.createGraphics(1920, 1080, PRenderers.P3D);
		keystone = new PGraphicsKeystone(p, mainBuffer, 10, FileUtil.getFile("text/keystoning/partycles.txt"));
		
		// camera/kinect
		kinectSilhouetteSmoothed = new KinectDepthSilhouetteSmoothed(p.depthCamera, 6);
		KinectDepthSilhouetteSmoothed.KINECT_FAR = 2000;
		activityMonitor = new BufferActivityMonitor(32, 16, 10);

		// init particles
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		imageGradient.randomGradientTexture();
		
		shapes = new ArrayList<ShapeParticle>();
		
		particleImages = new PImage[] {
			DemoAssets.particle(),
			P.getImage("images/partycles/futuristic-robot-white.png"),
		};
		
		particleColors = new int[] {
				0xfff0ff1c,
				0xffff278a,
				0xff7cffd5,
				0xff74cee5,
				0xffffffff,
				0xffffffff,
		};
		
		// load sponsor image if it exists
		String sponsorImgPath = FileUtil.getFile("images/partycles/sponsor.png");
		if(FileUtil.fileExists(sponsorImgPath)) {
			sponsorImg = p.loadImage(sponsorImgPath);
		}
		
		// dmx 
		if(dmxMode) {
			// init dmx hardware connection
			Serial.list();
			dmx = new DmxP512(P.p, p.appConfig.getInt(DMXPRO_UNIVERSE_SIZE, 256), true);
			dmx.setupDmxPro(p.appConfig.getString(DMXPRO_PORT, "COM1"), p.appConfig.getInt(DMXPRO_BAUDRATE, 115000));
			
			// init easing colors
			colorsDMX = new EasingColor[numLights];
			for (int i = 0; i < numLights; i++) {
				colorsDMX[i] = new EasingColor(0x000000, 0.15f);
			}
		}
		
		// init help menu
		DebugView.setHelpLine("Key Commands:", "");
		DebugView.setHelpLine("[R]", "Reset keystone");
		DebugView.setHelpLine("[D]", "Keystone test pattern");
	}

	public void drawApp() {
		// set up context
		p.background(0);
		if(dmxMode) updateDMX();
		updateKinect();
		drawParticles();
		drawMainBuffer();
		// draw to screen
		if(testPattern == true) keystone.drawTestPattern();
		keystone.update(p.g, true);
		// occasional screenshot
		if(activityMonitor.activityAmp() > 0.1f && p.millis() > lastScreenshotTime + (10 * 1000)) {
			mainBuffer.save(FileUtil.getHaxademicOutputPath() + "_screenshots/" + SystemUtil.getTimestamp() + ".jpg");
			lastScreenshotTime = p.millis();
		}
	}
	
	protected void updateKinect() {
		KinectDepthSilhouetteSmoothed.KINECT_FAR = P.round(4000 * Mouse.xNorm);
		// update silhouette
		kinectSilhouetteSmoothed.update();

		PImage depthImage = depthCamera.getDepthImage();
		PImage cameraImage = depthCamera.getRgbImage();
		
		// copy camera frame to buffer
		ImageUtil.cropFillCopyImage(cameraImage, cameraBuffer, true);
		
		// lazy-init motion detection to pass Kinect into
		if(motionDetectionMap == null && depthImage != null) {
			motionBuffer = P.p.createGraphics(cameraImage.width, cameraImage.height, P.P2D);
			motionDetectionMap = new BufferMotionDetectionMap(motionBuffer, 0.05f);
		}
		
		if(motionDetectionMap != null) {
			ImageUtil.cropFillCopyImage(kinectSilhouetteSmoothed.image(), motionBuffer, true);
			BlurHFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.width);
			BlurHFilter.instance(P.p).applyTo(motionBuffer);
			BlurVFilter.instance(P.p).setBlurByPercent(0.5f, motionBuffer.height);
			BlurVFilter.instance(P.p).applyTo(motionBuffer);
			
			motionDetectionMap.setBlendLerp(0.25f);
			motionDetectionMap.setDiffThresh(0.025f);
			motionDetectionMap.setFalloffBW(0.25f);
			motionDetectionMap.setThresholdCutoff(0.5f);
			motionDetectionMap.setBlur(1f);
			motionDetectionMap.updateSource(motionBuffer);
			
			DebugView.setTexture("kinectSilhouetteSmoothed", kinectSilhouetteSmoothed.image());
			DebugView.setTexture("motionDetectionMap.backplate", motionDetectionMap.backplate());
			DebugView.setTexture("motionDetectionMap.differenceBuffer", motionDetectionMap.differenceBuffer());
			DebugView.setTexture("motionDetectionMap.bwBuffer", motionDetectionMap.bwBuffer());
		}
		
		// update activity monitor
		activityMonitor.update(kinectSilhouetteSmoothed.image());
		DebugView.setValue("ACTIVITY", activityMonitor.activityAmp());
		// DebugView.setTexture(activityMonitor.differenceBuffer());
	}
	
	protected void drawMainBuffer() {
		// setup context
		mainBuffer.beginDraw();
		mainBuffer.noStroke();
		mainBuffer.background(0);
		mainBuffer.pushMatrix();
		PG.setDrawCenter(mainBuffer);
		mainBuffer.translate(mainBuffer.width/2, mainBuffer.height/2);

		// draw camera - compensate for depth image size (scale) and alignment (x & y)
		float cameraScale = 1.18f; // Mouse.xNorm * 3;
		DebugView.setValue("cameraScale", cameraScale);
		mainBuffer.image(cameraBuffer, cameraBuffer.width * -0.025f, cameraBuffer.height * -0.05f, cameraBuffer.width * cameraScale, cameraBuffer.height * cameraScale);
		
		// draw debug motion buffer
		if(DebugView.active()) {
			PG.setPImageAlpha(mainBuffer, 0.5f);
			if(motionBuffer != null) mainBuffer.image(motionBuffer, 0, 0);
			PG.setPImageAlpha(mainBuffer, 1f);
		}

		// draw shapes & sponsor
		mainBuffer.image(shapesLayer, 0, 0);
		if(sponsorImg != null) mainBuffer.image(sponsorImg, mainBuffer.width - sponsorImg.width, mainBuffer.height - sponsorImg.height);
		
		// close context
		mainBuffer.popMatrix();
		mainBuffer.endDraw();
	}
	
	protected void drawParticles() {
		// load motion detection map pixels
		motionDetectionMap.loadPixels();
		
		// draw shapes - find launch points
		shapesLayer.beginDraw();
		shapesLayer.clear();
		shapesLayer.fill(255);
		shapesLayer.noStroke();

		// draw shapes - find launch points
		p.fill(255, 0, 0);
		p.noStroke();
		
		int FRAME_LAUNCH_INTERVAL = 1;
		int MAX_LAUNCHED_PER_FRAME = 4;
		int LAUNCH_ATTEMPTS = 500;
		if(p.frameCount % FRAME_LAUNCH_INTERVAL == 0) {
			int numLaunched = 0;
			for (int i = 0; i < LAUNCH_ATTEMPTS; i++) {
				if(numLaunched < MAX_LAUNCHED_PER_FRAME) {
					int checkX = MathUtil.randRange(0, motionBuffer.width);
					int checkY = MathUtil.randRange(0, motionBuffer.height);
					if(motionDetectionMap.pixelActive(checkX, checkY)) {
						// shapesLayer.rect(checkX, checkY, 4, 4); // 4 because of 0.25 motion detection scale
						launchShape(checkX, checkY);
						numLaunched++;
					}
				}
			}
		}

		// update/draw particles
		//			p.blendMode(PBlendModes.ADD);
		for (int i = 0; i < shapes.size(); i++) {
			shapes.get(i).update(shapesLayer);
		}
		shapesLayer.endDraw();
		DebugView.setValue("shapes.size()", shapes.size());
	}

	protected void launchShape(float x, float y) {
		// look for an available shape
		for (int i = 0; i < shapes.size(); i++) {
			if(shapes.get(i).available()) {
				shapes.get(i).launch(x, y);
				return;
			}
		}
		// didn't find one
		if(shapes.size() < 10000) {
			ShapeParticle newShape = new ShapeParticle();
			newShape.launch(x, y);
			shapes.add(newShape);
		}
	}

	///////////////////////////////////////
	// KEYBOARD INPUT
	///////////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'd') testPattern = !testPattern;
		if(p.key == 'r') keystone.resetCorners();
//		if(p.key == ' ') imageGradient.randomGradientTexture();
	}

	///////////////////////////////////////
	// PARTICLE CLASS
	///////////////////////////////////////
	
	public class ShapeParticle {

		protected PVector pos = new PVector(0, -100, 0);
		protected PVector speed = new PVector(0, 0, 0);
		protected PVector gravity = new PVector(0, 0, 0);
		protected float vertices = 3f;
		protected float size = 30f;
		protected float shrink = 1;
		protected float rotation = 30f;
		protected LinearFloat sizeProgress = new LinearFloat(0, 0.1f);
		protected int color;
		protected PImage image;
		protected int audioIndex;
		
		public ShapeParticle() {
			audioIndex = MathUtil.randRange(0, 511);
		}

		public void update(PGraphics pg) {
			if(available()) return;
			
			// update position
			gravity.x *= 0.97f;
			speed.add(gravity);
			pos.add(speed);
			rotation += gravity.z;
			
			// update size
			sizeProgress.update();
			float audioAmp = (1f + 1f * AudioIn.audioFreq(audioIndex));
			if(sizeProgress.value() == 1) shrink -= 0.01f;
			float curSize = (sizeProgress.value() == 1) ?
					size * shrink * audioAmp:
					size * Penner.easeOutQuad(sizeProgress.value()) * audioAmp;
//			if(sizeProgress.value() == 1) sizeProgress.setTarget(0);
			
			// draw image or polygon
			if(image != null) {
				// draw image
				pg.pushMatrix();
				pg.translate(pos.x, pos.y);
				pg.rotate(rotation);
				pg.tint(color);
				pg.image(image, 0, 0, curSize * 2f, curSize * 2f);
				pg.tint(255);
				pg.popMatrix();
			} else {
				// draw shape
				float segmentRads = P.TWO_PI / vertices;
				pg.fill(color); // , 150);
//				pg.stroke(255);
				pg.noStroke();
				pg.pushMatrix();
				pg.translate(pos.x, pos.y);
				pg.rotate(rotation);
				pg.beginShape(P.POLYGON);
				for(float i = 0; i <= vertices; i++) {
					pg.vertex(P.cos(segmentRads * i) * curSize, P.sin(segmentRads * i) * curSize);
				}
				pg.endShape();
				pg.popMatrix();
				// pg.rect(pos.x, pos.y, 2, 2);
			}
			pg.tint(255);
		}

		public void launch(float x, float y) {
			if(particleImages != null) {
				image = particleImages[MathUtil.randRange(0, particleImages.length - 1)];
				if(MathUtil.randBooleanWeighted(0.2f)) {
					image = null;
				}
			}

			vertices = MathUtil.randRange(3, 4);
			size = MathUtil.randRangeDecimal(20, 50);
			sizeProgress.setCurrent(0);
			sizeProgress.setTarget(1);
			shrink = 1;

			pos.set(x, y, 0);
			speed.set(0, MathUtil.randRangeDecimal(-1f, -4f), 0);
			rotation = P.p.random(P.TWO_PI);
			gravity.set(MathUtil.randRangeDecimal(-0.2f, 0.2f), MathUtil.randRangeDecimal(0.2f, 0.9f), MathUtil.randRangeDecimal(-0.02f, 0.02f)); // z is rotation!

//			color = imageGradient.getColorAtProgress(P.p.random(1f));
			color = particleColors[MathUtil.randRange(0, particleColors.length - 1)];
		}

		public boolean available() {
			return pos.y < -100 || pos.y > shapesLayer.height + 100;
		}
	}

	
	///////////////////////////////
	// DMX
	///////////////////////////////

	protected void updateDMX() {
		// easing color zone
		for (int i = 0; i < numLights; i++) {
			colorsDMX[i].update();
		}
		
		// step through lights every x frames
		int frameInterval = 20; // P.round(Mouse.xNorm * 10 + 1);
		if(p.frameCount % frameInterval == 0) {
			int frameDivided = P.floor(p.frameCount / frameInterval);
			int curLightIndex = frameDivided % numLights;
			int nextColorIndex = frameDivided % (particleColors.length - 3);
			int randColor = particleColors[nextColorIndex];
//			colorsDMX[curLightIndex].setCurrentInt(randColor);
			colorsDMX[curLightIndex].setTargetInt(randColor);
		}
		
		// send light rgb colors
		float activityMult = activityMonitor.activityAmp() * 10f;
		for (int i = 0; i < numLights; i++) {
			// alternate motion vs. audio amp
			float lightAmp = (i % 2 == 0) ?
					AudioIn.audioFreq(10 + i * 20) * 10f * activityMult : 
					activityMult; 
			// P.out(lightAmp);
			
			// set light colors
			int lightColor = colorsDMX[i].colorInt(lightAmp);
			int channelR = i * numColors + 1;
			int channelG = i * numColors + 2;
			int channelB = i * numColors + 3;
			dmx.set(channelR, P.constrain(round(EasingColor.redFromColorInt(lightColor)), 0, 255));
			dmx.set(channelG, P.constrain(round(EasingColor.greenFromColorInt(lightColor)), 0, 255));
			dmx.set(channelB, P.constrain(round(EasingColor.blueFromColorInt(lightColor)), 0, 255));
		}
	}
	
	
	
	
	
	
	///////////////////////////////
	// old webcam methods
	///////////////////////////////
	
	/*
	// old webcam stuff
	protected PGraphics flippedCamera;
	protected PGraphics prevFrame;
	protected PGraphics curFrame;
	protected PGraphics differenceBuffer;
	protected PShader differenceShader;
	

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) {
			int cameraW = 640;	// frame.width (these are jacked up on OS X)
			int cameraH = 480;	// frame.height
			flippedCamera = p.createGraphics(cameraW, cameraH, PRenderers.P2D);

			// frame buffers
			prevFrame = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			curFrame = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			differenceBuffer = P.p.createGraphics(cameraW, cameraH, PRenderers.P2D);
			DebugView.setTexture(differenceBuffer);

			// frame diff buffer/shader
			differenceShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/texture-difference-threshold.glsl"));

			// shapes layer
			shapesLayer.smooth(8);
		}
		// copy flipped
		flippedCamera.copy(frame, 0, 0, frame.width, frame.height, flippedCamera.width, 0, -flippedCamera.width, flippedCamera.height);

		// copy previous frame, and current frame to buffer
		ImageUtil.copyImage(curFrame, prevFrame);
		ImageUtil.copyImage(flippedCamera, curFrame);
		DebugView.setTexture(curFrame);

		// set difference shader textures
		differenceShader.set("tex1", curFrame);
		differenceShader.set("tex2", prevFrame);
		differenceShader.set("falloffBW", 0.25f);
		differenceShader.set("diffThresh", 0.15f);
		differenceBuffer.filter(differenceShader);
	}
	*/

}
