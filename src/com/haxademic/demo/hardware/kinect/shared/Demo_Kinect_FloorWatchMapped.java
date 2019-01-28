package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.kinect.KinectSize;
import com.haxademic.core.hardware.kinect.KinectWrapperV1;
import com.haxademic.core.hardware.kinect.KinectWrapperV2;

import processing.core.PGraphics;

public class Demo_Kinect_FloorWatchMapped
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectWrapperV2 kinect;
	
	protected PGraphics buffer1;
	
	protected PGraphicsKeystone keystone;
	protected boolean debug1 = false;
	protected boolean keystoneMode = true;
	
	public static String PIXEL_SIZE = "KINECT_OVERLAP_PIXEL_SIZE";
	public static String KINECT_TOP = "KINECT_OVERLAP_TOP";
	public static String KINECT_BOTTOM = "KINECT_OVERLAP_BOTTOM";
	public static String KINECT_NEAR = "KINECT_OVERLAP_NEAR";
	public static String KINECT_FAR = "KINECT_OVERLAP_FAR";

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 960 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 960 );
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty(AppSettings.SHOW_FPS_IN_TITLE, true );
	}

	public void setupFirstFrame() {
		// init the cameras
		kinect = new KinectWrapperV2(p, true, false, true);
		
		// and buffer for each kinect
		buffer1 = p.createGraphics(KinectWrapperV1.KWIDTH, KinectWrapperV1.KWIDTH, PRenderers.P3D);
		
		// add keystones for each buffer
		keystone = new PGraphicsKeystone( p, buffer1, 12, FileUtil.getFile("text/keystoning/keystone-kinect.txt") );
		
		// add prefs sliders
		p.prefsSliders.addSlider(PIXEL_SIZE, 	3,    1, 20, 0.1f, false);
		p.prefsSliders.addSlider(KINECT_TOP, 	220,  0, KinectWrapperV1.KHEIGHT, 1, false);
		p.prefsSliders.addSlider(KINECT_BOTTOM, 240,  0, KinectWrapperV1.KHEIGHT, 1, false);
		p.prefsSliders.addSlider(KINECT_NEAR, 	1000, 0, 3000, 1, false);
		p.prefsSliders.addSlider(KINECT_FAR, 	7000, 0, 10000, 4, false);
	}

	public void drawApp() {
		// prep drawing
		p.background(0);
		p.noStroke();
		p.fill(255);

		// update kinects
		kinect.update();
		
		// draw depth
		// draw filtered web cam
		DrawUtil.setDrawCorner(p);
		
		// draw 2 cameras' depth data
		drawKinectDepthPixels(kinect, buffer1, p.color(100), true);
		int kinectPixels = drawKinectDepthPixels(kinect, buffer1, p.color(255), false);
		p.debugView.setValue("kinectPixels", kinectPixels);
		
		keystone.update(p.g);
		
		// draw map zone
		p.fill(0, 0);
		p.stroke(0, 255, 0);
		p.strokeWeight(4);
		p.rect(p.width / 2 - 160, p.height / 2 - 160, 320, 320);
		
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') {
			debug1 = !debug1;
			keystone.setActive(debug1);
		}
	}
	
	protected float slider(String key) {
		return p.prefsSliders.value(key);
	}
	
	protected int drawKinectDepthPixels(KinectWrapperV2 kinect, PGraphics buffer, int pixelColor, boolean drawAllData) {
		// open context
		buffer.beginDraw();
		if(drawAllData == true) buffer.background(0, 0);
		buffer.noStroke();
		buffer.fill(pixelColor);

		// loop through kinect data within player's control range
		float pixelDepth;
		float avgX = 0;
		float avgY = 0;
		float numPoints = 0;
		
		float kinectDepthZone = slider(KINECT_FAR) - slider(KINECT_NEAR);
		float distancePixels = (float) KinectWrapperV1.KWIDTH / kinectDepthZone;		// map distance to width
		float pixelSkip = slider(PIXEL_SIZE);
		// float pixelHalf = pixelSkip / 2f;
		
		// TODO: Switch to ONLY loop through kinect points that we need
		for ( int x = 0; x < KinectSize.WIDTH; x += pixelSkip ) {
			for ( int y = 0; y < KinectWrapperV2.KHEIGHT; y += pixelSkip ) {
				pixelDepth = kinect.getMillimetersDepthForKinectPixel( x, y );
				if(pixelDepth != 0 && pixelDepth > slider(KINECT_NEAR) && pixelDepth < slider(KINECT_FAR)) {
					// draw depth points
					float userZ = P.map(pixelDepth, slider(KINECT_NEAR), slider(KINECT_FAR), 0, kinectDepthZone * distancePixels);
					if(drawAllData == true || (y > slider(KINECT_TOP) && y < slider(KINECT_BOTTOM))) {
						buffer.rect(x - 5, userZ - 5, 10, 10);
					}
					
					// calc data processing
					numPoints++;
					avgX += x;
					avgY += userZ;
				}
			}
		}
		
		// show CoM
		buffer.fill(pixelColor);
		if(drawAllData == false) buffer.ellipse(avgX / numPoints, avgY / numPoints, 20, 20);
		
		// close buffer
		buffer.endDraw();
		return (int) numPoints;
	}


}
