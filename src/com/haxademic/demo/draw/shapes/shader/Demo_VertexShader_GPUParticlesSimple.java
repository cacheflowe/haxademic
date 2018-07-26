package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesSimple 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics bufferPositions;
	protected PShader positionShader;
	protected PShader randomShader;
	protected PGraphics renderedParticles;
	protected PShader particleVerticesShader;
	int FRAMES = 300;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.WIDTH, 768);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty(AppSettings.FILLS_SCREEN, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void setupFirstFrame() {
		// build random particle placement shader
		positionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/random-pixel-color.glsl"));

		// create texture to store positions
		int positionBufferSize = 64;
		bufferPositions = p.createGraphics(positionBufferSize, positionBufferSize, PRenderers.P3D);
		OpenGLUtil.setTextureQualityLow(bufferPositions);		// necessary for proper texel lookup!
		p.debugView.setTexture(bufferPositions);
		newPositions();
		
		// build final draw buffer
		renderedParticles = p.createGraphics(p.width, p.height, PRenderers.P3D);
		renderedParticles.smooth(8);
		p.debugView.setTexture(renderedParticles);

		// count vertices for debugView
		int vertices = P.round(bufferPositions.width * bufferPositions.height); 
		p.debugView.setValue("Vertices", vertices);
		
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
		particleVerticesShader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/particle-vert-simple.glsl")
		);
	}
	
	protected void newPositions() {
		positionShader.set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		bufferPositions.filter(positionShader);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newPositions();
	}
	
	public void drawApp() {
		// clear the screen
		background(0);
		
		// draw shape w/shader
		particleVerticesShader.set("width", (float) bufferPositions.width);
		particleVerticesShader.set("height", (float) bufferPositions.height);
		particleVerticesShader.set("scale", 6f + 1f * P.sin(p.frameCount * 0.01f));
		particleVerticesShader.set("positionMap", bufferPositions);
		particleVerticesShader.set("pointSize", 6f);

		renderedParticles.beginDraw();
		renderedParticles.background(0);
		renderedParticles.translate(renderedParticles.width/2, renderedParticles.height/2, 0);
		renderedParticles.rotateY(P.map(p.mouseX, 0, p.width, -3f, 3f));
		renderedParticles.rotateX(P.map(p.mouseY, 0, p.height, 3f, -3f));
		renderedParticles.blendMode(PBlendModes.ADD);
		renderedParticles.shader(particleVerticesShader);  	// update positions
		renderedParticles.shape(shape);					// draw vertices
		renderedParticles.resetShader();
		renderedParticles.endDraw();

		// draw buffer to screen
		p.image(renderedParticles, 0, 0);
	}
		
}