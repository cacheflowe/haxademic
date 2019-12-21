package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.KinectWrapperV2;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_Kinect_FloorWatchMapped
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectWrapperV2 kinect;
	
	protected PGraphics bufferDepthSlice;
	protected PGraphics bufferNormalizedDepth;
	
	protected PGraphicsKeystone keystone;
	protected boolean keystoneMode = true;
	protected boolean keystoneUI = true;
	
	public static String PIXEL_SIZE = "KINECT_OVERLAP_PIXEL_SIZE";
	public static String KINECT_TOP = "KINECT_OVERLAP_TOP";
	public static String KINECT_BOTTOM = "KINECT_OVERLAP_BOTTOM";
	public static String KINECT_NEAR = "KINECT_OVERLAP_NEAR";
	public static String KINECT_FAR = "KINECT_OVERLAP_FAR";

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 960 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 960 );
		p.appConfig.setProperty(AppSettings.SHOW_UI, true );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty(AppSettings.SHOW_FPS_IN_TITLE, true );
	}

	public void setupFirstFrame() {
		// init the cameras
		kinect = new KinectWrapperV2(p, false, true);
		
		// and buffer for each kinect
		bufferDepthSlice = p.createGraphics(KinectWrapperV2.KWIDTH, KinectWrapperV2.KWIDTH, PRenderers.P3D);
		bufferNormalizedDepth = p.createGraphics(KinectWrapperV2.KWIDTH, KinectWrapperV2.KWIDTH, PRenderers.P3D);
		
		// add keystones for each buffer
		keystone = new PGraphicsKeystone( p, bufferDepthSlice, 12, FileUtil.getFile("text/keystoning/keystone-kinect.txt") );
		
		// add prefs sliders
		UI.addSlider(PIXEL_SIZE, 	3,    1, 20, 0.1f, false);
		UI.addSlider(KINECT_TOP, 	220,  0, KinectWrapperV2.KHEIGHT, 1, false);
		UI.addSlider(KINECT_BOTTOM, 240,  0, KinectWrapperV2.KHEIGHT, 1, false);
		UI.addSlider(KINECT_NEAR, 	1000, 0, 3000, 1, false);
		UI.addSlider(KINECT_FAR, 	7000, 0, 10000, 4, false);
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
		PG.setDrawCorner(p);
		
		// draw kinect depth data in 2 passes for debugging
		if(keystoneUI) drawKinectDepthPixels(kinect, bufferDepthSlice, p.color(100), true, true);
		int kinectPixels = drawKinectDepthPixels(kinect, bufferDepthSlice, p.color(255), false, !keystoneUI);
		DebugView.setValue("kinectPixels", kinectPixels);
		
		// calc destination texture centered screen coordinates
		float textureX = p.width / 2 - bufferNormalizedDepth.width / 2;
		float textureY = p.height / 2 - bufferNormalizedDepth.height / 2;
		
		// draw depth data into normalized texture
		bufferNormalizedDepth.beginDraw();
		bufferNormalizedDepth.background(0);
		bufferNormalizedDepth.translate(-textureX, -textureY);
		keystone.update(bufferNormalizedDepth);
		bufferNormalizedDepth.endDraw();
		DebugView.setTexture("bufferNormalizedDepth", bufferNormalizedDepth);
		
		
		// draw keystone UI to screen
		if(keystoneUI) {
			keystone.update(p.g);
		} else {
			// draw normalized texture to center of screen when UI is disabled
			p.image(bufferNormalizedDepth, textureX, textureY);
		}
		
		// draw map zone
		p.fill(0, 0);
		p.stroke(0, 255, 0);
		p.strokeWeight(4);
		p.rect(p.width / 2 - bufferNormalizedDepth.width / 2, p.height / 2 - bufferNormalizedDepth.width / 2, bufferNormalizedDepth.width, bufferNormalizedDepth.height);
		
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') {
			keystoneUI = !keystoneUI;
			keystone.setActive(keystoneUI);
		}
	}
	
	protected float slider(String key) {
		return UI.value(key);
	}
	
	protected int drawKinectDepthPixels(KinectWrapperV2 kinect, PGraphics buffer, int pixelColor, boolean drawAllData, boolean clearBg) {
		// open context
		buffer.beginDraw();
		if(clearBg == true) buffer.background(0);
		buffer.noStroke();
		buffer.fill(pixelColor);

		// loop through kinect data within player's control range
		float pixelDepth;
		float avgX = 0;
		float avgY = 0;
		float numPoints = 0;
		
		float kinectDepthZone = slider(KINECT_FAR) - slider(KINECT_NEAR);
		float distancePixels = (float) KinectWrapperV2.KWIDTH / kinectDepthZone;		// map distance to width
		float pixelSkip = slider(PIXEL_SIZE);
		// float pixelHalf = pixelSkip / 2f;
		
		// TODO: Switch to ONLY loop through kinect points that we need
		for ( int x = 0; x < DepthCameraSize.WIDTH; x += pixelSkip ) {
			for ( int y = 0; y < KinectWrapperV2.KHEIGHT; y += pixelSkip ) {
				pixelDepth = kinect.getDepthAt( x, y );
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
		DebugView.setValue("avgX/avgY", avgX + ", " + avgY);
		
		// close buffer
		buffer.endDraw();
		return (int) numPoints;
	}


}
