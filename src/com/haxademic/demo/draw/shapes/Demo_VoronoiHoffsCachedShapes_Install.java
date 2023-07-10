package com.haxademic.demo.draw.shapes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.ColorCorrectionFilter;
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
import com.haxademic.core.system.Console;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PShader;

public class Demo_VoronoiHoffsCachedShapes_Install 
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// TODO:
	// Needs
	// - Sometimes some of the particles should assume an alternate mode of behavior for variety
	// - Hole in new pattern animation before releasing particles
	// - Clean up recycling vs new mode recycling
	// - Add Uptime suite & move dashboard URL into run.properties
	// - Add 5x grid placement mode
	// Maybes
	// - add UI for different modes
	// - add more movement modes
	// - add different color cycling modes/evolution
	// - use vertex shader to move grouped shapes - will be a big change

	// particles
	protected float hoffOrthoFactor;
	protected int NUM_CELLS = 888;
	protected int CELL_DETAIL = 36;
	protected VoronoiCell cells[] = new VoronoiCell[NUM_CELLS];
	protected LinearFloat globalSpeedMult = new LinearFloat(1, 0.003f);

	// colors
	protected float colorEaseFactor = 0.01f;
	protected EasingFloat offsetR = new EasingFloat(0, colorEaseFactor);
	protected EasingFloat offsetG = new EasingFloat(1, colorEaseFactor);
	protected EasingFloat offsetB = new EasingFloat(2, colorEaseFactor);
	protected float[][] colorOffsets;
	protected int colorSetIndex = 0;
	protected String UI_R = "UI_R";
	protected String UI_G = "UI_G";
	protected String UI_B = "UI_B";

	// current particle arrangement & behavior
	public enum MODE_PATTERN {
		WATERFALL,
		GRID,
		SPIRAL,
		RINGS
	}
	protected MODE_PATTERN curPatternMode = MODE_PATTERN.WATERFALL;
	private static final List<MODE_PATTERN> MODE_PATTERN_VALS = Collections.unmodifiableList(Arrays.asList(MODE_PATTERN.values()));
	private static final int PATTERNS_NUM = MODE_PATTERN_VALS.size();
	private static final Random RANDOM_PATTERN = new Random();

	// current cycling mode
	public enum SYSTEM_MODE {
		COLLECT,
		BE_FREE
	}
	protected SYSTEM_MODE curSystemMode = SYSTEM_MODE.COLLECT;

	protected PShader moveShader;

	protected void config() {
		int appW = 1080 * 2;
		int appH = 1920;
		Config.setAppSize(appW, appH);
		Config.setPgSize(appW, appH);
		Config.setProperty( AppSettings.FULLSCREEN, true );
		Config.setProperty( AppSettings.SCREEN_X, 1920 );
		Config.setProperty( AppSettings.SCREEN_Y, 0 );
		Config.setProperty( AppSettings.SHOW_DEBUG, false );
		Config.setProperty( AppSettings.SHOW_UI, false );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, false );
		Config.setProperty( AppSettings.LOOP_FRAMES, 1500 );
	}

	protected void firstFrame() {
		buildParticles();
		buildFakeLighting();
		buildColorOffsets();
		ColorCorrectionFilter.instance().buildUI(COLOR_CORRECTION_ID, true);
		// loadVertexShader();
		P.store.addListener(this);
	}

	protected void loadVertexShader() {
		moveShader = p.loadShader(
			P.path("haxademic/shaders/vertex/ColorFrag-positionAttrib.glsl"),
			P.path("haxademic/shaders/vertex/ColorVert-positionAttrib.glsl") 
		);
	}

	protected void buildColorOffsets() {
		// build UI
		if(colorOffsets == null) {
			UI.addTitle("Color Offsets");
			UI.addSlider(UI_R, 0, 0, P.TWO_PI, 0.01f, false);
			UI.addSlider(UI_G, 1, 0, P.TWO_PI, 0.01f, false);
			UI.addSlider(UI_B, 2, 0, P.TWO_PI, 0.01f, false);
		}
		// build array
		colorOffsets = new float[][] {
			new float[] {0, 1, 2},
			new float[] {1, 0, 2},
			new float[] {2.506f, 1.796f, 2.464f},
			new float[] {5.586f, 5.243f, 6.093f},
			new float[] {5.183f, 5.259f, 6.153f},
			new float[] {5.518f, 0.243f, 0.598f},
			new float[] {1.223f, 1.312f, 0.653f},
			new float[] {3.042f, 3.154f, 3.529f},
		};
	}

	protected void buildParticles() {
		hoffOrthoFactor = dist(0, 0, pg.width, pg.height);
		for (int i = 0; i < NUM_CELLS; i++) {
			cells[i] = new VoronoiCell(i);
		}
	}

	protected void resetParticles(MODE_PATTERN mode) {
		curPatternMode = mode;
		for (int i = 0; i < NUM_CELLS; i++) {
			cells[i].setStartPosition();
			cells[i].nextPatternMode(curPatternMode);
		}
	}

	protected void drawApp() {
		p.background(0);
		if(KeyboardState.keyOn('1')) resetParticles(MODE_PATTERN.WATERFALL);
		if(KeyboardState.keyOn('2')) resetParticles(MODE_PATTERN.SPIRAL);
		if(KeyboardState.keyOn('3')) resetParticles(MODE_PATTERN.RINGS);
		if(KeyboardState.keyOn('4')) resetParticles(MODE_PATTERN.GRID);
		if(KeyboardState.keyTriggered('c')) randomColorOffsets();
		if(KeyboardState.keyTriggered('v')) printColorOffsets();
		if(KeyboardState.keyTriggered('b')) nextColorOffsets(1);
		if(KeyboardState.keyTriggered('n')) nextColorOffsets(-1);
		if(KeyboardState.keyTriggered('m')) buildColorOffsets();
		
		updateColorOffsets();
		updateBehavior();
		drawParticles();
		applyPostEffects();
		ImageUtil.cropFillCopyImage(pg, p.g, false);
	}

	protected void updateColorOffsets() {
//		offsetR.setEaseFactor(0.1f);
//		offsetG.setEaseFactor(0.1f);
//		offsetB.setEaseFactor(0.1f);
		offsetR.update(true);
		offsetG.update(true);
		offsetB.update(true);
	}

	protected void randomColorOffsets() {
		offsetR.setTarget(p.random(P.TWO_PI));
		offsetG.setTarget(p.random(P.TWO_PI));
		offsetB.setTarget(p.random(P.TWO_PI));
		printColorOffsets();
	}

	protected void nextColorOffsets(int inc) {
		// cycle
		colorSetIndex += inc;
		colorSetIndex %= colorOffsets.length;
		if(colorSetIndex < 0) colorSetIndex = colorOffsets.length - 1;
		// print!
		// set index
		setColorIndex(colorSetIndex);
	}

	protected void setColorIndex(int index) {
		colorSetIndex = index;
		float r = colorOffsets[colorSetIndex][0];
		float g = colorOffsets[colorSetIndex][1];
		float b = colorOffsets[colorSetIndex][2];
		offsetR.setTarget(r);
		offsetG.setTarget(g);
		offsetB.setTarget(b);
		// print!
//		P.outColor(Console.YELLOW_BACKGROUND, "["+colorSetIndex+"]", r, g, b);
//		printColorOffsets();
	}

	protected void printColorOffsets() {
		P.outColor(
			Console.CYAN_BACKGROUND, 
			MathUtil.roundToPrecision(offsetR.target(), 3) + "f,", 
			MathUtil.roundToPrecision(offsetG.target(), 3) + "f,", 
			MathUtil.roundToPrecision(offsetB.target(), 3) + "f"
		);
	}

	protected void updateBehavior() {
		globalSpeedMult.update();
		
//		if(FrameLoop.frameModMinutes(0.3f)) {
		if(FrameLoop.loopCurFrame() == 5) {
			transitionToNewMode();
		}
//		if(p.frameCount == collectFrame + 240) {
		if(FrameLoop.loopCurFrame() == 280) {
			releaseInNewMode();
		}
	}

	protected void transitionToNewMode() {
		curSystemMode = SYSTEM_MODE.COLLECT;
		
		// make sure we get a new random pattern mode that's different
		MODE_PATTERN lastMode = curPatternMode;
		curPatternMode = randomMode();
		while(lastMode == curPatternMode) curPatternMode = randomMode(); 
		
		// tell particles to outro
		for (int i = 0; i < NUM_CELLS; i++) {
			cells[i].nextPatternMode(curPatternMode); 
		}

		
		// slow down for outro
		setColorIndex(MathUtil.randIndex(colorOffsets.length));
		globalSpeedMult.setTarget(0);
	}
	
	protected void releaseInNewMode() {
		curSystemMode = SYSTEM_MODE.BE_FREE;
		globalSpeedMult.setTarget(1);
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
		applyColorCorrection();
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
		protected MODE_PATTERN curMode;
		protected MODE_PATTERN queuedMode;

		VoronoiCell(int i) {
			this.i = i;
			this.x = MathUtil.randRange(0, pg.width);
			this.y = MathUtil.randRange(0, pg.height);
			this.speedX = 0; 
			this.speedY = 0;
			this.accelX = 1; 
			this.accelY = 1;
			setStartPosition();
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
			pg.fill(
				P.sin(i + offsetR.value()) * 127 + 127, 
				P.sin(i + offsetG.value()) * 127 + 127, 
				P.sin(i + offsetB.value()) * 127 + 127
			);
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
			shape.endShape();
		}

		public void advance() {
			checkRecycle();
			updateScale();
			updateMovement();
		}

		public void nextPatternMode(MODE_PATTERN mode) {
			queuedMode = mode;
			scale.setDelay((int) p.random(0, 180)).setTarget(0);
		}
		
		protected void updateScale() {
			scale.setInc(0.01f);
			scale.update();
			if(scale.value() == 0 && scale.target() == 0) {
				boolean modeChanged = curMode != queuedMode; 
				curMode = queuedMode;
				scale.setTarget(1);
				if(modeChanged) {
					setStartPosition();
				} else {
					recycle();
				}
			}
		}

		protected void updateMovement() {
//			if(curSystemMode != SYSTEM_MODE.BE_FREE) return;
			
			this.speedX *= this.accelX;
			this.speedY *= this.accelY;
			this.x += this.speedX * globalSpeedMult.value();
			this.y += this.speedY * globalSpeedMult.value();

			if (curMode == MODE_PATTERN.WATERFALL) {
				float heightProgress = this.y / pg.height;
				this.x += P.sin(i + heightProgress) * heightProgress;
			}
		}

		protected void setStartPosition() {
			age = 0;

			if(curMode == MODE_PATTERN.WATERFALL) {
				this.x = MathUtil.randRange(0, pg.width);
				this.y = MathUtil.randRange(0, pg.height);
				this.speedY = p.random(0.25f, 1f);
				this.speedX = 0f;
				this.accelY = 1 + MathUtil.randRangeDecimal(0.005f, 0.01f);
				this.accelX = 1f;
			}
			if(curMode == MODE_PATTERN.RINGS) {
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
			if(curMode == MODE_PATTERN.SPIRAL) {
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
			if(curMode == MODE_PATTERN.GRID) {
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
			if(curMode == MODE_PATTERN.WATERFALL) {
				if (this.y > pg.height * 1.25f) {
					scale.setTarget(0);
				}
			}
			if(curMode == MODE_PATTERN.RINGS || curMode == MODE_PATTERN.SPIRAL) {
				if(MathUtil.getDistance(this.x, this.y, pg.width / 2, pg.height / 2) > pg.width * 0.8f) {
					scale.setTarget(0);
				}
			}
		}
		
		protected void recycle() {
			if(curMode == MODE_PATTERN.WATERFALL) {
				this.x = MathUtil.randRange(0, pg.width);
				this.y = -pg.height * 0.2f;
				this.speedY = p.random(0.25f, 1f);
				this.accelY = 1 + MathUtil.randRangeDecimal(0.01f, 0.015f);
			}
			if(curMode == MODE_PATTERN.RINGS || curMode == MODE_PATTERN.SPIRAL) {
				setPosition(pg.width / 2, pg.height / 2);
			}
		}
	}



	////////////////////////////////////////////////////
	// PostFX / Lighting 
	////////////////////////////////////////////////////
	
	protected PGraphics pgLightingBlur;
	protected String FILTER_ACTIVE = "FILTER_ACTIVE";
	protected String LIGHTING_ID = "FAKELIGHT_";
	protected String COLOR_CORRECTION_ID = "COLOR_";

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
		// BlurHFilter.instance().applyTo(pgLightingBlur);
		// BlurVFilter.instance().applyTo(pgLightingBlur);
		// BlurHFilter.instance().applyTo(pgLightingBlur);
		// BlurVFilter.instance().applyTo(pgLightingBlur);
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

	protected void applyColorCorrection() {
		ColorCorrectionFilter.instance().setPropsFromUI(COLOR_CORRECTION_ID);
		ColorCorrectionFilter.instance().applyTo(pg);
	}

	////////////////////////////////////////////////////
	// AppStore listeners
	////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(UI_R)) { offsetR.setTarget(val.floatValue()); }
		if(key.equals(UI_G)) { offsetG.setTarget(val.floatValue()); }
		if(key.equals(UI_B)) { offsetB.setTarget(val.floatValue()); }
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}