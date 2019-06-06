package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class SmokeFeedback
extends BaseVideoFilter {
	
	protected BufferMotionDetectionMap motionDetectionMap;
	protected SimplexNoiseTexture noiseTexture;
	protected PGraphics feedbackSeedBuffer;
	protected PGraphics feedbackFinalBuffer;
	protected PShader feedbackShader;


	public SmokeFeedback(int width, int height) {
		super(width, height);

		noiseTexture = new SimplexNoiseTexture(width, height);
		feedbackSeedBuffer = P.p.createGraphics(width / 4, height / 4, PRenderers.P2D);
//		feedbackSeedBuffer.noSmooth();
		feedbackFinalBuffer = P.p.createGraphics(width, height, PRenderers.P2D);
//		feedbackFinalBuffer.noSmooth();
		P.p.debugView.setTexture(feedbackSeedBuffer);
		
		// feedback shader & map
		feedbackShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/filters/feedback-map.glsl"));
	}
	
	public void newFrame(PImage frame) {
		// store (and crop fill) frame into `sourceBuffer`
		super.newFrame(frame);
		
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(sourceBuffer, 0.1f);
			motionDetectionMap.setBlendLerp(0.2f);
			motionDetectionMap.setDiffThresh(0.05f);
			motionDetectionMap.setFalloffBW(0.2f);
			motionDetectionMap.setThresholdCutoff(0.5f);
			motionDetectionMap.setBlur(1f);
		}
		
		// run motion detection
		motionDetectionMap.updateSource(sourceBuffer);
	}
	
	public void update() {
		if(motionDetectionMap == null) return;

		// update simplex noise map
		noiseTexture.update(0.15f, P.p.frameCount * 0.005f, 0f, 0f);
		
		// copy noise to buffer, post-process noise map, & remove black
		ImageUtil.copyImage(motionDetectionMap.bwBuffer(), feedbackSeedBuffer);
		BlurHFilter.instance(P.p).setBlurByPercent(1f, feedbackSeedBuffer.width);
		BlurHFilter.instance(P.p).applyTo(feedbackSeedBuffer);
		BlurVFilter.instance(P.p).setBlurByPercent(1f, feedbackSeedBuffer.height);
		BlurVFilter.instance(P.p).applyTo(feedbackSeedBuffer);
		ThresholdFilter.instance(P.p).applyTo(feedbackSeedBuffer);
		LeaveWhiteFilter.instance(P.p).applyTo(feedbackSeedBuffer);
		
		// draw white noise on top of feedback buffer
		feedbackFinalBuffer.beginDraw();
		DrawUtil.setPImageAlpha(feedbackFinalBuffer, 0.4f);
		feedbackFinalBuffer.image(feedbackSeedBuffer, 0, 0, feedbackFinalBuffer.width, feedbackFinalBuffer.height);	// scaling up since seed buffer is scaled down
		feedbackFinalBuffer.endDraw();

		// apply feedback texture to main buffer
		feedbackShader.set("map", noiseTexture.texture());
		feedbackShader.set("samplemult", 0.88f);// P.map(P.p.mouseY, 0, height, 0.85f, 1.15f) );
		feedbackShader.set("amp", 1f/255f);//P.map(P.p.mouseX, 0, width, 1f/255f, 0.01f) );
		for (int i = 0; i < 1; i++) feedbackFinalBuffer.filter(feedbackShader); 
		
		// draw composite to output buffer
		destBuffer.beginDraw();
		destBuffer.background(0);
		
		// draw source & effect on top
		DrawUtil.resetPImageAlpha(destBuffer);
		destBuffer.blendMode(PBlendModes.BLEND);
		destBuffer.image(sourceBuffer, 0, 0);
		DrawUtil.setPImageAlpha(destBuffer, 0.4f);
		destBuffer.blendMode(PBlendModes.ADD);
		destBuffer.image(feedbackFinalBuffer, 0, 0);
		destBuffer.endDraw();
	}
}
