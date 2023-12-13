package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PShape;

public class TextureEQChladni 
extends BaseTexture {

	// Notes:
	// - https://thelig.ht/chladni/
	// - https://paulbourke.net/geometry/chladni/
	// - https://www.shadertoy.com/view/cssfRr
	// - https://www.shadertoy.com/view/WdKXRV

	// chaldni pattern
	protected PGraphics pgMap;
	protected PShaderHotSwap shader;
	protected EasingFloat[] easings = new EasingFloat[10];

	// particles
	protected PShape shape;
	protected PGraphics bufferPositions;
	protected PGraphics bufferRandom;
	protected PShaderHotSwap randomColorShader;
	protected PShaderHotSwap particleMoverShader;
	protected PShaderHotSwap particlesDrawShader;
	protected SimplexNoise3dTexture noiseTexture;


	public TextureEQChladni(int width, int height) {
		super(width, height);
		initChladni();
		initParticles();
	}

	protected void initChladni() {
		pgMap = PG.newPG(pg.width, pg.height);
		shader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/chladni.glsl"));
		for (int i = 0; i < easings.length; i++) {
			easings[i] = new EasingFloat(0, 0.2f);
		}
	}

	protected void initParticles() {
		// build random particle placement shader
		randomColorShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));
		particleMoverShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/point/particle-map-mover.glsl"));

		// create texture to store positions
		int positionBufferSize = 1024;
		bufferPositions = PG.newDataPG(positionBufferSize, positionBufferSize);
		bufferRandom = PG.newDataPG(positionBufferSize, positionBufferSize);
		bufferRandom.filter(randomColorShader.shader());
		DebugView.setTexture("bufferPositions", bufferPositions);
		DebugView.setTexture("bufferRandom", bufferRandom);

		newPositions();
		
		// count vertices for debugView
		int vertices = P.round(positionBufferSize * positionBufferSize); 
		DebugView.setValue("numParticles", vertices);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		
		// load shader
		particlesDrawShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/point/particle-map-vert.glsl"),
			FileUtil.getPath("haxademic/shaders/point/points-default-frag.glsl")
		);

		// noise texture
		noiseTexture = new SimplexNoise3dTexture(256, 256, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
		DebugView.setTexture("noiseTexture", noiseTexture.texture());
	}

	public void drawPre() {
		updatePattern();
		updateParticles();
	}

	protected void updatePattern() {
		for (int i = 0; i < easings.length; i++) easings[i].setEaseFactor(0.05f);

		// update & run shader
		shader.update();
		shader.shader().set("time", FrameLoop.count(0.0001f));
		// shader.shader().set("time", Mouse.xNorm * P.TWO_PI * 3f);
		// shader.shader().set("time", easings[8].setTarget(FrameLoop.count(0.0f) + AudioIn.amplitude() * 0.1f).update().value() * P.TWO_PI);
		shader.shader().set("zoom", 0.7f + Mouse.yNorm * 3f); // use audio amplitude?
		// shader.shader().set("zoom", 2f - 0.15f * easings[9].setTarget(AudioIn.amplitude()).update().value());
		shader.shader().set("thickness", 0.15f); // use audio amplitude
		float noiseSpeed = 0.003f;
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
		shader.shader().set("s1", 
			easings[0].setTarget(AudioIn.audioFreq(10) * 2f).update().value(),
			easings[1].setTarget(AudioIn.audioFreq(20) * 2f).update().value(),
			easings[2].setTarget(6f + AudioIn.audioFreq(30) * 2f).update().value(),
			easings[3].setTarget(1f + AudioIn.audioFreq(40) * 1f).update().value()
		); 
		shader.shader().set("s2", 
			easings[4].setTarget(-1 + AudioIn.audioFreq(150) * 2f).update().value(),
			easings[5].setTarget(2f - AudioIn.audioFreq(160) * 2f).update().value(),
			easings[6].setTarget(10f - AudioIn.audioFreq(170) * 2f).update().value(),
			easings[7].setTarget(6f - AudioIn.audioFreq(210) * 2f).update().value()
		); 
		pgMap.filter(shader.shader());

		// blur map
		BlurProcessingFilter.instance().setBlurSize(20);
		BlurProcessingFilter.instance().setSigma(10);
		BlurProcessingFilter.instance().applyTo(pgMap);
		BlurProcessingFilter.instance().applyTo(pgMap);

		// post fx for more radial movement
		FeedbackRadialFilter.instance().setWaveAmp(easings[8].value() * 2f);
		// FeedbackRadialFilter.instance().setAmp(easings[8].value() * 2f);
		// FeedbackRadialFilter.instance().applyTo(pgMap);
		
		RadialRipplesFilter.instance().setAmplitude(0.5f);
		RadialRipplesFilter.instance().setAmplitude(easings[8].value() * 15f);
		RadialRipplesFilter.instance().setTime(FrameLoop.count(0.01f));
		// RadialRipplesFilter.instance().applyTo(pgMap);
		
		// demo asset
		// ImageUtil.cropFillCopyImage(DemoAssets.justin(), pgMap, true);
		// ImageUtil.flipV(pgMap);

		VignetteFilter.instance().setDarkness(0.99f);
		VignetteFilter.instance().setSpread(0.45f);
		// VignetteFilter.instance().applyTo(pgMap);
		// VignetteFilter.instance().applyTo(pgMap);

		// debug
		shader.showShaderStatus(pgMap);
		DebugView.setTexture("pgMap", pgMap);
	}
	
	public void draw() {
		pg.background(0);
		// PG.setPImageAlpha(pg, 0.5f);
		// pg.image(pgMap, 0, 0);
		// PG.resetPImageAlpha(pg);
		drawParticles();
	}

	protected void updateParticles() {
		// check to recompile shaders
		randomColorShader.update();
		particleMoverShader.update();
		particlesDrawShader.update();

		// update noise texture
		noiseTexture.update(5f, 0, 0, 0, FrameLoop.count(0.01f), false, false);

		// update particle positions
		particleMoverShader.shader().set("mapRandom", bufferRandom);
		particleMoverShader.shader().set("mapTexture", pgMap);
		particleMoverShader.shader().set("mapNoise", noiseTexture.texture());
		particleMoverShader.shader().set("speed", 5f);
		particleMoverShader.shader().set("width", (float) pg.width);
		particleMoverShader.shader().set("height", (float) pg.height);
		bufferPositions.filter(particleMoverShader.shader());
	}

	protected void drawParticles() {
		// PG.setCenterScreen(pg);
		// PG.basicCameraFromMouse(pg);

		particlesDrawShader.shader().set("width", (float) pg.width);
		particlesDrawShader.shader().set("height", (float) pg.height);
		particlesDrawShader.shader().set("depth", 0f);
		particlesDrawShader.shader().set("positionMap", bufferPositions);
		particlesDrawShader.shader().set("colorMap", DemoAssets.justin());
		particlesDrawShader.shader().set("pointSize", 1.5f);
		pg.shader(particlesDrawShader.shader());
		pg.blendMode(PBlendModes.ADD);
		pg.shape(shape); // draw vertices
		pg.resetShader();
	}

	protected void newPositions() {
		randomColorShader.shader().set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		bufferPositions.filter(randomColorShader.shader());
	}

}
