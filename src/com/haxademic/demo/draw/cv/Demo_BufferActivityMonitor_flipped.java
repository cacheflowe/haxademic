package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferActivityMonitor;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BufferActivityMonitor_flipped
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected BufferActivityMonitor activityMonitor;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 1280 );
		Config.setProperty(AppSettings.HEIGHT, 720 );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// build activity monitor
		activityMonitor = new BufferActivityMonitor(32, 32, 10);

		// capture webcam frames
		WebCam.instance().setDelegate(this);
	}

	protected void drawApp() {
		// set up context
		p.background( 0 );

		// show activity calculation and texture in debug panel
		DebugView.setValue("ACTIVITY", activityMonitor.activityAmp());
		DebugView.setTexture("activityMonitor.differenceBuffer", activityMonitor.differenceBuffer());

		// show diff buffer
		ImageUtil.cropFillCopyImage(activityMonitor.differenceBuffer(), p.g, false);
		
		// draw activity value
		p.fill(0, 255, 0);
		p.text("Activity: " + activityMonitor.activityAmp(), 20, p.height - 40);
		p.rect(20, p.height - 30, activityMonitor.activityAmp() * p.width, 10);
	}

	/////////////////////
	// IWebCamCallback
	/////////////////////
	
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = PG.newPG2DFast(frame.width, frame.height);
		ImageUtil.copyImageFlipH(frame, flippedCamera);
		
		// calculate activity monitor with new frame
		activityMonitor.update(flippedCamera);
		DebugView.setTexture("flippedCamera", flippedCamera);
	}
}
