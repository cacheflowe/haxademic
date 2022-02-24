package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.video.MovieBuffer;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import VLCJVideo.VLCJVideo;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_OpticalFlow
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// optical flow effect & visual
	protected OpticalFlow opticalFlow;
	protected PGraphics curRgbFrame;	// just in case we want a separate color layer to blend in
	protected PGraphics curSourceFrame;
	protected PGraphics camDisplaced;

	// sources
	protected RealSenseWrapper realSenseWrapper;
	protected MovieBuffer video;
	protected VLCJVideo videoVLC;

	// ui
	protected String sourceLerp = "sourceLerp";
	protected String sourceDisplaceAmp = "sourceDisplaceAmp";
	protected String sourceDisplaceIters = "sourceDisplaceIters";
	protected String showDebug = "showDebug";

	// realsense-specific
	protected String faceMeltConfig = "{ \"uDecayLerp\": 0.0069999974, \"uForce\": 4.3399997, \"uOffset\": 8.0, \"uLambda\": 0.008800002, \"uThreshold\": 0.056000013, \"uInverseX\": -1.0, \"uInverseY\": -1.0, \"preBlurAmp\": 14.500001, \"preBlurSigma\": 6.9, \"resultFlowDecayLerp\": 0.13499989, \"resultFlowDisplaceAmp\": 0.412, \"resultFlowDisplaceIters\": 1.0, \"resultBlurAmp\": 47.800003, \"resultBlurSigma\": 49.9, \"sourceLerp\": 1.0, \"sourceDisplaceAmp\": 0.01, \"sourceDisplaceIters\": 8.0, \"showDebug\": 0.0 }";
	protected String flowwwwwConfig = "{ \"uDecayLerp\": 0.02, \"uForce\": 0.75, \"uOffset\": 8.0, \"uLambda\": 0.012, \"uThreshold\": 0.1, \"uInverseX\": -1.0, \"uInverseY\": -1.0, \"preBlurAmp\": 20.0, \"preBlurSigma\": 20.0, \"resultFlowDecayLerp\": 0.9, \"resultFlowDisplaceAmp\": 0.213, \"resultFlowDisplaceIters\": 6.0, \"resultBlurAmp\": 20.0, \"resultBlurSigma\": 20.0, \"sourceLerp\": 0.05, \"sourceDisplaceAmp\": 0.17, \"sourceDisplaceIters\": 1.0, \"showDebug\": 0.0 }";
	
	// flow-powered particle system
	protected PShape particleMesh;
	protected PGraphics bufferPositions;
	protected PGraphics randomNumbers;
	protected SimplexNoiseTexture varianceNoise;
	protected PShader randomColorShader;
	protected PShaderHotSwap simulationShader;
	protected PGraphics bufferRenderedParticles;
	protected PShaderHotSwap particlesSimulationRenderShader;
	float simW = 64; // 256;
	float simH = 64; // 256;
	protected boolean particlesShouldRespawn = false;
	
	// particle UI
	protected String PARTICLES_RENDER_WIDTH = "PARTICLES_RENDER_WIDTH"; 
	protected String PARTICLES_RENDER_HEIGHT = "PARTICLES_RENDER_HEIGHT"; 
	protected String PARTICLES_GLOBAL_SCALE = "PARTICLES_GLOBAL_SCALE"; 
	protected String PARTICLES_POINT_SCALE = "PARTICLES_POINT_SCALE"; 
	protected String PARTICLES_OPFLOW_AMP = "PARTICLES_OPFLOW_AMP"; 
	protected String PARTICLES_OVERRIDE_SOURCE = "PARTICLES_OVERRIDE_SOURCE"; 
	protected String PARTICLES_VISIBLE = "PARTICLES_VISIBLE"; 

	
	protected void config() {
		Config.setAppSize( 1280, 720 );
		Config.setProperty( AppSettings.RESIZABLE, true );
	}

	protected void firstFrame() {
		buildVideoSource();
		buildBuffers();
		buildOpticalFlow();
		buildFlowParticles();
		setDebugTextures();
		buildUI();
		buildUIParticles();
	}
	
	protected void buildVideoSource() {
		realSenseWrapper = new RealSenseWrapper(p, true, true);
//		video = new MovieBuffer(DemoAssets.movieKinectSilhouette());	// new MovieBuffer(FileUtil.getPath(DemoAssets.movieFractalCubePath));
//		video = new MovieBuffer("D:\\workspace\\pepsi-nitro-wall\\_assets\\video\\pepsi_h264_1080_best.mp4");
//		video.movie.loop();
//		videoVLC = new VLCJVideo(p);
//		videoVLC.open("D:\\workspace\\pepsi-nitro-wall\\_assets\\video\\pepsi_h264_1080_best.mp4");
//		videoVLC.play();
	}
	
	protected void buildBuffers() {
		curSourceFrame = PG.newPG32(p.width, p.height, true, false);
		curRgbFrame = PG.newPG(p.width, p.height);
		camDisplaced = PG.newPG32(p.width, p.height, true, false);
	}
	
	protected void buildOpticalFlow() {
		opticalFlow = new OpticalFlow(p.width, p.height);
		opticalFlow.buildUI();
	}

	protected void buildUI() {
		UI.addTitle("Final comp: Use the flow");
		UI.addSlider(sourceLerp, 0.1f, 0f, 1f, 0.01f, false);
		UI.addSlider(sourceDisplaceAmp, 0.2f, 0f, 1f, 0.01f, false);
		UI.addSlider(sourceDisplaceIters, 1, 0f, 10f, 1f, false);
		UI.addToggle(showDebug, true, false);
	}
	
	protected void buildUIParticles() {
		UI.addTitle("Particles Config");
		UI.addSlider(PARTICLES_RENDER_WIDTH, bufferRenderedParticles.width, 10, bufferRenderedParticles.width * 2, 5f, false);
		UI.addSlider(PARTICLES_RENDER_HEIGHT, bufferRenderedParticles.height, 10, bufferRenderedParticles.height * 2, 5f, false);
		UI.addSlider(PARTICLES_GLOBAL_SCALE, 1.1f, 0.1f, 5f, 0.01f, false);
		UI.addSlider(PARTICLES_POINT_SCALE, 2, 0, 20, 0.01f, false);
		UI.addSlider(PARTICLES_OPFLOW_AMP, 0.3f, 0, 5f, 0.01f, false);
		UI.addToggle(PARTICLES_OVERRIDE_SOURCE, false, false);
		UI.addToggle(PARTICLES_VISIBLE, true, false);
	}
	
	protected void setDebugTextures() {
		DebugView.setTexture("camDisplaced", camDisplaced);
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());
		DebugView.setTexture("opFlowResultFlowed", opticalFlow.resultFlowedBuffer());
		DebugView.setTexture("source video", curSourceFrame);
		DebugView.setTexture("rgb video", curRgbFrame);
	}
	
	protected void updateSourceVideo() {
		// update whichever source isn't null
		if(realSenseWrapper != null) {
			realSenseWrapper.update();
			ImageUtil.cropFillCopyImage(realSenseWrapper.getRgbImage(), curRgbFrame, true);
			ImageUtil.cropFillCopyImage(realSenseWrapper.getDepthImage(), curSourceFrame, true);
		} else if(video != null) {
//			if(video.movie.isPlaying() == false) video.movie.play();
			if(video.buffer != null) { 
				ImageUtil.cropFillCopyImage(video.buffer, curRgbFrame, true);
				ImageUtil.cropFillCopyImage(video.buffer, curSourceFrame, true);
			}
		} else if(videoVLC != null) {
			ImageUtil.cropFillCopyImage(videoVLC, curRgbFrame, true);
			ImageUtil.cropFillCopyImage(videoVLC, curSourceFrame, true);
		}
	}
	
	protected void buildFlowParticles() {
		// build offscreen buffer (thing don't work the same on the main drawing surface)
		bufferRenderedParticles = PG.newPG32(p.width, p.height, false, false); // p.createGraphics(p.width, p.height, PRenderers.P3D);
		DebugView.setTexture("bufferRenderedParticles", bufferRenderedParticles);

		varianceNoise = new SimplexNoiseTexture(256, 256, true, true);
		DebugView.setTexture("varianceNoise", varianceNoise.texture());

		// build particle mover shader - uses displacement map to move particles
		bufferPositions = PG.newDataPG((int) simW, (int) simH);
		DebugView.setTexture("bufferPositions", bufferPositions);
		simulationShader = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/vertex/textured-particles-mover-snow-frag.glsl"));
		
		randomColorShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/random-pixel-color.glsl"));
		resetParticlePositions();
		
		randomNumbers = PG.newDataPG((int) simW, (int) simH);
		bufferPositions.filter(randomColorShader);
		
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
		if(p.key == ' ') particlesShouldRespawn = true;
	}

	protected void drawApp() {
		p.background(0);
		updateSourceVideo();
		updateOpticalFlow();
		applyFlowToRgbsource();
		drawToScreen();
		updateParticles();
		updateUIPresets();
	}
	
	protected void updateOpticalFlow() {
		opticalFlow.updateOpticalFlowProps();
		opticalFlow.update(curSourceFrame, true);
		opticalFlow.drawDebugLines(opticalFlow.resultFlowedBuffer());
//		opticalFlow.drawDebugLines((FrameLoop.frameMod(200) < 100) ? opticalFlow.resultBuffer() : opticalFlow.resultFlowedBuffer());
	}	
	
	protected void applyFlowToRgbsource() {
		// copy source rgb to buffer and mix it slightly into the displaced buffer
		BlendTowardsTexture.instance(p).setSourceTexture(curRgbFrame);
		BlendTowardsTexture.instance(p).setBlendLerp(UI.value(sourceLerp));
		BlendTowardsTexture.instance(p).applyTo(camDisplaced);
		
		// flow the displaced source buffer
		PGraphics opFlowResult = opticalFlow.resultFlowedBuffer();
		DisplacementMapFilter.instance(P.p).setMap(opFlowResult);
		DisplacementMapFilter.instance(P.p).setMode(10);
		DisplacementMapFilter.instance(P.p).setAmp(UI.value(sourceDisplaceAmp));
		for (int i = 0; i < UI.valueInt(sourceDisplaceIters); i++) {
			DisplacementMapFilter.instance.applyTo(camDisplaced);	
		}
	}
	
	protected void updateParticles() {
		if(UI.valueToggle(PARTICLES_VISIBLE) == false) return; 
		
		if(particlesShouldRespawn) {
			resetParticlePositions();
			particlesShouldRespawn = false;
		}

		// update noise/randomness
		varianceNoise.update(
			// zoom
			1f, 
			// rotation
			P.sin(p.frameCount * 0.04f) * 0.07f,
			// offset x
			0f, 
			// offset y
			p.frameCount * -0.004f
		);
		
		// update particle positions
		simulationShader.shader().set("directionMap", varianceNoise.texture());
		simulationShader.shader().set("ampMap", varianceNoise.texture());
		simulationShader.shader().set("amp", 0.004f); // * (0.5f + 0.3f * P.sin(p.frameCount/20f))); // P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));
		simulationShader.shader().set("flowMap", opticalFlow.resultFlowedBuffer());
		simulationShader.shader().set("flowMode", 1);
		simulationShader.shader().set("flowAmp", UI.valueEased(PARTICLES_OPFLOW_AMP));

		simulationShader.update();
		bufferPositions.filter(simulationShader.shader());
		
		// update render shader
		particlesSimulationRenderShader.shader().set("displacementMap", varianceNoise.texture());
		particlesSimulationRenderShader.shader().set("positionMap", bufferPositions);
		particlesSimulationRenderShader.shader().set("randomMap", randomNumbers);
		particlesSimulationRenderShader.shader().set("texture1", ImageCacher.get("haxademic/images/particles/magic_05.png"));
		particlesSimulationRenderShader.shader().set("texture2", ImageCacher.get("haxademic/images/particles/star_08.png"));
		particlesSimulationRenderShader.shader().set("texture3", ImageCacher.get("haxademic/images/particles/star_09.png"));
		particlesSimulationRenderShader.shader().set("width", (float) UI.valueEased(PARTICLES_RENDER_WIDTH));
		particlesSimulationRenderShader.shader().set("height", (float) UI.valueEased(PARTICLES_RENDER_HEIGHT));
		particlesSimulationRenderShader.shader().set("rotateAmp", 1f);
		particlesSimulationRenderShader.shader().set("globalScale", UI.valueEased(PARTICLES_GLOBAL_SCALE));
		particlesSimulationRenderShader.shader().set("pointScale", UI.valueEased(PARTICLES_POINT_SCALE));
		particlesSimulationRenderShader.update();

		// render particles
		bufferRenderedParticles.beginDraw();
		PG.setDrawFlat2d(bufferRenderedParticles, true);
//		bufferRenderedParticles.background(0);
		bufferRenderedParticles.clear();
		bufferRenderedParticles.translate(0, 0,0);
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
		if(UI.valueToggle(PARTICLES_OVERRIDE_SOURCE)) p.image(curRgbFrame, 0, 0, p.width, p.height);
		p.blendMode(PBlendModes.SCREEN);
		p.image(bufferRenderedParticles, 0, 0);
		p.blendMode(PBlendModes.BLEND);
	}
	
	protected void updateUIPresets() {
		if(KeyboardState.keyTriggered('1')) UI.loadValuesFromJSON(faceMeltConfig);
		if(KeyboardState.keyTriggered('2')) UI.loadValuesFromJSON(flowwwwwConfig);
		if(KeyboardState.keyTriggered(' ')) P.out(JsonUtil.jsonToSingleLine(UI.configToJSON()));
		if(KeyboardState.keyTriggered('p')) { videoVLC.setTime(0); videoVLC.play(); }
	}
	
	protected void drawToScreen() {
		// draw flowed source image
		ImageUtil.cropFillCopyImage(camDisplaced, p.g, true);
		
		// draw optical flow debug lines
		if(UI.valueToggle(showDebug)) {
			PG.setPImageAlpha(p.g, 0.9f);
			ImageUtil.drawImageCropFill(opticalFlow.debugBuffer(), p.g, true);	
			PG.resetPImageAlpha(p.g);
		}
	}
	
}
