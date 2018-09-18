package com.haxademic.core.draw.particle;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class ParticleLauncher {
	
	protected int positionBufferSize = 32;
	protected PShape shape;
	protected PGraphics colorBuffer;
	protected PGraphics progressBuffer;
	protected PShader positionShader;
	protected int vertices = 0;

	protected PShader particlesRenderShader;
	protected int launchIndex = 0;

	public ParticleLauncher() {
		// build random particle placement shader
		positionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-launcher-frag.glsl"));
		
		// create texture to store positions
		colorBuffer = P.p.createGraphics(positionBufferSize, positionBufferSize, PRenderers.P2D);
		OpenGLUtil.setTextureQualityLow(colorBuffer);		// necessary for proper texel lookup!
//		p.debugView.setTexture(colorBuffer);
		colorBuffer.beginDraw();
		colorBuffer.background(255);
		colorBuffer.noStroke();
		colorBuffer.endDraw();
		
		progressBuffer = P.p.createGraphics(positionBufferSize, positionBufferSize, PRenderers.P3D);
		OpenGLUtil.setTextureQualityLow(progressBuffer);		// necessary for proper texel lookup!
		
		progressBuffer.beginDraw();
		progressBuffer.background(255);
		progressBuffer.noStroke();
		progressBuffer.endDraw();
//		P.p.debugView.setTexture(progressBuffer);
		
		// count vertices for debugView
		vertices = P.round(positionBufferSize * positionBufferSize); 
		
		// Build points vertices
		shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		for (int i = 0; i < vertices; i++) {
			float x = i % positionBufferSize;
			float y = P.floor(i / positionBufferSize);
			shape.vertex(x/(float)positionBufferSize, y/(float)positionBufferSize, 0); // x/y coords are used as UV coords for position map (0-1)
		}
		shape.endShape();
		
		// load shader
		particlesRenderShader = P.p.loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/particle-launcher-vert.glsl")
		);
	}
	
	public PGraphics progressBuffer() {
		return progressBuffer;
	}
	
	public int vertices() {
		return vertices;
	}
	
	protected float getGridX(float size, float index) {
		return index % size;
	}
	
	protected float getGridY(float size, float index) {
		return P.floor(index / size);
	}
	
	public void beginLaunch() {
		progressBuffer.beginDraw();
	}
	
	public void endLaunch() {
		progressBuffer.endDraw();
	}
	
	public void launch(float x, float y) {
		// get launch index - let's skip the last row, because something is breaking there. might be uv coords or something else...
		launchIndex++;
		if(launchIndex > (shape.getVertexCount() - positionBufferSize)) launchIndex = positionBufferSize;
//		launchIndex = launchIndex % (shape.getVertexCount() - positionBufferSize);
//		p.debugView.setValue("shape.getVertexCount()", shape.getVertexCount());
//		p.debugView.setValue("launchIndex", launchIndex);
//		p.debugView.setValue("launch x,y", getGridX(positionBufferSize, launchIndex) + ", " + getGridY(positionBufferSize, launchIndex));
		
		// reset progress
		progressBuffer.fill(MathUtil.randRangeDecimal(127f, 255f), MathUtil.randRangeDecimal(127, 255), MathUtil.randRangeDecimal(0f, 255f), 255);	// rgba = distAmp, size, rotation, progress
		progressBuffer.rect(getGridX(positionBufferSize, launchIndex), getGridY(positionBufferSize, launchIndex), 1, 1);
		
		// set particle color
//		coloffer.beginDraw();
//		colorBuffer.noStroke();
//		colorBuffer.fill(255f * (0.75f + 0.25f * P.sin(launchIndex * 0.1f)), 255f * (0.75f + 0.25f * P.sin(launchIndex * 0.3f)), 255f * (0.75f + 0.25f * P.sin(launchIndex * 0.2f)));
//		colorBuffer.rect(getGridX(positionBufferSize, launchIndex), getGridY(positionBufferSize, launchIndex), 1, 1);
//		colorBurBuffer.endDraw();
		
		// iterate through vertices and place on mouse
		int vertexIndex = (launchIndex + positionBufferSize) % shape.getVertexCount();	// offset for some reason... this has something to do with launchIndex skipping the last row
		shape.setVertex(vertexIndex, x, y, 0);

	}
	
	public void update() {
		// update particle movement
		positionShader.set("progressSpeed", (1f / 255f) * 3.5f);
		progressBuffer.filter(positionShader);
		
		// update vertex/rendering shader props
		particlesRenderShader.set("width", (float) positionBufferSize);
		particlesRenderShader.set("height", (float) positionBufferSize);
		particlesRenderShader.set("colorTexture", colorBuffer);
//		particlesRenderShader.set("colorTexture", DemoAssets.justin());
		particlesRenderShader.set("progressTexture", progressBuffer);
		particlesRenderShader.set("pointSize", 4f);
		particlesRenderShader.set("progressDistance", (float) P.p.width * 0.1f);
		particlesRenderShader.set("gravity", 2f);
		particlesRenderShader.set("mode", P.p.mousePercentY());	// test gl_VertexID method of accessing texture positions
	}
	
	public void renderTo(PGraphics buffer) {
		buffer.shader(particlesRenderShader);  	// update positions
		buffer.shape(shape);					// draw vertices
		buffer.resetShader();
	}
}