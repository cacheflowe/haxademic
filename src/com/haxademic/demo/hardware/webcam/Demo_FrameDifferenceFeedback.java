package com.haxademic.demo.hardware.webcam;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.shaders.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_FrameDifferenceFeedback 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics flippedCamera;
	protected PGraphics prevFrame;
	protected PGraphics curFrame;
	protected PGraphics differenceBuffer;
	protected PGraphics feedbackSeedBuffer;
	protected PGraphics feedbackFinalBuffer;
	protected PShader differenceShader;
	protected PShader feedbackShader;
	protected TextureShader textureShader;
	protected PGraphics feedbackMap;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty(AppSettings.WEBCAM_INDEX, 6 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false );
		p.appConfig.setProperty(AppSettings.FULLSCREEN, true );
	}
		
	public void setupFirstFrame () {
		// capture webcam frames
		p.webCamWrapper.setDelegate(this);
	}
	
	@Override
	public void newFrame(PImage frame) {
		// p.webCamWrapper.getImage()
		// lazy-init flipped camera buffer
		if(flippedCamera == null) {
			flippedCamera = p.createGraphics(frame.width, frame.height, PRenderers.P2D);
			
			// frame buffers
			prevFrame = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			curFrame = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			differenceBuffer = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			feedbackSeedBuffer = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			feedbackFinalBuffer = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			p.debugView.setTexture(feedbackSeedBuffer);
			
			// frame diff buffer/shader
			differenceShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/texture-difference-threshold.glsl"));
			
			// feedback shader & map
			feedbackShader = loadShader(FileUtil.getFile("haxademic/shaders/filters/feedback-map.glsl"));
			textureShader = new TextureShader(TextureShader.noise_simplex_2d_iq, 0.0015f);
			feedbackMap = P.p.createGraphics(flippedCamera.width, flippedCamera.height, PRenderers.P2D);
			p.debugView.setTexture(feedbackMap);
		}
		flippedCamera.copy(frame, 0, 0, frame.width, frame.height, frame.width, 0, -frame.width, frame.height);
		
		// copy previous frame, and current frame to buffer
		ImageUtil.copyImage(curFrame, prevFrame);
		ImageUtil.copyImage(flippedCamera, curFrame);
		p.debugView.setTexture(curFrame);

		// set difference shader textures
		differenceShader.set("tex1", curFrame);
		differenceShader.set("tex2", prevFrame);
		differenceShader.set("falloffBW", 0.3f);
		differenceShader.set("diffThresh", 0.05f);
		differenceBuffer.filter(differenceShader);
	}

	public void drawApp() {
		// set up context
		p.background(0);
		
		if(differenceBuffer != null) {
			// update seed buffer & blur, then threshold, then remove black
			ImageUtil.copyImage(differenceBuffer, feedbackSeedBuffer);
			BlurHFilter.instance(p).setBlurByPercent(1f, feedbackSeedBuffer.width);
			BlurHFilter.instance(p).applyTo(feedbackSeedBuffer);
			BlurVFilter.instance(p).setBlurByPercent(1f, feedbackSeedBuffer.height);
			BlurVFilter.instance(p).applyTo(feedbackSeedBuffer);
			ThresholdFilter.instance(p).applyTo(feedbackSeedBuffer);
			LeaveWhiteFilter.instance(p).applyTo(feedbackSeedBuffer);
			
			// show diff buffer
			feedbackFinalBuffer.beginDraw();
			feedbackFinalBuffer.image(feedbackSeedBuffer, 0, 0);
			feedbackFinalBuffer.endDraw();
			
			// update feedback map on feedback shader
			textureShader.updateTime();
			textureShader.shader().set("zoom", 2f + P.sin(p.frameCount * 0.01f));
			textureShader.shader().set("rotation", p.frameCount * 0.01f);
			feedbackMap.filter(textureShader.shader());
			
			// apply feedback texture to main buffer
			feedbackShader.set("map", feedbackMap);
			feedbackShader.set("samplemult", P.map(p.mouseY, 0, p.height, 0.85f, 1.15f) );
			feedbackShader.set("amp", P.map(p.mouseX, 0, p.width, 0.004f, 0.01f) );
			for (int i = 0; i < 1; i++) feedbackFinalBuffer.filter(feedbackShader); 
			
//			// draw to screen
//			DrawUtil.setDrawCenter(p);
//			DrawUtil.setPImageAlpha(p, 1f);
//			p.blendMode(PBlendModes.BLEND);
//			p.image(curFrame, p.width/2, p.height/2);
//			DrawUtil.setPImageAlpha(p, 0.5f);
//			p.blendMode(PBlendModes.ADD);
//			p.image(feedbackFinalBuffer, p.width/2, p.height/2);
			
			// fullscreen overdraw
			DrawUtil.setPImageAlpha(p, 1f);
			p.blendMode(PBlendModes.BLEND);
			ImageUtil.drawImageCropFill(curFrame, p.g, true);
			DrawUtil.setPImageAlpha(p, 0.5f);
			p.blendMode(PBlendModes.ADD);
			ImageUtil.drawImageCropFill(feedbackFinalBuffer, p.g, true);
		}
	}
	
}
