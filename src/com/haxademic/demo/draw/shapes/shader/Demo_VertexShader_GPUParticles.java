package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticles 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture perlin;
	protected BaseTexture audioTexture;

	protected PShape shape;
	protected PGraphics bufferPositions;
	protected PGraphics bufferDirection;
	protected PGraphics bufferAmp;
	protected PShader positionMover;
	protected PShader directionGenerator;
	protected PShader ampGenerator;
	protected PGraphics bufferRenderedParticles;
	protected PShader pointsParticleVertices;
	protected PGraphics colorBuffer;
	protected PShader colorMapShader;
	float w = 1024;
	float h = 1024;
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
		// build offscreen buffer (thing don't work the same on the main drawing surface)
		// We need a 32-bit float texture!
		// GL.GL_RGBA32F - but how?
		// int format = GL.GL_RGBA32F;
		// - https://github.com/processing/processing/issues/3321
		// - https://www.javatips.net/api/com.jogamp.opengl.gl2
		// - http://forum.jogamp.org/GL-RGBA32F-with-glTexImage2D-td4035766.html
		bufferPositions = p.createGraphics((int) w, (int) h, PRenderers.P3D);
		OpenGLUtil.setTextureQualityLow(bufferPositions);		// necessary for proper texel lookup!
		bufferDirection = p.createGraphics((int) w, (int) h, PRenderers.P3D);
		bufferDirection.noSmooth();
		bufferAmp = p.createGraphics((int) w, (int) h, PRenderers.P3D);
		bufferRenderedParticles = p.createGraphics(p.width, p.height, PRenderers.P3D);
		bufferRenderedParticles.smooth(8);
		colorBuffer = p.createGraphics(p.width, p.height, PRenderers.P3D);
		colorBuffer.smooth(8);
		
		// build displacement maps
//		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-liquid-moire.glsl"));
//		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/iq-voronoise.glsl"));
//		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/square-fade.glsl"));
		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));
		ampGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));
		colorMapShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/light-leak.glsl"));

		// build particle mover shader - uses displacement map to move particles
		positionMover = p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-mover-frag.glsl"));
		resetParticlePositions();
		
		// count vertices for debugView
		int vertices = P.round(w * h); 
		p.debugView.setValue("Vertices", vertices);
		
		// Build points vertices
		shape = P.p.createShape();
		shape.beginShape(PConstants.POINTS);
		shape.stroke(255);
		shape.strokeWeight(1);
		shape.noFill();
		for (int i = 0; i < vertices; i++) {
			float x = i % w;
			float y = P.floor(i / w);
			if(y % 2 == 1) x = w - x - 1;
			shape.vertex(x/w, y/h, 0); // x/y coords are used as UV coords (0-1), and multplied by `spread` uniform
		}
		shape.endShape();
		shape.setTexture(bufferDirection);	// PShape really wants a default texture
		
		// load shader
		pointsParticleVertices = loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/particle-vert.glsl")
		);
	}

	public void resetParticlePositions() {
		bufferPositions.beginDraw();
		bufferPositions.background(127);					// start in center
		bufferPositions.endDraw();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') resetParticlePositions();
	}
	
	public void drawApp() {
		// clear the screen
		background(0);
		
		// update colors
		colorMapShader.set("time", p.frameCount * 0.004f);
		colorBuffer.filter(colorMapShader);
		p.debugView.setTexture(colorBuffer);

		// update direction texture
		directionGenerator.set("time", p.frameCount * 0.004f);
		directionGenerator.set("zoom", 3f + 1f * P.sin(p.frameCount * 0.01f));
		bufferDirection.filter(directionGenerator);				// noise to change directions
		p.debugView.setTexture(bufferDirection);

		// update amp texture
		ampGenerator.set("zoom", 2f);
		ampGenerator.set("time", (p.frameCount + 1000) * 0.004f);
		bufferAmp.filter(ampGenerator);				// noise to change directions
		p.debugView.setTexture(bufferAmp);
		
		// update particle positions
		positionMover.set("directionMap", bufferDirection);
		positionMover.set("ampMap", bufferAmp);
		positionMover.set("amp", P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));
		bufferPositions.filter(positionMover);
		p.debugView.setTexture(bufferPositions);
		
		// move to screen center
		bufferRenderedParticles.beginDraw();
		bufferRenderedParticles.background(0);
//		bufferParticles.translate(bufferParticles.width/2f, bufferParticles.height/2f, 0);
		// draw vertex points. strokeWeight w/disableStyle works here for point size
//		shape.disableStyle();
		bufferRenderedParticles.translate(0, -p.height * 0.3f, -p.height);
		bufferRenderedParticles.rotateX(1.2f);
		bufferRenderedParticles.strokeWeight(1f);
		bufferRenderedParticles.blendMode(PBlendModes.ADD);
		pointsParticleVertices.set("positionMap", bufferPositions);
		pointsParticleVertices.set("colorMap", colorBuffer);
		pointsParticleVertices.set("pointSize", 1f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsParticleVertices.set("width", (float) p.width);
		pointsParticleVertices.set("height", (float) p.height);
		bufferRenderedParticles.shader(pointsParticleVertices);  	// update positions
		bufferRenderedParticles.shape(shape);					// draw vertices
		bufferRenderedParticles.resetShader();
		bufferRenderedParticles.endDraw();
		p.debugView.setTexture(bufferRenderedParticles);

		// draw buffer to screen
		p.image(bufferRenderedParticles, 0, 0);
	}
		
}