package com.haxademic.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurHMapFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BlurVMapFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SharpenMapFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class ReactionDiffusionCirclePulse
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SimplexNoise3dTexture noiseTexture;
	protected PGraphics pgBlur;
	
	protected int animStyle = 0;
	protected int FRAMES = 60 * 5;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setPgSize(1024*2, 1024*2);
		Config.setProperty(AppSettings.PG_32_BIT, true);
		if(animStyle == 0) FRAMES = 900;
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + (FRAMES * 4));
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + (FRAMES * 5));
	}
	
	protected void firstFrame() {
		// init noise object
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
		DebugView.setTexture("noise", noiseTexture.texture());
	}
	
	protected void drawApp() {
		p.background(0);
		
		// update blur map
		noiseTexture.update(
				1.5f,								// zoom
				0,									// rotation
				0,									// offset x
				0,									// offset y
				FrameLoop.count(0.003f),			// offset z
				false,								// fractal mode
				false								// xRepeat mode
		);
		ContrastFilter.instance(p).setContrast(1.f);
		ContrastFilter.instance(p).applyTo(noiseTexture.texture());
		

		
		// prep pg context
		pg.beginDraw();
		if(FrameLoop.loopCurFrame() == 1) pg.background(0);

		// set to center
		pg.push();
		PG.setDrawCenter(pg);
		PG.setCenterScreen(pg);
		pg.blendMode(PBlendModes.ADD);

		// draw circle
		float circleSize = pg.height * 3.0f * FrameLoop.progress();
		float strokeSize = pg.height * 0.005f;
		strokeSize *= (1f - FrameLoop.progress() * 4f);
		if(strokeSize < 0) strokeSize = 0;
		pg.noFill();
		pg.stroke(255);
		pg.strokeWeight(strokeSize);
		pg.ellipse(0, 0, circleSize, circleSize);
		
		// close context
		pg.pop();
		pg.endDraw();
		
		
		
		// R/D
		BlurHMapFilter.instance(p).setMap(noiseTexture.texture());
		BlurHMapFilter.instance(p).setAmpMin(0.9f);
		BlurHMapFilter.instance(p).setAmpMax(1.85f);
		BlurVMapFilter.instance(p).setMap(noiseTexture.texture());
		BlurVMapFilter.instance(p).setAmpMin(0.9f);
		BlurVMapFilter.instance(p).setAmpMax(1.85f);
		SharpenMapFilter.instance(p).setMap(noiseTexture.texture());
		SharpenMapFilter.instance(p).setAmpMin(5f);
		SharpenMapFilter.instance(p).setAmpMax(26f - FrameLoop.progress() * 20f);
		
		DisplacementMapFilter.instance(p).setMap(noiseTexture.texture());
		DisplacementMapFilter.instance(p).setMode(3);
		DisplacementMapFilter.instance(p).setRotRange(P.TWO_PI * 2f);
		DisplacementMapFilter.instance(p).setAmp(FrameLoop.progress() * 0.005f);
		
		RotateFilter.instance(p).setZoom(1f - 0.01f * FrameLoop.progress());
		RotateFilter.instance(p).setRotation(0.0f * FrameLoop.progress());
		RotateFilter.instance(p).applyTo(pg);
		
		float brightProgress = 50f + FrameLoop.progress() * 80f;
		BrightnessStepFilter.instance(p).setBrightnessStep(-brightProgress/255f);
		for (int i = 0; i < 1; i++) {
			BrightnessStepFilter.instance(p).applyTo(pg);
			DisplacementMapFilter.instance(p).applyTo(pg);
//			GrainFilter.instance(p).applyTo(pg);	// add jitter
			BlurHMapFilter.instance(p).applyTo(pg);
			BlurVMapFilter.instance(p).applyTo(pg);
			BlurHMapFilter.instance(p).applyTo(pg);
			BlurVMapFilter.instance(p).applyTo(pg);
			SharpenMapFilter.instance(p).applyTo(pg);
		}

		// blurred fake light map
		if(pgBlur == null) pgBlur = PG.newPG(512, 512);
		ImageUtil.copyImage(pg, pgBlur);
		BlurHFilter.instance(p).setBlurByPercent(1f, pg.width);
		BlurVFilter.instance(p).setBlurByPercent(1f, pg.height);
		BlurHFilter.instance(p).applyTo(pgBlur);
		BlurVFilter.instance(p).applyTo(pgBlur);
		BlurHFilter.instance(p).applyTo(pgBlur);
		BlurVFilter.instance(p).applyTo(pgBlur);
		BlurHFilter.instance(p).applyTo(pgBlur);
		BlurVFilter.instance(p).applyTo(pgBlur);
		BlurHFilter.instance(p).applyTo(pgBlur);
		BlurVFilter.instance(p).applyTo(pgBlur);
		
		// draw to screen
		p.image(pg, 0, 0, p.width, p.height);

		// post process
		VignetteFilter.instance(p).setDarkness(0.5f);
//		VignetteFilter.instance(p).applyTo(p.g);
		FakeLightingFilter.instance(p).setAmbient(2.2f);
		FakeLightingFilter.instance(p).setGradAmp(0.4f);
		FakeLightingFilter.instance(p).setGradBlur(2f);
		FakeLightingFilter.instance(p).setSpecAmp(1.25f);
		FakeLightingFilter.instance(p).setDiffDark(0.5f);
		FakeLightingFilter.instance(p).setMap(pgBlur);
		FakeLightingFilter.instance(p).applyTo(p.g);
		
		// border
		p.fill(255);
		p.noStroke();
		int padd = 50;
		p.rect(0, 0, p.width, padd);
		p.rect(0, 0, padd, p.height);
		p.rect(0, p.height - padd, p.width, padd);
		p.rect(p.width - padd, 0, padd, p.height);
	}

}