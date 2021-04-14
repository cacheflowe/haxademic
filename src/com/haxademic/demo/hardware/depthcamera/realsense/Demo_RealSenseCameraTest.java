package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.ui.UI;

import ch.bildspur.realsense.RealSenseCamera;
import ch.bildspur.realsense.type.ColorScheme;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_RealSenseCameraTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	 * Old resolution.framerate chart
	| width | height | fps                         | depth stream | color stream |
	|-------|--------|-----------------------------|--------------|--------------|
	| 424   | 240    | `6`, `15`, `30`, `60`       | âœ…            | âœ…            |
	| 480   | 270    | `6`, `15`, `30`, `60`, `90` | âœ…            | â�Œ            |
	| 640   | 480    | `6`, `15`, `30`, `60`       | âœ…            | âœ…            |
	| 640   | 480    | `90`                        | âœ…            | â�Œ            |
	| 848   | 480    | `6`, `15`, `30`, `60`       | âœ…            | âœ…            | 848/480/30 works but runs at 60fps/depth-only
	| 848   | 480    | `90`                        | âœ…            | â�Œ            |
	| 960   | 540    | `6`, `15`, `30`, `60`       | â�Œ            | âœ…            |
	| 1280  | 720    | `30`                        | âœ…            | âœ…            | 1280/720/30 works but runs at 15fps/rgb+depth and 20fps/rgb
	| 1280  | 800    | `6`, `15`, `30`, `60`, `90` | â�Œ            | â�Œ            |
	| 1920  | 1080   | `6`, `15`, `30`             | â�Œ            | âœ…            | 1920/1080/30 works but runs at 10fps/rgb-only
	*/
	
	protected RealSenseCamera camera;
	protected int CAMERA_W = 1280;
	protected int CAMERA_H = 720;
	protected int CAMERA_NEAR = 180;
	protected String CAMERA_FAR = "CAMERA_FAR";
	protected String MIRROR = "MIRROR";
	protected boolean DEPTH_ACTIVE = true;
	protected boolean RGB_ACTIVE = true;
	protected boolean IR_ACTIVE = false;
	protected PGraphics mirrorRGB;
	protected PGraphics mirrorDepth;
	protected short[][] data = new short[CAMERA_H][CAMERA_W];
	protected boolean cameraThreadBusy = false;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init camera
		camera = new RealSenseCamera(this);
		camera.enableColorStream(CAMERA_W, CAMERA_H);
		camera.enableDepthStream(CAMERA_W, CAMERA_H);
		camera.enableColorizer(ColorScheme.Cold);
//		camera.enableIRStream(640, 480, 30);
		camera.enableAlign();
		camera.start();
		
//		DebugView.setTexture("camera.getIRImage", camera.getIRImage());
		// init mirrored buffers
		mirrorRGB = PG.newPG(CAMERA_W, CAMERA_H);
		mirrorDepth = PG.newPG(CAMERA_W, CAMERA_H);
		
		// UI
		UI.addTitle("Realsense settings");
		UI.addSlider(CAMERA_FAR, 5000, 400, 10000, 10, false);
		UI.addToggle(MIRROR, false, false);
	}

	protected void drawApp() {
		p.background(0);
		
		// threaded reading!
		if(cameraThreadBusy == false) {
			new Thread(new Runnable() { public void run() {
				cameraThreadBusy = true;
				camera.readFrames();
				if(DEPTH_ACTIVE) data = camera.getDepthData();
				cameraThreadBusy = false;
			}}).start();
		}

		// show depth image
		fill(255);
		noStroke();
		if(DEPTH_ACTIVE) {
//			camera.createDepthImage(CAMERA_NEAR, CAMERA_FAR); // min/max depth
			if(UI.valueToggle(MIRROR)) ImageUtil.copyImageFlipH(camera.getDepthImage(), mirrorDepth);
//			image(getDepthImage(), 0, 0);
			DebugView.setTexture("camera.getDepthImage", getDepthImage());
		}
		
		// show color image
		if(RGB_ACTIVE) {
			if(UI.valueToggle(MIRROR)) ImageUtil.copyImageFlipH(camera.getColorImage(), mirrorRGB);
			PG.setPImageAlpha(p, 0.6f);
//			p.image(getRGBImage(), 0, 0);
			PG.setPImageAlpha(p, 1f);
			DebugView.setTexture("camera.getColorImage", getRGBImage());
		}

		// show color image
		if(IR_ACTIVE) {
//			if(UI.valueToggle(MIRROR)) ImageUtil.copyImageFlipH(camera.getIRImage(), mirrorRGB);
			PG.setPImageAlpha(p, 0.6f);
//			p.image(getRGBImage(), 30, 0);
			PG.setPImageAlpha(p, 1f);
		}

		// show depth pixels
		drawDepthPixels();
	}
	
	public float getDepth(int x, int y) {
		return Math.round(camera.getDistance((UI.valueToggle(MIRROR)) ? CAMERA_W - x : x, y) * 1000f);
	}
	
	public PImage getRGBImage() {
		return (UI.valueToggle(MIRROR)) ? mirrorRGB : camera.getColorImage();
	}
	
	public PImage getDepthImage() {
		return (UI.valueToggle(MIRROR)) ? mirrorDepth : camera.getDepthImage();
	}
	
	protected void drawDepthPixels() {
		if(RGB_ACTIVE) getRGBImage().loadPixels();
		int numPixelsProcessed = 0;
		int pixelSize = 6;
		for ( int x = 0; x < CAMERA_W; x += pixelSize ) {
			for ( int y = 0; y < CAMERA_H; y += pixelSize ) {
			    // get intensity
			    float pixelDepth = (UI.valueToggle(MIRROR)) ? data[y][CAMERA_W - 1 - x] : data[y][x];
				if( pixelDepth != 0 && pixelDepth > CAMERA_NEAR && pixelDepth < UI.value(CAMERA_FAR)) {
//				if( pixelDepth == 0 || (pixelDepth > CAMERA_NEAR && pixelDepth < CAMERA_FAR)) {
//					P.out(pixelDepth);
//					if(RGB_ACTIVE) {
//					} else {
						p.fill(P.map(pixelDepth, CAMERA_NEAR, UI.value(CAMERA_FAR), 255, 0));
//						p.fill(ImageUtil.getPixelColor(getRGBImage(), x - 30, y));
//					}
					p.rect(x, y, pixelSize, pixelSize);
					numPixelsProcessed++;
				}
			}
		}
		DebugView.setValue("numPixelsProcessed", numPixelsProcessed);
	}

}
