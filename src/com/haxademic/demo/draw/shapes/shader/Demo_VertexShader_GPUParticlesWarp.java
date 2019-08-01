package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
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
	protected String WIDTH = "WIDTH";
	protected String HEIGHT = "HEIGHT";
	protected String DEPTH = "DEPTH";
	protected String POINT_SIZE = "POINT_SIZE";
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
		randomColorShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/random-pixel-color.glsl"));
		particleMoverShader = p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-warp-z-mover.glsl"));

		// create texture to store positions
		int positionBufferSize = 1024;
		bufferPositions = PG.newDataPG(positionBufferSize, positionBufferSize);
		p.debugView.setTexture("bufferPositions", bufferPositions);
		newPositions();
		
		// count vertices for debugView
		int vertices = P.round(positionBufferSize * positionBufferSize); 
		p.debugView.setValue("numParticles", vertices);
		
		// Build points vertices
		shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		for (int i = 0; i < vertices; i++) {
			float x = i % positionBufferSize;
			float y = P.floor(i / positionBufferSize);
			shape.vertex(x/(positionBufferSize-1f), y/(positionBufferSize-1f), 0); // x/y coords are used as UV coords for position map (0-1)
		}
		
		shape.endShape();
		
		// load shader
		particlesDrawShader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/particle-warp-vert.glsl")
		);	
		
		// build UI
		p.ui.addSlider(WIDTH, 256, 0, 4196, 1, false);
		p.ui.addSlider(HEIGHT, 256, 0, 4196, 1, false);
		p.ui.addSlider(DEPTH, 256, 0, 4196, 1, false);
		p.ui.addSlider(POINT_SIZE, 1, 0.1f, 20, 0.1f, false);
	}
	
	protected void newPositions() {
		randomColorShader.set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		bufferPositions.filter(randomColorShader);
	}

	public void preDraw() {
	}
	
	public void drawApp() {
		p.background(0);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);
		
		// update particle positions
		bufferPositions.filter(particleMoverShader);

		// draw shape w/shader
		particlesDrawShader.set("width", p.ui.value(WIDTH));
		particlesDrawShader.set("height", p.ui.value(HEIGHT));
		particlesDrawShader.set("depth", p.ui.value(DEPTH));
		particlesDrawShader.set("positionMap", bufferPositions);
		particlesDrawShader.set("pointSize", p.ui.value(POINT_SIZE));
		p.shader(particlesDrawShader);  	// update positions
		p.shape(shape);						// draw vertices
		p.resetShader();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newPositions();
	}
	
}