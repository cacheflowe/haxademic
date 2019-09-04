package com.haxademic.demo.draw.shapes.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.net.JsonUtil;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.data.JSONObject;
import processing.opengl.PShader;

public class Demo_VertexShader_GPUParticlesWarp 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics bufferPositions;
	protected PShader randomColorShader;
	protected PShader particleMoverShader;
	protected PShader particlesDrawShader;
	protected String WIDTH = "WIDTH";
	protected String HEIGHT = "HEIGHT";
	protected String DEPTH = "DEPTH";
	protected String POINT_SIZE = "POINT_SIZE";
	protected String ROT_X = "ROT_X";
	protected String ROT_Y = "ROT_Y";
	int interval = 160;
	int FRAMES = 7 * interval;
	
	protected EasingFloat w;
	protected EasingFloat h;
	protected EasingFloat d;
	protected EasingFloat pointSize;
	protected EasingFloat rotX;
	protected EasingFloat rotY;
	
	protected String[] particleSettings;
	protected int settingsIndex = -1;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		p.appConfig.setProperty(AppSettings.WIDTH, 1080);
		p.appConfig.setProperty(AppSettings.HEIGHT, 1080);
		p.appConfig.setProperty(AppSettings.FILLS_SCREEN, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + interval/2 + FRAMES);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + interval/2 + FRAMES * 2);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, false);
	}
	
	protected void setupFirstFrame() {
		generateSettings();
		
		// build random particle placement shader
		randomColorShader = p.loadShader(FileUtil.getFile("haxademic/shaders/textures/random-pixel-color.glsl"));
		particleMoverShader = p.loadShader(FileUtil.getFile("haxademic/shaders/point/particle-warp-z-mover.glsl"));

		// create texture to store positions
		int positionBufferSize = 1024;
		bufferPositions = PG.newDataPG(positionBufferSize, positionBufferSize);
		p.debugView.setTexture("bufferPositions", bufferPositions);
		newPositions();
		
		// count vertices for debugView
		int vertices = P.round(positionBufferSize * positionBufferSize); 
		p.debugView.setValue("numParticles", vertices);
		
		// Build points vertices
		shape = PShapeUtil.pointsShapeForGPUData(positionBufferSize);
		
		// load shader
		particlesDrawShader = p.loadShader(
			FileUtil.getFile("haxademic/shaders/point/points-default-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/point/particle-warp-vert.glsl")
		);	
		
		// build UI
		p.ui.addSlider(WIDTH, 256, 0, 4196, 1, false);
		p.ui.addSlider(HEIGHT, 256, 0, 4196, 1, false);
		p.ui.addSlider(DEPTH, 256, 0, 4196, 1, false);
		p.ui.addSlider(POINT_SIZE, 1, 0.1f, 20, 0.1f, false);
		p.ui.addSlider(ROT_X, 0, -P.TWO_PI, P.TWO_PI, 0.001f, false);
		p.ui.addSlider(ROT_Y, 0, -P.TWO_PI, P.TWO_PI, 0.001f, false);
		
		float easeFactor = 0.1f;
		w = new EasingFloat(p.ui.value(WIDTH), easeFactor);
		h = new EasingFloat(p.ui.value(HEIGHT), easeFactor);
		d = new EasingFloat(p.ui.value(DEPTH), easeFactor);
		pointSize = new EasingFloat(p.ui.value(POINT_SIZE), easeFactor);
		rotX = new EasingFloat(p.ui.value(ROT_X), easeFactor);
		rotY = new EasingFloat(p.ui.value(ROT_Y), easeFactor);
	}
	
	protected void newPositions() {
		randomColorShader.set("offset", MathUtil.randRangeDecimal(0, 100), MathUtil.randRangeDecimal(0, 100));
		bufferPositions.filter(randomColorShader);
	}

	public void preDraw() {
	}
	
	public void drawApp() {
		// animate!
		if(p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
			if(p.frameCount % interval == 1) nextSettings();
		}
		
		// update values
		float upscale = 1.4f;
		w.setTarget(p.ui.value(WIDTH) * upscale);
		w.update(true);
		h.setTarget(p.ui.value(HEIGHT) * upscale);
		h.update(true);
		d.setTarget(p.ui.value(DEPTH) * upscale);
		d.update(true);
		pointSize.setTarget(p.ui.value(POINT_SIZE) * upscale);
		pointSize.update(true);
		rotX.setTarget(p.ui.value(ROT_X));
		rotX.update(true);
		rotY.setTarget(p.ui.value(ROT_Y));
		rotY.update(true);
		
		// set context
		p.background(0);
		pg.beginDraw();
		pg.background(0);
		PG.setCenterScreen(pg);
		pg.rotateX(rotX.value());
		pg.rotateY(rotY.value());
//		PG.basicCameraFromMouse(p.g);
		
		// update particle positions
		particleMoverShader.set("speed", 1f/2048f);
		particleMoverShader.set("variableSpeed", false); // p.frameCount % 200 > 100);
		bufferPositions.filter(particleMoverShader);

		// draw shape w/shader
		particlesDrawShader.set("width", w.value());
		particlesDrawShader.set("height", h.value());
		particlesDrawShader.set("depth", d.value());
		particlesDrawShader.set("positionMap", bufferPositions);
		particlesDrawShader.set("pointSize", pointSize.value());
		pg.shader(particlesDrawShader);  	// update positions
		pg.shape(shape);						// draw vertices
		pg.resetShader();
		
		
		pg.endDraw();

		// post process
//		BloomFilter.instance(p).setStrength(0.2f);
//		BloomFilter.instance(p).setBlurIterations(6);
//		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance(p).applyTo(pg);
////		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.65f);
		VignetteFilter.instance(p).applyTo(pg);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.07f);
		GrainFilter.instance(p).applyTo(pg);
		
		// draw result to screen
		p.image(pg, 0, 0);

	}
	
	protected void generateSettings() {
		particleSettings = new String[] {
				"{ \"POINT_SIZE\": 15, \"ROT_X\": 4.307366907596588E-8, \"ROT_Y\": 8.048955351114273E-7, \"WIDTH\": 428, \"HEIGHT\": 0, \"DEPTH\": 0 }",
				"{ \"POINT_SIZE\": 2.9999992847442627, \"ROT_X\": 0.0030000489205121994, \"ROT_Y\": 8.048955351114273E-7, \"WIDTH\": 428, \"HEIGHT\": 428, \"DEPTH\": 0 }",
				"{ \"POINT_SIZE\": 1.1000001430511475, \"ROT_X\": 0, \"ROT_Y\": 0, \"WIDTH\": 304, \"HEIGHT\": 339, \"DEPTH\": 1713 }",
				"{ \"POINT_SIZE\": 1, \"ROT_X\": -0.06699997931718826, \"ROT_Y\": 0.708000123500824, \"WIDTH\": 320, \"HEIGHT\": 320, \"DEPTH\": 425 }",
				"{ \"POINT_SIZE\": 1.2000000476837158, \"ROT_X\": -1.9208528101444244E-7, \"ROT_Y\": 1.076001763343811, \"WIDTH\": 989, \"HEIGHT\": 29, \"DEPTH\": 754 }",
				"{ \"POINT_SIZE\": 2.900001287460327, \"ROT_X\": -1.3239951133728027, \"ROT_Y\": 9.725335985422134E-7, \"WIDTH\": 1114, \"HEIGHT\": 734, \"DEPTH\": 323 }",
				"{ \"POINT_SIZE\": 1.2000000476837158, \"ROT_X\": -0.6249988079071045, \"ROT_Y\": -0.7769986391067505, \"WIDTH\": 420, \"HEIGHT\": 1, \"DEPTH\": 420 }",
			};
	}
	
	protected void nextSettings() {
//		generateSettings();
		settingsIndex++;
		if(settingsIndex >= particleSettings.length) settingsIndex = 0;
		p.ui.loadValuesFromJSON(JSONObject.parse(particleSettings[settingsIndex]));
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') newPositions();
		if(p.key == 's') P.out(JsonUtil.jsonToSingleLine(p.ui.valuesToJSON()));
		if(p.key == 'l') nextSettings();
	}
	
}