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
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.media.audio.interphase.SequencerTexture;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Example
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected int numSequencers;
	protected LinearFloat[] sequencerHits;
	protected FloatBuffer[] sequencerAmps;
	protected MidiDevice knobs;
	protected ArrayList<DMXFixture> fixture;
		
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
		// init device for UI knobs MIDI input
		knobs = new MidiDevice(LaunchControlXL.deviceName, null);
		
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		SequencerConfig.setAbsolutePath();
//		interphase = new Interphase(SequencerConfig.interphaseChannels());
		interphase = new Interphase(SequencerConfig.interphaseChannelsAlt());
		interphase.initUI();
		interphase.initGlobalControlsUI(LaunchControlXL.KNOBS_ROW_1, LaunchControlXL.SLIDERS, LaunchControlXL.KNOBS_ROW_2, LaunchControlXL.KNOBS_ROW_3);
//		interphase.initLaunchpads(4, 7, 8, 11);
		interphase.initLaunchpads(2, 5, 4, 7);
		interphase.initAudioAnalysisPerChannel();
		
//		interphase = new Interphase(SequencerConfig.interphaseChannelsMinimal(), hasUI, hasMidi);
		numSequencers = interphase.sequencers().length;

		// add DMX output
		DMXUniverse.instanceInit("COM8", 9600);
		fixture = new ArrayList<DMXFixture>(); 
		
		// add drawable sequencers
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.setDrawable(new SequencerTexture(i));
			fixture.add((new DMXFixture(1 + i * 3)).setEaseFactor(0.25f));
		}
		
		// create local easing objects for each track
		sequencerHits = new LinearFloat[numSequencers];
		sequencerAmps = new FloatBuffer[numSequencers];
		for (int i = 0; i < sequencerHits.length; i++) {
			sequencerHits[i] = new LinearFloat(0, 0.05f);
			sequencerAmps[i] = new FloatBuffer(6);
		}
		
		P.store.addListener(this);
		
		P.out("WebServer.DEBUG", WebServer.DEBUG);
		HttpInputState.DEBUG = false;
	}
	
	protected void drawApp() {
		interphase.update();
		drawVisuals();
		drawSequencerDrawablesToScreen();
		updateLights();
	}

	protected void drawVisuals() {
		// update easings for hits & amp 
		for (int i = 0; i < sequencerHits.length; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			sequencerHits[i].update();
			sequencerAmps[i].update(seq.audioAmp());
		}
		
		// set draw context
		p.background(30);
		p.noStroke();
		PG.setDrawCorner(p);
		
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
			float x = pg.width/2 - totalW/2 + spacing * i;
			float y = pg.height/2 - totalW/2 + spacing * i;
			
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

		// draw to screen
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.image(pg, 0, 0);
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

	protected void updateLights() {
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
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

}
