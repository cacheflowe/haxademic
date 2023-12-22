package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.RepeatFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticlesGPU;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class TextureEQChladni 
extends BaseTexture {

	// chaldni pattern
	protected PGraphics pgMap;
	protected PShaderHotSwap shader;
	protected EasingFloat[] easings = new EasingFloat[10];
	protected boolean audioReactive = true;

	// particles
	protected ParticlesGPU particles;

	// post fx
	protected ImageGradient gradient;
	protected PGraphics pgPost;
	protected PGraphics pgPostColor;

	// amplitude
	protected float amp = 0;

	public TextureEQChladni(int width, int height) {
		super(width, height);
		initChladni();
		initPostFx();
		particles = new ParticlesGPU();
	}

	protected void initChladni() {
		pgMap = PG.newPG(pg.width, pg.height);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/chladni.glsl"));
		for (int i = 0; i < easings.length; i++) {
			easings[i] = new EasingFloat(0, 0.2f);
		}
	}
	
	protected void initPostFx() {
		pgPost = PG.newPG(pg.width, pg.height);
		pgPostColor = PG.newPG(pg.width, pg.height);
		gradient = new ImageGradient(ImageGradient.HEAT());

		DebugView.setTexture("pgPost", pgPost);
		DebugView.setTexture("pgPostColor", pgPostColor);
		DebugView.setTexture("gradient", gradient.texture());
	}

	public void drawPre() {
		audioReactive = false;
		amp = FrameLoop.osc(0.01f, 0, 1);
		updatePattern();
		updateParticles();
		updatePostFx();
	}

	protected void updatePattern() {
		// update & run shader
		shader.update();
		if(audioReactive) {
			for (int i = 0; i < easings.length; i++) easings[i].setEaseFactor(0.1f);
			easings[8].setEaseFactor(0.1f);
			easings[9].setEaseFactor(0.1f);
			shader.shader().set("time", 1.15f + easings[8].setTarget(AudioIn.amplitude() * 0.1f).update().value()); //  * P.TWO_PI
			// shader.shader().set("time", FrameLoop.count(0.0001f));
			shader.shader().set("zoom", 0.75f + 0.05f * easings[9].setTarget(AudioIn.amplitude()).update().value());
			shader.shader().set("s1", 
				easings[0].setTarget(AudioIn.audioFreq(10) * 1f).update().value(),
				easings[1].setTarget(AudioIn.audioFreq(20) * 1f).update().value(),
				easings[2].setTarget(6f + AudioIn.audioFreq(30) * 2f).update().value(),
				easings[3].setTarget(1f + AudioIn.audioFreq(40) * 1f).update().value()
			); 
			shader.shader().set("s2", 
				easings[4].setTarget(-1 + AudioIn.audioFreq(50) * 1f).update().value(),
				easings[5].setTarget(1f + AudioIn.audioFreq(60) * 1f).update().value(),
				easings[6].setTarget(10f - AudioIn.audioFreq(70) * 1f).update().value(),
				easings[7].setTarget(6f - AudioIn.audioFreq(80) * 2f).update().value()
			); 
		} else {
			float noiseSpeed = 0.003f;
			shader.shader().set("time", FrameLoop.count(0.0001f));
			shader.shader().set("zoom", 1f - 0.5f * P.p.noise(FrameLoop.count(noiseSpeed)));
			shader.shader().set("s1", 
				20f * P.p.noise(0 + FrameLoop.count(noiseSpeed)),
				20f * P.p.noise(10 + FrameLoop.count(noiseSpeed * 2f)),
				6f + 20f * P.p.noise(20 + FrameLoop.count(noiseSpeed)),
				1f + 20f * P.p.noise(30 + FrameLoop.count(noiseSpeed * 2f))
			); 
			shader.shader().set("s2", 
				-3f + 20f * P.p.noise(100 + FrameLoop.count(noiseSpeed * 2f)),
				20f * P.p.noise(110 + FrameLoop.count(noiseSpeed)),
				3f + 20f * P.p.noise(120 + FrameLoop.count(noiseSpeed * 2f)),
				3f + 20f * P.p.noise(130 + FrameLoop.count(noiseSpeed))
			); 
		}
		shader.shader().set("thickness", 0.5f); // use audio amplitude
		shader.shader().set("time", P.map(amp, 0, 1f, 1, 3));
		shader.shader().set("zoom", P.map(amp, 0, 1f, 0.25f, 0.65f));

		// shader.shader().set("time", Mouse.xNorm * P.TWO_PI * 3f);
		// shader.shader().set("zoom", 0.7f + Mouse.yNorm * 3f); // use audio amplitude?
		pgMap.filter(shader.shader());

		// blur map
		BlurProcessingFilter.instance().setBlurSize(10);
		BlurProcessingFilter.instance().setSigma(4);
		BlurProcessingFilter.instance().applyTo(pgMap);
		// BlurProcessingFilter.instance().applyTo(pgMap);

		// post fx for more radial movement
		FeedbackRadialFilter.instance().setWaveAmp(easings[8].value() * 2f);
		// FeedbackRadialFilter.instance().setAmp(easings[8].value() * 2f);
		// FeedbackRadialFilter.instance().applyTo(pgMap);
		
		RadialRipplesFilter.instance().setAmplitude(0.5f);
		// RadialRipplesFilter.instance().setAmplitude(easings[8].value() * 15f);
		RadialRipplesFilter.instance().setTime(FrameLoop.count(0.01f));
		// RadialRipplesFilter.instance().applyTo(pgMap);
		
		// demo asset
		// PImage webcamImg = WebCam.instance().image();
		// ImageUtil.cropFillCopyImage(DemoAssets.justin(), pgMap, true);
		// ImageUtil.cropFillCopyImage(webcamImg, pgMap, true);
		// ImageUtil.flipV(pgMap);

		VignetteFilter.instance().setDarkness(0.99f);
		VignetteFilter.instance().setSpread(0.45f);
		// VignetteFilter.instance().applyTo(pgMap);
		// VignetteFilter.instance().applyTo(pgMap);

		// debug
		shader.showShaderStatus(pgMap);
		DebugView.setTexture("pgMap", pgMap);
	}

	protected void updateParticles() {
		particles.setBaseParticleSize(1.5f);
		particles.setBaseParticleSpeed(3f);
		particles.setMapDecelCurve(3f);
		particles.updateParticles(pg, pgMap);
		if(KeyboardState.keyTriggered(' ')) particles.resetRandomPositions();
	}

	protected void updatePostFx() {
		// fade out previous frame
		float bright = P.map(amp, 0, 1f, -50f, -4f);
		// bright = -5f;
		bright = P.constrain(bright, -50f, -4f);
		DebugView.setValue("bright", bright);
		BrightnessStepFilter.instance().setBrightnessStep(bright / 255f);
		BrightnessStepFilter.instance().applyTo(pgPost);

		// zoom/feedback
		float zoom = P.map(amp, 0, 1f, 1f, 0.97f);
		RepeatFilter.instance().setOffset(0, 0);
		RepeatFilter.instance().setZoom(zoom);
		RepeatFilter.instance().applyTo(pgPost);
		// RepeatFilter.instance().applyTo(pgPost);
		
		// blur
		BlurProcessingFilter.instance().setBlurSize(10);
		BlurProcessingFilter.instance().setSigma(5);
		BlurProcessingFilter.instance().applyTo(pgPost);

		// overdraw with new frame
		pgPost.beginDraw();
		// pgPost.background(0);
		pgPost.blendMode(PBlendModes.ADD);
		pgPost.image(pgMap, 0, 0);
		pgPost.blendMode(P.BLEND);
		pgPost.endDraw();

		// blur
		BlurProcessingFilter.instance().setBlurSize(20);
		BlurProcessingFilter.instance().setSigma(5);
		BlurProcessingFilter.instance().applyTo(pgPost);

		// copy to color pg, vignette, and colorize
		ImageUtil.copyImage(pgPost, pgPostColor);

		// DisplacementMapFilter.instance().setMap(particles.noiseTexture());
		// DisplacementMapFilter.instance().setMode(2);
		// DisplacementMapFilter.instance().setAmp(0.1f);
		// DisplacementMapFilter.instance().applyTo(pgPostColor);
		
		VignetteFilter.instance().setDarkness(0.8f);
		VignetteFilter.instance().setSpread(0.3f);
		VignetteFilter.instance().applyTo(pgPostColor);

		// ColorizeFromTexture.instance().updateHotSwap();
		ColorizeFromTexture.instance().setTexture(gradient.texture());
		ColorizeFromTexture.instance().setCrossfade(amp);
		ColorizeFromTexture.instance().applyTo(pgPostColor);
		
		VignetteFilter.instance().setDarkness(0.8f);
		VignetteFilter.instance().setSpread(0.3f);
		VignetteFilter.instance().applyTo(pgPostColor);
	}
	
	public void draw() {
		pg.background(0);
		// PG.setPImageAlpha(pg, 0.5f);
		particles.drawParticles(pg, pgMap);
		// draw colorized texture
		pg.blendMode(PBlendModes.LIGHTEST);
		pg.image(pgPostColor, 0, 0); // pgMap
		PG.resetPImageAlpha(pg);
		pg.blendMode(PBlendModes.BLEND);
	}

}
