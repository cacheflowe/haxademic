package com.haxademic.core.hardware.depthcamera.cameras;

import org.intel.rs.device.Device;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import ch.bildspur.realsense.RealSenseCamera;
import ch.bildspur.realsense.type.ColorScheme;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RealSenseWrapper 
implements IDepthCamera {
	
	protected RealSenseCamera camera;
	public static int RGB_W = 1280;
	public static int RGB_H = 720;
	public static int DEPTH_W = RGB_W;
	public static int DEPTH_H = RGB_H;
	protected boolean DEPTH_ACTIVE = true;
	protected boolean RGB_ACTIVE = true;
	protected boolean MIRROR = true;
	protected boolean threaded = true;
	protected Boolean threadBusy = false;
	protected Thread curThread;
	protected boolean hasUpdated = false;
	protected PGraphics mirrorRGB;
	protected PGraphics mirrorDepth;
	protected short[][] data;
	public static float METERS_FAR_THRESH = 15;
	public static float METERS_NEAR_QUEUED = -1;
	public static float METERS_FAR_QUEUED = -1;
	public static ColorScheme COLOR_SCHEME = ColorScheme.Cold;
	
	public static void setSmallStream() {
		RGB_W = DEPTH_W = 640;
		RGB_H = DEPTH_H = 480;
	}

	public static void setTinyStream() {
		RGB_W = DEPTH_W = 424;
		RGB_H = DEPTH_H = 240;
	}
	
	public RealSenseWrapper(PApplet p, boolean initRGB, boolean initDepthImage) {
		this(p, initRGB, initDepthImage, RGB_W, RGB_H, null);
	}
	
	public RealSenseWrapper(PApplet p, boolean initRGB, boolean initDepthImage, String serialNumber) {
		this(p, initRGB, initDepthImage, RGB_W, RGB_H, serialNumber);
	}
	
	public RealSenseWrapper(PApplet p, boolean initRGB, boolean initDepthImage, int width, int height) {
		this(p, initRGB, initDepthImage, width, height, null);
	}
	
	public RealSenseWrapper(PApplet p, boolean initRGB, boolean initDepthImage, int width, int height, String serialNumber) {
		RGB_ACTIVE = initRGB;
		DEPTH_ACTIVE = initDepthImage;
		RGB_W = width;
		RGB_H = height;
		DEPTH_W = width;
		DEPTH_H = height;

		data = new short[RGB_H][RGB_W];
		DepthCameraSize.setSize(RGB_W, RGB_H);
		
		camera = new RealSenseCamera(p);
		if(initRGB)        camera.enableColorStream(RGB_W, RGB_H);
		if(initDepthImage) camera.enableDepthStream(DEPTH_W, DEPTH_H);
//		camera.enableDepthStream(480, 270);
		if(COLOR_SCHEME != null) camera.enableColorizer(COLOR_SCHEME);
//		camera.enableIRStream(CAMERA_W, CAMERA_H, 30);
		camera.enableAlign();
		camera.addThresholdFilter(0.0f, METERS_FAR_THRESH);
//		camera.addSpatialFilter(1, 0.75f, 50, 1);
//		camera.addDecimationFilter(2);
//		camera.addDisparityTransform(true);
//		camera.addHoleFillingFilter(HoleFillingType.FarestFromAround);
//		camera.addTemporalFilter(0.5f, 30, PersistencyIndex.ValidIn1_Last2);
		if(serialNumber == null) {
			camera.start();
		} else {
			camera.start(serialNumber);
		}
		
		mirrorRGB = PG.newPG(RGB_W, RGB_H);
		mirrorDepth = PG.newPG(DEPTH_W, DEPTH_H);
	}
	
	public static void listConnectedCameras() {
		P.out("getDeviceCount", RealSenseCamera.getDeviceCount());
		Device devices[] = RealSenseCamera.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			P.out("Device["+i+"] SerialNumber:", device.getSerialNumber());
		}
	}
	
//	public void setNearFar(float near, float far) {
//		METERS_NEAR_QUEUED = near;
//		METERS_FAR_QUEUED = far;
//	}
	
	public RealSenseCamera camera() {
		return camera;
	}
	
	public void stop() {
		if (camera().isRunning()) {
			camera.stop();
		}
		if(threaded && curThread != null) {
			curThread.interrupt();
			curThread = null;
		}
	}
	
	///////////////////////////
	// THREADED UPDATING
	///////////////////////////

	public void update() {
		if(camera.isRunning() == false) return;
		if(threaded) {
			if(threadBusy == false) {
				curThread = new Thread(new Runnable() { public void run() {
					threadBusy = true;
					boolean successfulFrame = false;
					try {
						camera.readFrames();
						successfulFrame = true;
					} catch (NullPointerException e) {
						successfulFrame = false;
						P.out("RealSenseWrapper failed to update");
						e.printStackTrace();
					}
					if(DEPTH_ACTIVE && successfulFrame) data = camera.getDepthData();
					threadBusy = false;
					hasUpdated = true;
				}});
				curThread.start();
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
			if(METERS_NEAR_QUEUED != -1 || METERS_FAR_QUEUED != -1) {
				camera.clearFilters();
				camera.addThresholdFilter(METERS_NEAR_QUEUED, METERS_FAR_QUEUED);
				METERS_NEAR_QUEUED = -1;
				METERS_FAR_QUEUED = -1;
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
	
	public int rgbWidth() {return RGB_W;};
	public int rgbHeight() {return RGB_H;};
	
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
	    /*
	    if(x < 0 || x >= data[y].length || y < 0 || y >= data[y].length) {
	        P.out("BAD!!! getDepthAt("+x+", "+y+")");
	        P.out("data.length: ", data.length);
	        P.out("data[y].length: ", data[y].length);
	        P.out("RGB_W, RGB_H: ", RGB_W, RGB_H);
	        P.out("DEPTH_W, DEPTH_H: ", DEPTH_W, DEPTH_H);
	        return 0;
	    }
	    */
		return (MIRROR) ? 
			data[y][DEPTH_W - 1 - x] : 
			data[y][x];
	}
}
