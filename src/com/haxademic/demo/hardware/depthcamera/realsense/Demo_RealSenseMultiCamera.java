package com.haxademic.demo.hardware.depthcamera.realsense;

import org.intel.rs.device.Device;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

import ch.bildspur.realsense.RealSenseCamera;
import ch.bildspur.realsense.type.ColorScheme;

public class Demo_RealSenseMultiCamera
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseCamera camera1;
	protected RealSenseCamera camera2;
	protected boolean cameraThreadBusy1 = false;
	protected boolean cameraThreadBusy2 = false;
	protected int CAMERA_NEAR = 180;
	protected int CAMERA_FAR = 2000;
	protected int CAMERA_W = 640;
	protected int CAMERA_H = 480;
	protected short[][] data1 = new short[CAMERA_H][CAMERA_W];
	protected short[][] data2 = new short[CAMERA_H][CAMERA_W];

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, CAMERA_H );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init camera
		camera1 = newCamera("851112060694");
		camera2 = newCamera("953122060282");
		debugCamerasConnected();
	}
	
	protected void debugCamerasConnected() {
		P.out("getDeviceCount", camera1.getDeviceCount());
		Device devices[] = camera1.getDevices();
		for (int i = 0; i < devices.length; i++) {
			Device device = devices[i];
			P.out("Device["+i+"] SerialNumber:", device.getSerialNumber());
		}
	}

	protected RealSenseCamera newCamera(String serialNumber) {
		RealSenseCamera cam = new RealSenseCamera(this);
		cam.enableColorStream();
		cam.enableDepthStream(CAMERA_W, CAMERA_H);
		cam.enableColorizer(ColorScheme.Cold);
		cam.start(serialNumber);
		return cam;
	}
	
	protected void drawApp() {
		p.background(0);

		// update cameras w/threading
		if(cameraThreadBusy1 == false) {
			new Thread(new Runnable() { public void run() {
				cameraThreadBusy1 = true;
				camera1.readFrames();
				data1 = camera1.getDepthData();
				cameraThreadBusy1 = false;
			}}).start();
		}
		if(camera2 != null) {
			if(cameraThreadBusy2 == false) {
				new Thread(new Runnable() { public void run() {
					cameraThreadBusy2 = true;
					camera2.readFrames();
					data2 = camera2.getDepthData();
					cameraThreadBusy2 = false;
				}}).start();
			}
		}
		
		// draw
		if(data1 != null) drawDepthPixels(data1, 0, 1);
		if(data2 != null) drawDepthPixels(data2, CAMERA_W, 2);
	}
	
	protected void drawDepthPixels(short[][] data, int offsetX, int camNum) {
		int numPixelsProcessed = 0;
		int pixelSize = 6;
		for ( int x = 0; x < CAMERA_W; x += pixelSize ) {
			for ( int y = 0; y < CAMERA_H; y += pixelSize ) {
			    // get intensity
			    float pixelDepth = data[y][CAMERA_W - 1 - x]; // : data[y][x];
				if( pixelDepth != 0 && pixelDepth > CAMERA_NEAR && pixelDepth < CAMERA_FAR) {
					p.fill(P.map(pixelDepth, CAMERA_NEAR, CAMERA_FAR, 255, 0));
					p.rect(offsetX + x, y, pixelSize, pixelSize);
					numPixelsProcessed++;
				}
			}
		}
		DebugView.setValue("numPixelsProcessed_"+camNum, numPixelsProcessed);
	}

}
