package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.app.haxmapper.textures.BaseTexture;
import com.haxademic.app.haxmapper.textures.TextureEQGrid;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.MirrorFilter;
import com.haxademic.core.draw.image.PerlinTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_PointsParticles_WIP 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture perlin;
	protected BaseTexture audioTexture;

	protected PShape shape;
//	protected PImage texture;
	protected PGraphics bufferPositions;
	protected PGraphics bufferDirection;
	protected PShader positionMover;
	protected PShader displacementGenerator;
	protected PGraphics bufferParticles;
	protected PShader pointsParticleVertices;
	float w = 32;//1024;
	float h = 32;//512;
	int FRAMES = 300;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 512);
		p.appConfig.setProperty(AppSettings.FULLSCREEN, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void setupFirstFrame() {
		// build offsecreen buffer (thing don't work the same on the main drawing surface)
		bufferPositions = p.createGraphics((int) w, (int) h, PRenderers.P3D);
		bufferPositions.smooth(8);
		bufferDirection = p.createGraphics((int) w, (int) h, PRenderers.P3D);
		bufferDirection.smooth(8);
		bufferParticles = p.createGraphics(p.width, p.height, PRenderers.P3D);
		bufferParticles.smooth(8);
		
		// build displacement maps
//		displacementGenerator = p.loadShader(FileUtil.getFile("shaders/textures/cacheflowe-liquid-moire.glsl"));
//		displacementGenerator = p.loadShader(FileUtil.getFile("shaders/textures/iq-voronoise.glsl"));
		displacementGenerator = p.loadShader(FileUtil.getFile("shaders/textures/square-fade.glsl"));
		displacementGenerator = p.loadShader(FileUtil.getFile("shaders/textures/noise-simplex-2d-iq.glsl"));
//		displacementGenerator = p.loadShader(FileUtil.getFile("shaders/textures/light-leak.glsl"));

		// build particle mover shader - uses displacement map to move particles
		positionMover = p.loadShader(FileUtil.getFile("shaders/point/particle-mover-frag.glsl"));
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
			FileUtil.getFile("shaders/point/point-frag.glsl"), 
			FileUtil.getFile("shaders/point/particle-vert.glsl")
		);
	}

	public void resetParticlePositions() {
		bufferPositions.beginDraw();
		bufferPositions.background(127);	// start in center
		bufferPositions.endDraw();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') resetParticlePositions();
	}
	
	public void drawApp() {
		// clear the screen
		background(0);
		
		// update displacement texture
		displacementGenerator.set("time", p.frameCount * 0.001f);
		bufferDirection.filter(displacementGenerator);
		p.debugView.setTexture(bufferDirection);
		
		// update particle positions
		positionMover.set("directionMap", bufferDirection);
		bufferPositions.filter(positionMover);
		p.debugView.setTexture(bufferPositions);
		
		// move to screen center
		bufferParticles.beginDraw();
		bufferParticles.background(0);
//		bufferParticles.translate(bufferParticles.width/2f, bufferParticles.height/2f, 0);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
		shape.disableStyle();
		bufferParticles.strokeWeight(1f);
		pointsParticleVertices.set("positionMap", bufferPositions);
		pointsParticleVertices.set("pointSize", 5f); // 2.5f + 1.5f * P.sin(P.TWO_PI * percentComplete));
		pointsParticleVertices.set("width", (float) p.width);
		pointsParticleVertices.set("height", (float) p.height);
		bufferParticles.shader(pointsParticleVertices);  	// update positions
		bufferParticles.shape(shape);					// draw vertices
		bufferParticles.resetShader();
		bufferParticles.endDraw();
		p.debugView.setTexture(bufferParticles);

		// draw buffer to screen
		p.image(bufferParticles, 0, 0);
	}
		
}