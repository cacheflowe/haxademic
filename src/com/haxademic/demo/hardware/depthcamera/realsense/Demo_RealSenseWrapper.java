package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;

public class Demo_RealSenseWrapper
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper realSenseWrapper;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}


	protected void firstFrame() {
		realSenseWrapper = new RealSenseWrapper(p, true, true);
	}

	protected void drawApp() {
		p.background(0);
		p.noStroke();
		realSenseWrapper.update();
		p.image(realSenseWrapper.getRgbImage(), 0, 0);
		p.image(realSenseWrapper.getDepthImage(), 0, realSenseWrapper.getRgbImage().height);
		drawDepthPixels();
	}
	
	protected void drawDepthPixels() {
		p.pushMatrix();
		p.translate(realSenseWrapper.getRgbImage().width, 0);
		
		int numPixelsProcessed = 0;
		int pixelSize = 6;
		int depthFar = 1000;
		for ( int x = 0; x < RealSenseWrapper.CAMERA_W; x += pixelSize ) {
			for ( int y = 0; y < RealSenseWrapper.CAMERA_H; y += pixelSize ) {
			    // get intensity
			    float pixelDepth = realSenseWrapper.getDepthAt(x, y);
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
