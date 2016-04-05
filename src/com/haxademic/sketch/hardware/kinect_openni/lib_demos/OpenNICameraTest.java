
package com.haxademic.sketch.hardware.kinect_openni.lib_demos;

import processing.core.PApplet;

import com.haxademic.core.hardware.kinect.IKinectWrapper;
import com.haxademic.core.hardware.kinect.KinectSize;
import com.haxademic.core.hardware.kinect.KinectWrapperV1;

public class OpenNICameraTest extends PApplet {

	//	SimpleOpenNI context;
	IKinectWrapper _kinect;
	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.hardware.kinect_openni.OpenNICameraTest" });
	}
	public void setup()
	{
		//		super.setup();

		//TODO: This is temporary. Redesign to use dependency injection of the Kinect services
		_kinect = new KinectWrapperV1( this, true, true, true );
//		_kinect = new KinectWrapperV2( this, true, true, true );

		background(200,0,0);
		size(KinectSize.WIDTH + _kinect.rgbWidth() + 10, _kinect.rgbHeight()); 
	}

	public void draw()
	{
		// update the cam
		_kinect.update();
		// draw depthImageMap
		image(_kinect.getDepthImage(),0,0);

		// draw camera
		image(_kinect.getIRImage(), KinectSize.WIDTH + 10,0);
	}
}
