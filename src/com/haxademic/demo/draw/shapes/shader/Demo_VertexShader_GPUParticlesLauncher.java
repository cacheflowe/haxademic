package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.OpticalFlow;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.ParticleLauncherGPU;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesLauncher 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 60 * 4;
	protected ParticleLauncherGPU gpuParticles;
	protected boolean pointsShader = false;

	protected PGraphics randomNumbers;
	protected PShader randomColorShader;

	protected OpticalFlow opticalFlow;
	protected PGraphics curSourceFrame;


	protected void config() {
		Config.setAppSize(1000, 1000);
		Config.setProperty(AppSettings.SHOW_DEBUG, false);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false);
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 3);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 4);
	}
	
	protected void firstFrame() {
		// build particles launcher
		if(pointsShader) {
			gpuParticles = new ParticleLauncherGPU(256, "haxademic/shaders/point/particle-launcher-fizz-frag.glsl");
		} else {
			PImage particle = DemoAssets.particle();
			gpuParticles = new ParticleLauncherGPU(1024, "haxademic/shaders/point/particle-launcher-fizz-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-frag.glsl", "haxademic/shaders/vertex/particles-launcher-textured-vert.glsl", particle);
		}
		DebugView.setValue("gpuParticles.vertices()", gpuParticles.numParticles());
		DebugView.setTexture("gpuParticles.positionBuffer()", gpuParticles.positionBuffer());
		DebugView.setTexture("gpuParticles.colorBuffer()", gpuParticles.colorBuffer());
		
		// build random number texture
		randomColorShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));
		randomNumbers = PG.newDataPG(1024, 1024);
		randomNumbers.filter(randomColorShader);
	}
	
	protected void buildOpticalFlow() {
		opticalFlow = new OpticalFlow(p.width, p.height);
		opticalFlow.buildUI();
		curSourceFrame = PG.newPG32(p.width, p.height, true, false);
	}

	protected void updateOpticalFlow() {
		opticalFlow.updateOpticalFlowProps();
		opticalFlow.update(curSourceFrame, true);
		opticalFlow.drawDebugLines(true);
//		opticalFlow.drawDebugLines((FrameLoop.frameMod(200) < 100) ? opticalFlow.resultBuffer() : opticalFlow.resultFlowedBuffer());
	}	
	
	protected void setDebugTextures() {
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());
		DebugView.setTexture("opFlowResultFlowed", opticalFlow.resultFlowedBuffer());
		DebugView.setTexture("source video", curSourceFrame);
	}
	
	protected void updateTextureMaps() {
		// set color map
		ImageUtil.copyImage(ImageGradient.BLACK_HOLE(), gpuParticles.colorBuffer());
		ImageUtil.copyImage(ImageGradient.SPARKS_FLAMES(), gpuParticles.colorBuffer());
		ImageUtil.copyImage(ImageGradient.THERMAL(), gpuParticles.colorBuffer());
	}
	
	protected void launchParticles() {
		// launch! need to open & close the position buffer where we're writing new launch pixels
		int startLaunchTime = p.millis();
		int launchesPerFrame = 500;
		gpuParticles.beginLaunch();
//		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, Mouse.xEased, Mouse.yEased);
//		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, p.width/2 + p.width/4 * P.sin(p.frameCount/40f), p.height/2 + p.height/6 * P.sin(p.frameCount/20f));
		for (int j = 0; j < launchesPerFrame; j++) gpuParticles.launch(pg, p.width/2 + p.width/4 * P.sin(FrameLoop.progressRads() * 1f), p.height/2 + p.height/6 * P.sin(FrameLoop.progressRads() * 2f));
		gpuParticles.endLaunch();
		DebugView.setValue("launchTime", p.millis() - startLaunchTime);
	}
	
	protected void updateSimulation() {
		int startUpdateTime = p.millis();
		
		if(pointsShader) {
		
		} else {
//			gpuParticles.simulationShader().set("directionMap", varianceNoise.texture());
//			gpuParticles.simulationShader().set("ampMap", varianceNoise.texture());
//			gpuParticles.simulationShader().set("amp", 0.004f); // * (0.5f + 0.3f * P.sin(p.frameCount/20f))); // P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));
//			gpuParticles.simulationShader().set("randomMap", randomNumbers);
//			gpuParticles.simulationShader().set("flowMap", opticalFlow.resultFlowedBuffer());
//			gpuParticles.simulationShader().set("flowMode", 0);
//			gpuParticles.simulationShader().set("flowAmp", UI.valueEased(PARTICLES_OPFLOW_AMP));
		}
		
		gpuParticles.updateSimulation();
		DebugView.setValue("updateTime", p.millis() - startUpdateTime);
	}

	protected void updateRender() {
		// update render uniforms & properties
		if(pointsShader) {
			gpuParticles.pointSize(4);
		} else {
			if(gpuParticles.renderShader() != null) {
	//			gpuParticles.renderShader().set("displacementMap", varianceNoise.texture());
				gpuParticles.renderShader().set("randomMap", randomNumbers);
				gpuParticles.renderShader().set("width", pg.width); // (float) UI.valueEased(PARTICLES_RENDER_WIDTH));
				gpuParticles.renderShader().set("height", pg.height); //, (float) UI.valueEased(PARTICLES_RENDER_HEIGHT));
	//			gpuParticles.renderShader().set("rotateAmp", 1f);
				gpuParticles.renderShader().set("globalScale", 1f); // UI.valueEased(PARTICLES_GLOBAL_SCALE));
			}
		}
		
		// render!
		int startRenderTime = p.millis();
		pg.beginDraw();
		pg.background(0);
		PG.setCenterScreen(pg);
//		PG.basicCameraFromMouse(pg, 0.5f);
		pg.blendMode(PBlendModes.BLEND);
		gpuParticles.renderTo(pg);
		pg.endDraw();
		DebugView.setValue("renderTime", p.millis() - startRenderTime);
	}
	
	protected void drawApp() {
		// clear the screen
		p.background(0);
		updateTextureMaps();
		updateSimulation();
		launchParticles();
		updateRender();
		p.image(pg, 0, 0);
	}
	
}