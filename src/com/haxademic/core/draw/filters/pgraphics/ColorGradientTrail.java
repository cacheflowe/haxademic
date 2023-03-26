package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferMotionDetectionMap;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;

public class ColorGradientTrail
extends BaseVideoFilter {
	
	protected PGraphics gradientTexture;

	protected BufferMotionDetectionMap motionDetectionMap;
	protected PGraphics blurredMotion;
	protected PGraphics blendedSpread;
	protected PGraphics colorizedTrail;

	
	public ColorGradientTrail( int width, int height ) {
		super(width, height);
		
		gradientTexture = ImageUtil.imageToGraphics(ImageGradient.SPARKS_FLAMES()); // ImageUtil.imageToGraphics(P.getImage("images/_sketch/thermal-gradient-posterized.png"));
	}
	
	public void update() {
		doMotionDetection();
		makeMotionBlurredCopy();
		addBlendBlurredAndSpread();
		colorizeFinal(); 
		
		// draw to final buffer
		destBuffer.beginDraw();
		destBuffer.image(sourceBuffer, 0, 0);
		destBuffer.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(blendedSpread, 0.99f);
		destBuffer.image(colorizedTrail, 0, 0);
		destBuffer.blendMode(PBlendModes.BLEND);
		destBuffer.endDraw();
	}
	
	protected void doMotionDetection() {
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(sourceBuffer, 0.15f);
			blurredMotion = PG.newPG(motionDetectionMap.bwBuffer().width, motionDetectionMap.bwBuffer().height);
			blendedSpread = PG.newPG(motionDetectionMap.bwBuffer().width, motionDetectionMap.bwBuffer().height);
			colorizedTrail = PG.newPG(sourceBuffer.width, sourceBuffer.height);
		}

		// run motion detection
		motionDetectionMap.setBlendLerp(0.25f);
		motionDetectionMap.setDiffThresh(0.03f);
		motionDetectionMap.setFalloffBW(0.75f);
		motionDetectionMap.setThresholdCutoff(0.5f);
		motionDetectionMap.setBlur(1f);
		motionDetectionMap.updateSource(sourceBuffer);
		
//		DebugView.setTexture(sourceBuffer);
//		DebugView.setTexture(motionDetectionMap.bwBuffer());
//		DebugView.setTexture(blurredMotion);
//		DebugView.setTexture(blendedSpread);
//		DebugView.setTexture(colorizedTrail);
	}
	
	protected void makeMotionBlurredCopy() {
		ImageUtil.copyImage(motionDetectionMap.bwBuffer(), blurredMotion);
		
		// blur to smooth clocky motion detection
		float blurAmp = 2f;
		BlurHFilter.instance().setBlurByPercent(blurAmp, blurredMotion.width);
		BlurHFilter.instance().applyTo(blurredMotion);
		BlurVFilter.instance().setBlurByPercent(blurAmp, blurredMotion.height);
		BlurVFilter.instance().applyTo(blurredMotion);
	}
	
	protected void addBlendBlurredAndSpread() {
		// fade down
		BrightnessStepFilter.instance().setBrightnessStep(-1f/255);
		BrightnessStepFilter.instance().applyTo(blendedSpread);
		
		// add new frame on top
		blendedSpread.beginDraw();
		blendedSpread.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(blendedSpread, 0.9f);
		blendedSpread.image(blurredMotion, 0, 0);
		blendedSpread.blendMode(PBlendModes.BLEND);
		blendedSpread.endDraw();

		// spread out
		float blurAmp = 1f;
		for (int i = 0; i < 5; i++) {
			BlurHFilter.instance().setBlurByPercent(blurAmp, blendedSpread.width);
			BlurHFilter.instance().applyTo(blendedSpread);
			BlurVFilter.instance().setBlurByPercent(blurAmp, blendedSpread.height);
			BlurVFilter.instance().applyTo(blendedSpread);
		}
	}
	
	protected void colorizeFinal() {
		ImageUtil.copyImage(blendedSpread, colorizedTrail);
		ColorizeFromTexture.instance().setTexture(gradientTexture);
		ColorizeFromTexture.instance().applyTo(colorizedTrail);
	}
}
