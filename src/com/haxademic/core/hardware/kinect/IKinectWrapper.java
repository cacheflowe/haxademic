package com.haxademic.core.hardware.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public interface IKinectWrapper {
		
	public abstract void update();
	public abstract PImage getDepthImage();

	public abstract PImage getIRImage();

	public abstract PImage getRgbImage();

	public abstract int[] getDepthData();

	public abstract void enableDepth(boolean enable);

	public abstract void enableIR(boolean enable);

	public abstract void enableRGB(boolean enable);
		
	public abstract int rgbWidth();
	public abstract int rgbHeight();

	public abstract boolean isActive();

	public abstract SimpleOpenNI openni(); //TODO: Remove this once the SkeltonsTracker class is fixed. It's referring to many Openni APIs directly

	public abstract void setMirror(boolean mirrored);
	public abstract void setFlipped(boolean flipped);

	public abstract boolean isMirrored();

	public abstract void drawCamFrustum();

	public abstract void tiltUp();

	public abstract void tiltDown();

	public abstract void drawPointCloudForRect(PApplet p, boolean mirrored,
			int pixelSkip, float alpha, float scale, float depthClose,
			float depthFar, int top, int right, int bottom, int left);

	/**
	 * Shuts down Kinect properly when the PApplet shuts down
	 */
	public abstract void stop();

	public abstract PVector getRealWorldDepthForKinectPixel(int x, int y);

	public abstract int getMillimetersDepthForKinectPixel(int x, int y);

	public abstract void startTrackingSkeleton(int userId);
	public abstract void enableUser(int i);
	public abstract int[] getUsers();
	public abstract void getCoM(int i, PVector _utilPVec);
	public abstract float getJointPositionSkeleton(int userId,
			int skelLeftHand, PVector _utilPVec);
	public abstract void convertRealWorldToProjective(PVector _utilPVec,
			PVector _utilPVec2);
	public abstract void drawLimb(int userId, int skelHead, int skelNeck);

}