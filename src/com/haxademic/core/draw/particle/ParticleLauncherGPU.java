package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class ParticleLauncherGPU {
	
	protected int positionBufferSize;
	protected int vertices = 0;
	protected int launchIndex = 0;
	
	protected PShape shape;
	protected PGraphics colorBuffer;
	protected PGraphics positionBuffer;
	
	protected PShaderHotSwap particlesSimulationHotSwap;
	protected PShaderHotSwap particlesRenderHotSwap;
	protected static String simulationShaderFragPath = "haxademic/shaders/point/particle-simulation-frag.glsl";
	protected static String renderShaderFragPath = "haxademic/shaders/point/points-default-frag.glsl";
	protected static String renderShaderVertPath = "haxademic/shaders/point/particle-launcher-render-points-vert.glsl";

	// points props
	protected float pointSize = 2;

	public ParticleLauncherGPU(int size) {
		this(size, simulationShaderFragPath);
	}
	
	public ParticleLauncherGPU(int size, String shaderPath) {
		positionBufferSize = size;
		// build random particle placement shader
		particlesSimulationHotSwap = new PShaderHotSwap(FileUtil.getPath(shaderPath));
		
		// create texture to store positions
		colorBuffer = PG.newDataPG(positionBufferSize, positionBufferSize);
		colorBuffer.beginDraw();
		colorBuffer.background(255);
		colorBuffer.noStroke();
		colorBuffer.endDraw();
		
		positionBuffer = PG.newDataPG(positionBufferSize, positionBufferSize);
		positionBuffer.beginDraw();
		positionBuffer.background(255, 0);
		positionBuffer.noStroke();
		positionBuffer.endDraw();
		
		// Build points vertices
		vertices = P.round(positionBufferSize * positionBufferSize); 
		shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		
		// load shader
//		particlesRenderShader = P.p.loadShader(
		particlesRenderHotSwap = new PShaderHotSwap(FileUtil.getPath(renderShaderVertPath), FileUtil.getPath(renderShaderFragPath));
	}
	
	public PGraphics positionBuffer() { return positionBuffer; }
	public PGraphics colorBuffer() { return colorBuffer; }
	public int vertices() { return vertices; }
	public ParticleLauncherGPU pointSize(float pointSize) { this.pointSize = pointSize; return this; }
	
	public static int getGridX(int size, int index) {
		return index % size;
	}
	
	public static int getGridY(int size, int index) {
		return P.floor(index / size);
	}
	
	public void beginLaunch() {
		positionBuffer.beginDraw();
	}
	
	public void endLaunch() {
		positionBuffer.endDraw();
	}
	
	public void launch(PGraphics buffer, float x, float y) {
		// writes new pixels to reset particles
		// set next launch index  
		launchIndex++;
		launchIndex = launchIndex % (shape.getVertexCount());

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
	
	public void update() {
		// update particle movement
//		positionBuffer.filter(positionShader);
		positionBuffer.filter(particlesSimulationHotSwap.shader());
	}
	
	public void renderTo(PGraphics buffer) {
		renderTo(buffer, false);
	}
	
	public void renderTo(PGraphics buffer, boolean translateCenter) {
		// update vertex/rendering shader props
		PShader renderShader = particlesRenderHotSwap.shader();
		renderShader.set("width", (float) buffer.width);
		renderShader.set("height", (float) buffer.height);
		renderShader.set("depth", (float) buffer.width);
		renderShader.set("colorTexture", colorBuffer);
		renderShader.set("positionTexture", positionBuffer);
		renderShader.set("pointSize", pointSize);
		
		buffer.shader(renderShader);	// set vertex shader
		if(translateCenter) buffer.translate(buffer.width/2, buffer.height/2);
		buffer.shape(shape);			// draw particles
		buffer.resetShader();

		// recompile if needed & show shader compile error messages
		particlesSimulationHotSwap.update();
		particlesRenderHotSwap.update();
//		positionShaderHotSwap.showShaderStatus(buffer);
	}
}