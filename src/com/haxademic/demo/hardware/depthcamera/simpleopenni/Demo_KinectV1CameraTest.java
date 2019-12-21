package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

public class Demo_KinectV1CameraTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 480 );
		Config.setProperty(AppSettings.KINECT_ACTIVE, true );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}
	
	public void drawApp() {
		p.image(p.depthCamera.getRgbImage(), 0, 0);
		p.image(p.depthCamera.getDepthImage(), 640, 0);
		if(p.depthCamera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", p.depthCamera.getRgbImage());
		if(p.depthCamera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", p.depthCamera.getDepthImage());
	}
	
}
