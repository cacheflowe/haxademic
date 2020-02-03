package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_BufferFrameDifference 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferFrameDifference bufferFrameDifference;
	protected String diffFalloffBW = "diffFalloffBW";
	protected String diffThresh = "diffThresh";

	
	protected void config() {
		Config.setAppSize(1280, 720);
	}
		
	protected void firstFrame () {
		// load webcam
		WebCam.instance().setDelegate(this);
		
		// add UI
		UI.addTitle("BufferFrameDifference Config");
		UI.addSlider(diffFalloffBW, 0.1f, 0, 1, 0.001f, false);
		UI.addSlider(diffThresh, 0.025f, 0, 1, 0.001f, false);
		
		// show controls by default
		UI.active(true);
		DebugView.active(true);
	}

	protected void drawApp() {
		// set up context
		p.background(0);
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
		// draw difference to screen
		if(bufferFrameDifference != null) p.image(bufferFrameDifference.differenceBuffer(), 0, 0);
	}

	////////////////////////
	// IWebCamCallback
	////////////////////////
	
	public void newFrame(PImage frame) {
		// lazy init once we get a good webcam frame
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
		}
		
		// update diff analysis shader
		bufferFrameDifference.falloffBW(UI.value(diffFalloffBW));
		bufferFrameDifference.diffThresh(UI.value(diffThresh));
		bufferFrameDifference.update(frame);
		
		// add cam to debug view
		DebugView.setTexture("webcam", frame);
	}

}
