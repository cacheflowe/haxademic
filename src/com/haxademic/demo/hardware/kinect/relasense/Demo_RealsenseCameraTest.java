package com.haxademic.demo.hardware.kinect.relasense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

import ch.bildspur.realsense.RealSenseCamera;

public class Demo_RealsenseCameraTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected RealSenseCamera camera;
	protected int CAMERA_W = 640;
	protected int CAMERA_H = 480;
	protected int CAMERA_NEAR = 180;
	protected int CAMERA_FAR = 5000;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, false );
	}


	protected void setupFirstFrame() {
		camera = new RealSenseCamera(this);
		camera.start(CAMERA_W, CAMERA_H, 30, true, true);
		p.debugView.setTexture(camera.getDepthImage());
	}

	public void drawApp() {
		p.background(0);

		// TODO: thread this!
		camera.readFrames();
//		camera.createDepthImage(CAMERA_NEAR, CAMERA_FAR); // min/max depth

		// create grayscale image form depth buffer
		// min and max depth

		// show color image
		fill(255);
		noStroke();
//		image(camera.getDepthImage(), 0, 0);
//		PG.setPImageAlpha(p, 0.6f);
		image(camera.getColorImage(), 0, 0);
//		PG.setPImageAlpha(p, 1f);

		fill(0, 255, 255);
		textSize(20);
		textAlign(RIGHT, BOTTOM);
		text(camera.getDepth(P.min(mouseX, CAMERA_W-1), P.min(mouseY, CAMERA_H-1)), mouseX, mouseY);


		int numPixelsProcessed = 0;
		int pixelSize = 8;
		for ( int x = 0; x < CAMERA_W; x += pixelSize ) {
			for ( int y = 0; y < CAMERA_H; y += pixelSize ) {
				int pixelDepth = camera.getDepth( x, y );
				if( pixelDepth != 0 && pixelDepth > CAMERA_NEAR && pixelDepth < CAMERA_FAR ) {
					p.pushMatrix();
//					p.translate(0, 0, -pixelDepth/depthDiv);
					p.fill(P.map(pixelDepth, CAMERA_NEAR, CAMERA_FAR, 255, 0));
					p.rect(x, y, pixelSize, pixelSize);
					p.popMatrix();
					numPixelsProcessed++;
				}
			}
		}
		p.debugView.setValue("numPixelsProcessed", numPixelsProcessed);


	}

}
