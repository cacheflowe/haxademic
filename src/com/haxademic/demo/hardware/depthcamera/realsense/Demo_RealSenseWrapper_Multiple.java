package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.render.FrameLoop;

public class Demo_RealSenseWrapper_Multiple
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper camera1;
	protected RealSenseWrapper camera2;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}

	protected void firstFrame() {
		RealSenseWrapper.METERS_FAR_THRESH = 3;
		camera1 = new RealSenseWrapper(p, true, true, "851112060694");
		camera2 = new RealSenseWrapper(p, true, true, "953122060282");
	}

	protected void drawApp() {
		// clear screen
		p.background(0);
		p.noStroke();
		
		// update cameras
		camera1.update();
		camera2.update();
		
		// draw depth grid
		drawDepthPixels(camera1, 0, 0);
		drawDepthPixels(camera2, 640, 0);

		// print out uptime
		if(FrameLoop.frameModHours(1)) P.out("Still running:", DebugView.uptimeStr());
	}
	
	protected void drawDepthPixels(RealSenseWrapper camera, int cameraX, int cameraY) {
		p.pushMatrix();
		p.translate(cameraX, cameraY);
		
		int numPixelsProcessed = 0;
		int pixelSize = 6;
		int depthFar = P.round(RealSenseWrapper.METERS_FAR_THRESH * 1000);
		for ( int x = 0; x < RealSenseWrapper.CAMERA_W; x += pixelSize ) {
			for ( int y = 0; y < RealSenseWrapper.CAMERA_H; y += pixelSize ) {
			    // get intensity
			    float pixelDepth = camera.getDepthAt(x, y);
			    if(pixelDepth != 0 && pixelDepth < depthFar) {
				    p.fill(P.map(pixelDepth, 0, depthFar, 255, 0));
				    p.rect(x, y, pixelSize, pixelSize);
				    numPixelsProcessed++;
			    }
			}
		}
		
		p.popMatrix();
		DebugView.setValue("numPixelsProcessed", numPixelsProcessed);
	}
	
}
