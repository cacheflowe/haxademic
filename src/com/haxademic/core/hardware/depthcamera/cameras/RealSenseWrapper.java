package com.haxademic.core.hardware.depthcamera.cameras;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import ch.bildspur.realsense.RealSenseCamera;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RealSenseWrapper 
implements IDepthCamera {
	
	protected RealSenseCamera camera;
	protected int CAMERA_W = 848;	// KWIDTH
	protected int CAMERA_H = 480;
	protected int CAMERA_FPS = 60;
	protected int CAMERA_NEAR = 180;
	protected int CAMERA_FAR = 5000;
	protected boolean DEPTH_ACTIVE = true;
	protected boolean RGB_ACTIVE = true;
	protected boolean MIRROR = true;
	protected boolean threaded = false;
	protected PGraphics mirrorRGB;
	protected PGraphics mirrorDepth;


	// multithread the kinect communication
	protected ThreadedUpdate updateThread;
	protected Boolean _updateComplete = true;
	

	public RealSenseWrapper(PApplet p, boolean initRGB, boolean initDepthImage) {
		RGB_ACTIVE = initRGB;
		DEPTH_ACTIVE = initDepthImage;

		DepthCameraSize.setSize(CAMERA_W, CAMERA_H);
		
		camera = new RealSenseCamera(p);
		camera.start(CAMERA_W, CAMERA_H, CAMERA_FPS, DEPTH_ACTIVE, RGB_ACTIVE);
		
		mirrorRGB = PG.newPG(CAMERA_W, CAMERA_H);
		mirrorDepth = PG.newPG(CAMERA_W, CAMERA_H);
	}
	
	public void stop() {
	}
	
	///////////////////////////
	// THREADED UPDATING
	///////////////////////////
	
	class ThreadedUpdate implements Runnable {
		public ThreadedUpdate() {}    

		public void run() {
			readCamera();
		} 
	}

	public void update() {
//		threaded = true;
		if(_updateComplete == true ) {
			if(threaded) {
				_updateComplete = false;
				if(updateThread == null) updateThread = new ThreadedUpdate();
				(new Thread(updateThread)).start();
			} else {
//				if(P.p.frameCount % 2 == 0) 
					readCamera();
			}
			
			// copy image to buffers
			if(DEPTH_ACTIVE) {
				camera.createDepthImage(CAMERA_NEAR, CAMERA_FAR); // min/max depth
				if(MIRROR) ImageUtil.copyImageFlipH(camera.getDepthImage(), mirrorDepth);
			}
			if(RGB_ACTIVE) {
				if(MIRROR) ImageUtil.copyImageFlipH(camera.getColorImage(), mirrorRGB);
			}
		}
	}
	
	protected void readCamera() {
		camera.readFrames();
		_updateComplete = true;
	}
	
	///////////////////////////
	// GETTERS
	///////////////////////////
	
	public PImage getDepthImage() {
		return (MIRROR) ? mirrorDepth : camera.getDepthImage();
	}
	
	public PImage getIRImage() {
		return null;
	}
	
	public PImage getRgbImage() {
		return (MIRROR) ? mirrorRGB : camera.getColorImage();
	}
	
	public int rgbWidth() {return CAMERA_W;};
	public int rgbHeight() {return CAMERA_H;};
	
	public int[] getDepthData() {
		return null;
	}
	
	public boolean isActive() {
		return camera.isRunning();
	}
	
	public void setMirror( boolean mirrored ) {
		MIRROR = mirrored;
	}
	
	public boolean isMirrored() {
		return MIRROR;
	}
	
	public int getDepthAt( int x, int y ) {
		return camera.getDepth((MIRROR) ? CAMERA_W - x : x, y);
	}
}
