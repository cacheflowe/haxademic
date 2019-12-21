package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.BufferActivityMonitor;
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

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame () {
		// build activity monitor
		activityMonitor = new BufferActivityMonitor(32, 32, 10);

		// capture webcam frames
		WebCam.instance().setDelegate(this);
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
		ImageUtil.copyImageFlipH(frame, flippedCamera);

		// calculate activity monitor with new frame
		activityMonitor.update(flippedCamera);
		DebugView.setTexture("flippedCamera", flippedCamera);
	}

	public void drawApp() {
		// set up context
		p.background( 0 );
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);

		// show activity calculation and texture in debug panel
		DebugView.setValue("ACTIVITY", activityMonitor.activityAmp());
		DebugView.setTexture("activityMonitor.differenceBuffer", activityMonitor.differenceBuffer());

		// show diff buffer
		ImageUtil.cropFillCopyImage(activityMonitor.differenceBuffer(), p.g, true);
	}

}
