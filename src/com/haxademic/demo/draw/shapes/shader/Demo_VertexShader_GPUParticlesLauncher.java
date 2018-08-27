package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.constants.PBlendModes;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesLauncher 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics renderedParticles;
	protected ParticleLauncher[] particles;
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
		// build final draw buffer
		renderedParticles = p.createGraphics(p.width, p.height, PRenderers.P3D);
		renderedParticles.smooth(8);
//		DrawUtil.setDrawFlat2d(renderedParticles, true);
//		p.debugView.setTexture(renderedParticles);
		
		// build multiple particles launchers
		particles = new ParticleLauncher[] {
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
				new ParticleLauncher(),
		};
	}
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == ' ') newPositions();
	}
	
	public void drawApp() {
		// clear the screen
		background(0);
		
		// launch! x5 for each particle obj
		int particleLauncherIndex = p.frameCount % particles.length;
//		for (int i = 0; i < particles.length; i++) {
			for (int j = 0; j < 100; j++) {
				particles[particleLauncherIndex].launch(p.mouseX, p.mouseY);
			}
//		}
		
		// update particles launcher buffers
		for (int i = 0; i < particles.length; i++) {
			particles[i].update();
		}

		// render!
		renderedParticles.beginDraw();
		DrawUtil.setDrawFlat2d(renderedParticles, true);
		renderedParticles.background(0);
		renderedParticles.fill(255);
		renderedParticles.blendMode(PBlendModes.ADD);
		for (int i = 0; i < particles.length; i++) {
			particles[i].renderTo(renderedParticles);
		}
		renderedParticles.endDraw();

		// draw buffer to screen
		p.image(renderedParticles, 0, 0);
	}
	
	public class ParticleLauncher {
		
		protected int positionBufferSize = 32;
		protected PShape shape;
		protected PGraphics colorBuffer;
		protected PGraphics progressBuffer;
		protected PShader positionShader;

		protected PShader particlesRenderShader;
		protected int launchIndex = 0;

		public ParticleLauncher() {
			// build random particle placement shader
			positionShader = p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-launcher-frag.glsl"));
			
			// create texture to store positions
			colorBuffer = p.createGraphics(positionBufferSize, positionBufferSize, PRenderers.P3D);
			OpenGLUtil.setTextureQualityLow(colorBuffer);		// necessary for proper texel lookup!
//			p.debugView.setTexture(colorBuffer);
			
			progressBuffer = p.createGraphics(positionBufferSize, positionBufferSize, PRenderers.P3D);
			OpenGLUtil.setTextureQualityLow(progressBuffer);		// necessary for proper texel lookup!
			
			progressBuffer.beginDraw();
			progressBuffer.background(255);
			progressBuffer.noStroke();
			progressBuffer.endDraw();
			p.debugView.setTexture(progressBuffer);
			
			// count vertices for debugView
			int vertices = P.round(positionBufferSize * positionBufferSize); 
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
			particlesRenderShader = p.loadShader(
				FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
				FileUtil.getFile("haxademic/shaders/point/particle-launcher-vert.glsl")
			);

		}
		
		protected float getGridX(float size, float index) {
			return index % size;
		}
		
		protected float getGridY(float size, float index) {
			return P.floor(index / size);
		}
		
		public void launch(float x, float y) {
			// get launch index - let's skip the last row, because something is breaking there. might be uv coords or something else...
			launchIndex++;
			if(launchIndex > (shape.getVertexCount() - positionBufferSize)) launchIndex = positionBufferSize;
//			launchIndex = launchIndex % (shape.getVertexCount() - positionBufferSize);
//			p.debugView.setValue("shape.getVertexCount()", shape.getVertexCount());
//			p.debugView.setValue("launchIndex", launchIndex);
//			p.debugView.setValue("launch x,y", getGridX(positionBufferSize, launchIndex) + ", " + getGridY(positionBufferSize, launchIndex));
			
			// reset progress
			progressBuffer.beginDraw();
			progressBuffer.fill(MathUtil.randRangeDecimal(127f, 255f), MathUtil.randRangeDecimal(0, 255), MathUtil.randRangeDecimal(0f, 255f), 255);	// rgba = distAmp, size, rotation, progress
			progressBuffer.rect(getGridX(positionBufferSize, launchIndex), getGridY(positionBufferSize, launchIndex), 1, 1);
			progressBuffer.endDraw();
			
			// set particle color
			colorBuffer.beginDraw();
			colorBuffer.noStroke();
			colorBuffer.fill(255f * (0.75f + 0.25f * P.sin(launchIndex * 0.1f)), 255f * (0.75f + 0.25f * P.sin(launchIndex * 0.3f)), 255f * (0.75f + 0.25f * P.sin(launchIndex * 0.2f)));
			colorBuffer.rect(getGridX(positionBufferSize, launchIndex), getGridY(positionBufferSize, launchIndex), 1, 1);
			colorBuffer.endDraw();
			
			// iterate through vertices and place on mouse
			int vertexIndex = (launchIndex + positionBufferSize) % shape.getVertexCount();	// offset for some reason... this has something to do with launchIndex skipping the last row
			shape.setVertex(vertexIndex, x, y, 0);

		}
		
		public void update() {
			// update particle movement
			progressBuffer.filter(positionShader);
			
			// update vertex/rendering shader props
			particlesRenderShader.set("width", (float) positionBufferSize);
			particlesRenderShader.set("height", (float) positionBufferSize);
			particlesRenderShader.set("colorTexture", colorBuffer);
			particlesRenderShader.set("progressTexture", progressBuffer);
			particlesRenderShader.set("pointSize", 13f);
			particlesRenderShader.set("particleOffsetDistance", 200f);
			particlesRenderShader.set("mode", p.mousePercentY());	// test gl_VertexID method of accessing texture positions
		}
		
		public void renderTo(PGraphics buffer) {
			buffer.shader(particlesRenderShader);  	// update positions
			buffer.shape(shape);						// draw vertices
			buffer.resetShader();
		}
	}
		
}