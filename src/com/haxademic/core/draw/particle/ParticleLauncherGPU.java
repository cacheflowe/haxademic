package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

public class ParticleLauncherGPU {
	
	protected int positionBufferSize;
	protected int numParticles = 0;
	protected int launchIndex = 0;
	
	protected PShape shape;
	protected PGraphics colorBuffer;
	protected PGraphics positionBuffer;
	
	protected PShaderHotSwap particlesSimulationHotSwap;
	protected PShaderHotSwap particlesRenderHotSwap;
	protected static String simulationShaderFragPath = "haxademic/shaders/point/particle-simulation-frag.glsl";
	protected static String renderShaderFragPath = "haxademic/shaders/point/points-default-frag.glsl";
	protected static String renderShaderVertPath = "haxademic/shaders/point/particle-launcher-render-points-vert.glsl";

	protected PImage particleTexture;
	
	// points props
	protected float pointSize = 2;
	protected float rotateAmp = 1;
	protected PVector gravity = new PVector();

	public ParticleLauncherGPU(int size) {
		this(size, simulationShaderFragPath, renderShaderFragPath, renderShaderVertPath, null);
	}
	
	public ParticleLauncherGPU(int size, String simFragPath) {
		this(size, simFragPath, renderShaderFragPath, renderShaderVertPath, null);
	}
	
	public ParticleLauncherGPU(int size, String simFragPath, String renderFragPath, String renderVertPath, PImage texture) {
		positionBufferSize = size;
		numParticles = P.round(positionBufferSize * positionBufferSize);
		
		// build random particle placement shader
		particlesSimulationHotSwap = new PShaderHotSwap(FileUtil.getPath(simFragPath));
		
		// create texture to store positions
		colorBuffer = PG.newPG(positionBufferSize, positionBufferSize);
		colorBuffer.beginDraw();
		colorBuffer.background(255);
		colorBuffer.noStroke();
		colorBuffer.endDraw();
		
		positionBuffer = PG.newDataPG(positionBufferSize, positionBufferSize);
		positionBuffer.beginDraw();
		positionBuffer.background(255, 0);
		positionBuffer.noStroke();
		positionBuffer.endDraw();
		
		// Build points vertices or textured planes
		particleTexture = texture;
		if(particleTexture != null) {
			shape = PShapeUtil.texturedParticlesShapeForGPUData(positionBufferSize, positionBufferSize, 1, particleTexture);
		} else {
			shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		}
		
		// load shader
//		particlesRenderShader = P.p.loadShader(
		particlesRenderHotSwap = new PShaderHotSwap(FileUtil.getPath(renderVertPath), FileUtil.getPath(renderFragPath));
	}
	
	public PShader simulationShader() { return particlesSimulationHotSwap.shader(); }
	public PShader renderShader() { return particlesRenderHotSwap.shader(); }
	public PGraphics positionBuffer() { return positionBuffer; }
	public PGraphics colorBuffer() { return colorBuffer; }
	public int numParticles() { return numParticles; }
	public ParticleLauncherGPU pointSize(float pointSize) { this.pointSize = pointSize; return this; }
	public ParticleLauncherGPU rotateAmp(float rotateAmp) { this.rotateAmp = rotateAmp; return this; }
	public ParticleLauncherGPU gravity(float gravityX, float gravityY) { this.gravity.set(gravityX, gravityY); return this; }
	
	public static int getGridX(int size, int index) {
		return index % size;
	}
	
	public static int getGridY(int size, int index) {
		return P.floor(index / size);
	}
	
	public void beginLaunch() {
		positionBuffer.beginDraw();
		positionBuffer.blendMode(PBlendModes.REPLACE); // ensures proper re-spawning and not additive alpha channel
	}
	
	public void endLaunch() {
		positionBuffer.endDraw();
	}
	
	public void launch(PGraphics buffer, float x, float y) {
		// writes new pixels to reset particles
		// set next launch index  
		launchIndex++;
		launchIndex = launchIndex % numParticles; // (shape.getVertexCount());

		// get x/y coords for that pixel to reset properties
		int texX = getGridX(positionBufferSize, launchIndex);
		int texY = getGridY(positionBufferSize, launchIndex);
		
		// reset progress
		// set new position color: // rgba = x, y, rotation, speed
		float launchX = (x / buffer.width) * 255f;
		float launchY = (y / buffer.height) * 255f;
		float radians = MathUtil.randRangeDecimal(0f, 255f);
		float progress = 255f;	// reset progress to 100%, always, since alpha is additive. switch to pixels[].set() 
		int positionColor = P.p.color(launchX, launchY, radians, progress);
		positionBuffer.fill(positionColor);
		positionBuffer.rect(texX, texY, 1, 1);
	}
	
	public void updateSimulation() {
		// update particle movement
		simulationShader().set("gravity", gravity.x, gravity.y);
		particlesSimulationHotSwap.update();
		positionBuffer.filter(particlesSimulationHotSwap.shader());
	}
	
	public void renderTo(PGraphics buffer) {
		renderTo(buffer, false);
	}
	
	public void renderTo(PGraphics buffer, boolean translateCenter) {
		// recompile if needed & show shader compile error messages
		particlesRenderHotSwap.update();
//		positionShaderHotSwap.showShaderStatus(buffer);
		
		// update vertex/rendering shader props
		renderShader().set("width", (float) buffer.width);
		renderShader().set("height", (float) buffer.height);
		renderShader().set("depth", (float) buffer.width);
		renderShader().set("colorMap", colorBuffer);
		renderShader().set("positionMap", positionBuffer);
		renderShader().set("pointSize", pointSize);
		renderShader().set("rotateAmp", rotateAmp);
		if(particleTexture != null) {
			renderShader().set("textureParticle", particleTexture);
		}
		
		buffer.push();
		buffer.shader(renderShader());	// set vertex shader
		if(translateCenter) buffer.translate(buffer.width/2, buffer.height/2);
		buffer.shape(shape);			// draw particles
		buffer.resetShader();
		buffer.pop();
	}
}