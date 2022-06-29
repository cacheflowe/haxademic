package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.BufferMotionDetectionMap;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.EdgeColorDarkenFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class MotionFire
extends BaseVideoFilter {
	
	protected BufferMotionDetectionMap motionDetectionMap;
	
	// compositing/fx buffers
	protected PGraphics sourcePG;
	protected PGraphics blurPG;
	protected PGraphics fadePG;
	protected PGraphics firePG;
	
	// feedback
	protected PShader feedbackShader;
	protected SimplexNoiseTexture noiseTexture;
	
	// color remp
	protected PGraphics gradientPG;
	protected int color1 = 0xff500F0A; 
	protected int color2 = 0xffE56939; 
	protected int color3 = 0xffF5DE8B; 
	
	protected String UI_SCROLL_UP = "UI_SCROLL_UP";
	protected String UI_SCROLL_ZOOM = "UI_SCROLL_ZOOM";
	protected String UI_DARKEN = "UI_DARKEN";
	protected String UI_BLUR_SPREAD_ITERS = "UI_BLUR_SPREAD_ITERS";
	protected String UI_BLUR_SMOOTH_AMP = "UI_BLUR_SMOOTH_AMP";
	protected String UI_DEPTH_ADD_ALPHA = "UI_DEPTH_ADD_ALPHA";
	protected String UI_BLUR_DISPLACER_AMP = "UI_BLUR_DISPLACER_AMP";
	protected String UI_APPLY_AMP = "UI_APPLY_AMP";
	protected String UI_APPLY_ITERS = "UI_APPLY_ITERS";
	protected String UI_NOISE_ZOOM = "UI_NOISE_ZOOM";
	protected String UI_NOISE_BRIGHTNESS = "UI_NOISE_BRIGHTNESS";
	protected String GRADIENT_STOP_1 = "GRADIENT_1";
	protected String GRADIENT_STOP_2 = "GRADIENT_2";
	protected String GRADIENT_STOP_3 = "GRADIENT_3";

	
	public MotionFire( int width, int height ) {
		super(width, height);
		
		// init buffers
		int smallW = P.round(width * 0.25f);
		int smallH = P.round(height * 0.25f);
		sourcePG = PG.newPG(smallW, smallH);
		blurPG = PG.newPG(smallW, smallH);
		fadePG = PG.newPG(smallW, smallH);
		gradientPG = PG.newPG(smallW, smallH);
		firePG = PG.newPG(width, height);
		
		// feedback shader & map
		feedbackShader = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/displacement-map.glsl"));
		noiseTexture = new SimplexNoiseTexture(width/4, height/4);
		// DebugView.setTexture(noiseTexture.texture());
		
		// init ui
		UI.addSlider(UI_SCROLL_UP, 0.007f, 0, 0.02f, 0.001f, false);
		UI.addSlider(UI_SCROLL_ZOOM, 0.995f, 0.5f, 1.5f, 0.001f, false);
		UI.addSlider(UI_DARKEN, 4, 0, 200, 1f, false);
		UI.addSlider(UI_BLUR_SPREAD_ITERS, 0, 0, 10, 1, false);
		UI.addSlider(UI_BLUR_SMOOTH_AMP, 0.5f, 0, 5, 0.1f, false);
		UI.addSlider(UI_DEPTH_ADD_ALPHA, 0.33f, 0, 1, 0.01f, false);
		UI.addSlider(UI_BLUR_DISPLACER_AMP, 0, 0, 3, 0.01f, false);
		UI.addSlider(UI_APPLY_AMP, 0.005f, 0, 0.1f, 0.001f, false);
		UI.addSlider(UI_APPLY_ITERS, 1, 0, 10, 1, false);
		UI.addSlider(UI_NOISE_ZOOM, 4, 0.1f, 10, 0.01f, false);
		UI.addSlider(UI_NOISE_BRIGHTNESS, 1.36f, 0.1f, 2, 0.001f, false);	// basic rotation b/c of displacement
		UI.addSliderVector(GRADIENT_STOP_1, 30f, 0, 255, 1, false);
		UI.addSliderVector(GRADIENT_STOP_2, 0, 0, 255, 1, false);
		UI.addSliderVector(GRADIENT_STOP_3, 255, 0, 255, 1, false);

	}
	
	public void update() {
		doMotionDetection();
		makeMotionBlurredCopy();
		updateMapShader();
		fadeAndScrollLastFrame();
		addBlendBlurredAndSpread();
		applyFeedbackShaderTo(fadePG);
		ImageUtil.copyImage(fadePG, firePG);
		updateGradient();
		applyGradient();

		// draw to final buffer
		destBuffer.beginDraw();
		destBuffer.image(sourceBuffer, 0, 0);
		destBuffer.blendMode(PBlendModes.ADD);
		destBuffer.image(firePG, 0, 0);
		destBuffer.blendMode(PBlendModes.BLEND);
		destBuffer.endDraw();
	}
	
	protected void doMotionDetection() {
		// lazy init and update motion detection buffers/calcs
		if(motionDetectionMap == null) {
			motionDetectionMap = new BufferMotionDetectionMap(sourceBuffer, 0.15f);
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
//		ImageUtil.copyImage(sourcePG, blurPG);
		ImageUtil.copyImage(motionDetectionMap.differenceBuffer(), blurPG);
		DebugView.setTexture("blurPG", blurPG);
		
		// blur to smooth clocky motion detection
		float blurAmp = UI.value(UI_BLUR_SMOOTH_AMP);
		BlurHFilter.instance(P.p).setBlurByPercent(blurAmp, blurPG.width);
		BlurHFilter.instance(P.p).applyTo(blurPG);
		BlurVFilter.instance(P.p).setBlurByPercent(blurAmp, blurPG.height);
		BlurVFilter.instance(P.p).applyTo(blurPG);
	}
	
	
	protected void fadeAndScrollLastFrame() {
		// fade down
		BrightnessStepFilter.instance(P.p).setBrightnessStep(-UI.value(UI_DARKEN)/255);
		BrightnessStepFilter.instance(P.p).applyTo(fadePG);
		
		// scroll
		RotateFilter.instance(P.p).setRotation(0.0f);// + 0.01f * P.sin(p.frameCount * 0.1f));
//		RotateFilter.instance(P.p).setRotation(0);
		RotateFilter.instance(P.p).setZoom(UI.value(UI_SCROLL_ZOOM));
		RotateFilter.instance(P.p).setOffset(0f, -UI.value(UI_SCROLL_UP));
		RotateFilter.instance(P.p).applyTo(fadePG);
		
		// draw bottom bar
		EdgeColorDarkenFilter.instance(P.p).setSpreadY(0.01f);
		EdgeColorDarkenFilter.instance(P.p).applyTo(fadePG);
	}
	
	protected void addBlendBlurredAndSpread() {
		// loop aplha add
		float alphaAdd = UI.value(UI_DEPTH_ADD_ALPHA);
		
		// for rendering loop:
//		int fadeFrames = 100;
//		int darkFrames = 60;
//		if(AnimationLoop.loopCurFrame() < fadeFrames) alphaAdd = P.map(AnimationLoop.loopCurFrame(), 0, fadeFrames, 0, alphaAdd);
//		if(AnimationLoop.loopCurFrame() > FRAMES - fadeFrames - darkFrames) alphaAdd = P.map(AnimationLoop.loopCurFrame(), FRAMES - fadeFrames - darkFrames, FRAMES - darkFrames, alphaAdd, 0);
		
		// add new frame on top
		fadePG.beginDraw();
		fadePG.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(fadePG, alphaAdd);
		fadePG.image(blurPG, 0, 0);
		fadePG.blendMode(PBlendModes.BLEND);
		fadePG.endDraw();

		// spread out
		int blurSpreadIters = UI.valueInt(UI_BLUR_SPREAD_ITERS);
		float blurAmp = 1f;
		if(blurSpreadIters > 0) {
			for (int i = 0; i < blurSpreadIters; i++) {
				BlurHFilter.instance(P.p).setBlurByPercent(blurAmp, fadePG.width);
				BlurHFilter.instance(P.p).applyTo(fadePG);
				BlurVFilter.instance(P.p).setBlurByPercent(blurAmp, fadePG.height);
				BlurVFilter.instance(P.p).applyTo(fadePG);
			}
		}
	}

	protected void updateMapShader() {
		noiseTexture.update(
				UI.value(UI_NOISE_ZOOM),
				0,
				P.p.frameCount * 0.01f + 0.1f * P.sin(P.p.frameCount * 0.1f),
				-P.p.frameCount * 0.05f
		);
		
		BrightnessFilter.instance(P.p).setBrightness(UI.value(UI_NOISE_BRIGHTNESS));
		BrightnessFilter.instance(P.p).applyTo(noiseTexture.texture());
		
		DebugView.setTexture("noiseTexture", noiseTexture.texture());
		
		float blurAmp = UI.value(UI_BLUR_DISPLACER_AMP);
		BlurHFilter.instance(P.p).setBlurByPercent(blurAmp, noiseTexture.texture().width);
		BlurHFilter.instance(P.p).applyTo(noiseTexture.texture());
		BlurVFilter.instance(P.p).setBlurByPercent(blurAmp, noiseTexture.texture().height);
		BlurVFilter.instance(P.p).applyTo(noiseTexture.texture());
	}

	protected void applyFeedbackShaderTo(PGraphics pgToFeedback) {
		feedbackShader.set("map", noiseTexture.texture());
		feedbackShader.set("mode", 3);
		feedbackShader.set("amp", UI.value(UI_APPLY_AMP));
		for (int i = 0; i < UI.value(UI_APPLY_ITERS); i++) pgToFeedback.filter(feedbackShader); 
	}
	
	protected void updateGradient() {
		// redraw 3-color gradient
		int color1 = P.p.color(UI.valueX(GRADIENT_STOP_1), UI.valueY(GRADIENT_STOP_1), UI.valueZ(GRADIENT_STOP_1));
		int color2 = P.p.color(UI.valueX(GRADIENT_STOP_2), UI.valueY(GRADIENT_STOP_2), UI.valueZ(GRADIENT_STOP_2));
		int color3 = P.p.color(UI.valueX(GRADIENT_STOP_3), UI.valueY(GRADIENT_STOP_3), UI.valueZ(GRADIENT_STOP_3));
//		color1 = p.color(30, 0, 0);
//		color2 = p.color(174, 0, 0);
//		color3 = p.color(255, 255, 121);
		
		// draw gradient with vertices
		gradientPG.beginDraw();
		gradientPG.noStroke();
		gradientPG.translate(gradientPG.width * 0.25f, gradientPG.height / 2);
		Gradients.linear(gradientPG, gradientPG.width/2f, gradientPG.height * 1.2f, color1, color2);
		gradientPG.translate(gradientPG.width * 0.5f, 0);
		Gradients.linear(gradientPG, gradientPG.width/2f, gradientPG.height * 1.2f, color2, color3);
		gradientPG.endDraw();
	}
	
	protected void applyGradient() {
		ColorizeFromTexture.instance(P.p).setTexture(gradientPG);
		ColorizeFromTexture.instance(P.p).applyTo(firePG);
	}
	
}
