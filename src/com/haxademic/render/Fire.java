package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.context.PG;
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

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Fire
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 457;

	// compositing/fx buffers
	protected PGraphics sourcePG;
	protected PGraphics blurPG;
	protected PGraphics fadePG;
	
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

	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, false);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1024);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}

	public void setupFirstFrame() {
		// init buffers
		int smallW = P.round(pg.width * 0.25f);
		int smallH = P.round(pg.height * 0.25f);
		sourcePG = PG.newPG(smallW, smallH);
		blurPG = PG.newPG(smallW, smallH);
		fadePG = PG.newPG(smallW, smallH);
		gradientPG = PG.newPG(smallW, smallH);
		
		p.debugView.setTexture("sourcePG", sourcePG);
		p.debugView.setTexture("blurPG", blurPG);
		p.debugView.setTexture("fadePG", fadePG);
		p.debugView.setTexture("gradientPG", gradientPG);

		// feedback shader & map
		feedbackShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/displacement-map.glsl"));
		noiseTexture = new SimplexNoiseTexture(p.width/4, p.height/4);
		p.debugView.setTexture("noiseTexture", noiseTexture.texture());
		
		// init ui
		p.ui.addSlider(UI_SCROLL_UP, 0.007f, 0, 0.02f, 0.001f, false);
		p.ui.addSlider(UI_SCROLL_ZOOM, 0.995f, 0.5f, 1.5f, 0.001f, false);
		p.ui.addSlider(UI_DARKEN, 4, 0, 200, 1f, false);
		p.ui.addSlider(UI_BLUR_SPREAD_ITERS, 0, 0, 10, 1, false);
		p.ui.addSlider(UI_BLUR_SMOOTH_AMP, 0.5f, 0, 5, 0.1f, false);
		p.ui.addSlider(UI_DEPTH_ADD_ALPHA, 0.33f, 0, 1, 0.01f, false);
		p.ui.addSlider(UI_BLUR_DISPLACER_AMP, 0, 0, 3, 0.01f, false);
		p.ui.addSlider(UI_APPLY_AMP, 0.005f, 0, 0.1f, 0.001f, false);
		p.ui.addSlider(UI_APPLY_ITERS, 1, 0, 10, 1, false);
		p.ui.addSlider(UI_NOISE_ZOOM, 4, 0.1f, 10, 0.01f, false);
		p.ui.addSlider(UI_NOISE_BRIGHTNESS, 1.36f, 0.1f, 2, 0.001f, false);	// basic rotation b/c of displacement
		p.ui.addSliderVector(GRADIENT_STOP_1, 30f/255f, 0, 255, 1, false);
		p.ui.addSliderVector(GRADIENT_STOP_2, 0, 0, 255, 1, false);
		p.ui.addSliderVector(GRADIENT_STOP_3, 1, 0, 255, 1, false);

	}

	public void drawApp() {
		// set up context
		background(0);
		PG.setDrawCorner(p);
		p.fill(255);
		p.noStroke();
		
		// compositing!
		drawSourceShape();
		makeMotionBlurredCopy();
		updateMapShader();
		fadeAndScrollLastFrame();
		addBlendBlurredAndSpread();
		applyFeedbackShaderTo(fadePG);
		ImageUtil.copyImage(fadePG, pg);
		updateGradient();
		applyGradient();

		// final draw
		p.image(pg, 0, 0);
		
	}
	
	protected void drawSourceShape() {
		float shapeSize = sourcePG.width * 0.2f * (1f + 0.05f * P.sin(p.loop.progressRads() * 3f));
		sourcePG.beginDraw();
		sourcePG.background(0);
		sourcePG.fill(255);
		sourcePG.noStroke();
		sourcePG.translate(sourcePG.width/2, sourcePG.height * 0.65f);
		PG.setDrawCenter(sourcePG);
		Gradients.radial(sourcePG, shapeSize, shapeSize, p.color(180), p.color(1), 36);
		
		// triangle
		float w = 80;
		sourcePG.strokeWeight(7);
		sourcePG.stroke(255);
		sourcePG.line(-w, sourcePG.height * 0.2f, w, sourcePG.height * 0.2f);
		sourcePG.strokeWeight(2);
		sourcePG.line(-w, sourcePG.height * 0.2f, 0, -sourcePG.height * 0.39f);
		sourcePG.line(w, sourcePG.height * 0.2f, 0, -sourcePG.height * 0.39f);
		sourcePG.endDraw();
	}
	
	protected void makeMotionBlurredCopy() {
		ImageUtil.copyImage(sourcePG, blurPG);
		
		// blur to smooth clocky motion detection
		float blurAmp = p.ui.value(UI_BLUR_SMOOTH_AMP);
		BlurHFilter.instance(P.p).setBlurByPercent(blurAmp, blurPG.width);
		BlurHFilter.instance(P.p).applyTo(blurPG);
		BlurVFilter.instance(P.p).setBlurByPercent(blurAmp, blurPG.height);
		BlurVFilter.instance(P.p).applyTo(blurPG);
	}
	
	
	protected void fadeAndScrollLastFrame() {
		// fade down
		BrightnessStepFilter.instance(P.p).setBrightnessStep(-p.ui.value(UI_DARKEN)/255);
		BrightnessStepFilter.instance(P.p).applyTo(fadePG);
		
		// scroll
		RotateFilter.instance(p).setRotation(0.0f);// + 0.01f * P.sin(p.frameCount * 0.1f));
//		RotateFilter.instance(p).setRotation(0);
		RotateFilter.instance(p).setZoom(p.ui.value(UI_SCROLL_ZOOM));
		RotateFilter.instance(p).setOffset(0f, -p.ui.value(UI_SCROLL_UP));
		RotateFilter.instance(p).applyTo(fadePG);
		
		// draw bottom bar
		EdgeColorDarkenFilter.instance(p).setSpreadY(0.01f);
		EdgeColorDarkenFilter.instance(p).applyTo(fadePG);
	}
	
	protected void addBlendBlurredAndSpread() {
		// loop aplha add
		float alphaAdd = p.ui.value(UI_DEPTH_ADD_ALPHA);
		
		// for rendering loop:
//		int fadeFrames = 100;
//		int darkFrames = 60;
//		if(p.loop.loopCurFrame() < fadeFrames) alphaAdd = P.map(p.loop.loopCurFrame(), 0, fadeFrames, 0, alphaAdd);
//		if(p.loop.loopCurFrame() > FRAMES - fadeFrames - darkFrames) alphaAdd = P.map(p.loop.loopCurFrame(), FRAMES - fadeFrames - darkFrames, FRAMES - darkFrames, alphaAdd, 0);
		
		// add new frame on top
		fadePG.beginDraw();
		fadePG.blendMode(PBlendModes.ADD);
		PG.setPImageAlpha(fadePG, alphaAdd);
		fadePG.image(blurPG, 0, 0);
		fadePG.blendMode(PBlendModes.BLEND);
		fadePG.endDraw();

		// spread out
		int blurSpreadIters = p.ui.valueInt(UI_BLUR_SPREAD_ITERS);
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
				p.ui.value(UI_NOISE_ZOOM),
				0,
				p.frameCount * 0.01f + 0.1f * P.sin(p.loop.progressRads() * 3f),
				-frameCount * 0.05f
		);
		
		BrightnessFilter.instance(p).setBrightness(p.ui.value(UI_NOISE_BRIGHTNESS));
		BrightnessFilter.instance(p).applyTo(noiseTexture.texture());
		
		float blurAmp = p.ui.value(UI_BLUR_DISPLACER_AMP);
		BlurHFilter.instance(P.p).setBlurByPercent(blurAmp, noiseTexture.texture().width);
		BlurHFilter.instance(P.p).applyTo(noiseTexture.texture());
		BlurVFilter.instance(P.p).setBlurByPercent(blurAmp, noiseTexture.texture().height);
		BlurVFilter.instance(P.p).applyTo(noiseTexture.texture());
	}

	protected void applyFeedbackShaderTo(PGraphics pgToFeedback) {
		feedbackShader.set("map", noiseTexture.texture());
		feedbackShader.set("mode", 3);
		feedbackShader.set("amp", p.ui.value(UI_APPLY_AMP));
		for (int i = 0; i < p.ui.value(UI_APPLY_ITERS); i++) pgToFeedback.filter(feedbackShader); 
	}
	
	protected void updateGradient() {
		// redraw 3-color gradient
		int color1 = p.color(p.ui.valueX(GRADIENT_STOP_1), p.ui.valueY(GRADIENT_STOP_1), p.ui.valueZ(GRADIENT_STOP_1));
		int color2 = p.color(p.ui.valueX(GRADIENT_STOP_2), p.ui.valueY(GRADIENT_STOP_2), p.ui.valueZ(GRADIENT_STOP_2));
		int color3 = p.color(p.ui.valueX(GRADIENT_STOP_3), p.ui.valueY(GRADIENT_STOP_3), p.ui.valueZ(GRADIENT_STOP_3));
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
		ColorizeFromTexture.instance(p).setTexture(gradientPG);
		ColorizeFromTexture.instance(p).applyTo(pg);
	}
}
