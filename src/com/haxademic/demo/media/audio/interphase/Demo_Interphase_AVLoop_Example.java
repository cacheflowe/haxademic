package com.haxademic.demo.media.audio.interphase;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.particle.Particle;
import com.haxademic.core.draw.particle.ParticleSystem;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.dmx.artnet.LedMatrix48x12;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.media.audio.interphase.draw.SequencerTexture;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Example
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MidiDevice knobs;
	protected Interphase interphase;
	protected int numSequencers;
	protected LinearFloat[] sequencerHits;
	protected FloatBuffer[] sequencerAmps;
	protected ArrayList<DMXFixture> fixture;
	protected LedMatrix48x12 ledMatrix;

	protected InterphaseVizDemo interphaseVizDemo;
		
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setPgSize(1024, 2048);
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
		initInterphase();		
		numSequencers = interphase.sequencers().length;
		initEasingValues();
		initDMX();
		initSequencerDrawables();		
		interphaseVizDemo = new InterphaseVizDemo2();
		ledMatrix = new LedMatrix48x12();
		P.store.addListener(this);
	}
	
	protected void initInterphase() {
		// init device for UI knobs MIDI input
		knobs = new MidiDevice(LaunchControlXL.deviceName, null);
		// init interphase + config 
		SequencerConfig.setAbsolutePath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsAlt());
		interphase.initUI();
		interphase.initLaunchControls(LaunchControlXL.BUTTONS_1, LaunchControlXL.BUTTONS_2, LaunchControlXL.KNOBS_ROW_1, LaunchControlXL.SLIDERS, LaunchControlXL.KNOBS_ROW_2, LaunchControlXL.KNOBS_ROW_3);
		interphase.initLaunchpads(2, 5, 4, 7);
		interphase.initAudioAnalysisPerChannel();
		// for UI controls debugging
		// P.out("WebServer.DEBUG", WebServer.DEBUG);
		// HttpInputState.DEBUG = false;
	}
	
	protected void initEasingValues() {
		sequencerHits = new LinearFloat[numSequencers];
		sequencerAmps = new FloatBuffer[numSequencers];
		for (int i = 0; i < sequencerHits.length; i++) {
			sequencerHits[i] = new LinearFloat(0, 0.05f);
			sequencerAmps[i] = new FloatBuffer(6);
		}
	}

	protected void initDMX() {
		DMXUniverse.instanceInit("COM8", 9600);
		fixture = new ArrayList<DMXFixture>();
		for (int i = 0; i < numSequencers; i++) {
			fixture.add((new DMXFixture(1 + i * 3)).setEaseFactor(0.25f));
		}
	}

	protected void initSequencerDrawables() {
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.setDrawable(new SequencerTexture(i));
		}
	}

	protected void drawApp() {
		interphase.update();
		drawVisuals();
		// drawSequencerDrawablesToScreen();
		updateLightsDMX();
		ledMatrix.update(pg);
	}

	protected void updateEasingValues() {
		for (int i = 0; i < sequencerHits.length; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			sequencerHits[i].update();
			sequencerAmps[i].update(seq.audioAmp());
		}
	}

	protected void drawVisuals() {
		// set draw context
		p.background(30);
		p.noStroke();
		PG.setDrawCorner(p);
		
		interphaseVizDemo.update(pg);

		// draw to screen
		// PG.setCenterScreen(p.g);
		// PG.setDrawCenter(p.g);
		// p.image(pg, 0, 0);
		ImageUtil.cropFillCopyImage(pg, p.g, true);
	}
	
	protected void drawSequencerDrawablesToScreen() {
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			SequencerTexture drawable = (SequencerTexture) seq.getDrawable();
			int w = p.width / numSequencers;
			int x = w * i;
			ImageUtil.cropFillCopyImage(drawable.buffer(), p.g, x, 0, w, p.height, true);
			;
		}
	}

	protected void updateLightsDMX() {
		for (int i = 0; i < numSequencers; i++) {
			// dmx colors from amp scale
			// use the oldest value in the buffer, because the FFT values are a little ahead of the sound
			// this would likely need adjustment on different machines
			int lightColor = p.color(
				sequencerAmps[i].oldestValue() * (127 + 127f * P.sin(i+0)),
				sequencerAmps[i].oldestValue() * (127 + 127f * P.sin(i+1)),
				sequencerAmps[i].oldestValue() * (127 + 127f * P.sin(i+2))
			);
			fixture.get(i)
				.color().setTargetInt(lightColor)
				.setEaseFactor(0.75f);
		}
	}
	
	
	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
//		if(key.equals(Interphase.BEAT)) {
		if(key.equals(Interphase.CUR_STEP)) {}
		if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) { // uses delay to visually appear on-beat
			sequencerHits[val.intValue()].setCurrent(1).setTarget(0);
		}
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) {
			if(val.equals("b")) SystemUtil.openWebPage("http://localhost:8080/ui");
			if(p.key == '6') AudioUtil.buildRecorder(Metronome.ac, 1500);
			if(p.key == '7') AudioUtil.finishRecording();
		}

	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}


	/////////////////////////////////////////////////////////////////
	// Custom Draw class
	/////////////////////////////////////////////////////////////////

	public class InterphaseVizDemo {

		public InterphaseVizDemo() {
		}

		public void update(PGraphics pg) {
			// draw results
			pg.beginDraw();
			pg.background(0);
			PG.setDrawCenter(pg);

			// draw background square with overall progress as rotation
			DebugView.setValue("Metronome.loopProgress()", Metronome.loopProgress());
			pg.push();
			PG.setCenterScreen(pg);
			pg.fill(80);
			pg.rotate(Metronome.loopProgress() * P.TWO_PI);
			pg.rect(0, 0, pg.width * 0.3f, pg.width * 0.3f);
			pg.pop();

			// draw circle per sequencer
			for (int i = 0; i < numSequencers; i++) {
				////////////////////////////////////
				// draw circles to screen
				float spacing = pg.width / 2f / numSequencers;
				float totalW = spacing * numSequencers;
				float x = pg.width / 2 - totalW / 2 + spacing * i;
				float y = pg.height / 2 - totalW / 2 + spacing * i;

				// sequence hits via LinearFloat objects
				float circleSize = pg.width * 0.05f;
				circleSize *= (1f + sequencerHits[i].value());
				pg.fill(ColorsHax.COLOR_GROUPS[6][i % 4]);
				pg.ellipse(x, y, circleSize, circleSize);

				// amp scale
				circleSize = pg.width * 0.05f;
				circleSize *= 1f + sequencerAmps[i].average();
				pg.ellipse(x, y + 150, circleSize, circleSize);

			}

			pg.endDraw();

			// postprocessing
			BloomFilter.instance().setStrength(9f);
			BloomFilter.instance().setBlurIterations(12);
			BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
			BloomFilter.instance().applyTo(pg);

			GrainFilter.instance().setTime(p.frameCount * 0.01f);
			GrainFilter.instance().setCrossfade(0.11f);
			GrainFilter.instance().applyTo(pg);
		}

	}

	@SuppressWarnings("rawtypes")
	public class InterphaseVizDemo2 
	extends InterphaseVizDemo
	implements IAppStoreListener {

		protected ParticleSystem particles;

		public InterphaseVizDemo2() {
			particles = new ParticleSystem(ParticleCustomPolygons.class);
			P.store.addListener(this);
		}

		public void update(PGraphics pg) {
			// draw results
			pg.beginDraw();
			pg.background(0);
			PG.setDrawCenter(pg);
			PG.setCenterScreen(pg);
			particles.updateAndDrawParticles(pg, BLEND);
			pg.endDraw();

			// postprocessing
			BloomFilter.instance().setStrength(5f);
			BloomFilter.instance().setBlurIterations(5);
			BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
			// BloomFilter.instance().applyTo(pg);

			GrainFilter.instance().setTime(p.frameCount * 0.01f);
			GrainFilter.instance().setCrossfade(0.11f);
			// GrainFilter.instance().applyTo(pg);
		}

		protected ParticleCustomPolygons launchParticleType(ParticleCustomPolygons.ParticleType type, int lifespan, int partiColor) {
			// reasonable particle defaults, overridden by type of Interphase channel
			ParticleCustomPolygons particle = (ParticleCustomPolygons) particles.launchParticle(0, 0, 0);
			particle
					.setType(type)
					.setSpeed(0, 0, -0.1f) // z-speed ensures proper z-stacking
					.setAcceleration(1, 1, 1)
					.setLifespan(lifespan)
					.setLifespanSustain(0)
					.setRotation(0, 0, 0, 0, 0, 0)
					.setColor(partiColor);
			return particle;
		}

		protected void triggerParticles(int index) {
			int partiColor = ColorsHax.colorFromGroupAt(9, index);
			if(index == 0) {
				launchParticleType(ParticleCustomPolygons.ParticleType.KICK, 30, partiColor);
			} else if(index == 1) {
				float rotInit = P.PI / 3f / 2f;
				launchParticleType(ParticleCustomPolygons.ParticleType.SNARE, 25, partiColor)
						.setGravity(0, 0.0f, 0)
						.setRotation(0, 0, rotInit, 0, 0, 0);
				// launchParticleType(ParticleCustom.ParticleType.SNARE, 25, partiColor)
				// 		.setGravity(0, 0.3f, 0)
				// 		.setRotation(0, 0, rotInit + P.PI, 0, 0, 0);
			} else if(index == 2) {
				launchParticleType(ParticleCustomPolygons.ParticleType.HAT, 20, partiColor)
						.setSpeed(0, -2, -0.1f) // z-speed ensures proper z-stacking
						.setGravity(0, 0.2f, 0)
						.setRotation(0, 0, 0, 0, 0, 0);
			} else if(index == 3) {
				float numParticles = 32;
				float speedAmp = 40;
				float decel = 0.95f;
				float segmentRads = P.TWO_PI / numParticles;
				for (int i = 0; i < numParticles; i++) {
					float curRads = segmentRads * i;
					float speedX = speedAmp * P.cos(curRads);
					float speedY = speedAmp * P.sin(curRads);
					launchParticleType(ParticleCustomPolygons.ParticleType.PERC, 40, partiColor)
							.setSpeed(speedX, speedY, -0.1f) // z-speed ensures proper z-stacking
							.setAcceleration(decel, decel, 1)
							.setRotation(0, 0, curRads - P.PI + segmentRads, 0, 0, 0);
				}
			}
		}

		/////////////////////////////////////////////////////////////////
		// IAppStoreListener
		/////////////////////////////////////////////////////////////////
		
		public void updatedNumber(String key, Number val) {
			if (key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
				triggerParticles(val.intValue());
			}
		}
		public void updatedString(String key, String val) {}
		public void updatedBoolean(String key, Boolean val) {}
		public void updatedImage(String key, PImage val) {}
		public void updatedBuffer(String key, PGraphics val) {}
	}



	/////////////////////////////////////////////////////////////////
	// Custom Particle
	/////////////////////////////////////////////////////////////////

	public static class ParticleCustomPolygons<T>
	extends Particle {

		public static enum ParticleType {
			KICK,
			SNARE,
			HAT,
			PERC,
			SFX,
			BASS,
			KEYS,
			LEAD,
		}
		protected ParticleType type;

		public ParticleCustomPolygons() {
			super();
		}

		protected ParticleCustomPolygons setType(ParticleType type) {
			this.type = type;
			return this;
		}

		protected void drawParticle(PGraphics pg) {
			// common props
			float minDim = P.min(pg.width, pg.height);
			float progress = Penner.easeOutQuad(this.ageProgress());
			float progressAlpha = Penner.easeInQuad(this.ageProgress());

			// draw different types of shapes
			if (type == ParticleType.KICK) {
				pg.fill(color, (255 - 255 * progressAlpha));
				float partiSize = P.map(progress, 0, 1, minDim * 0.1f, minDim * 0.65f);
				float thickness = minDim * 0.05f;
				Shapes.drawDisc(pg, partiSize, partiSize - thickness, 6);
			} else if (type == ParticleType.SNARE) {
				pg.fill(color, (255 - 255 * progressAlpha));
				float partiSize = P.map(progress, 0, 1, minDim * 0.15f, minDim * 0.6f);
				float thickness = minDim * 0.1f;
				Shapes.drawDisc(pg, partiSize, partiSize - thickness, 30);
				// Shapes.drawPolygon(pg, P.map(progress, 0, 1, minDim * 0.5f, minDim * 0.3f), 3);
			} else if (type == ParticleType.HAT) {
				pg.fill(color, (255 - 255 * progressAlpha));
				float partiSize = P.map(progress, 0, 1, minDim * 0.1f, minDim * 0.3f);
				float thickness = minDim * 0.1f;
				Shapes.drawDisc(pg, partiSize, partiSize - thickness, 4);
			} else if (type == ParticleType.PERC) {
				pg.fill(color);
				float partiSize = P.map(progress, 0, 1, minDim * 0.05f, 0);
				Shapes.drawPolygon(pg, partiSize, 3);
			}
		}

	}

}
