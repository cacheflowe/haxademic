package com.haxademic.demo.draw.shapes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.RadialFlareFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VoronoiHoffsCachedShapes_Install 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// TODO:
	// - add UI for different modes
	// - add more movement modes
	// - use vertex shader to move grouped shapes - will be a big change
	// - add different color cycling modes/evolution

	protected float hoffOrthoFactor;
	protected int NUM_CELLS = 888;
	protected int CELL_DETAIL = 36;
	protected VoronoiCell cells[] = new VoronoiCell[NUM_CELLS];

	protected EasingFloat offsetR = new EasingFloat(0, 0.1f);
	protected EasingFloat offsetG = new EasingFloat(1, 0.1f);
	protected EasingFloat offsetB = new EasingFloat(2, 0.1f);

	public enum MODE_PATTERN {
		WATERFALL,
		GRID,
		SPIRAL,
		RINGS
	}
	protected MODE_PATTERN curMode = MODE_PATTERN.WATERFALL;
	private static final List<MODE_PATTERN> MODE_PATTERN_VALS = Collections.unmodifiableList(Arrays.asList(MODE_PATTERN.values()));
	private static final int PATTERNS_NUM = MODE_PATTERN_VALS.size();
	private static final Random RANDOM_PATTERN = new Random();

	public enum MODE_BEHAVIOR {
		COLLECT,
		BE_FREE
	}
	protected MODE_BEHAVIOR curBehavior = MODE_BEHAVIOR.COLLECT;
	protected int collectFrame = 0;

	protected PShader moveShader;

	protected void config() {
		int scaleUp = 1;
		Config.setAppSize( 1920 * scaleUp, 1080 * scaleUp );
		Config.setPgSize( 1920 * scaleUp, 1080 * scaleUp );
		Config.setAppSize(1920, 1080);
		Config.setPgSize(1080 * 2, 1920);
//		Config.setProperty( AppSettings.FULLSCREEN, true );
//		Config.setProperty( AppSettings.SCREEN_X, 0 );
//		Config.setProperty( AppSettings.SCREEN_Y, 0 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
		Config.setProperty( AppSettings.LOOP_FRAMES, 600 );
	}

	protected void firstFrame() {
		buildParticles();
		buildFakeLighting();
		// loadVertexShader();
	}

	protected void loadVertexShader() {
		moveShader = p.loadShader(
			P.path("haxademic/shaders/vertex/ColorFrag-positionAttrib.glsl"),
			P.path("haxademic/shaders/vertex/ColorVert-positionAttrib.glsl") 
		);
	}

	protected void buildParticles() {
		hoffOrthoFactor = dist(0, 0, pg.width, pg.height);
		for (int i = 0; i < NUM_CELLS; i++) {
			cells[i] = new VoronoiCell(i);
		}
	}

	protected void resetParticles(MODE_PATTERN mode) {
		curMode = mode;
		for (int i = 0; i < NUM_CELLS; i++)
			cells[i].resetParticle();
	}

	protected void drawApp() {
		p.background(0);
		if(KeyboardState.keyOn('1')) resetParticles(MODE_PATTERN.WATERFALL);
		if(KeyboardState.keyOn('2')) resetParticles(MODE_PATTERN.SPIRAL);
		if(KeyboardState.keyOn('3')) resetParticles(MODE_PATTERN.RINGS);
		if(KeyboardState.keyOn('4')) resetParticles(MODE_PATTERN.GRID);
		
		updateColorOffsets();
		updateBehavior();
		drawParticles();
		applyPostEffects();
		ImageUtil.cropFillCopyImage(pg, p.g, false);
	}

	protected void updateColorOffsets() {
		offsetR.update();
		offsetG.update();
		offsetB.update();
	}

	protected void newColorOffsets() {
		offsetR.setTarget(p.random(P.TWO_PI));
		offsetG.setTarget(p.random(P.TWO_PI));
		offsetB.setTarget(p.random(P.TWO_PI));
	}

	protected void updateBehavior() {
		if(FrameLoop.frameModMinutes(0.3f)) {
			curBehavior = MODE_BEHAVIOR.COLLECT;
			curMode = randomMode();
			for (int i = 0; i < NUM_CELLS; i++) cells[i].setPatternMode(curMode); // tell them to outro
			collectFrame = p.frameCount;
			P.out("MODE_BEHAVIOR.COLLECT", curMode);
			newColorOffsets();
		}
		if(p.frameCount == collectFrame + 240) {
			curBehavior = MODE_BEHAVIOR.BE_FREE;
			P.out("MODE_BEHAVIOR.BE_FREE");
		}
	}

	protected MODE_PATTERN randomMode() {
		return MODE_PATTERN_VALS.get(RANDOM_PATTERN.nextInt(PATTERNS_NUM));
	}

	protected void drawParticles() {
		pg.beginDraw();
		//		PG.setDrawFlat2d(pg, true);
		pg.background(127);
		pg.noStroke();
		pg.ortho();
		// pg.shader(moveShader);
		for (int i = 0; i < NUM_CELLS; i++) {
			cells[i].draw();
			cells[i].advance();
		}			
		// pg.resetShader();
		pg.endDraw();
	}

	protected void applyPostEffects() {
		applyLighting();
	}



	//////////////////////////
	// PARTICLE
	//////////////////////////

	public class VoronoiCell {
		protected int i;
		protected int age;
		protected LinearFloat scale = new LinearFloat(1, 0.25f);
		protected float x, y;
		protected float speedX, speedY;
		protected float accelX, accelY;
		protected PShape shape;
		protected MODE_PATTERN mode;

		VoronoiCell(int i) {
			this.i = i;
			this.x = MathUtil.randRange(0, pg.width);
			this.y = MathUtil.randRange(0, pg.height);
			this.speedX = 0; 
			this.speedY = 0;
			this.accelX = 1; 
			this.accelY = 1;
			resetParticle();
		}
		
		public VoronoiCell setPosition(float x, float y) {
			this.x = x;
			this.y = y; 
			return this;
		}
		public float x() { return x; }
		public float y() { return y; }

		public void draw() {
			if(shape == null) buildShape();

			// draw shape
			pg.push();
			shape.disableStyle();
			pg.fill(P.sin(i + offsetR.value()) * 127 + 127, P.sin(i + offsetG.value()) * 127 + 127, P.sin(i + offsetB.value()) * 127 + 127);
			pg.translate(this.x, this.y, 0);
			float curScale = (scale.value() != 1) ? 
				Penner.easeInOutExpo(scale.value()) : 
				1;
			pg.scale(curScale, curScale, 1);
			pg.shape(shape);
			pg.pop();
		}

		protected void buildShape() {
			float rads = 0;
			float segmentRads = P.TWO_PI / CELL_DETAIL;
			float rFix = hoffOrthoFactor * 0.5f;

			// PShape group = p.createShape(P.GROUP);
			shape = p.createShape();
			shape.beginShape();
			shape.noStroke();
			shape.fill( p.random(0, 255));
			for (int i = 0; i < CELL_DETAIL; i++) {
				rads = segmentRads * i;
				shape.vertex(0, 0, -rFix);
				// shape.attrib("shapeCenter", p.random(0, width));
				shape.vertex(hoffOrthoFactor * cos(rads), hoffOrthoFactor * sin(rads), -hoffOrthoFactor);
				// shape.attrib("shapeCenter", p.random(0, width));
				shape.vertex(hoffOrthoFactor * cos(rads + segmentRads), hoffOrthoFactor * sin(rads + segmentRads), -hoffOrthoFactor);
				// shape.attrib("shapeCenter", p.random(0, width));
			}
			shape.disableStyle();
			shape.endShape();
		}

		public void advance() {
			updateScale();
			updateMovement();
			checkRecycle();
		}

		public void setPatternMode(MODE_PATTERN mode) {
			scale.setDelay((int) p.random(0, 180)).setTarget(0);
		}
		
		protected void updateScale() {
			scale.setInc(0.01f);
			scale.update();
			if(scale.value() == 0 && scale.target() == 0) {
				this.mode = curMode;
				scale.setTarget(1);
				// if(curBehavior == MODE_BEHAVIOR.COLLECT) 
					resetParticle();
			}
		}

		protected void updateMovement() {
			if(curBehavior != MODE_BEHAVIOR.BE_FREE) return;
			
			this.speedX *= this.accelX;
			this.speedY *= this.accelY;
			this.x += this.speedX;
			this.y += this.speedY;

			if (mode == MODE_PATTERN.WATERFALL) {
				float heightProgress = this.y / pg.height;
				this.x += P.sin(i + heightProgress) * heightProgress;
			}
		}

		protected void resetParticle() {
			age = 0;

			if(mode == MODE_PATTERN.WATERFALL) {
				this.x = MathUtil.randRange(0, pg.width);
				this.y = MathUtil.randRange(0, pg.height);
				this.speedY = p.random(0.25f, 1f);
				this.speedX = 0f;
				this.accelY = 1 + MathUtil.randRangeDecimal(0.005f, 0.01f);
				this.accelX = 1f;
			}
			if(mode == MODE_PATTERN.RINGS) {
				float numVertices = 28;
				float segmentRads = P.TWO_PI / numVertices;
				float rads = i * segmentRads;
				float radius = P.floor(i / numVertices) * segmentRads * 250f;
				float offsetX = FrameLoop.noiseLoop(0.9f, i) * 0.1f;
				float offsetY = FrameLoop.noiseLoop(0.9f, i) * 0.1f;
				float offsetScale = FrameLoop.noiseLoop(0.9f, i * 2) * 0.4f;
				radius *= 1f + offsetScale;
				setPosition(pg.width / 2 + P.cos(rads + offsetX) * radius, pg.height / 2 + P.sin(rads + offsetY) * radius);
				this.speedX = P.cos(rads) * 0.6f;
				this.speedY = P.sin(rads) * 0.6f;
				this.accelY = 1f;
				this.accelX = 1f;
			}
			if(mode == MODE_PATTERN.SPIRAL) {
				float segmentRads = P.TWO_PI / 36;
				float rads = p.frameCount * 0.004f + i * segmentRads;
				float radius = i * 1.5f;
				if (i % 2 == 0) {
					setPosition(pg.width / 2 + P.cos(rads) * radius, pg.height / 2 + P.sin(rads) * radius);
				} else {
					setPosition(pg.width / 2 + P.cos(-rads) * radius * 1.1f, pg.height / 2 + P.sin(-rads) * radius * 1.1f);
				}
				this.speedX = P.cos(rads) * 0.6f;
				this.speedY = P.sin(rads) * 0.6f;
				this.accelY = 1f;
				this.accelX = 1f;
			}
			if(mode == MODE_PATTERN.GRID) {
				int gridRes = P.floor(P.sqrt(NUM_CELLS));
				float spacingH = pg.width / gridRes * 1.1f;
				float spacingV = pg.height / gridRes * 1.1f;
				float xIndex = MathUtil.gridXFromIndex(i, gridRes);
				float yIndex = MathUtil.gridYFromIndex(i, gridRes);
				if (xIndex % 2 == 0) {
					xIndex -= 0.5f;
					yIndex += 0.5f;
				}
				if (xIndex % 3 == 0) {
					xIndex += 0.5f;
					yIndex += 0.5f;
				}
				this.speedY = 0.2f;
				this.speedX = 0f;
				this.accelY = 1f;
				this.accelX = 1f;
				setPosition(xIndex * spacingH, yIndex * spacingV);
			}
		}

		protected void checkRecycle() {
			if(mode == MODE_PATTERN.WATERFALL) {
				if (this.y > pg.height * 1.5f) {
					this.y = -pg.height * 0.1f;
					this.speedY = p.random(0.25f, 1f);
					this.accelY = 1 + MathUtil.randRangeDecimal(0.01f, 0.015f);
					scale.setCurrent(0).setTarget(0);	// set cleanup via scale updates!
				}
			}
			if(mode == MODE_PATTERN.RINGS || mode == MODE_PATTERN.SPIRAL) {
				if(MathUtil.getDistance(this.x, this.y, pg.width / 2, pg.height / 2) > pg.width * 0.7f) {
					setPosition(pg.width / 2, pg.height / 2);
					scale.setCurrent(0).setTarget(0);	// set cleanup via scale updates!
				}
			}
		}
	}



	////////////////////////////////////////////////////
	// PostFX / Lighting 
	////////////////////////////////////////////////////
	
	protected PGraphics pgLightingBlur;
	protected String FILTER_ACTIVE = "FILTER_ACTIVE";
	protected String LIGHTING_ID = "FAKELIGHT_";

	protected void buildFakeLighting() {
		FakeLightingFilter.instance().buildUI(LIGHTING_ID, false);
		UI.addToggle(FILTER_ACTIVE, true, false);
		pgLightingBlur = PG.newPG(pg.width/4, pg.height/4);
	}

	protected void applyLighting() {
		if(UI.valueToggle(FILTER_ACTIVE) == false) return;

		// update blur map
		ImageUtil.copyImage(pg, pgLightingBlur);
		BlurHFilter.instance().setBlurByPercent(1, pgLightingBlur.width);
		BlurVFilter.instance().setBlurByPercent(1, pgLightingBlur.height);
		BlurHFilter.instance().applyTo(pgLightingBlur);
		BlurVFilter.instance().applyTo(pgLightingBlur);
		BlurHFilter.instance().applyTo(pgLightingBlur);
		BlurVFilter.instance().applyTo(pgLightingBlur);
		BlurHFilter.instance().applyTo(pgLightingBlur);
		BlurVFilter.instance().applyTo(pgLightingBlur);
		DebugView.setTexture("pgLightingBlur", pgLightingBlur);

		// apply lighting effect to main drawing
		// Ambient: 2.8
		// Grad Amp: 0.45
		// Grad Blur: 0.25
		// Spec Amp: 4.5
		// DiffDark: 0.8
		FakeLightingFilter.instance().setMap(pgLightingBlur);
		FakeLightingFilter.instance().setPropsFromUI(LIGHTING_ID);
		FakeLightingFilter.instance().applyTo(pg);

		GodRays.instance().setDecay(0.8f);
		GodRays.instance().setWeight(0.3f);
		GodRays.instance().setRotation(Mouse.xEasedNorm * -6f);
		GodRays.instance().setAmp(0.1f);
		// GodRays.instance().applyTo(pg);

		RadialFlareFilter.instance().setImageBrightness(10f);
		RadialFlareFilter.instance().setFlareBrightness(10f);
		RadialFlareFilter.instance().setRadialLength(.5f);
		RadialFlareFilter.instance().setIters(50);
		RadialFlareFilter.instance().applyTo(pg);
	}
}
