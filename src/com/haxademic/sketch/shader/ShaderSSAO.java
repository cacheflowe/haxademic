package com.haxademic.sketch.shader;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class ShaderSSAO
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// ported from: view-source:https://threejs.org/examples/webgl_postprocessing_ssao.html
	
	PGraphics canvas;
	PGraphics depth;
	PShader depthShader;
	PShader ssaoShader;
	float _frames = 420;
	float percentComplete;
	float progressRads;
	
	ControlP5 _cp5;
	public boolean onlyAO, onlyAODefault = false;
	public float aoClamp, aoClampDefault = 1.5f;
	public float lumInfluence = 0.2f;
	public float cameraNear = 235f;
	public float cameraFar = 1160f;
	public int samples = 32;
	public float radius = 17.0f;
	public boolean useNoise = true;
	public float noiseAmount = 0.00003f;
	public float diffArea = 0.65f;
	public float gDisplace = 0.65f;
	public float diffMult = 100f;
	public float gaussMult = -2.0f;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames );
	}

	public void setup() {
		super.setup();
		
		// controls
//		_showControls = true;
		_cp5 = new ControlP5(this);
		int spacing = 20;
		int cntrlY = 0;
		int cntrlW = 100;
		
		_cp5.addToggle("onlyAO").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setHeight(10).setValue(onlyAODefault);
		_cp5.addSlider("aoClamp").setPosition(20,cntrlY+=spacing+10).setWidth(cntrlW).setRange(-5f,5f).setValue(aoClampDefault);
		_cp5.addSlider("lumInfluence").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(-5f,5f).setValue(lumInfluence);
		_cp5.addSlider("cameraNear").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(1f,500f).setValue(175);
		_cp5.addSlider("cameraFar").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(500f,2000f).setValue(1700);
		_cp5.addSlider("samples").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(2,128).setValue(samples);
		_cp5.addSlider("radius").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(1f,50f).setValue(radius);
		_cp5.addSlider("diffArea").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,5f).setValue(diffArea);
		_cp5.addSlider("gDisplace").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(0,5f).setValue(gDisplace);
		_cp5.addSlider("diffMult").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(1f,1000f).setValue(diffMult);
		_cp5.addSlider("gaussMult").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setRange(-4f,2f).setValue(-2f);
		_cp5.addToggle("useNoise").setPosition(20,cntrlY+=spacing).setWidth(cntrlW).setHeight(10).setValue(useNoise);
		_cp5.addSlider("noiseAmount").setPosition(20,cntrlY+=spacing+10).setWidth(cntrlW).setRange(0.00003f, 0.003f).setValue(noiseAmount);


		canvas = p.createGraphics(p.width, p.height, P.P3D);
		canvas.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(canvas);
		
		depth = p.createGraphics(p.width, p.height, P.P3D);
		depth.smooth(OpenGLUtil.SMOOTH_HIGH);
		OpenGLUtil.setTextureQualityHigh(depth);
		
		depthShader = loadShader(
				FileUtil.getFile("shaders/vertex/depth-frag.glsl"), 
				FileUtil.getFile("shaders/vertex/depth-vert.glsl")
				);

		ssaoShader = loadShader(
			FileUtil.getFile("shaders/vertex/ssao-frag.glsl"), 
			FileUtil.getFile("shaders/vertex/ssao-vert.glsl")
		);
		ssaoShader.set("size", (float) p.width, (float) p.height );
		ssaoShader.set("tDiffuse", canvas );
		ssaoShader.set("tDepth", depth );
	}

	public void drawApp() {
		background(0);
		
		depthShader.set("near", cameraNear );
		depthShader.set("far", cameraFar );

		ssaoShader.set("onlyAO", onlyAO );
		ssaoShader.set("aoClamp", aoClamp );
		ssaoShader.set("lumInfluence", lumInfluence );
		ssaoShader.set("cameraNear", cameraNear );
		ssaoShader.set("cameraFar", cameraFar );
		
		ssaoShader.set("samples", samples);
		ssaoShader.set("radius", radius);
		ssaoShader.set("useNoise", useNoise);
		ssaoShader.set("noiseAmount", noiseAmount);
		ssaoShader.set("diffArea", diffArea);
		ssaoShader.set("gDisplace", gDisplace);
		ssaoShader.set("diffMult", diffMult);
		ssaoShader.set("gaussMult", gaussMult);
		
//		setSSAOForCubes();
		
		
		// rendering
		percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		progressRads = percentComplete * P.TWO_PI;

		depth.shader(depthShader);
		drawShapes(depth, false);
		
		drawShapes(canvas, true);
		
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
		DrawUtil.setDrawCenter(pg);
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
	
	protected void setSSAOForCubes() {
		depthShader.set("near", 450f );
		depthShader.set("far", 1565f );

		ssaoShader.set("onlyAO", false );
		ssaoShader.set("aoClamp", -4.6f );
		ssaoShader.set("lumInfluence", -0.5f );
		ssaoShader.set("cameraNear", 450f );
		ssaoShader.set("cameraFar", 1565f );
		
		ssaoShader.set("samples", 105);
		ssaoShader.set("radius", 13f);
		ssaoShader.set("useNoise", false);
		ssaoShader.set("noiseAmount", 0);
		ssaoShader.set("diffArea", 0.65f);
		ssaoShader.set("gDisplace", 1.7f);
		ssaoShader.set("diffMult", 140f);
		ssaoShader.set("gaussMult", -2f);
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

