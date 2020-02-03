package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

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
	
	protected PShape template;

	protected void config() {
		Config.setAppSize(1280, 720);
//		Config.setProperty(AppSettings.PG_WIDTH, 3438 );
//		Config.setProperty(AppSettings.PG_HEIGHT, 1080 );
		Config.setProperty(AppSettings.FULLSCREEN, false);
//		Config.setProperty(AppSettings.SCREEN_X, 0);
//		Config.setProperty(AppSettings.SCREEN_Y, 0);
	}
		
	protected void firstFrame () {
//		template = p.loadShape( FileUtil.getPath("images/_sketch/clocktower/clocktower.svg"));

		// init webcam
		WebCam.instance().setDelegate(this);
		
		// ui
		UI.addSlider(mapZoom, 2, 0.1f, 15, 0.1f, false);
		UI.addSlider(mapRot, 0, 0, P.TWO_PI, 0.01f, false);
		
		UI.addSlider(feedbackIters, 3, 0, 5, 1, false);
		UI.addSlider(feedbackAmp, 0.0006f, 0.00001f, 0.005f, 0.00001f, false);
		UI.addSlider(feedbackBrightStep, -0.005f, -0.3f, 0.3f, 0.0001f, false);
		UI.addSlider(feedbackAlphaStep, 0f, -0.3f, 0.3f, 0.0001f, false);
		UI.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, false);
		
		UI.addSlider(diffFalloffBW, 0.7f, 0, 1, 0.01f, false);
		UI.addSlider(diffThresh, 0.1f, 0, 1, 0.001f, false);
		UI.addSlider(diffSmoothThresh, 0.66f, 0, 1, 0.001f, false);
	}

	protected void drawApp() {
		// set up context
		if(p.frameCount < 10) p.background(0);	// clear screen up front, but then stop
		PG.setDrawCenter(p);
		PG.setCenterScreen(p);
		
		// set difference mask on webcam image
		if(knockoutWebCam != null) {
			updateFeedback();
			knockoutWebCam.mask(diffBufferSmoothed);
			pg.beginDraw();
			ImageUtil.drawImageCropFill(knockoutWebCam, pg, true);
//			pg.image(knockoutWebCam, 0, 0);
			if(template != null) pg.shape(template, 0, 0);
			pg.endDraw();
		}
		
		p.image(pg, 0, 0);
		ImageUtil.cropFillCopyImage(pg, p.g, false);

	}
	
	protected void updateFeedback() {
		// update feedback noise map
		simplexNoise.update(UI.value(mapZoom), UI.value(mapRot), 0, 0);
		DebugView.setTexture("simplexNoise.texture()", simplexNoise.texture());

		// apply feedback shader
		FeedbackMapFilter.instance(P.p).setMap(simplexNoise.texture());
		FeedbackMapFilter.instance(p).setAmp(UI.value(feedbackAmp));
		FeedbackMapFilter.instance(p).setBrightnessStep(UI.value(feedbackBrightStep));
		FeedbackMapFilter.instance(p).setAlphaStep(UI.value(feedbackAlphaStep));
		FeedbackMapFilter.instance(p).setRadiansStart(p.frameCount/20f); // UI.value(feedbackRadiansStart));
		FeedbackMapFilter.instance(p).setRadiansRange(UI.value(feedbackRadiansRange));
		for (int i = 0; i < UI.valueInt(feedbackIters); i++) FeedbackMapFilter.instance(P.p).applyTo(pg);
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
		bufferFrameDifference.falloffBW(UI.value(diffFalloffBW));
		bufferFrameDifference.diffThresh(UI.value(diffThresh));
		bufferFrameDifference.update(frame);
		
		// copy to diff buffer smoothed version
		ImageUtil.copyImageFlipH(bufferFrameDifference.differenceBuffer(), diffBufferSmoothed);
		ThresholdFilter.instance(p).setCutoff(UI.value(diffSmoothThresh));
		ThresholdFilter.instance(p).applyTo(diffBufferSmoothed);
		BlurProcessingFilter.instance(p).setBlurSize(10);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance(p).applyTo(diffBufferSmoothed);
		
		// debug webcam view
		DebugView.setTexture("webcam", frame);
		DebugView.setTexture("diffBufferSmoothed", diffBufferSmoothed);
	}

}
