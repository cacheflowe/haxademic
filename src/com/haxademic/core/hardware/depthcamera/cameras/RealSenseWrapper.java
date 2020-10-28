package com.haxademic.core.hardware.depthcamera.cameras;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import ch.bildspur.realsense.RealSenseCamera;
import ch.bildspur.realsense.type.ColorScheme;
import ch.bildspur.realsense.type.HoleFillingType;
import ch.bildspur.realsense.type.PersistencyIndex;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RealSenseWrapper 
implements IDepthCamera {
	
	protected RealSenseCamera camera;
	public static final int CAMERA_W = 640;	// KWIDTH
	public static final int CAMERA_H = 480;
	protected boolean DEPTH_ACTIVE = true;
	protected boolean RGB_ACTIVE = true;
	protected boolean MIRROR = true;
	protected boolean threaded = true;
	protected Boolean threadBusy = false;
	protected boolean hasUpdated = false;
	protected PGraphics mirrorRGB;
	protected PGraphics mirrorDepth;
	protected short[][] data = new short[CAMERA_H][CAMERA_W];
	public static float METERS_FAR_THRESH = 15;

	public RealSenseWrapper(PApplet p, boolean initRGB, boolean initDepthImage) {
		RGB_ACTIVE = initRGB;
		DEPTH_ACTIVE = initDepthImage;

		DepthCameraSize.setSize(CAMERA_W, CAMERA_H);
		
		camera = new RealSenseCamera(p);
		camera.enableColorStream();
		camera.enableDepthStream(640, 480);
		camera.enableColorizer(ColorScheme.Cold);
//		camera.enableIRStream(640, 480, 30);
		camera.enableAlign();
		camera.addThresholdFilter(0.0f, METERS_FAR_THRESH);
//		camera.addSpatialFilter(1, 0.75f, 50, 1);
//		camera.addDecimationFilter(2);
//		camera.addDisparityTransform(true);
//		camera.addHoleFillingFilter(HoleFillingType.FarestFromAround);
//		camera.addTemporalFilter(0.5f, 30, PersistencyIndex.ValidIn1_Last2);
		camera.start();
		
		mirrorRGB = PG.newPG(CAMERA_W, CAMERA_H);
		mirrorDepth = PG.newPG(CAMERA_W, CAMERA_H);
	}
	
	public void stop() {
	}
	
	///////////////////////////
	// THREADED UPDATING
	///////////////////////////

	public void update() {
		if(threaded) {
			if(threadBusy == false) {
				new Thread(new Runnable() { public void run() {
					threadBusy = true;
					try {
						camera.readFrames();
					} catch (NullPointerException e) {
						P.out("RealSenseWrapper failed to update");
					}
					if(DEPTH_ACTIVE) data = camera.getDepthData();
					threadBusy = false;
					hasUpdated = true;
				}}).start();
			}
		} else {
			camera.readFrames();
			hasUpdated = true;
		}
		
		// copy image to buffers
		// make sure camera has updated once before reading images & data
		if(hasUpdated) {
			if(DEPTH_ACTIVE) {
				if(MIRROR) ImageUtil.copyImageFlipH(camera.getDepthImage(), mirrorDepth);
			}
			if(RGB_ACTIVE) {
				if(MIRROR) ImageUtil.copyImageFlipH(camera.getColorImage(), mirrorRGB);
			}
		}
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
		return (MIRROR) ? data[y][CAMERA_W - 1 - x] : data[y][x];
//		return Math.round(camera.getDistance((MIRROR) ? CAMERA_W - x : x, y) * 1000f);
	}
}
