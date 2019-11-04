package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;

import ch.bildspur.realsense.RealSenseCamera;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_RealSenseCameraTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	| width | height | fps                         | depth stream | color stream |
	|-------|--------|-----------------------------|--------------|--------------|
	| 424   | 240    | `6`, `15`, `30`, `60`       | ✅            | ✅            |
	| 480   | 270    | `6`, `15`, `30`, `60`, `90` | ✅            | ❌            |
	| 640   | 480    | `6`, `15`, `30`, `60`       | ✅            | ✅            |
	| 640   | 480    | `90`                        | ✅            | ❌            |
	| 848   | 480    | `6`, `15`, `30`, `60`       | ✅            | ✅            | 848/480/30 works but runs at 60fps/depth-only
	| 848   | 480    | `90`                        | ✅            | ❌            |
	| 960   | 540    | `6`, `15`, `30`, `60`       | ❌            | ✅            |
	| 1280  | 720    | `30`                        | ✅            | ✅            | 1280/720/30 works but runs at 15fps/rgb+depth and 20fps/rgb
	| 1280  | 800    | `6`, `15`, `30`, `60`, `90` | ❌            | ❌            |
	| 1920  | 1080   | `6`, `15`, `30`             | ❌            | ✅            | 1920/1080/30 works but runs at 10fps/rgb-only
	*/
	
	// TODO: 
	// * Test all reslutions and framerates
	
	protected RealSenseCamera camera;
	protected int CAMERA_W = 848;
	protected int CAMERA_H = 480;
	protected int CAMERA_FPS = 60;
	protected int CAMERA_NEAR = 180;
	protected int CAMERA_FAR = 5000;
	protected boolean DEPTH_ACTIVE = true;
	protected boolean RGB_ACTIVE = true;
	protected boolean MIRROR = true;
	protected PGraphics mirrorRGB;
	protected PGraphics mirrorDepth;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}


	protected void setupFirstFrame() {
		camera = new RealSenseCamera(this);
		camera.start(CAMERA_W, CAMERA_H, CAMERA_FPS, DEPTH_ACTIVE, RGB_ACTIVE);
		p.debugView.setTexture("camera.getDepthImage", camera.getDepthImage());
		
		mirrorRGB = PG.newPG(CAMERA_W, CAMERA_H);
		mirrorDepth = PG.newPG(CAMERA_W, CAMERA_H);
	}

	public void drawApp() {
		p.background(0);
		
		MIRROR = true;
		CAMERA_FAR = 10000;

		// TODO: thread this!
//		new Thread(new Runnable() { public void run() {
			camera.readFrames();
//		}}).start();

		// show depth image
		fill(255);
		noStroke();
		if(DEPTH_ACTIVE) {
			camera.createDepthImage(CAMERA_NEAR, CAMERA_FAR); // min/max depth
			if(MIRROR) ImageUtil.copyImageFlipH(camera.getDepthImage(), mirrorDepth);
			image(getDepthImage(), 0, 0);
		}
		
		// show color image
		if(RGB_ACTIVE) {
			if(MIRROR) ImageUtil.copyImageFlipH(camera.getColorImage(), mirrorRGB);
			PG.setPImageAlpha(p, 0.6f);
//			p.image(getRGBImage(), 30, 0);
			PG.setPImageAlpha(p, 1f);
		}

		// show depth pixels
		drawDepthPixels();
	}
	
	public int getDepth(int x, int y) {
		return camera.getDepth((MIRROR) ? CAMERA_W - x : x, y);
	}
	
	public PImage getRGBImage() {
		return (MIRROR) ? mirrorRGB : camera.getColorImage();
	}
	
	public PImage getDepthImage() {
		return (MIRROR) ? mirrorDepth : camera.getDepthImage();
	}
	
	protected void drawDepthPixels() {
//		if(RGB_ACTIVE) getRGBImage().loadPixels();
		int numPixelsProcessed = 0;
		int pixelSize = 6;
		for ( int x = 0; x < CAMERA_W; x += pixelSize ) {
			for ( int y = 0; y < CAMERA_H; y += pixelSize ) {
				int pixelDepth = getDepth(x, y);
				if( pixelDepth != 0 && pixelDepth > CAMERA_NEAR && pixelDepth < CAMERA_FAR ) {
//				if( pixelDepth == 0 || (pixelDepth > CAMERA_NEAR && pixelDepth < CAMERA_FAR)) {
					p.pushMatrix();
//					if(RGB_ACTIVE) {
//						p.fill(ImageUtil.getPixelColor(getRGBImage(), x - 30, y));
//					} else {
						p.fill(P.map(pixelDepth, CAMERA_NEAR, CAMERA_FAR, 255, 0));
//					}
					p.rect(x, y, pixelSize, pixelSize);
					p.popMatrix();
					numPixelsProcessed++;
				}
			}
		}
		p.debugView.setValue("numPixelsProcessed", numPixelsProcessed);
	}

}
