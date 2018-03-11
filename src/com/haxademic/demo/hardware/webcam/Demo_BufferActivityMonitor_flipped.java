package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.BufferActivityMonitor;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BufferActivityMonitor_flipped 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected BufferActivityMonitor activityMonitor;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 ); // 18
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		// build activity monitor
		activityMonitor = new BufferActivityMonitor();
		
		// capture webcam frames
		p.webCamWrapper.setDelegate(this);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// p.webCamWrapper.getImage()
		// lazy-init flipped camera buffer
		if(flippedCamera == null) flippedCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
		flippedCamera.copy(frame, 0, 0, frame.width, frame.height, frame.width, 0, -frame.width, frame.height);
		
		// calculate activity monitor with new frame
		activityMonitor.update(flippedCamera);
		p.debugView.setTexture(flippedCamera);
	}

	public void drawApp() {
		// set up context
		p.background( 0 );
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		// show activity calculation and texture in debug panel
		p.debugView.setValue("ACTIVITY", activityMonitor.activityAmp());
		p.debugView.setTexture(activityMonitor.differenceBuffer());

		// show diff buffer
		ImageUtil.cropFillCopyImage(activityMonitor.differenceBuffer(), p.g, true);
	}
	
}
