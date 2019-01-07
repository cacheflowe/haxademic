package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PImage;

public class Demo_BufferFrameDifference 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferFrameDifference bufferFrameDifference;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 3 );
	}
		
	public void setupFirstFrame () {
		p.webCamWrapper.setDelegate(this);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		DrawUtil.setDrawCenter(p);
		DrawUtil.setCenterScreen(p);
		
		// draw difference to screen
		if(bufferFrameDifference != null) p.image(bufferFrameDifference.differenceBuffer(), 0, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
		}
		bufferFrameDifference.update(frame);
		// debug view
		p.debugView.setTexture(p.webCamWrapper.getImage());
	}

}
