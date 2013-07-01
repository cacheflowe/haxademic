
package com.haxademic.sketch.hardware.kinect_openni.lib_demos;

import processing.core.PApplet;

import com.haxademic.core.hardware.kinect.KinectWrapper;

public class OpenNICameraTest extends PApplet {

	//	SimpleOpenNI context;
	KinectWrapper _kinect;
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

		_kinect = new KinectWrapper( this, true, true, true );

		background(200,0,0);
		size(_kinect.openni().depthWidth() + _kinect.openni().rgbWidth() + 10, _kinect.openni().rgbHeight()); 
	}

	public void draw()
	{
		// update the cam
		_kinect.update();
		// draw depthImageMap
		image(_kinect.openni().depthImage(),0,0);

		// draw camera
		image(_kinect.openni().rgbImage(), KinectWrapper.KWIDTH + 10,0);
	}
}
