package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesWarp 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics bufferPositions;
	protected PShader randomColorShader;
	protected PShader particleMoverShader;
	protected PShader particlesDrawShader;
	protected PShader speedShader;
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
		randomColorShader = p.loadShader(FileUtil.getFile("shaders/textures/random-pixel-color.glsl"));
		speedShader = p.loadShader(FileUtil.getFile("shaders/textures/noise-simplex-2d-iq.glsl"));
		particleMoverShader = p.loadShader(FileUtil.getFile("shaders/point/particle-warp-z-mover.glsl"));

		// create texture to store positions
		int positionBufferSize = 256;
		bufferPositions = p.createGraphics(positionBufferSize, positionBufferSize, PRenderers.P3D);
		OpenGLUtil.setTextureQualityLow(bufferPositions);		// necessary for proper texel lookup!
		p.debugView.setTexture(bufferPositions);
		p.debugView.setValue("numParticles", positionBufferSize * positionBufferSize);
		newPositions();
		
		// count vertices for debugView
		int vertices = P.round(bufferPositions.width * bufferPositions.height); 
		// p.debugView.setValue("Vertices", vertices);
		
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
		particlesDrawShader = p.loadShader(
			FileUtil.getFile("shaders/point/point-frag.glsl"), 
			FileUtil.getFile("shaders/point/particle-warp-vert.glsl")
		);	
		
		// !test - processing bug :( https://github.com/processing/processing-docs/issues/172
		//shape.attrib("test_att", 1.0f); // doesn't work

	}
	
	protected void newPositions() {
		randomColorShader.set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		bufferPositions.filter(randomColorShader);
	}

	public void preDraw() {
	}
	
	public void drawApp() {
		p.background(0);
		DrawUtil.setCenterScreen(p);
		
		// update particle positions
		bufferPositions.filter(particleMoverShader);
		p.debugView.setTexture(bufferPositions);

		// draw shape w/shader
		float particlesScale = p.mousePercentX() * 100f;
		particlesDrawShader.set("width", (float) bufferPositions.width);
		particlesDrawShader.set("height", (float) bufferPositions.height);
		particlesDrawShader.set("scale", particlesScale);
		particlesDrawShader.set("positionMap", bufferPositions);
		particlesDrawShader.set("pointSize", p.mousePercentY() * 10f);
		p.shader(particlesDrawShader);  	// update positions
		p.shape(shape);					// draw vertices
		p.resetShader();
	}
	

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newPositions();
	}
	
}