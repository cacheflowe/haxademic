package com.haxademic.demo.hardware.depthcamera.shared;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.BufferThresholdMonitor;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.depthcamera.KinectRoomScanDiff;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

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
	
	protected BufferThresholdMonitor userActive;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.REALSENSE_ACTIVE, true );
	}

	public void setupFirstFrame() {
		surface.setResizable(true);
		// Choose depthImageMode vs raw depth data method.
		// Depending on whether the camera re-calibrates the depth image, we might need to go with pixel data
//		kinectDiff = new KinectRoomScanDiff(p.depthCamera, 3, true);
		kinectDiff = new KinectRoomScanDiff(p.depthCamera, 8, false);
		
		// build ui
		UI.addSlider(colorDiffThresh, 0.005f, 0f, 1f, 0.001f, false);
		UI.addSlider(smoothThresh, 0.5f, 0f, 1f, 0.001f, false);
		UI.addSlider(diffSmoothBlur, 0.43f, 0f, 3f, 0.001f, false);
		UI.addSlider(newFrameLerp, 0.15f, 0f, 1f, 0.001f, false);

		UI.addSlider(debugScale, 1, 0.5f, 6f, 0.01f, false);
		
		userActive = new BufferThresholdMonitor(32, 24, 20);
	}
	
	public void drawApp() {
		// reset if we need to run out of the frame
		if(needsDelayedReset) {
			kinectDiff.reset();
			needsDelayedReset = false;
		}
		
		// update room scan via ui
		kinectDiff.colorDiffThresh(UI.value(colorDiffThresh));
		kinectDiff.smoothThresh(UI.value(smoothThresh));
		kinectDiff.smoothBlur(UI.value(diffSmoothBlur));
		kinectDiff.smoothLerp(UI.value(newFrameLerp));
		
		// update depth frame diffing
		kinectDiff.update();
		
		// draw all
		p.scale(UI.value(debugScale));
		p.background(0, 127, 0);
		p.image(kinectDiff.roomScanBuffer(), 0, 0);
		p.image(kinectDiff.depthBuffer(), kinectDiff.roomScanBuffer().width, 0);
		p.image(kinectDiff.depthDifference(), kinectDiff.roomScanBuffer().width * 2, 0);
		p.image(kinectDiff.resultLerped(), 0, kinectDiff.depthDifference().height);
		p.image(kinectDiff.resultSmoothed(), kinectDiff.resultLerped().width, kinectDiff.depthDifference().height);
		p.image(userActive.thresholdBuffer(), kinectDiff.resultLerped().width * 2, kinectDiff.depthDifference().height, kinectDiff.resultLerped().width, kinectDiff.depthDifference().height);
		
		// check user active monitor
		userActive.update(kinectDiff.resultSmoothed());
		float activeMonitorResult = userActive.thresholdCalc();
		DebugView.setValue("activeMonitorResult", activeMonitorResult);
		DebugView.setTexture("activeMonitorResult buffer", userActive.thresholdBuffer());
		// activity indicator
		int userIndicatorFill = (activeMonitorResult > 0.01f) ? p.color(0,255,0) : p.color(255,0,0);
		p.fill(userIndicatorFill);
		p.ellipse(20 + kinectDiff.resultLerped().width * 2, 20 + kinectDiff.depthDifference().height, 20, 20);
		FontCacher.setFontOnContext(p.g, FontCacher.getFont(DemoAssets.fontInterPath, 24), p.color(255), 1, PTextAlign.LEFT, PTextAlign.TOP);
		p.text(activeMonitorResult, 20 + kinectDiff.resultLerped().width * 2, kinectDiff.depthDifference().height * 2 - 40);
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
