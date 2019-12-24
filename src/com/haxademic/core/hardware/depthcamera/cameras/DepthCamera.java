package com.haxademic.core.hardware.depthcamera.cameras;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRegisterableMethods;

public class DepthCamera {

	public enum DepthCameraType {
		KinectV1,
		KinectV2,
		Realsense,
	}
	public IDepthCamera camera = null;
	public DepthCameraType cameraType = null;
	
	/////////////////////////////
	// static instance & initializer for quick & easy access
	/////////////////////////////
	
	public static DepthCamera instance;
	
	public static DepthCamera instance(DepthCameraType lib) {
		if(instance != null) return instance;
		instance = new DepthCamera(lib);
		return instance;
	}
	
	public static DepthCamera instance(IDepthCamera input) {
		if(instance != null) return instance;
		instance = new DepthCamera(input);
		return instance;
	}
	
	public static DepthCamera instance() {
		if(instance != null) return instance;
		instance = new DepthCamera(DepthCameraType.KinectV1);
		return instance;
	}
	
	public DepthCamera(DepthCameraType lib) {
		this(initKinect(lib));
	}
	
	public DepthCamera(IDepthCamera input) {
		this.camera = input;
		if(camera != null) {
			camera.setMirror( Config.getBoolean( "kinect_mirrored", true ) );
		}
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}

	protected static IDepthCamera initKinect(DepthCameraType lib) {
		boolean rgbActive = Config.getBoolean(AppSettings.DEPTH_CAM_RGB_ACTIVE, true);
		boolean depthActive = Config.getBoolean(AppSettings.DEPTH_CAM_DEPTH_ACTIVE, true);
		
		switch (lib) {
			case KinectV1:
				return new KinectWrapperV1( P.p, rgbActive, depthActive );
			case KinectV2:
				return new KinectWrapperV2( P.p, rgbActive, depthActive );
			case Realsense:
				return new RealSenseWrapper( P.p, rgbActive, depthActive );
		}
		return null;
	}
	
	public void pre() {
		if(camera != null) camera.update();
	}

	public void dispose() {
		if(camera != null) camera.stop();
	}
}
