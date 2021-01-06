package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticles 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

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

	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 960);
		Config.setProperty(AppSettings.FILLS_SCREEN, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// build offscreen buffer (thing don't work the same on the main drawing surface)
		// We need a 32-bit float texture!
		// GL.GL_RGBA32F - but how?
		// int format = GL.GL_RGBA32F;
		// - https://github.com/processing/processing/issues/3321
		// - https://www.javatips.net/api/com.jogamp.opengl.gl2
		// - http://forum.jogamp.org/GL-RGBA32F-with-glTexImage2D-td4035766.html
		bufferPositions = PG.newDataPG((int) w, (int) h);
		bufferDirection = PG.newDataPG((int) w, (int) h); // p.createGraphics((int) w, (int) h, PRenderers.P3D);
//		bufferDirection.noSmooth();
		bufferAmp = PG.newDataPG((int) w, (int) h); // p.createGraphics((int) w, (int) h, PRenderers.P3D);
		bufferRenderedParticles = PG.newPG32(p.width, p.height, false, false); // p.createGraphics(p.width, p.height, PRenderers.P3D);
//		bufferRenderedParticles.smooth(8);
		colorBuffer = PG.newPG32(p.width, p.height, false, false);
//		colorBuffer.smooth(8);

		DebugView.setTexture("colorBuffer", colorBuffer);
		DebugView.setTexture("bufferDirection", bufferDirection);
		DebugView.setTexture("bufferAmp", bufferAmp);
		DebugView.setTexture("bufferPositions", bufferPositions);
		DebugView.setTexture("bufferRenderedParticles", bufferRenderedParticles);

		// build displacement maps
//		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/cacheflowe-liquid-moire.glsl"));
//		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/iq-voronoise.glsl"));
//		directionGenerator = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/square-fade.glsl"));
		directionGenerator = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));
		ampGenerator = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/noise-simplex-2d-iq.glsl"));
		colorMapShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/light-leak.glsl"));

		// build particle mover shader - uses displacement map to move particles
		positionMover = p.loadShader(FileUtil.getPath("haxademic/shaders/point/particle-mover-frag.glsl"));
		resetParticlePositions();
		
		// count vertices for debugView
		int vertices = P.round(w * h); 
		DebugView.setValue("Vertices", vertices);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData((int)w);
		shape.setTexture(bufferDirection);	// PShape really wants a default texture
		
		// load shader
		pointsParticleVertices = loadShader(
			FileUtil.getPath("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getPath("haxademic/shaders/point/particle-vert.glsl")
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
	
	protected void drawApp() {
		// clear the screen
		p.background(0);
		PG.setDrawCorner(p);
		
		
		// float adjust increment - must be 1f/255f is using Processing's default 8-bit textures
		// but can be smaller using 32-bit support
		float minAmp = 0.00001f;
		
		// update colors
		colorMapShader.set("time", p.frameCount * 0.01f);
		colorBuffer.filter(colorMapShader);

		// update direction texture
		directionGenerator.set("time", p.frameCount * 0.01f);
		directionGenerator.set("zoom", 3f + 2f * P.sin(p.frameCount * 0.01f));
		directionGenerator.set("offset", p.frameCount * 0.003f, p.frameCount * 0.003f);
		bufferDirection.filter(directionGenerator);				// noise to change directions
		ContrastFilter.instance(p).setContrast(1.5f);
		ContrastFilter.instance(p).applyTo(bufferDirection);
//		EdgeColorDarkenFilter.instance(p).applyTo(bufferDirection);

		// update amp texture
		ampGenerator.set("zoom", 1f);
		ampGenerator.set("time", (p.frameCount + 1000) * minAmp);
		ampGenerator.set("offset", p.frameCount * -0.003f, p.frameCount * 0.003f);
		bufferAmp.filter(ampGenerator);				// noise to change directions
		
		// update particle positions
		positionMover.set("directionMap", bufferDirection);
		positionMover.set("ampMap", bufferAmp);
		positionMover.set("amp", 0.001f);// * (0.5f + 0.3f * P.sin(p.frameCount/20f))); // P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));
		bufferPositions.filter(positionMover);
		
		// update render shader
		float renderW = bufferRenderedParticles.width * 5f;
		float renderH = bufferRenderedParticles.height * 5f;
		pointsParticleVertices.set("positionMap", bufferPositions);
		pointsParticleVertices.set("colorMap", colorBuffer);
		pointsParticleVertices.set("pointSize", 2f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsParticleVertices.set("width", (float) renderW);
		pointsParticleVertices.set("height", (float) renderH);
		pointsParticleVertices.set("depth", (float) renderW/3f);

		// render particles
		bufferRenderedParticles.beginDraw();
		bufferRenderedParticles.background(0);
		bufferRenderedParticles.translate(0, 0, -p.width);
		PG.setDrawCorner(bufferRenderedParticles);
		PG.basicCameraFromMouse(bufferRenderedParticles);
		// draw vertex points. strokeWeight w/disableStyle works here for point size
//		shape.disableStyle();
		bufferRenderedParticles.strokeWeight(2f);
		bufferRenderedParticles.blendMode(PBlendModes.BLEND);
		bufferRenderedParticles.shader(pointsParticleVertices);  	// update positions
		bufferRenderedParticles.shape(shape);					// draw vertices
		bufferRenderedParticles.resetShader();
		bufferRenderedParticles.endDraw();

		// draw buffer to screen
		p.image(bufferRenderedParticles, 0, 0);
	}
		
}