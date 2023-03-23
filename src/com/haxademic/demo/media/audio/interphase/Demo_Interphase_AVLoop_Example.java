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
	
	protected String USE_OVERRIDES = "USE_OVERRIDES";
	
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
		
		UI.addToggle(USE_OVERRIDES, false, false);
	}
	
	protected void drawApp() {
		updateMusic();
		drawVisuals();
	}

	protected void drawVisuals() {
		// update easings
		for (int i = 0; i < sequencerHits.length; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			// update hits
			sequencerHits[i].update();
			// update amp
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
			float spacing = pg.width / 2f / numSequencers;
			float totalW = spacing * numSequencers;
			float x = pg.width/2 - totalW/2 + spacing * i;
			float y = pg.height/2 - totalW/2 + spacing * i;
			
			// 
			float circleSize = pg.width * 0.05f;
			circleSize *= (1f + sequencerHits[i].value());
			pg.fill(ColorsHax.COLOR_GROUPS[6][i % 4]);
			pg.ellipse(x, y, circleSize, circleSize);
			
			// amp scale
			circleSize = pg.width * 0.05f;
			circleSize *= 1f + sequencerAmps[i].average();
			pg.ellipse(x, y + 150, circleSize, circleSize);
			
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
		
		pg.endDraw();
		
		// postprocessing
		BloomFilter.instance().setStrength(9f);
		BloomFilter.instance().setBlurIterations(12);
		BloomFilter.instance().setBlendMode(BloomFilter.BLEND_SCREEN);
		BloomFilter.instance().setOnContext(pg);
		
		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.11f);
		GrainFilter.instance().setOnContext(pg);

		// draw to screen
		p.image(pg, 0, 0);
	}
	
	protected void updateMusic() {
		// update music playback
		// set sequencer properties
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.notesByStep(true);
			DebugView.setValue("evolves_"+i, seq.evolves());
		}
		
		// update audio effects
//		for (int i = 0; i < sequencerAmps.length; i++) {
//			Sequencer seq = interphase.sequencerAt(i);
//			seq.reverb(1.5f, 1.5f);
//			if(i == 0) seq.reverb(0.01f, 0.9f);
//			seq.attack(0).release(0);
//		}
		
		if(UI.valueToggle(USE_OVERRIDES)) {
			// override current instruments sample selection by setting UI, then reading from UI
			UI.setValue("SAMPLE_1", 24);
			UI.setValue("SAMPLE_2", 50);
			UI.setValue("SAMPLE_3", 24);	// 7, 24, 27
			UI.setValue("SAMPLE_4", 28);
			UI.setValue("SAMPLE_5", 17);
			UI.setValue("SAMPLE_6", 44);
			UI.setValue("SAMPLE_7", 19);
			UI.setValue("SAMPLE_8", 6);
			
			// override sequences!
//			interphase.sequencerAt(0).setPatternByInts(new int[] {1,0,0,0,0,0,0,0,1,0,1,0,0,1,0,0}).noteOffset(0);
//			interphase.sequencerAt(1).setPatternByInts(new int[] {0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0}).noteOffset(0);
//			interphase.sequencerAt(2).setPatternByInts(new int[] {0,0,1,0,0,0,1,0,0,0,1,1,0,0,1,0}).noteOffset(0);
//			interphase.sequencerAt(3).setPatternByInts(new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1}).noteOffset(0);
//			interphase.sequencerAt(4).setPatternByInts(new int[] {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0}).noteOffset(0);
//			interphase.sequencerAt(5).setPatternByInts(new int[] {0,0,1,0,1,0,1,0,0,0,0,0,1,0,0,1}).noteOffset(8);
//			interphase.sequencerAt(6).setPatternByInts(new int[] {0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0}).noteOffset(4);
//			interphase.sequencerAt(7).setPatternByInts(new int[] {0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,0}).noteOffset(1);
		}
		
		// update Interphase object every frame
		interphase.update();
	}
	
	protected void outputConfig() {
		// export json for fun
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			P.out(JsonUtil.jsonToSingleLine(seq.json()));
		}
		
		// write out code for setting samples
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			P.out("UI.setValue(\"SAMPLE_" + (i+1) + "\", " + seq.sampleIndex() + ");");
		}
		
		// write out code for setting samples
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			P.out("interphase.sequencerAt("+i+").setPatternByInts(new int[] {" + seq.stepsListString() + "});");
		}
	}
	
	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
//		if(key.equals(Interphase.BEAT)) {
		if(key.equals(Interphase.CUR_STEP)) {
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
			// add delay - signals happen before audio is audible
			sequencerHits[val.intValue()].setCurrent(1).setTarget(0);
		}
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) {
			if(val.equals("o")) outputConfig();
			if(val.equals("b")) SystemUtil.openWebPage("http://localhost:8080/ui");
			if(p.key == '6') AudioUtil.buildRecorder(Metronome.ac, 1500);
			if(p.key == '7') AudioUtil.finishRecording();
		}

	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
