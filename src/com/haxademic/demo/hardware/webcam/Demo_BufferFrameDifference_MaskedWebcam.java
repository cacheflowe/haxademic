package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BufferFrameDifference;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_BufferFrameDifference_MaskedWebcam 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected BufferFrameDifference bufferFrameDifference;
	
	protected PGraphics diffBufferSmoothed;
	protected PGraphics knockoutWebCam;
	protected SimplexNoiseTexture simplexNoise;
	protected PShader feedbackShader;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
	}
		
	public void setupFirstFrame () {
		WebCam.instance().setDelegate(this);
		feedbackShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/feedback-map.glsl"));
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
		simplexNoise.update(
				P.map(p.mousePercentX(), 0, 1, 0.01f, 15f), 
				P.map(p.mousePercentY(), 0, 1, -P.TWO_PI, P.TWO_PI), 
				50f * P.cos(p.frameCount / 5000f), 
				50f * P.sin(p.frameCount / 5000f));
		
		// apply feedback shader
		feedbackShader.set("map", simplexNoise.texture());
		feedbackShader.set("amp", 0.001f); //P.map(p.mousePercentY(), 0, 1, 0.0001f, 0.01f) );
		for (int i = 0; i < 2; i++) p.filter(feedbackShader);
		
		// darken slightly
		BrightnessStepFilter.instance(p).setBrightnessStep(-2f/255f);
		BrightnessStepFilter.instance(p).applyTo(p);
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
		bufferFrameDifference.falloffBW(0.25f);
		bufferFrameDifference.diffThresh(0.05f);
		bufferFrameDifference.update(frame);
		
		// copy to diff buffer smoothed version
		ImageUtil.copyImageFlipH(bufferFrameDifference.differenceBuffer(), diffBufferSmoothed);
		ThresholdFilter.instance(p).setCutoff(0.25f);
		ThresholdFilter.instance(p).applyTo(diffBufferSmoothed);
		BlurProcessingFilter.instance(p).setBlurSize(10);
		for(int i=0; i < 5; i++) BlurProcessingFilter.instance(p).applyTo(diffBufferSmoothed);

		
		// debug webcam view
		p.debugView.setTexture("webcam", frame);
		p.debugView.setTexture("diffBufferSmoothed", diffBufferSmoothed);
	}

}
