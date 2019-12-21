package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class ShaderSSAO
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// ported from: view-source:https://threejs.org/examples/webgl_postprocessing_ssao.html
	
	protected PGraphics canvas;
	protected PGraphics depth;
	protected PShader depthShader;
	protected PShader ssaoShader;
	protected float _frames = 420;
	protected float percentComplete;
	protected float progressRads;
	
	protected String onlyAO = "onlyAO";
	protected String aoClamp = "aoClamp";
	protected String lumInfluence = "lumInfluence";
	protected String cameraNear = "cameraNear";
	protected String cameraFar = "cameraFar";
	protected String samples = "samples";
	protected String radius = "radius";
	protected String useNoise = "useNoise";
	protected String noiseAmount = "noiseAmount";
	protected String diffArea = "diffArea";
	protected String gDisplace = "gDisplace";
	protected String diffMult = "diffMult";
	protected String gaussMult = "gaussMult";

	protected String config1 = "{\r\n" + 
			"  \"gDisplace\": 0.5600005388259888,\r\n" + 
			"  \"diffMult\": 307,\r\n" + 
			"  \"gaussMult\": -2,\r\n" + 
			"  \"cameraNear\": 8,\r\n" + 
			"  \"aoClamp\": -2.980001449584961,\r\n" + 
			"  \"samples\": 12,\r\n" + 
			"  \"cameraFar\": 845,\r\n" + 
			"  \"onlyAO\": 1,\r\n" + 
			"  \"noiseAmount\": 0.002739999908953905,\r\n" + 
			"  \"lumInfluence\": 1.1099995374679565,\r\n" + 
			"  \"radius\": 1,\r\n" + 
			"  \"diffArea\": 0.5399996638298035,\r\n" + 
			"  \"useNoise\": 0\r\n" + 
			"}";

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	public void firstFrame() {
		UI.addSlider(onlyAO, 0, 0, 1, 1, false);
		UI.addSlider(aoClamp, 1.5f, -5f, 5f, 0.01f, false);
		UI.addSlider(lumInfluence, 0.2f, -5f, 5f, 0.01f, false);
		UI.addSlider(cameraNear, 175, 1, 500, 1, false);
		UI.addSlider(cameraFar, 1700, 500f, 2000f, 1, false);
		UI.addSlider(samples, 32, 2, 128, 1, false);
		UI.addSlider(radius, 1, 0, 2, 0.001f, false);
		UI.addSlider(diffArea, 0.65f, 0, 5, 0.01f, false);
		UI.addSlider(gDisplace, 0.65f, 0, 5, 0.01f, false);
		UI.addSlider(diffMult, 100f, 1, 1000, 1f, false);
		UI.addSlider(gaussMult, -2, -4, 4, 0.01f, false);
		UI.addSlider(useNoise, 12, 0, 1, 1, false);
		UI.addSlider(noiseAmount, 0.00003f, 0.00003f, 0.003f, 0.00001f, false);
		UI.loadValuesFromJSON(JsonUtil.jsonFromString(config1));

		canvas = p.createGraphics(p.width, p.height, P.P3D);
		canvas.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(canvas);
		
		depth = p.createGraphics(p.width, p.height, P.P3D);
		depth.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(depth);
		
		depthShader = loadShader(
				FileUtil.getFile("haxademic/shaders/vertex/depth-frag.glsl"), 
				FileUtil.getFile("haxademic/shaders/vertex/depth-vert.glsl")
				);

		ssaoShader = loadShader(
			FileUtil.getFile("haxademic/shaders/vertex/ssao-frag.glsl"), 
			FileUtil.getFile("haxademic/shaders/vertex/ssao-vert.glsl")
		);
		ssaoShader.set("size", (float) p.width, (float) p.height );
		ssaoShader.set("tDiffuse", canvas );
		ssaoShader.set("tDepth", depth );
	}

	public void drawApp() {
		background(0);
		
		depthShader.set("near", UI.value(cameraNear));
		depthShader.set("far", UI.value(cameraFar));

		ssaoShader.set("onlyAO", UI.value(onlyAO) == 1);
		ssaoShader.set("aoClamp", UI.value(aoClamp));
		ssaoShader.set("lumInfluence", UI.value(lumInfluence));
		ssaoShader.set("cameraNear", UI.value(cameraNear));
		ssaoShader.set("cameraFar", UI.value(cameraFar));
		
		ssaoShader.set("samples", UI.valueInt(samples));
		ssaoShader.set("radius", UI.value(radius));
		ssaoShader.set("useNoise", UI.value(useNoise) == 1);
		ssaoShader.set("noiseAmount", UI.value(noiseAmount));
		ssaoShader.set("diffArea", UI.value(diffArea));
		ssaoShader.set("gDisplace", UI.value(gDisplace));
		ssaoShader.set("diffMult", UI.value(diffMult));
		ssaoShader.set("gaussMult", UI.value(gaussMult));
				
		// rendering
		percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete);
		progressRads = percentComplete * P.TWO_PI;

		depth.shader(depthShader);
		drawShapes(depth, false);
		
		drawShapes(canvas, false);
		
		ssaoShader.set("tDiffuse", canvas );
		ssaoShader.set("tDepth", depth );
		
		p.filter(ssaoShader);
		
		// debug
		p.image(canvas, 0, 0, 100, 100);
		p.image(depth, 100, 0, 100, 100);
//		p.translate(p.width * 5, 0); // hide Controls
	}	


	protected void drawShapes(PGraphics pg, boolean addLights) {
		pg.beginDraw();
		pg.clear();
		PG.setDrawCenter(pg);
		pg.sphereDetail(7);
		pg.noStroke();
				
		// move to center
		pg.translate(pg.width/2, pg.height/2, 0);
		
		// lighting
		if(addLights == true) pg.lights();
		
//		drawCubesInCircle(pg);
//		drawCubesInGrid(pg);
		drawFewCubes(pg);
		
		pg.endDraw();
	}
		
	protected void drawCubesInGrid(PGraphics pg) {
		// spin it
		pg.rotateX(-P.PI/3f - 0.1f + P.sin(progressRads) * 0.1f);  
		pg.translate(0, 0, -pg.height * 0.5f);
		// draw plane
		pg.fill(100);
		pg.box(p.width * 3);

		// grid setup
		float boxSize = p.width / 10f;
		float numBoxes = 120f;

		for (int x = 0; x < numBoxes; x++) {
			for (int z = 0; z < numBoxes; z++) {
				pg.fill(255f);
				pg.pushMatrix();
				float xx = (-numBoxes/2f * boxSize) + x * boxSize;
				float yy = pg.height * 1.1f;
				float zz = (-numBoxes/2f * boxSize) + z * boxSize;
				pg.translate(xx, yy, zz);
				float dist = MathUtil.getDistance(0, 0, xx, zz);
				pg.box(
						boxSize,
						pg.height * (0.4f + 0.4f * P.sin(dist/250f + progressRads)),
						boxSize
					);
				pg.popMatrix();
			}
		}	
	}
	
	protected void drawCubesInCircle(PGraphics pg) {
		// spin it
		pg.rotateZ(progressRads);

		// draw plane
		pg.fill(100);
		pg.rect(0, 0, p.width * 3, p.height * 3);

		float radius = pg.width * 0.2f;
		for (int i = 0; i < 14; i++) {
			pg.fill(200f + 55f * P.sin(i), 200f + 55f * P.cos(i * 2f), 200f + 55f * P.sin(i));
			pg.pushMatrix();
			pg.translate(radius * P.sin(i + progressRads), radius * P.cos(i + progressRads), 0);
			pg.rotateX(progressRads + i);
			pg.rotateY(progressRads + i);
			pg.box(pg.height * 0.2f);
			pg.popMatrix();
		}
	}

	protected void drawFewCubes(PGraphics pg) {
		// spin it
		pg.rotateX(P.PI/3 + P.sin(progressRads) * 0.1f);

		// draw plane
		pg.fill(255);
		pg.rect(0, 0, p.width * 3, p.height * 3);

		float radius = pg.width * 0.2f;
		for (int i = 0; i < 24; i++) {
			pg.fill(60f + 55f * P.sin(i), 170f + 35f * P.cos(i * 2f), 150f + 75f * P.sin(i));
			pg.pushMatrix();
//			pg.translate(radius * P.sin(i/3f + progressRads), radius * P.cos(i/3f + progressRads), 0);
			pg.rotateX(P.TWO_PI * p.noise(i + 0.1f * P.cos(progressRads + i)));
			pg.rotateY(P.TWO_PI * p.noise(i + 0.1f * P.sin(progressRads + i)));
			pg.rotateZ(P.TWO_PI * p.noise(i + 0.1f * P.sin(progressRads + i * 20f)));
			pg.box(
					pg.height * P.sin(i + progressRads),
					pg.height * P.sin(i + progressRads) * 0.1f,
					pg.height * P.sin(i + progressRads) * 0.1f
					);
			pg.popMatrix();
		}
	}

}

