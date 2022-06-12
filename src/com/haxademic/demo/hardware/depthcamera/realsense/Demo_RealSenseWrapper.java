package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.render.FrameLoop;

public class Demo_RealSenseWrapper
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper realSenseWrapper;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}

	protected void firstFrame() {
		RealSenseWrapper.METERS_FAR_THRESH = 3;
		realSenseWrapper = new RealSenseWrapper(p, true, true);
		realSenseWrapper.setMirror(false);
	}

	protected void drawApp() {
		p.background(0);
		p.noStroke();
		realSenseWrapper.update();
		if(realSenseWrapper.getRgbImage() != null) {
			p.image(realSenseWrapper.getRgbImage(), 0, 0);
			p.image(realSenseWrapper.getDepthImage(), 0, realSenseWrapper.getRgbImage().height);
		} else {
			p.image(realSenseWrapper.getDepthImage(), 0, 0);
		}
		p.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(p, 0.25f);
		p.image(realSenseWrapper.getDepthImage(), 0, 0);
		PG.resetPImageAlpha(p);
		p.blendMode(PBlendModes.BLEND);
		drawDepthPixels();
		if(FrameLoop.frameModHours(1)) P.out("Still running:", DebugView.uptimeStr());
	}
	
	protected void drawDepthPixels() {
		p.pushMatrix();
		p.translate(realSenseWrapper.getRgbImage().width, 0);
		
		int numPixelsProcessed = 0;
		int pixelSize = 6;
		int depthFar = P.round(RealSenseWrapper.METERS_FAR_THRESH * 1000);
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
