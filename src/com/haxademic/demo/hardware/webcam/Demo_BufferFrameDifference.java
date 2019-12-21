package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PImage;

public class Demo_BufferFrameDifference 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferFrameDifference bufferFrameDifference;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	public void setupFirstFrame () {
		WebCam.instance().setDelegate(this);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
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
		DebugView.setTexture("webcam", frame);
	}

}
