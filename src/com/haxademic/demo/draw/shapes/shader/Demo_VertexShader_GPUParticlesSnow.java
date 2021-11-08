package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesSnow 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape particleMesh;
	protected PGraphics bufferPositions;
	protected SimplexNoiseTexture varianceNoise;
	protected PShader randomColorShader;
	protected PShaderHotSwap simulationShader;
	protected PGraphics bufferRenderedParticles;
	protected PShaderHotSwap particlesSimulationRenderShader;
	float simW = 64;
	float simH = 64;
	int FRAMES = 300;
	protected boolean needsRestart = false;
	
	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 1920);
		Config.setProperty(AppSettings.HEIGHT, 1080);
		Config.setProperty(AppSettings.FILLS_SCREEN, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		buildSimulationBuffers();
		buildSimulation();
		buildParticles();
	}
	
	protected void buildSimulationBuffers() {
		// build offscreen buffer (thing don't work the same on the main drawing surface)
		bufferRenderedParticles = PG.newPG32(p.width, p.height, false, false); // p.createGraphics(p.width, p.height, PRenderers.P3D);
//		bufferRenderedParticles.smooth(8);

		DebugView.setTexture("bufferRenderedParticles", bufferRenderedParticles);
	}
	
	protected void buildSimulation() {
		varianceNoise = new SimplexNoiseTexture(256, 256, true, true);
		DebugView.setTexture("varianceNoise", varianceNoise.texture());

		randomColorShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));

		// build particle mover shader - uses displacement map to move particles
		bufferPositions = PG.newDataPG((int) simW, (int) simH);
		DebugView.setTexture("bufferPositions", bufferPositions);
		simulationShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/vertex/textured-particles-mover-snow-frag.glsl"));
		resetParticlePositions();	
	}
	
	protected void buildParticles() {
		// Build points vertices
		particleMesh = PShapeUtil.texturedParticlesShapeForGPUData(simW, simH, 10, P.getImage("haxademic/images/particles/star_07.png"));
		
		// load shader
		particlesSimulationRenderShader = new PShaderHotSwap(
			FileUtil.getPath("haxademic/shaders/vertex/textured-particles-vert.glsl"),
			FileUtil.getPath("haxademic/shaders/vertex/textured-particles-frag.glsl")
		);
	}

	
	public void resetParticlePositions() {
		bufferPositions.beginDraw();
		bufferPositions.background(127);					// start in center
		bufferPositions.endDraw();
		bufferPositions.filter(randomColorShader);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') needsRestart = true;
	}
	
	protected void drawApp() {
		if(needsRestart) {
			resetParticlePositions();
			needsRestart = false;
		}
		
		// clear the screen
		p.background(0);
		PG.setDrawCorner(p);
		
		// update noise/randomness
		varianceNoise.update(2f, P.sin(p.frameCount * 0.04f) * 0.07f, 0f, p.frameCount * 0.004f);
		
		// update particle positions
		simulationShader.shader().set("directionMap", varianceNoise.texture());
		simulationShader.shader().set("ampMap", varianceNoise.texture());
		simulationShader.shader().set("amp", 0.004f);// * (0.5f + 0.3f * P.sin(p.frameCount/20f))); // P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));
		simulationShader.update();
		bufferPositions.filter(simulationShader.shader());
		
		// update render shader
		float renderW = bufferRenderedParticles.width * 5f;
		float renderH = bufferRenderedParticles.height * 5f;
		particlesSimulationRenderShader.shader().set("displacementMap", varianceNoise.texture());
		particlesSimulationRenderShader.shader().set("positionMap", bufferPositions);
		particlesSimulationRenderShader.shader().set("width", (float) renderW);
		particlesSimulationRenderShader.shader().set("height", (float) renderH);
		particlesSimulationRenderShader.shader().set("displaceAmp", 10f);
		particlesSimulationRenderShader.shader().set("rotateAmp", 1f);
		particlesSimulationRenderShader.shader().set("globalScale", 2f);
		particlesSimulationRenderShader.shader().set("pointScale", 2f);
		particlesSimulationRenderShader.update();

		// render particles
		bufferRenderedParticles.beginDraw();
		PG.setDrawFlat2d(bufferRenderedParticles, true);
		bufferRenderedParticles.background(0);
		bufferRenderedParticles.translate(0, 0,0);
		PG.setCenterScreen(bufferRenderedParticles);
		PG.setDrawCorner(bufferRenderedParticles);
//		PG.basicCameraFromMouse(bufferRenderedParticles);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
//		shape.disableStyle();
		bufferRenderedParticles.blendMode(PBlendModes.ADD);
		bufferRenderedParticles.shader(particlesSimulationRenderShader.shader());  	// update positions
		bufferRenderedParticles.shape(particleMesh);					// draw vertices
		bufferRenderedParticles.resetShader();
		bufferRenderedParticles.endDraw();

		// draw buffer to screen
		p.image(bufferRenderedParticles, 0, 0);
	}
		
}