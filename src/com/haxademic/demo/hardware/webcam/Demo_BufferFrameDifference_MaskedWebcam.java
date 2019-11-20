package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BufferFrameDifference_MaskedWebcam 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferFrameDifference bufferFrameDifference;
	
	protected PGraphics diffBufferSmoothed;
	protected PGraphics knockoutWebCam;
	protected SimplexNoiseTexture simplexNoise;

	protected String mapZoom = "mapZoom";
	protected String mapRot = "mapRot";
	
	protected String feedbackIters = "feedbackIters";
	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackBrightStep = "feedbackBrightStep";
	protected String feedbackAlphaStep = "feedbackAlphaStep";
	protected String feedbackRadiansStart = "feedbackRadiansStart";
	protected String feedbackRadiansRange = "feedbackRadiansRange";
	
	protected String diffFalloffBW = "diffFalloffBW";
	protected String diffThresh = "diffThresh";
	protected String diffSmoothThresh = "diffSmoothThresh";
	

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	public void setupFirstFrame () {
		// init webcam
		WebCam.instance().setDelegate(this);
		
		// ui
		p.ui.addSlider(mapZoom, 2, 0.1f, 15, 0.1f, false);
		p.ui.addSlider(mapRot, 0, 0, P.TWO_PI, 0.01f, false);
		
		p.ui.addSlider(feedbackIters, 3, 0, 5, 1, false);
		p.ui.addSlider(feedbackAmp, 0.0006f, 0.00001f, 0.005f, 0.00001f, false);
		p.ui.addSlider(feedbackBrightStep, -0.005f, -0.3f, 0.3f, 0.0001f, false);
		p.ui.addSlider(feedbackAlphaStep, 0f, -0.3f, 0.3f, 0.0001f, false);
		p.ui.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, false);
		p.ui.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, false);
		
		p.ui.addSlider(diffFalloffBW, 0.7f, 0, 1, 0.01f, false);
		p.ui.addSlider(diffThresh, 0.1f, 0, 1, 0.001f, false);
		p.ui.addSlider(diffSmoothThresh, 0.66f, 0, 1, 0.001f, false);
	}

	public void drawApp() {
		// set up context
		if(p.frameCount < 10) p.background(0);	// clear screen up front, but then stop
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
		// set difference mask on webcam image
		if(knockoutWebCam != null) {
			updateMapPerlin();
			knockoutWebCam.mask(diffBufferSmoothed);
			p.image(knockoutWebCam, 0, 0);
		}
	}
	
	protected void updateMapPerlin() {
		// update feedback noise map
		simplexNoise.update(p.ui.value(mapZoom), p.ui.value(mapRot), 0, 0);
		
		// apply feedback shader
		FeedbackMapFilter.instance(P.p).setMap(simplexNoise.texture());
		FeedbackMapFilter.instance(p).setAmp(p.ui.value(feedbackAmp));
		FeedbackMapFilter.instance(p).setBrightnessStep(p.ui.value(feedbackBrightStep));
		FeedbackMapFilter.instance(p).setAlphaStep(p.ui.value(feedbackAlphaStep));
		FeedbackMapFilter.instance(p).setRadiansStart(p.frameCount/10f); // p.ui.value(feedbackRadiansStart));
		FeedbackMapFilter.instance(p).setRadiansRange(p.ui.value(feedbackRadiansRange));
		for (int i = 0; i < p.ui.valueInt(feedbackIters); i++) FeedbackMapFilter.instance(P.p).applyTo(p.g);
	}

	@Override
	public void newFrame(PImage frame) {
		// lazy init graphics based on webcam size
		if(bufferFrameDifference == null) {
			bufferFrameDifference = new BufferFrameDifference(frame.width, frame.height);
			diffBufferSmoothed = PG.newPG(frame.width, frame.height);
			knockoutWebCam = PG.newPG(frame.width, frame.height);
			simplexNoise = new SimplexNoiseTexture(128, 128);
		}
		
		// copy webcam to buffer
		ImageUtil.copyImageFlipH(frame, knockoutWebCam);
		
		// update different buffer on last webcam frame
		bufferFrameDifference.falloffBW(p.ui.value(diffFalloffBW));
		bufferFrameDifference.diffThresh(p.ui.value(diffThresh));
		bufferFrameDifference.update(frame);
		
		// copy to diff buffer smoothed version
		ImageUtil.copyImageFlipH(bufferFrameDifference.differenceBuffer(), diffBufferSmoothed);
		ThresholdFilter.instance(p).setCutoff(p.ui.value(diffSmoothThresh));
		ThresholdFilter.instance(p).applyTo(diffBufferSmoothed);
		BlurProcessingFilter.instance(p).setBlurSize(10);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance(p).applyTo(diffBufferSmoothed);
		
		// debug webcam view
		p.debugView.setTexture("webcam", frame);
		p.debugView.setTexture("diffBufferSmoothed", diffBufferSmoothed);
	}

}
