package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingBoolean.IEasingBooleanCallback;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PImage;

public class Demo_Kinect_RunDetector
extends PAppletHax
implements IEasingBooleanCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String kinectLeft = "kinectLeft";
	protected String kinectRight = "kinectRight";
	protected String kinectTop = "kinectTop";
	protected String kinectBottom = "kinectBottom";
	protected String kinectNear = "kinectNear";
	protected String kinectFar = "kinectFar";
	protected String pixelSkip = "pixelSkip";
	protected String depthDivider = "depthDivider";
	protected String pixelDrawSize = "pixelDrawSize";

	protected int[] curDepths;
	protected int[] lastDepths;
	protected int changeLevel = 0;
	protected int changeLevelFalloff = 100;
	protected EasingBoolean isRecording;
	
	protected int recordFrames = 120;
	protected int recordFrame = 999;
	protected ImageSequenceRecorder recorder;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.REALSENSE_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.DEPTH_CAM_RGB_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.SHOW_UI, true);
	}
	
	public void setupFirstFrame() {
		p.ui.addSlider(kinectLeft, 50, 0, DepthCameraSize.WIDTH, 1, false);
		p.ui.addSlider(kinectRight, 420, 0, DepthCameraSize.WIDTH, 1, false);
		p.ui.addSlider(kinectTop, 140, 0, DepthCameraSize.HEIGHT, 1, false);
		p.ui.addSlider(kinectBottom, 290, 0,DepthCameraSize.HEIGHT, 1, false);
		p.ui.addSlider(kinectNear, 500, 0, 12000, 1, false);
		p.ui.addSlider(kinectFar, 1650, 300, 12000, 1, false);
		p.ui.addSlider(pixelSkip, 6, 1, 10, 1, false);
		p.ui.addSlider(depthDivider, 50, 1, 100, 0.1f, false);
		p.ui.addSlider(pixelDrawSize, 0.8f, 0, 1, 0.01f, false);
		
		isRecording = new EasingBoolean(false, 300, this);
		recorder = new ImageSequenceRecorder(p.depthCamera.getRgbImage().width, p.depthCamera.getRgbImage().height, recordFrames);
	}

	public void drawApp() {
		// setup context
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		// set depth pixel color
		p.fill(255f);
		p.noStroke();
		
		// draw depth image
		PImage depthImg = p.depthCamera.getDepthImage();
		int depthW = 640;
		float depthHScale = MathUtil.scaleToTarget(depthImg.width, depthW);
		p.image(p.depthCamera.getRgbImage(), 0, 0, depthW, depthImg.height * depthHScale);
		PG.setPImageAlpha(p.g, 0.5f);
		p.image(depthImg, 0, 0, depthW, depthImg.height * depthHScale);
		PG.setPImageAlpha(p.g, 1f);
		
		// draw kinect depth
		int pixelSkipp = p.ui.valueInt(pixelSkip);
		float kNear = p.ui.valueInt(kinectNear);
		float kFar = p.ui.valueInt(kinectFar);
		int kLeft = p.ui.valueInt(kinectLeft);
		float kRight = p.ui.valueInt(kinectRight);
		int kTop= p.ui.valueInt(kinectTop);
		float kBottom = p.ui.valueInt(kinectBottom);
//		float depthDiv = p.ui.valueInt(depthDivider);
		
		// lazy-init arrays
		int numPixelsProcessed = 0;
		if(curDepths == null) {
			// figure out how many depth values we're working with
			for ( int x = kLeft; x < kRight; x += pixelSkipp ) {
				for ( int y = kTop; y < kBottom; y += pixelSkipp ) {
					numPixelsProcessed++;
				}
			}
			// init arrays to zeroes
			curDepths = new int[numPixelsProcessed];
			lastDepths = new int[numPixelsProcessed];
			for (int i = 0; i < curDepths.length; i++) curDepths[i] = 0;
			for (int i = 0; i < lastDepths.length; i++) lastDepths[i] = 0;
		}
		
		// swap analysis arrays for cur/last
		int[] swapArr = curDepths;
		curDepths = lastDepths;
		lastDepths = swapArr;

		// gather depth data
		int numPixelsActive = 0;
		numPixelsProcessed = 0;
		float pixelsize = (float) pixelSkipp * p.ui.value(pixelDrawSize);
		for ( int x = kLeft; x < kRight; x += pixelSkipp ) {
			for ( int y = kTop; y < kBottom; y += pixelSkipp ) {
				// get depth val
				int pixelDepth = p.depthCamera.getDepthAt( x, y );
				
				// draw depths to screen
				if( pixelDepth != 0 && pixelDepth > kNear && pixelDepth < kFar ) {
					// put depth into analysis array
					curDepths[numPixelsProcessed] = pixelDepth;

					p.pushMatrix();
//					p.translate(0, 0, -pixelDepth/depthDiv);
//					p.fill(P.map(pixelDepth, kNear, kFar, 255, 0));
					p.rect(x, y, pixelsize, pixelsize);
					p.popMatrix();
					numPixelsActive++;
				} else {
					curDepths[numPixelsProcessed] = 0;
				}
				
				// increment for analysis arrays
				numPixelsProcessed++;
			}
		}
		
		// analyze difference between arrays
		int arrayDiff = 0;
		for (int i = 0; i < curDepths.length; i++) {
			arrayDiff += P.abs(curDepths[i] - lastDepths[i]);
		}
		
		// immediate rise, slow falloff
		changeLevelFalloff = 1000;
		if(arrayDiff > changeLevel) {
			if(arrayDiff > 150000) arrayDiff = 150000; // first diff value is bogus, so clamp it
			changeLevel = arrayDiff;
		} else {
			changeLevel -= changeLevelFalloff;
			if(changeLevel < 0) changeLevel = 0;
		}
		
		p.debugView.setValue("arrayDiff", arrayDiff);
		p.debugView.setValue("numPixelsActive", numPixelsActive);
		p.debugView.setValue("changeLevel", changeLevel);
		
		// draw activity value to screen
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 14);
		FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		p.text("changeLevel: " + changeLevel, depthW + 20, 420);
		p.text("arrayDiff: " + arrayDiff, depthW + 20, 444);

		// set easing boolean
		int minChangeThresh = 100000;
		isRecording.target(changeLevel > minChangeThresh);
		isRecording.update();
		
		// save to recorder
		if(isRecording.value() == true && recordFrame < recordFrames) {
			recorder.addFrame(p.depthCamera.getRgbImage());
			recordFrame++;
		}
		
		// draw boolean progress
		p.rect(depthW + 20, 400, 200, 10);
		p.fill(0, 255, 0);
		p.rect(depthW + 20, 400, 200 * isRecording.progress(), 10);
		p.fill(255);

		// debug view
		p.debugView.setTexture("depthCamera.getDepthImage", p.depthCamera.getDepthImage());
		p.debugView.setTexture("depthCamera.getRgbImage", p.depthCamera.getRgbImage());
		p.debugView.setValue("numPixelsProcessed", numPixelsProcessed);

		// draw recorded frames
		p.text("recordFrame: " + recordFrame, 1020, 400);
		int speedDivisor = 3;
		PG.setDrawCorner(p);
		recorder.drawDebug(p.g);
		PImage lilImg = recorder.images()[(p.frameCount/speedDivisor) % recorder.images().length];
		depthHScale = MathUtil.scaleToTarget(lilImg.width, depthW);
		p.image(lilImg, depthW, 0, depthW, lilImg.height * depthHScale);
	}
	
	public void keyPressed() {
		super.keyPressed();	
		if( p.key == 'd' ) {}
	}

	public void booleanSwitched(EasingBoolean booleanSwitch, boolean value) {
		if(value == true) {
			recordFrame = 0;
		}
	}
	

}
