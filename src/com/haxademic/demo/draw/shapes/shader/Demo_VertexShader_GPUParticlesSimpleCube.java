package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesSimpleCube 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int positionBufferSize = 128;
	protected PShape shape;
	protected PGraphics positionsBuffer;
	protected PShader positionShader;
	protected PShader randomShader;
	protected PGraphics renderedParticles;
	protected PShader particleVerticesShader;
	int FRAMES = 300;

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 768);
		Config.setProperty(AppSettings.HEIGHT, 768);
		Config.setProperty(AppSettings.FILLS_SCREEN, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// build random particle placement shader
		positionShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));

		// create texture to store positions
		positionsBuffer = PG.newDataPG(positionBufferSize, positionBufferSize);
		DebugView.setTexture("positionsBuffer", positionsBuffer);
		newPositions();
		
		// build final draw buffer
		renderedParticles = PG.newPG(p.width, p.height);
		DebugView.setTexture("renderedParticles", renderedParticles);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		DebugView.setValue("Vertices", shape.getVertexCount());

		// load shader
		particleVerticesShader = p.loadShader(
			FileUtil.getPath("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/point/particle-vert-simple.glsl")
		);
	}
	
	protected void newPositions() {
		positionShader.set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		positionsBuffer.filter(positionShader);
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newPositions();
	}
	
	protected void drawApp() {
		// clear the screen
		background(0);
		
		// draw shape w/shader
		particleVerticesShader.set("width", (float) positionBufferSize);
		particleVerticesShader.set("height", (float) positionBufferSize);
		particleVerticesShader.set("scale", (p.width / positionBufferSize) * 0.5f * (1f + 0.1f * P.sin(p.frameCount * 0.01f)));
		particleVerticesShader.set("positionMap", positionsBuffer);
		particleVerticesShader.set("pointSize", 6f + 2f * P.sin(p.frameCount * 0.01f));

		renderedParticles.beginDraw();
		renderedParticles.background(0);
		PG.setCenterScreen(renderedParticles);
		PG.basicCameraFromMouse(renderedParticles);
		renderedParticles.blendMode(PBlendModes.ADD);
		renderedParticles.shader(particleVerticesShader);  	// update positions
		renderedParticles.shape(shape);					// draw vertices
		renderedParticles.resetShader();
		renderedParticles.endDraw();

		// draw buffer to screen
		p.image(renderedParticles, 0, 0);
	}
		
}