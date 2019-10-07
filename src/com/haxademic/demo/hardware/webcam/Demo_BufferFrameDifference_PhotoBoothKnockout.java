package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PImage;

public class Demo_BufferFrameDifference_PhotoBoothKnockout 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage bufferCamera;
	protected PImage bufferBg;
	protected BufferFrameDifference bufferFrameDifference;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 800);
		p.appConfig.setProperty(AppSettings.HEIGHT, 600);
	}
	
	protected void setupFirstFrame() {
		// webcam callback
		WebCam.instance().setDelegate(this);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') captureBackground();
	}
	
	protected void captureBackground() {
		ImageUtil.copyImage(bufferCamera, bufferBg);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// lazy init frame difference object
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
			bufferFrameDifference.diffThresh(0.03f);
			bufferFrameDifference.falloffBW(0.7f);
			bufferCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
			bufferBg = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
		}
		
		// flip webcam frame & update frame difference object
		ImageUtil.copyImage(frame, bufferCamera);
		ImageUtil.flipH(bufferCamera);
		bufferFrameDifference.update(bufferBg, bufferCamera);
		
		// blur to smooth out displacement
//		BlurHFilter.instance(p).setBlurByPercent(1f, bufferFrameDifference.differenceBuffer().width);
//		BlurHFilter.instance(p).applyTo(bufferFrameDifference.differenceBuffer());
//		BlurVFilter.instance(p).setBlurByPercent(1f, bufferFrameDifference.differenceBuffer().height);
//		BlurVFilter.instance(p).applyTo(bufferFrameDifference.differenceBuffer());
		
		// debug view
		p.debugView.setTexture("bufferCamera", bufferCamera);
		p.debugView.setTexture("differenceBuffer", bufferFrameDifference.differenceBuffer());
	}

	public void drawApp() {
		p.background(0);
		
		// update shader & draw mesh
		if(bufferFrameDifference != null) {
			bufferCamera.mask(bufferFrameDifference.differenceBuffer());
			ImageUtil.drawImageCropFill(bufferCamera, p.g, true);
			// ImageUtil.cropFillCopyImage(bufferFrameDifference.differenceBuffer(), p.g, true);
		}
	}
		
}