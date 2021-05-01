package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class ParticleLauncherGPU {
	
	protected int positionBufferSize;
	protected PShape shape;
	protected PGraphics colorBuffer;
	protected PGraphics positionBuffer;
//	protected PShader positionShader;
	protected PShaderHotSwap positionShaderHotSwap;
	protected PShaderHotSwap particlesRenderHotSwap;
	protected int vertices = 0;

//	protected PShader particlesRenderShader;
	protected int launchIndex = 0;

	public ParticleLauncherGPU(int size) {
		positionBufferSize = size;
		// build random particle placement shader
//		positionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-launcher-frag.glsl"));
		positionShaderHotSwap = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/point/particle-launcher-frag.glsl"));

		
		// create texture to store positions
		colorBuffer = PG.newDataPG(positionBufferSize, positionBufferSize);
		DebugView.setTexture("colorBuffer", colorBuffer);
		colorBuffer.beginDraw();
		colorBuffer.background(255);
		colorBuffer.noStroke();
		colorBuffer.endDraw();
		
		positionBuffer = PG.newDataPG(positionBufferSize, positionBufferSize);
		positionBuffer.beginDraw();
		positionBuffer.background(255);
		positionBuffer.noStroke();
		positionBuffer.endDraw();
		
		// Build points vertices
		vertices = P.round(positionBufferSize * positionBufferSize); 
		shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		
		// load shader
//		particlesRenderShader = P.p.loadShader(
		particlesRenderHotSwap = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/point/particle-launcher-vert.glsl"),
			FileUtil.getPath("haxademic/shaders/point/points-default-frag.glsl")
		);
	}
	
	public PGraphics positionBuffer() {
		return positionBuffer;
	}
	
	public int vertices() {
		return vertices;
	}
	
	protected int getGridX(int size, int index) {
		return index % size;
	}
	
	protected int getGridY(int size, int index) {
		return P.floor(index / size);
	}
	
	public void beginLaunch() {
		DebugView.setTexture("progressBuffer", positionBuffer);
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
		float launchX = (x / P.p.width) * 255f;
		float launchY = (y / P.p.height) * 255f;
		float radians = MathUtil.randRangeDecimal(0f, 255f);
		float progress = 255f;	// reset progress to 100%, always, since alpha is additive. not sure if there's a good way to reset alpha to non-100% 
		int positionColor = P.p.color(launchX, launchY, radians, progress);
		positionBuffer.fill(positionColor);
		positionBuffer.rect(texX, texY, 1, 1);
	}
	
	public void update() {
		// update particle movement
//		positionBuffer.filter(positionShader);
		positionBuffer.filter(positionShaderHotSwap.shader());
	}
	
	public void renderTo(PGraphics buffer) {
		// update vertex/rendering shader props
		PShader renderShader = particlesRenderHotSwap.shader();
		renderShader.set("width", (float) buffer.width);
		renderShader.set("height", (float) buffer.height);
		renderShader.set("depth", (float) buffer.width);
		renderShader.set("colorTexture", colorBuffer);
		renderShader.set("colorTexture", DemoAssets.textureJupiter());
		renderShader.set("colorTexture", ImageGradient.BLACK_HOLE());
		renderShader.set("positionTexture", positionBuffer);
		renderShader.set("pointSize", 3f);
		
		buffer.shader(renderShader);	// set vertex shader
		buffer.shape(shape);			// draw particles
		buffer.resetShader();

		// recompile if needed & show shader compile error messages
		positionShaderHotSwap.update();
		particlesRenderHotSwap.update();
//		positionShaderHotSwap.showShaderStatus(buffer);
	}
}