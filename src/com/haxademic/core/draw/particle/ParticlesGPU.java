package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class ParticlesGPU {
  /////////////////////////////////////////////////
	// Particles TODO
	// - Add color gradient ramp 
	//   - Add color easing based on underlying map texture as an alternative colorization technique
	// - Add audioreactive texture for speed ramp
	// - newPositions() needs to be float-32 positions, not normalized
  //   - Something about the random color map makes this weird and improperly-distributed... Needs more investigation for a fix, but leave as-is for now
  // - Add optical flow
	/////////////////////////////////////////////////

	// particles
	protected PShape shape;
  protected int positionBufferSize;
	protected PGraphics bufferPositions;
	protected PGraphics bufferRandom;
	protected PShaderHotSwap randomColorShader;
	protected PShaderHotSwap simulationShader;
	protected PShaderHotSwap renderShader;
	protected SimplexNoise3dTexture noiseTexture;

	protected float baseParticleSpeed = 5;
	protected float baseParticleSize = 1.5f;
	protected float mapDecelCurve = 0f;

  protected boolean resetPositionsDirty = false;

  
  public ParticlesGPU() {
    this(1024);
  }

  public ParticlesGPU(int size) {
    positionBufferSize = 1024;
    initParticles();
  }

	protected void initParticles() {
		// build random particle placement shader
		randomColorShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));
		
		// create texture & shader to store positions
		simulationShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/point/particle-map-mover.glsl"));
		bufferPositions = PG.newDataPG(positionBufferSize, positionBufferSize);
		bufferRandom = PG.newDataPG(positionBufferSize, positionBufferSize);
		bufferRandom.filter(randomColorShader.shader());
		DebugView.setTexture("bufferPositions", bufferPositions);
		DebugView.setTexture("bufferRandom", bufferRandom);
		
		// count vertices for debugView
		int vertices = P.round(positionBufferSize * positionBufferSize); 
		resetRandomPositions();
		DebugView.setValue("numParticles", vertices);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		
		// load shader
		renderShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/point/particle-map-vert.glsl"),
			FileUtil.getPath("haxademic/shaders/point/points-default-frag.glsl")
		);

		// noise texture
		noiseTexture = new SimplexNoise3dTexture(256, 256, true);
		noiseTexture.update(0.07f, 0, 0, 0, 0, false, false);
		DebugView.setTexture("noiseTexture", noiseTexture.texture());
	}

	public void updateParticles(PGraphics pg, PImage imgMap) {
		// check to recompile shaders
		randomColorShader.update();
		simulationShader.update();
		renderShader.update();

		// update noise texture
		noiseTexture.update(5f, 0, 0, 0, FrameLoop.count(0.005f), false, false);

		// update particle positions
		simulationShader.shader().set("width", (float) pg.width);
		simulationShader.shader().set("height", (float) pg.height);
		simulationShader.shader().set("mapRandom", bufferRandom);
		simulationShader.shader().set("mapTexture", imgMap);
		simulationShader.shader().set("mapNoise", noiseTexture.texture());
		simulationShader.shader().set("speed", baseParticleSpeed);
		simulationShader.shader().set("decelCurve", mapDecelCurve);
		simulationShader.shader().set("resetPositionsDirty", resetPositionsDirty);
		bufferPositions.filter(simulationShader.shader());
    resetPositionsDirty = false;
	}

	public void drawParticles(PGraphics pg, PImage colorMap) {
		renderShader.shader().set("width", (float) pg.width);
		renderShader.shader().set("height", (float) pg.height);
		renderShader.shader().set("depth", 0f);
		renderShader.shader().set("mapPositions", bufferPositions);
		renderShader.shader().set("mapColor", colorMap);
		renderShader.shader().set("mapRandom", bufferRandom);
		renderShader.shader().set("pointSize", baseParticleSize);
		pg.shader(renderShader.shader());
		pg.blendMode(PBlendModes.ADD);
		pg.shape(shape);
		pg.resetShader();
	}

  public void setBaseParticleSpeed(float baseParticleSpeed) {
    this.baseParticleSpeed = baseParticleSpeed;
  }

  public void setBaseParticleSize(float baseParticleSize) {
    this.baseParticleSize = baseParticleSize;
  }

  public void setMapDecelCurve(float mapDecelCurve) {
    this.mapDecelCurve = mapDecelCurve;
  }

	public void resetRandomPositions() {
		resetPositionsDirty = true;
	}

  public PImage noiseTexture() {
    return noiseTexture.texture();
  }
}
