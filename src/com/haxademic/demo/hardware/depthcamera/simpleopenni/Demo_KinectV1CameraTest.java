package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

public class Demo_KinectV1CameraTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 480 );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
	}
	
	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		p.image(depthCamera.getRgbImage(), 0, 0);
		p.image(depthCamera.getDepthImage(), 640, 0);
		if(depthCamera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		if(depthCamera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
	}
	
}
