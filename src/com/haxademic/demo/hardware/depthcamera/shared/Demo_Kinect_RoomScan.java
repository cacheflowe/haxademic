package com.haxademic.demo.hardware.depthcamera.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.KinectRoomScanDiff;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;

public class Demo_Kinect_RoomScan
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectRoomScanDiff kinectDiff;
	protected boolean needsDelayedReset = false;
	
	protected String colorDiffThresh = "colorDiffThresh";
	protected String smoothThresh = "smoothThresh";
	protected String diffSmoothBlur = "diffSmoothBlur";
	protected String newFrameLerp = "newFrameLerp";

	protected String debugScale = "debugScale";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.REALSENSE_ACTIVE, true );
	}

	public void setupFirstFrame() {
		// Choose depthImageMode vs raw depth data method.
		// Depending on whether the camera re-calibrates the depth image, we might need to go with pixel data
		kinectDiff = new KinectRoomScanDiff(p.depthCamera, 3, true);
//		kinectDiff = new KinectRoomScanDiff(p.depthCamera, 8, false);
		
		// build ui
		p.ui.addSlider(colorDiffThresh, 0.005f, 0f, 1f, 0.001f, false);
		p.ui.addSlider(smoothThresh, 0.5f, 0f, 1f, 0.001f, false);
		p.ui.addSlider(diffSmoothBlur, 0.43f, 0f, 3f, 0.001f, false);
		p.ui.addSlider(newFrameLerp, 0.15f, 0f, 1f, 0.001f, false);

		p.ui.addSlider(debugScale, 1, 0.5f, 6f, 0.01f, false);
	}
	
	public void drawApp() {
		// reset if we need to run out of the frame
		if(needsDelayedReset) {
			kinectDiff.reset();
			needsDelayedReset = false;
		}
		
		// update room scan via ui
		kinectDiff.colorDiffThresh(p.ui.value(colorDiffThresh));
		kinectDiff.smoothThresh(p.ui.value(smoothThresh));
		kinectDiff.smoothBlur(p.ui.value(diffSmoothBlur));
		kinectDiff.smoothLerp(p.ui.value(newFrameLerp));
		
		// update depth frame diffing
		kinectDiff.update();
		
		// draw all
		p.scale(p.ui.value(debugScale));
		p.background(0, 127, 0);
		p.image(kinectDiff.roomScanBuffer(), 0, 0);
		p.image(kinectDiff.depthBuffer(), kinectDiff.roomScanBuffer().width, 0);
		p.image(kinectDiff.depthDifference(), kinectDiff.roomScanBuffer().width * 2, 0);
		p.image(kinectDiff.resultLerped(), 0, kinectDiff.depthDifference().height);
		p.image(kinectDiff.resultSmoothed(), kinectDiff.resultLerped().width, kinectDiff.depthDifference().height);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectDiff.reset();
		if(p.key == 'd') SystemUtil.setTimeout(delayedReset, 4000);
	}
	
	protected ActionListener delayedReset = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			needsDelayedReset = true;
		}
	};
}
