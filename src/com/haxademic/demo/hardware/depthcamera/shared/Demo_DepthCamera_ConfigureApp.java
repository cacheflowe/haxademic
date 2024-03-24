package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.ui.UI;

public class Demo_DepthCamera_ConfigureApp
extends PAppletHax {
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
	protected String drawGradient = "drawGradient";

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1680 );
		Config.setProperty(AppSettings.HEIGHT, 840 );
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.SHOW_FPS_IN_TITLE, true);
		Config.setProperty(AppSettings.DEPTH_CAM_RGB_ACTIVE, false);
	}
	
	protected void firstFrame() {
		// init camera
		RealSenseWrapper.setMidStreamFast();
		DepthCamera.instance(DepthCameraType.Realsense);
		DepthCamera.instance().camera.setMirror(false);

		// add UI controls
		UI.addTitle("Depth Data Settings");
		UI.addSlider(kinectLeft, 0, 0, DepthCameraSize.WIDTH, 1, false);
		UI.addSlider(kinectRight, DepthCameraSize.WIDTH, 0, DepthCameraSize.WIDTH, 1, false);
		UI.addSlider(kinectTop, 0, 0, DepthCameraSize.HEIGHT, 1, false);
		UI.addSlider(kinectBottom, DepthCameraSize.HEIGHT, 0, DepthCameraSize.HEIGHT, 1, false);
		UI.addSlider(kinectNear, 300, 300, 12000, 10, false);
		UI.addSlider(kinectFar, 7000, 300, 12000, 10, false);
		UI.addSlider(pixelSkip, 5, 1, 30, 1, false);
		UI.addSlider(depthDivider, 50, 1, 100, 0.1f, false);
		UI.addSlider(pixelDrawSize, 0.5f, 0, 1, 0.01f, false);
		UI.addToggle(drawGradient, false, false);
	}

	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
//		DebugView.setTexture("rgb", DepthCamera.instance().camera.getRgbImage());
		background(0);
		PG.setDrawCorner(p);

		p.pushMatrix();
		
		// move kinect depth pixels over
		p.translate(290, 20);
		
		// draw frame
		p.noFill();
		p.stroke(0, 255, 0);
		p.strokeWeight(0.5f);
		p.rect(0, 0, DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);

		// set depth pixel color
		p.fill(255f);
		p.noStroke();
				
		// draw kinect depth
		int pixelSkipp = UI.valueInt(pixelSkip);
		float kNear = UI.valueInt(kinectNear);
		float kFar = UI.valueInt(kinectFar);
		int kLeft = UI.valueInt(kinectLeft);
		float kRight = UI.valueInt(kinectRight);
		int kTop= UI.valueInt(kinectTop);
		float kBottom = UI.valueInt(kinectBottom);
		float depthDiv = UI.valueInt(depthDivider);
		
		// loop through depth data grid, draw, and count how many depth points we found
		int numPixelsProcessed = 0;
		float pixelsize = (float) pixelSkipp * UI.value(pixelDrawSize);
		for ( int x = kLeft; x < kRight; x += pixelSkipp ) {
			for ( int y = kTop; y < kBottom; y += pixelSkipp ) {
				int pixelDepth = depthCamera.getDepthAt( x, y );
				if( pixelDepth != 0 && pixelDepth > kNear && pixelDepth < kFar ) {
					p.pushMatrix();
					p.translate(0, 0, -pixelDepth/depthDiv);
					p.fill((UI.valueToggle(drawGradient)) ? P.map(pixelDepth, kNear, kFar, 255, 0) : 255);
					p.rect(x, y, pixelsize, pixelsize);
					p.popMatrix();
					numPixelsProcessed++;
				}
			}
		}

		p.popMatrix();

		// debug view updates
		DebugView.setValue("DepthCameraSize.WIDTH", DepthCameraSize.WIDTH);
		DebugView.setValue("DepthCameraSize.HEIGHT", DepthCameraSize.HEIGHT);
		DebugView.setValue("numPixelsProcessed", numPixelsProcessed);
		DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
	}
	
}
