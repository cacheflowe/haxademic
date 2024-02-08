package com.haxademic.core.hardware.depthcamera.cameras;

import org.intel.rs.device.Device;
import org.intel.rs.types.Option;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;

import ch.bildspur.realsense.RealSenseCamera;
import ch.bildspur.realsense.processing.RSThresholdFilter;
import ch.bildspur.realsense.type.ColorScheme;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RealSenseWrapper 
implements IDepthCamera {
	
	protected RealSenseCamera camera;
	protected String serialNumber;
	public static int RGB_W = 1280;
	public static int RGB_H = 720;
	public static int FPS = 30;
	public static int FPS_D = 30;
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
	public static boolean FIXED_COLOR_SCHEME_GRADIENT = true;
	protected RSThresholdFilter thresholdFilter;
	public static ColorScheme COLOR_SCHEME = ColorScheme.WhiteToBlack;
	
	public static void set720p() {
		setConfig(1280, 720, 30, 30);
	}
	
	public static void setMidStreamFast() {
		setConfig(848, 480, 60, 60);
	}
	
	public static void setSmallStream() {
		setConfig(640, 480, 30, 30);
	}
	
	public static void setTinyStream() {
		setConfig(424, 240, 30, 30);
	}
	
	public static void setTinyStreamSuperFast() {
		setConfig(424, 240, 60, 90);
	}

	public static void setConfig(int width, int height, int fps, int fpsDepth) {
		RGB_W = DEPTH_W = width;
		RGB_H = DEPTH_H = height;
		FPS = fps;
		FPS_D = fpsDepth;
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
		this.serialNumber = serialNumber;

		data = new short[RGB_H][RGB_W];
		DepthCameraSize.setSize(RGB_W, RGB_H);
		initBuffers();

		// init camera streams as needed
		if(RealSenseCamera.getDeviceCount() > 0) {
			initCamera();
		}
	}
	
	protected void initCamera() {
		camera = new RealSenseCamera(P.p);
		if(RGB_ACTIVE)   camera.enableColorStream(RGB_W, RGB_H, FPS);
		if(DEPTH_ACTIVE) camera.enableDepthStream(DEPTH_W, DEPTH_H, FPS_D);

		// enable color sceheme - black/white far/near makes most sense to me
		camera.enableColorizer(COLOR_SCHEME);
		if(FIXED_COLOR_SCHEME_GRADIENT) camera.getColorizer().setOption(Option.VisualPreset, 1f); // set fixed "Visual Preset", which makes the depth texture much more usable!

//		camera.enableIRStream(CAMERA_W, CAMERA_H, 30);
		camera.enableAlign();
		thresholdFilter = camera.addThresholdFilter(0, METERS_FAR_THRESH);
		
		// init camera!
		if(serialNumber == null) {
			camera.start();
		} else {
			camera.start(serialNumber);
		}

		// Print camera info
		// camera.getAdvancedDevice().setAdvancedModeEnabled(true);
		P.outInitLineBreak();
		P.outInit("Realsense config:");
		P.outInit("-----------------");
		P.outInit("Name", camera.getAdvancedDevice().getName());
		P.outInit("SerialNumber", camera.getAdvancedDevice().getSerialNumber());
		P.outInit("FirmwareVersion", camera.getAdvancedDevice().getFirmwareVersion());
		P.outInit("USBTypeDescriptor", camera.getAdvancedDevice().getUSBTypeDescriptor());
		P.outInit("ProductId", camera.getAdvancedDevice().getProductId());
		P.outInit("PhysicalPort", camera.getAdvancedDevice().getPhysicalPort());
		P.outInitLineBreak();

		// set advanced options
		// highest laser poser leads to much better depth data - less noise/holes
		camera.getDepthSensor().setOption(Option.LaserPower, 360);
		camera.getColorizer().setOption(Option.ColorScheme, COLOR_SCHEME.getIndex());

		// https://github.com/IntelRealSense/librealsense/wiki/D400-Series-Visual-Presets#preset-table
		// camera.setJsonConfiguration("{\"controls-laserpower\": \"360\", \"param-neighborthresh\": \"0\"}");
		// camera.setJsonConfiguration("{\"controls-depth-white-balance-auto\": \"False\"}");
		// P.out("getOptionMin(Option.VisualPreset", camera.getDepthSensor().getOptionMin(Option.VisualPreset));
		// P.out("getOptionMax(Option.VisualPreset", camera.getDepthSensor().getOptionMax(Option.VisualPreset));
		// P.out("getOptionStep(Option.VisualPreset", camera.getDepthSensor().getOptionStep(Option.VisualPreset));
		// P.out("getOptionDefault(Option.VisualPreset", camera.getDepthSensor().getOptionDefault(Option.VisualPreset));
	}

	protected void initBuffers() {
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
	
	public void setNearFar(float near, float far) {
		thresholdFilter.setMinDistance(near);
		thresholdFilter.setMaxDistance(far);
	}
	
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
		if(camera == null) return;
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
		}
	}
	
	///////////////////////////
	// GETTERS
	///////////////////////////
	
	public PImage getDepthImage() {
		if(this.camera == null) return mirrorDepth; // return empty pg
		return (MIRROR) ? mirrorDepth : camera.getDepthImage();
	}
	
	public PImage getIRImage() {
		return null;
	}
	
	public PImage getRgbImage() {
		if(this.camera == null) return mirrorRGB; // return empty pg
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
		return (MIRROR) ? 
			data[y][DEPTH_W - 1 - x] : 
			data[y][x];
	}
}
