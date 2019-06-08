package com.haxademic.core.hardware.depthcamera;

import org.openkinect.processing.Kinect;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.core.PVector;

public class OpenKinectPixelImg {
	
	public PGraphics texture;
	protected int pixelSkip;
	protected int near;
	protected int far;
	float[] depthLookUp = new float[2048];
	int[] depth;
	protected Kinect kinect;

	public OpenKinectPixelImg(int pixelSkip, int near, int far) {
		this.pixelSkip = pixelSkip;
		this.near = near;
		this.far = far;
		texture = P.p.createGraphics(640/pixelSkip, 480/pixelSkip, P.P2D);
		texture.noSmooth();
		buildLookupTable();
		initKinect();
	}
	
	protected void initKinect() {
		kinect = new Kinect(P.p);
		kinect.initDepth();
		kinect.enableMirror(true);
//		deg = kinect.getTilt();
		// kinect.tilt(deg);
	}
	
	protected void buildLookupTable() {
		// Lookup table for all possible depth values (0 - 2047)
		for (int i = 0; i < depthLookUp.length; i++) {
			depthLookUp[i] = rawDepthToMeters(i);
		}
	}
	
	public void update() {
		// Get the raw depth as array of integers
		depth = kinect.getRawDepth();

		texture.beginDraw();
		texture.clear();
		texture.fill(255);
		texture.noStroke();

		
		for (int x = 0; x < kinect.width; x += pixelSkip) {
			for (int y = 0; y < kinect.height; y += pixelSkip) {
				float rawDepth = getRawDepth(x, y);
				if(rawDepth > near && rawDepth < far) {
					texture.rect(x/pixelSkip, y/pixelSkip, 1, 1);
				}
			}
		}
		
		texture.endDraw();
	}
	
	protected float getRawDepth(int x, int y) {
		// Convert kinect data to world xyz coordinate
		int offset = x + y * kinect.width;
		return 1000f * depthLookUp[depth[offset]];
	}
	
	// These functions come from: http://graphics.stanford.edu/~mdfisher/Kinect.html
	float rawDepthToMeters(int depthValue) {
	  if (depthValue < 2047) {
	    return (float)(1.0 / ((double)(depthValue) * -0.0030711016 + 3.3309495161));
	  }
	  return 0.0f;
	}

	protected PVector depthToWorld(int x, int y, int depthValue) {

	  final double fx_d = 1.0 / 5.9421434211923247e+02;
	  final double fy_d = 1.0 / 5.9104053696870778e+02;
	  final double cx_d = 3.3930780975300314e+02;
	  final double cy_d = 2.4273913761751615e+02;

	  PVector result = new PVector();
	  double depth =  depthLookUp[depthValue];//rawDepthToMeters(depthValue);
	  result.x = (float)((x - cx_d) * depth * fx_d);
	  result.y = (float)((y - cy_d) * depth * fy_d);
	  result.z = (float)(depth);
	  return result;
	}

}