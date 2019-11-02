package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.BufferMotionDetectionMap;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;

import processing.core.PGraphics;
import processing.core.PImage;

public class SmokeFeedback
extends BaseVideoFilter {
	
	protected BufferMotionDetectionMap motionDetectionMap;
	protected SimplexNoiseTexture noiseTexture;
	protected PGraphics feedbackSeedBuffer;
	protected PGraphics feedbackFinalBuffer;


	public SmokeFeedback(int width, int height) {
		super(width, height);

		noiseTexture = new SimplexNoiseTexture(width, height);
		feedbackSeedBuffer = PG.newPG(width / 2, height / 2);
//		feedbackSeedBuffer.noSmooth();
		feedbackFinalBuffer = PG.newPG(width, height);
//		feedbackFinalBuffer.noSmooth();
		P.p.debugView.setTexture("feedbackSeedBuffer", feedbackSeedBuffer);
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
		noiseTexture.update(0.5f, P.p.frameCount * 0.005f, 0f, 0f);
		
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
		PG.setPImageAlpha(feedbackFinalBuffer, 0.4f);
		feedbackFinalBuffer.image(feedbackSeedBuffer, 0, 0, feedbackFinalBuffer.width, feedbackFinalBuffer.height);	// scaling up since seed buffer is scaled down
		feedbackFinalBuffer.endDraw();

		// apply feedback texture to main buffer
		FeedbackMapFilter.instance(P.p).setMap(noiseTexture.texture());
		FeedbackMapFilter.instance(P.p).setAmp(0.001f);
		FeedbackMapFilter.instance(P.p).setBrightnessStep(-4f/255f);
		FeedbackMapFilter.instance(P.p).setAlphaStep(-2f/255f);
		for (int i = 0; i < 2; i++) FeedbackMapFilter.instance(P.p).applyTo(feedbackFinalBuffer);
		
		// draw composite to output buffer
		destBuffer.beginDraw();
		destBuffer.background(0);
		
		// draw source & effect on top
		PG.resetPImageAlpha(destBuffer);
		destBuffer.blendMode(PBlendModes.BLEND);
		destBuffer.image(sourceBuffer, 0, 0);
		PG.setPImageAlpha(destBuffer, 0.4f);
		destBuffer.blendMode(PBlendModes.ADD);
		destBuffer.image(feedbackFinalBuffer, 0, 0);
		destBuffer.endDraw();
	}
}
