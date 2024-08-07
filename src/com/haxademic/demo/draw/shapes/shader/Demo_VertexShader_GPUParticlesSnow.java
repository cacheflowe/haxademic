package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesSnow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Add z-depth and distribution of (mostly) small vs large flakes
	
	protected String SNOW_RENDER_WIDTH = "SNOW_RENDER_WIDTH"; 
	protected String SNOW_RENDER_HEIGHT = "SNOW_RENDER_HEIGHT"; 
	protected String SNOW_GLOBAL_SCALE = "SNOW_GLOBAL_SCALE"; 
	protected String SNOW_POINT_SCALE = "SNOW_POINT_SCALE"; 
	
	protected PShape particleMesh;
	protected PGraphics bufferPositions;
	protected PGraphics randomNumbers;
	protected SimplexNoiseTexture varianceNoise;
	protected PShader randomColorShader;
	protected PShaderHotSwap simulationShader;
	protected PGraphics bufferRenderedParticles;
	protected PShaderHotSwap particlesSimulationRenderShader;
	float simW = 128;
	float simH = 128;
	int FRAMES = 300;
	protected boolean particlesShouldRespawn = false;
		
	protected void config() {
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 1920);
		Config.setProperty(AppSettings.HEIGHT, 1920);
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
		buildUI();
	}
	
	protected void buildUI() {
		UI.addTitle("SNOW Config");
		UI.addSlider(SNOW_RENDER_WIDTH, bufferRenderedParticles.width, 10, bufferRenderedParticles.width * 2, 5f, false);
		UI.addSlider(SNOW_RENDER_HEIGHT, bufferRenderedParticles.height, 10, bufferRenderedParticles.height * 2, 5f, false);
		UI.addSlider(SNOW_GLOBAL_SCALE, 1.1f, 0.1f, 5f, 0.01f, false);
		UI.addSlider(SNOW_POINT_SCALE, 2, 0, 20, 0.01f, false);
	}
		
	protected void buildSimulationBuffers() {
		// build offscreen buffer (thing don't work the same on the main drawing surface)
		bufferRenderedParticles = PG.newPG32(p.width, p.height, false, false); // p.createGraphics(p.width, p.height, PRenderers.P3D);
		DebugView.setTexture("bufferRenderedParticles", bufferRenderedParticles);
	}
	
	protected void buildSimulation() {
		// determines rotation & scale in `textured-particles-vert.glsl`
		varianceNoise = new SimplexNoiseTexture(256, 256, true, true);
		
		// determines which texture to use for each particle
		randomNumbers = PG.newDataPG((int) simW, (int) simH);
		randomColorShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));
		randomNumbers.filter(randomColorShader);
		
		// build particle mover shader - uses displacement map to move particles
		bufferPositions = PG.newDataPG((int) simW, (int) simH);
		simulationShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/vertex/textured-particles-mover-snow-basic-frag.glsl"));
		resetParticlePositions();

		DebugView.setTexture("varianceNoise", varianceNoise.texture());
		DebugView.setTexture("bufferPositions", bufferPositions);
		DebugView.setTexture("randomNumbers", randomNumbers);
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
		bufferPositions.background(127);
		bufferPositions.endDraw();
		bufferPositions.filter(randomColorShader);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') particlesShouldRespawn = true;
	}
	
	protected void drawApp() {
		p.background(0);
		PG.setDrawCorner(p);
		updateParticles();
	}
	
	protected void updateParticles() {
		if(particlesShouldRespawn) {
			resetParticlePositions();
			particlesShouldRespawn = false;
		}

		// update noise/randomness
		varianceNoise.update(2f, P.sin(p.frameCount * 0.01f) * 0.07f, 0f, p.frameCount * 0.004f);
		
		// update particle positions
		simulationShader.shader().set("directionMap", varianceNoise.texture());
		simulationShader.shader().set("ampMap", varianceNoise.texture());
		simulationShader.shader().set("amp", 0.004f); // * (0.5f + 0.3f * P.sin(p.frameCount/20f))); // P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));

		simulationShader.update();
		bufferPositions.filter(simulationShader.shader());
		
		// update render shader
		particlesSimulationRenderShader.shader().set("displacementMap", varianceNoise.texture());
		particlesSimulationRenderShader.shader().set("positionMap", bufferPositions);
		particlesSimulationRenderShader.shader().set("randomMap", randomNumbers);
		particlesSimulationRenderShader.shader().set("texture1", ImageCacher.get("haxademic/images/particles/magic_05.png"));
		particlesSimulationRenderShader.shader().set("texture2", ImageCacher.get("haxademic/images/particles/star_08.png"));
		particlesSimulationRenderShader.shader().set("texture3", ImageCacher.get("haxademic/images/particles/star_09.png"));
		particlesSimulationRenderShader.shader().set("width", (float) UI.valueEased(SNOW_RENDER_WIDTH));
		particlesSimulationRenderShader.shader().set("height", (float) UI.valueEased(SNOW_RENDER_HEIGHT));
		particlesSimulationRenderShader.shader().set("rotateAmp", 1f);
		particlesSimulationRenderShader.shader().set("globalScale", UI.valueEased(SNOW_GLOBAL_SCALE));
		particlesSimulationRenderShader.shader().set("pointSize", UI.valueEased(SNOW_POINT_SCALE));
		particlesSimulationRenderShader.update();

		// render particles
		bufferRenderedParticles.beginDraw();
		PG.setDrawFlat2d(bufferRenderedParticles, true);
		bufferRenderedParticles.clear();
		PG.setCenterScreen(bufferRenderedParticles);
		PG.setDrawCorner(bufferRenderedParticles);
//		PG.basicCameraFromMouse(bufferRenderedParticles);
		
		// draw vertex points. strokeWeight w/disableStyle works here for point size
		bufferRenderedParticles.blendMode(PBlendModes.ADD);
		bufferRenderedParticles.shader(particlesSimulationRenderShader.shader());  	// update positions
		bufferRenderedParticles.shape(particleMesh);					// draw vertices
		bufferRenderedParticles.resetShader();
		bufferRenderedParticles.endDraw();

		// draw buffer to screen
		p.blendMode(PBlendModes.ADD);
		p.image(bufferRenderedParticles, 0, 0);
		p.blendMode(PBlendModes.BLEND);
	}
		
}