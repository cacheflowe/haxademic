package com.haxademic.demo.draw.particle;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.AppRestart;
import com.haxademic.core.system.SystemUtil;

import processing.core.PVector;

public class Demo_ParticleBranchers 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<BranchingParticle> particles;
	protected ArrayList<BranchingParticle> deadParticles;
	protected boolean resetQueued = false;
	protected int updatesPerFrame = 5;
	protected float strokeSize = 2;

	protected float maxLifespan = 200;
	protected float maxBranchAge = 100;
	protected int numInitialParticles = 2;
	protected float maxDescendants = 10;
	protected boolean evenInitialSpread = false;
	protected boolean childrenBranchFaster = false;
	protected float curveAmp = 0.01f;
	
	protected final int TURN_MODE_NONE = 0;
	protected final int TURN_MODE_ON = 1;
	protected final int TURN_MODE_MIXED = 2;
	protected final int TURN_MODE_NOISE = 3;
	protected int[] turnModes = new int[] {TURN_MODE_NONE, TURN_MODE_ON, TURN_MODE_MIXED, TURN_MODE_NOISE};
	protected int turnMode = TURN_MODE_NONE;
	
	protected final int COLOR_MODE_ALL_COLORS = 0;
	protected final int COLOR_MODE_COLORS_ON_WHITE = 1;
	protected final int COLOR_MODE_COLORS_ON_BLACK = 2;
	protected final int COLOR_MODE_WHITE_ON_BLACK = 3;
	protected final int COLOR_MODE_WHITE_ON_BLACK_FADE = 4;
	protected final int COLOR_MODE_BLACK_ON_WHITE = 5;
	protected final int COLOR_MODE_TRIG = 6;
	protected int[] colorModes = new int[] {COLOR_MODE_ALL_COLORS, COLOR_MODE_COLORS_ON_WHITE, COLOR_MODE_COLORS_ON_BLACK, COLOR_MODE_WHITE_ON_BLACK, COLOR_MODE_WHITE_ON_BLACK_FADE, COLOR_MODE_BLACK_ON_WHITE, COLOR_MODE_TRIG};
	protected int colorMode = COLOR_MODE_ALL_COLORS;
	
	protected int bgColor;
	protected ImageGradient imageGradient;
	
	protected int endFrame = -1;
	protected boolean renderImages = false;
	protected boolean renderSingleMovie = false;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1080 );
		Config.setProperty( AppSettings.HEIGHT, 1080 );
		Config.setProperty( AppSettings.FULLSCREEN, true );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		if(renderSingleMovie) Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + 60 * 60 );
	}

	public void firstFrame() {
		// load palette
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
		imageGradient.randomGradientTexture();
		
		// build particles arrays for recycling / object pool
		particles = new ArrayList<BranchingParticle>();
		deadParticles = new ArrayList<BranchingParticle>();
		
		// start with a clear canvas
		bgColor = p.color(0);
		pg.beginDraw();
		pg.background(bgColor);
		pg.endDraw();

		// new props to start
		reset();
	}
	
	protected void reset() {
		// new parameters
		maxLifespan = MathUtil.randRange(100, 500);
		maxBranchAge = maxLifespan * MathUtil.randRangeDecimal(0.1f, 0.9f);
		numInitialParticles = MathUtil.randRange(2, 10);
		maxDescendants = MathUtil.randRange(3, 30);
		evenInitialSpread = MathUtil.randBoolean();
		childrenBranchFaster = MathUtil.randBoolean();
		turnMode = turnModes[MathUtil.randRange(0, turnModes.length - 1)];
		colorMode = colorModes[MathUtil.randRange(0, colorModes.length - 1)];
		curveAmp = MathUtil.randRangeDecimal(0.001f, 0.03f);
		
		// override for rendering
		colorMode = COLOR_MODE_ALL_COLORS;
		
		// new color palette
		imageGradient.randomGradientTexture();
		switch (colorMode) {
			case COLOR_MODE_ALL_COLORS:
				bgColor = imageGradient.getColorAtProgress(0);
				break;
			case COLOR_MODE_COLORS_ON_WHITE:
			case COLOR_MODE_BLACK_ON_WHITE:
				bgColor = p.color(255);
				break;
			case COLOR_MODE_COLORS_ON_BLACK:
			case COLOR_MODE_WHITE_ON_BLACK:
			case COLOR_MODE_WHITE_ON_BLACK_FADE:
			case COLOR_MODE_TRIG:
				bgColor = p.color(0);
				break;
			default: break;
		}

		// slay all particles
		for (int i = 0; i < particles.size(); i++) particles.get(i).die();
		resetQueued = true;
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') reset();
	}
	
	public void drawApp() {
		// clear app background
		background(0);
		
		// draw to main canvas. make canvas pixels accessible
		pg.beginDraw();
		pg.strokeWeight(strokeSize);

		// clear background if queued
		checkClearCanvas();

		// update particles
		updatesPerFrame = 10;
		pg.loadPixels();
		for (int j = 0; j < updatesPerFrame; j++) {
			for (int i = 0; i < particles.size(); i++) particles.get(i).update();
			for (int i = 0; i < particles.size(); i++) particles.get(i).update();
		
			// post-processing for special fade mode
			if(colorMode == COLOR_MODE_WHITE_ON_BLACK_FADE) {
				BrightnessStepFilter.instance(p).setBrightnessStep(-1f/255f * 1.0f);
				BrightnessStepFilter.instance(p).applyTo(pg);
			}
		}
		
		// close offscreen buffer
		pg.endDraw();
				
		// draw to screen & postprocess
		p.image(pg, 0, 0);
		postProcess();
		
		// auto reset particles
		DebugView.setValue("particles", particles.size());
		if(particles.size() == 0 && endFrame == -1) {
			endFrame = p.frameCount;
		}
		if(endFrame != -1 && p.frameCount > endFrame + 60) {
			if(renderSingleMovie) {
				Renderer.instance().videoRenderer.stop();
				AppRestart.restart(p);
			} else {
				if(renderImages) saveFrame(FileUtil.getHaxademicOutputPath() + "branchers" + FileUtil.SEPARATOR + SystemUtil.getTimestampFine() + ".png");
				reset();
				endFrame = -1;
			}
		}
	}
	
	protected void checkClearCanvas() {
		if(resetQueued) {
			pg.background(bgColor);
			regenerateParticles();
			resetQueued = false;
		}
	}
	
	protected void postProcess() {
		FXAAFilter.instance(p).applyTo(p.g);
		
		BloomFilter.instance(p).setStrength(0.1f);
		BloomFilter.instance(p).setBlurIterations(2);
		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_DARKEST);
//		BloomFilter.instance(p).applyTo(p.g);
//		BloomFilter.instance(p).applyTo(pg);
		
		VignetteFilter.instance(p).setDarkness(0.4f);
		VignetteFilter.instance(p).applyTo(p.g);

		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.08f);
		GrainFilter.instance(p).applyTo(p.g);
	}
	
	protected void regenerateParticles() {
		for (int i = 0; i < numInitialParticles; i++) {
			// set direction
			float dir = p.random(P.TWO_PI);
			if(evenInitialSpread) dir = P.TWO_PI/numInitialParticles * i;

			// launch particle
			BranchingParticle newParticle = getParticle();
			newParticle.reset(pg.width/2, pg.height/2, dir, 0);
			particles.add(newParticle);
		}
	}
	
	public BranchingParticle getParticle() {
		if(deadParticles.size() > 0) return deadParticles.remove(deadParticles.size() - 1);
		else return new BranchingParticle();
	}
	
	///////////////////////////
	// Particle class
	///////////////////////////
	
	public class BranchingParticle {
		
		protected PVector position = new PVector();
		protected float direction = 0;
		protected float speed = 1;
		protected int age = 0;
		protected int branchAge = 0;
		protected int lifeSpan = 0;
		protected int gen = 0;
		protected int curColor = 0;
		protected float colorSample = 0;
		protected float turnAmp = 0;
		
		public BranchingParticle() {}
		
		public void reset(float x, float y, float dir, int generation) {
			// reset
			position.set(x, y);
			direction = dir;
			gen = generation + 1;
			age = 0;
			
			// new random lifespan & branch time values
			lifeSpan = MathUtil.randRange(maxLifespan/2f, maxLifespan);
			branchAge = MathUtil.randRange(maxBranchAge/2f, maxBranchAge);
			if(childrenBranchFaster) branchAge /= this.gen / 4f;	// branch faster with child depth
			
			// pick new color
			newLineColor();
			while(curColor == bgColor) newLineColor();
			
			// pick a turn mode
			if(turnMode == TURN_MODE_NONE || turnMode == TURN_MODE_NOISE) {
				turnAmp = 0;
			} else if(turnMode == TURN_MODE_ON || turnMode == TURN_MODE_MIXED) {
//				turnAmp = (gen % 2 == 0) ? 0.01f * gen : -0.01f * gen; 	// turn based on generation
				turnAmp = curveAmp * gen;
				if(MathUtil.randBoolean()) turnAmp *= -1f;
			} 
			if(turnMode == TURN_MODE_MIXED) {
				if(MathUtil.randBoolean()) turnAmp = 0;
			}
		}
		
		protected void newLineColor() {
			switch (colorMode) {
				case COLOR_MODE_ALL_COLORS:
				case COLOR_MODE_COLORS_ON_WHITE:
				case COLOR_MODE_COLORS_ON_BLACK:
					colorSample += MathUtil.randRangeDecimal(0.01f, 0.5f);
					colorSample = colorSample % 1f;
					curColor = imageGradient.getColorAtProgress(colorSample);
					break;
				case COLOR_MODE_BLACK_ON_WHITE:
					curColor = p.color(0);
					break;
				case COLOR_MODE_WHITE_ON_BLACK:
				case COLOR_MODE_WHITE_ON_BLACK_FADE:
				case COLOR_MODE_TRIG:
					curColor = p.color(255);
					break;
				default: break;
			}
		}
		
		public void die() {
			if(particles.contains(this)) {
				particles.remove(this);
				deadParticles.add(this);
			}
		}
		
		public void update() {
			// turn?
			if(turnMode == TURN_MODE_NOISE) {
				float noiseAdd = -0.5f + p.noise(p.frameCount * lifeSpan * 0.01f, gen);
				turnAmp += noiseAdd * 0.005f;
				DebugView.setValue("turnAmp", turnAmp);
			}
			direction = direction + turnAmp;
			
			// move/age
			float lastX = position.x;
			float lastY = position.y;
			position.add(P.cos(direction) * speed, P.sin(direction) * speed);
			age++;

			// branch
			if(age == branchAge) {
				newBranch();
			}

			// hit test on existing lines
			float nextX = position.x + P.cos(direction) * speed * 2f;
			float nextY = position.y + P.sin(direction) * speed * 2f;
			int canvasColor = ImageUtil.getPixelColor(pg, (int) nextX, (int) nextY);
			
			// draw
			if(colorMode == COLOR_MODE_TRIG) updateColorTrig();
			pg.stroke(curColor);
			pg.line(lastX, lastY, position.x, position.y);

			// check for EOL
			if (age == lifeSpan) {
				newBranch();
				die();
			} else {
				if(canvasColor != bgColor && age > strokeSize * 1.5f) {	// make sure pixel detection is far enough from starting location before hit test kills
					pg.line(position.x, position.y, nextX, nextY);
					die();
				} else if (position.x < 0 || position.x > pg.width || position.y < 0 || position.y > pg.height) {
					die();
				} else if(canvasColor != bgColor && gen > 1 && age > strokeSize * 1.005f) {
					// further generations don't need the same initial distance check for collisions. Let's not let them go past each other
					die();
				}
			}
		}
		
		protected void newBranch() {
			// limit branch numbers
			if(gen > maxDescendants) return;

			// calculate a new direction for child branch
			float dirAdd = MathUtil.randRangeDecimal(0.2f, 0.99f);
			if(gen > 3) dirAdd = MathUtil.randRangeDecimal(0.9f, P.HALF_PI * 0.8f );
			float angleOptions = 6;
			// if(MathUtil.randBoolean()) 
				angleOptions = numInitialParticles;
			dirAdd = P.PI/angleOptions * MathUtil.randRange(1, angleOptions - 1); 			// keep within 0-PI, but exclude 0 and PI
			// dirAdd = P.PI/angleOptions * MathUtil.randRange(1, P.floor(angleOptions/2)); 	// optionally, don't allow acute angles backward
			if(MathUtil.randBoolean()) dirAdd *= -1f;										// randomly reverse
//			if(MathUtil.randBoolean()) dirAdd = 0;											// keep same direction
			
			// branch off a new particle with new properties 
			BranchingParticle newParticle = getParticle();
			newParticle.reset(position.x, position.y, direction + dirAdd, gen);
			particles.add(newParticle);
			
			// debug text
//			pg.fill(0);
//			pg.text(gen+"-", position.x + 10, position.y + 0);
			
			// set another random time for the next branch
			branchAge = age + MathUtil.randRange(30, 70);
		}
		
		protected void updateColorTrig() {
			curColor = p.color(
				P.sin(age * 0.02f) *  100f + 155f,
				P.sin(age * 0.025f) * 100f + 155f,
				P.sin(age * 0.03f) *  100f + 155f
			);
		}
		
	}
}