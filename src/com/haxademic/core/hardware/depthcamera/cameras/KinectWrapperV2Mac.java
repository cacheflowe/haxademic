package com.haxademic.core.hardware.depthcamera.cameras;

import org.openkinect.processing.Kinect2;

import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

//Kinect Wrapper for Microsoft Kinect V2 for Windows
public class KinectWrapperV2Mac 
implements IDepthCamera {
	
	protected PApplet p;
	protected Kinect2 _kinect;
	protected boolean _kinectActive = true;
	public static boolean KINECT_ERROR_SHOWN = false;

	public static int KWIDTH = 512;
	public static int KHEIGHT = 424;

	//Kinect V2
	public int[] _depthArray;
	public PVector[] _realWorldMap;
	private boolean mirror = false;
	
	public KinectWrapperV2Mac( PApplet p, boolean initDepth, boolean initRGB, boolean initDepthImage ) {
		this.p = p;
		
		new Thread(new Runnable() { public void run() {
			
			DepthCameraSize.setSize(KWIDTH, KHEIGHT);
			
			_kinect = new Kinect2(p);
			_kinect.initVideo();
			_kinect.initDepth();
			_kinect.initIR();
			_kinect.initRegistered();
			// Start all data
			_kinect.initDevice();
			
			
//		_depthArray = new int[_kinect.getRawDepth().length];
			setMirror(false);
			
	    }}).start();
	}
	
	@Override
	public void update() {
		// Get the raw depth as array of integers
		if( _kinectActive == true && _kinect != null ) {
			_depthArray = _kinect.getRawDepth();
		}
	}
	
	public static int getUnsignedInt(int x) {
	    return (x & 0x00ffffff);
	}
	
	@Override
	public PImage getDepthImage() {
		return _kinect.getDepthImage();
	}
	
	@Override
	public PImage getIRImage() {
		return _kinect.getIrImage();
	}
	
	@Override
	public PImage getRgbImage() {
		return _kinect.getVideoImage();
	}
	
	public int rgbWidth() {return 1920;};
	public int rgbHeight() {return 1080;};
	
	@Override
	public int[] getDepthData() {
		return _depthArray;
	}
	
	@Override
	public boolean isActive() {
		return _kinectActive;
	}

	@Override
	public void setMirror( boolean mirrored ) {
		this.mirror = mirrored;
	}
	
	@Override
	public boolean isMirrored() {
		return this.mirror;
	}
		
	public void stop() {
		if( _kinectActive ) {
			_kinect.dispose();
		}
	}
	
	public int getMillimetersDepthForKinectPixel( int x, int y ) {
		if(_kinect == null) return 0;
		int offset = x + y * _kinect.depthWidth;
		if( offset >= _depthArray.length ) {
			return 0;
		} else {
			return _depthArray[offset];
		}
	}
	
	//calculte the xyz camera position based on the depth data
	public PVector depthToPointCloudPos(int x, int y, float depthValue) {
		PVector point = new PVector();
		point.z = (depthValue);// / (1.0f); // Convert from mm to meters
		point.x = (x - CameraParams.cx) * point.z / CameraParams.fx;
		point.y = (y - CameraParams.cy) * point.z / CameraParams.fy;
		return point;
	}
	
	//camera information based on the Kinect v2 hardware
	public static class CameraParams {
	  static float cx = 254.878f;
	  static float cy = 205.395f;
	  static float fx = 365.456f;
	  static float fy = 365.456f;
	  static float k1 = 0.0905474f;
	  static float k2 = -0.26819f;
	  static float k3 = 0.0950862f;
	  static float p1 = 0.0f;
	  static float p2 = 0.0f;
	}

}
