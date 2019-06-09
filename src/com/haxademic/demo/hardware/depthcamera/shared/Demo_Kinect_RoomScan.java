package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.depthcamera.KinectRoomScanDiff;

public class Demo_Kinect_RoomScan
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected KinectRoomScanDiff kinectDiff;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.REALSENSE_ACTIVE, true );
	}

	public void setupFirstFrame() {
		kinectDiff = new KinectRoomScanDiff(p.depthCamera);
	}
	
	public void drawApp() {
		kinectDiff.update();
		
		// draw all
		p.background(0, 127, 0);
		p.scale(5f);
		p.image(kinectDiff.roomScanBuffer(), 0, 0);
		p.image(kinectDiff.depthBuffer(), kinectDiff.roomScanBuffer().width, 0);
		p.image(kinectDiff.depthDifference(), kinectDiff.roomScanBuffer().width * 2, 0);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectDiff.reset();
	}
}
