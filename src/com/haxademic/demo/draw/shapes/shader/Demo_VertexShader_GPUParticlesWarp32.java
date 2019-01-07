package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGL32Util;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLSLProgram;
import com.thomasdiewald.pixelflow.java.dwgl.DwGLTexture;

import processing.core.PConstants;
import processing.core.PShape;
import processing.opengl.PGraphics2D;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesWarp32 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics2D texturePositions;
	protected DwGLTexture texturePositions32;
	protected PShader randomColorShader;
	protected DwGLSLProgram particleMoverShader;
	protected PShader particlesDrawShader;
	protected PShader speedShader;
	protected int positionBufferSize = 256;
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
		speedShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));

		// 32-bit shader to move particles
//		particleMoverShader = p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-warp-z-mover.glsl"));
		particleMoverShader = OpenGL32Util.newShader(FileUtil.getFile("haxademic/shaders/float32/particle-warp-mover.glsl"));
		
		// create texture to store positions
		texturePositions32 = OpenGL32Util.newTexture32(positionBufferSize, positionBufferSize);
		// backing PGraphics
		texturePositions = OpenGL32Util.newPGraphics2D(positionBufferSize, positionBufferSize);
		OpenGLUtil.setTextureQualityLow(texturePositions);		// necessary for proper texel lookup!
		p.debugView.setTexture(texturePositions);
		p.debugView.setValue("numParticles", positionBufferSize * positionBufferSize);
		newPositions();
		
		// count vertices for debugView
		int vertices = P.round(positionBufferSize * positionBufferSize); 
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
		
		// load particle shader
		particlesDrawShader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/particle-warp-vert.glsl")
		);	
	}
	
	protected void newPositions() {
		randomColorShader.set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		texturePositions.filter(randomColorShader);
		OpenGL32Util.pGraphics2dToTexture32(texturePositions, texturePositions32);
	}

	public void preDraw() {
	}
	
	public void drawApp() {
		p.background(0);
		DrawUtil.setCenterScreen(p);
		p.translate(0, 0, -p.height);
		
		// camera
		DrawUtil.basicCameraFromMouse(p.g);
		
		// update particle positions
	    OpenGL32Util.context().begin();
	    OpenGL32Util.context().beginDraw(texturePositions32);
	    particleMoverShader.begin();
	    particleMoverShader.uniformTexture("texture", texturePositions32);
	    particleMoverShader.uniform2f("resolution", positionBufferSize, positionBufferSize);
	    particleMoverShader.drawFullScreenQuad();
	    particleMoverShader.end();	  
	    OpenGL32Util.context().endDraw();
	    OpenGL32Util.context().end();
	    // and copy back to PGRaphics
	    OpenGL32Util.texture32ToPGraphics2d(texturePositions32, texturePositions);

		// draw shape w/shader
		float particlesScale = 3f; // p.mousePercentX() * 100f;
		particlesDrawShader.set("width", (float) positionBufferSize);
		particlesDrawShader.set("height", (float) positionBufferSize);
		particlesDrawShader.set("scale", particlesScale);
		particlesDrawShader.set("positionMap", texturePositions);
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