package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_RealSenseWrapper
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper realSenseWrapper;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1280 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}

	protected void firstFrame() {
		P.store.addListener(this);
		
		// bild realsense wrapper
		RealSenseWrapper.METERS_FAR_THRESH = 3;
		// RealSenseWrapper.setSmallStream();
		// RealSenseWrapper.setTinyStream();
		RealSenseWrapper.setTinyStreamSuperFast();
		realSenseWrapper = new RealSenseWrapper(p, true, true);
//		realSenseWrapper.setThreaded(false);
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
		
		DebugView.setTexture("getRgbImage()", realSenseWrapper.getRgbImage());
		DebugView.setTexture("getDepthImage()", realSenseWrapper.getDepthImage());
	}
	
	protected void drawDepthPixels() {
		p.pushMatrix();
		p.translate(realSenseWrapper.getDepthImage().width, 0);
		
		int numPixelsProcessed = 0;
		int pixelSize = 10;
		int depthFar = P.round(RealSenseWrapper.METERS_FAR_THRESH * 1000);
		for ( int x = 0; x < RealSenseWrapper.DEPTH_W; x += pixelSize ) {
			for ( int y = 0; y < RealSenseWrapper.DEPTH_H; y += pixelSize ) {
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
	
	// AppStore listeners

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
}
