package com.haxademic.demo.draw.cv;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferFrameDifference;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorRotateFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BufferFrameDifference_ColorSplash 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferFrameDifference bufferFrameDifference;
	protected String diffFalloffBW = "diffFalloffBW";
	protected String diffThresh = "diffThresh";

	protected PGraphics colorCopy;
	protected PGraphics flattenPg;
	
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
		if(bufferFrameDifference != null) {
			// copy to buffer
			colorCopy.beginDraw();
//			colorCopy.blendMode(PBlendModes.LIGHTEST);
//			colorCopy.background(0);
			colorCopy.tint(FrameLoop.osc(0.1f, 127, 255), FrameLoop.osc(0.17f, 100, 255), FrameLoop.osc(0.15f, 100, 255));
			colorCopy.image(bufferFrameDifference.differenceBuffer(), 0, 0);
			colorCopy.endDraw();
			ColorRotateFilter.instance().setRotate(FrameLoop.count(0.01f) % 1f);
			ColorRotateFilter.instance().applyTo(colorCopy);
			
			// pre-blur flattened before copying new frame
			BlurProcessingFilter.instance().setBlurSize(10);
			BlurProcessingFilter.instance().setSigma(10);
			BlurProcessingFilter.instance().applyTo(flattenPg);

			// copy color frame on top of composite
			flattenPg.beginDraw();
			flattenPg.blendMode(PBlendModes.LIGHTEST);
			flattenPg.image(colorCopy, 0, 0);
			flattenPg.endDraw();
			BrightnessStepFilter.instance().setBrightnessStep(-3f/255f);
			BrightnessStepFilter.instance().applyTo(flattenPg);
			
			// zoom feedback
			RotateFilter.instance().setZoom(0.99f);
			RotateFilter.instance().applyTo(flattenPg);
			
			p.image(WebCam.instance().image(), 0, 0);
			p.blendMode(PBlendModes.ADD);
//			p.image(bufferFrameDifference.differenceBuffer(), 0, 0);
			p.image(flattenPg, 0, 0);
			p.blendMode(PBlendModes.BLEND);
		}
	}

	////////////////////////
	// IWebCamCallback
	////////////////////////
	
	public void newFrame(PImage frame) {
		// lazy init once we get a good webcam frame
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
			colorCopy = PG.newPG(frame.width, frame.height);
			flattenPg = PG.newPG(frame.width, frame.height);
			DebugView.setTexture("colorCopy", colorCopy);
			DebugView.setTexture("flattenPg", flattenPg);
		}
		
		// update diff analysis shader
		bufferFrameDifference.falloffBW(UI.value(diffFalloffBW));
		bufferFrameDifference.diffThresh(UI.value(diffThresh));
		bufferFrameDifference.update(frame);
		
		// add cam to debug view
		DebugView.setTexture("webcam", frame);
	}

}
