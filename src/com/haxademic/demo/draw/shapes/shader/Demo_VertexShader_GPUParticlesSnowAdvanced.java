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
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.OpticalFlow;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesSnowAdvanced 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Add z-depth and distribution of (mostly) small vs large flakes
	
	protected String SNOW_RENDER_WIDTH = "SNOW_RENDER_WIDTH"; 
	protected String SNOW_RENDER_HEIGHT = "SNOW_RENDER_HEIGHT"; 
	protected String SNOW_GLOBAL_SCALE = "SNOW_GLOBAL_SCALE"; 
	protected String SNOW_POINT_SCALE = "SNOW_POINT_SCALE"; 
	protected String SNOW_OPFLOW_AMP = "SNOW_OPFLOW_AMP"; 
	
	protected PShape particleMesh;
	protected PGraphics bufferPositions;
	protected PGraphics randomNumbers;
	protected SimplexNoiseTexture varianceNoise;
	protected PShader randomColorShader;
	protected PShaderHotSwap simulationShader;
	protected PGraphics bufferRenderedParticles;
	protected PShaderHotSwap particlesSimulationRenderShader;
	float simW = 256;
	float simH = 256;
	int FRAMES = 300;
	protected boolean particlesShouldRespawn = false;
	
	// optical flow
	protected PGraphics camBuffer;
	protected OpticalFlow opticalFlow;

	
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
		initUserCamera();
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
		UI.addSlider(SNOW_OPFLOW_AMP, 0.3f, 0, 5f, 0.01f, false);
	}
	
	protected void initUserCamera() {
		// webcam
		WebCam.instance().setDelegate(this);
		camBuffer = PG.newPG(1920, 1080);

		// optical flow
		opticalFlow = new OpticalFlow(camBuffer.width, camBuffer.height);
		opticalFlow.buildUI();
		PG.setTextureRepeat(opticalFlow.resultBuffer(), false);	// don't wrap optical flow results

		// add textures to debug panel
		DebugView.setTexture("opFlowResult", opticalFlow.resultBuffer());
		DebugView.setTexture("getDepthImage()", camBuffer);

	}
	
	protected void buildSimulationBuffers() {
		// build offscreen buffer (thing don't work the same on the main drawing surface)
		bufferRenderedParticles = PG.newPG32(p.width, p.height, false, false); // p.createGraphics(p.width, p.height, PRenderers.P3D);
		DebugView.setTexture("bufferRenderedParticles", bufferRenderedParticles);
	}
	
	protected void buildSimulation() {
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
		if(p.key == ' ') particlesShouldRespawn = true;
	}
	
	protected void drawApp() {
		
		// clear the screen
		p.background(0);
		PG.setDrawCorner(p);
		
		// draw app~!
		updateOpticalFlow();
		updateParticles();
	}
	
	protected void updateOpticalFlow() {
		opticalFlow.updateOpticalFlowProps();
		// override w/decent values
		opticalFlow.uDecayLerp(0.005f);
		opticalFlow.resultFlowDisplaceAmp(0.55f);
		opticalFlow.resultBlurSigma(40f);
		opticalFlow.resultBlurAmp(40);
		// apply new camera frame
		opticalFlow.update(camBuffer, true);
	}
	
	protected void updateParticles() {
		if(particlesShouldRespawn) {
			resetParticlePositions();
			particlesShouldRespawn = false;
		}

		// update noise/randomness
		varianceNoise.update(2f, P.sin(p.frameCount * 0.04f) * 0.07f, 0f, p.frameCount * 0.004f);
		
		// update particle positions
		simulationShader.shader().set("directionMap", varianceNoise.texture());
		simulationShader.shader().set("ampMap", varianceNoise.texture());
		simulationShader.shader().set("amp", 0.004f); // * (0.5f + 0.3f * P.sin(p.frameCount/20f))); // P.map(p.mouseX, 0, p.width, 0.001f, 0.05f));
		simulationShader.shader().set("flowMap", opticalFlow.resultBuffer());
		simulationShader.shader().set("flowMode", 1);
		simulationShader.shader().set("flowAmp", UI.valueEased(SNOW_OPFLOW_AMP));

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
		particlesSimulationRenderShader.shader().set("pointScale", UI.valueEased(SNOW_POINT_SCALE));
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
		p.image(camBuffer, 0, 0, p.width, p.height);
		p.blendMode(PBlendModes.SCREEN);
		p.image(bufferRenderedParticles, 0, 0);
		p.blendMode(PBlendModes.BLEND);
	}
	
	// IWebCamCallback
	
	@Override
	public void newFrame(PImage frame) {
		DebugView.setValue("Last WebCam frame", p.frameCount);
		ImageUtil.copyImageFlipH(frame, camBuffer);
	}

		
}