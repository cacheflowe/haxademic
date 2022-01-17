package com.haxademic.demo.media.audio.interphase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_AVLoop_Poster
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected int numSequencers;
	protected LinearFloat[] sequencerHits;
	
	protected String SAMPLE_ = "SAMPLE_";
	protected String GLOBAL_BPM = "GLOBAL_BPM";
	protected String GLOBAL_EVOLVES = "GLOBAL_EVOLVES";
	protected String CUR_SCALE = "CUR_SCALE";
	protected String USE_OVERRIDES = "USE_OVERRIDES";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1024 );
		Config.setProperty( AppSettings.HEIGHT, 1024 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_UI, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}
	
	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
		interphase = new Interphase(SequencerConfig.interphaseChannels());
		interphase.initUI().initLaunchpads(1, 4, 2, 5);
		numSequencers = interphase.sequencers().length;

		// add drawable sequencers
//		for (int i = 0; i < numSequencers; i++) {
//			Sequencer seq = interphase.sequencerAt(i);
//			seq.setDrawable(new SequencerTexture(i));
//		}
		// create local easing objects for each track
		sequencerHits = new LinearFloat[numSequencers];
		for (int i = 0; i < sequencerHits.length; i++) {
			sequencerHits[i] = new LinearFloat(0, 0.05f);
		}
		
		P.store.addListener(this);
		
		P.out("WebServer.DEBUG", WebServer.DEBUG);
		HttpInputState.DEBUG = false;
		
		// Interphase UI
		UI.addTitle("Interphase");
		UI.addSlider(GLOBAL_BPM, 105, 60, 170, 1, false);
		UI.addToggle(GLOBAL_EVOLVES, false, false);
		UI.addSlider(CUR_SCALE, 0, 0, Scales.SCALES.length-1, 1, false);
		UI.addToggle(USE_OVERRIDES, true, false);
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			UI.addSlider(SAMPLE_+(i+1), 0, 0, seq.numSamples() - 1, 1, false);
		}
	}
	
	protected void drawApp() {
		updateMusic();
		drawVisuals();
	}

	protected void drawVisuals() {
		// update easings
		for (int i = 0; i < sequencerHits.length; i++) sequencerHits[i].update();
		for (int i = 0; i < sequencerHits.length; i++) interphase.sequencerAt(0).update();
		
		// set draw context
		p.background(255);
		p.noStroke();
		PG.setDrawCorner(p);
		
		// draw results
		pg.beginDraw();
		pg.background(0);
		PG.setDrawCenter(pg);
		
		int[] colors = ColorsHax.COLOR_GROUPS[9];

		// draw background square with overall progress as rotation
		DebugView.setValue("Metronome.loopProgress()", Metronome.loopProgress());
		float rotOffset = 0.10f;
		pg.push();
		PG.setCenterScreen(pg);
		pg.fill(colors[3]);
		pg.rotate(Penner.easeInOutQuart(4f * ((Metronome.loopProgress() + rotOffset) % 0.25f)) * P.TWO_PI / 4f);	// rotate 1/4, 4x per loop... :-D 
		pg.rect(0, 0, pg.width * 0.3f, pg.width * 0.3f);
		pg.pop();
		
		// 
		for (int i = 0; i < numSequencers; i++) {
			float spacing = pg.width / 2f / numSequencers;
			float totalW = spacing * numSequencers;
			float x = pg.width/2 - totalW/2 + spacing * i;
			float y = pg.height/2 - totalW/2 + spacing * i;
			float circleSize = pg.width * 0.05f;
			circleSize *= (1f + sequencerHits[i].value());
			pg.fill(colors[i % 4]);
//			pg.ellipse(x, y, circleSize, circleSize);
		}
		
		// hat 
		pg.push();
		PG.setDrawCenter(pg);
		float hatSize = sizeH(0.0175f);
		hatSize *= (1f + Penner.easeInQuad(sequencerHits[2].value()));
		pg.fill(colors[4]);
		pg.rect(sizeW(0.5f + 0.2f), sizeH(0.15f), pg.width, hatSize);
		pg.pop();
		
		// kick 
		pg.push();
		PG.setDrawCenter(pg);
		float kickSize = sizeW(0.05f);
		kickSize *= (1f + Penner.easeInQuad(sequencerHits[0].value()));
		pg.fill(colors[0]);
		pg.rect(sizeW(0.2f), sizeH(0.5f), kickSize, pg.height);
		pg.pop();
		
		// snare
		pg.push();
		PG.setDrawCenter(pg);
		float snareSize = sizeW(0.1f);
		snareSize *= (1f + 0.25f + Penner.easeOutQuad(sequencerHits[1].value()));
		pg.fill(colors[1]);
		pg.ellipse(sizeW(0.5f), sizeH(0.5f), snareSize, snareSize);
		pg.pop();

		// bass
		pg.push();
		PG.setDrawCenter(pg);
		float bassPos = sizeW(0.0175f);
		pg.fill(colors[2]);
		pg.rect(sizeW(0.75f), sizeH(0.85f) - bassPos * sequencerHits[5].value(), sizeW(0.2f), sizeH(0.05f));
		pg.pop();
		
		// lead
		pg.push();
		PG.setDrawCorner(pg);
		float leadPos = sizeW(0.0275f);
		pg.fill(colors[3]);
//		pg.rect(sizeW(0.3f) + leadPos * sequencerHits[7].value(), sizeH(0.85f) - leadPos, sizeW(0.025f), sizeW(0.1f));
		pg.rect(sizeW(0.35f), sizeH(0.825f), sizeW(0.025f) + leadPos * sequencerHits[7].value(), sizeW(0.05f));
		pg.pop();
		
		// synth
		pg.push();
		PG.setDrawCorner(pg);
		float synthAdd = sizeW(0.05f) * sequencerHits[6].value();
		pg.fill(colors[0]);
		pg.triangle(sizeW(0.85f), sizeH(0.25f), sizeW(0.85f), sizeH(0.35f) + synthAdd, sizeW(0.75f) - synthAdd, sizeH(0.25f));
//		pg.rect(sizeW(0.8f), sizeH(0.4f), sizeW(0.025f), sizeW(0.1f));
		///  + leadPos * 
		pg.pop();

		
		pg.endDraw();
		
		// postprocessing
//		BloomFilter.instance(p).setStrength(9f);
//		BloomFilter.instance(p).setBlurIterations(12);
//		BloomFilter.instance(p).setBlendMode(BloomFilter.BLEND_SCREEN);
//		BloomFilter.instance(p).applyTo(pg);
		
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.09f);
		GrainFilter.instance(p).applyTo(pg);

		// draw to screen
		p.image(pg, 0, 0);
	}
	
	protected float sizeW(float norm) {
		return pg.width * norm;
	}
	
	protected float sizeH(float norm) {
		return pg.height * norm;
	}
	
	protected void updateMusic() {
		// update music playback
		// overall interphase props
		P.store.setNumber(Interphase.BPM, UI.value(GLOBAL_BPM));
		P.store.setBoolean(Interphase.GLOBAL_PATTERNS_EVLOVE, UI.valueToggle(GLOBAL_EVOLVES));
		P.store.setNumber(Interphase.CUR_SCALE_INDEX, UI.valueInt(CUR_SCALE));
		// set current instruments
		for (int i = 0; i < numSequencers; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.setSampleByIndex(UI.valueInt(SAMPLE_+(i+1)));
			seq.notesByStep(true);
		}
		
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
			interphase.sequencerAt(0).setPatternByInts(new int[] {1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0}).noteOffset(0);
			interphase.sequencerAt(1).setPatternByInts(new int[] {0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0}).noteOffset(0);
			interphase.sequencerAt(2).setPatternByInts(new int[] {0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0}).noteOffset(0);
			interphase.sequencerAt(3).setPatternByInts(new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1}).noteOffset(0);
			interphase.sequencerAt(4).setPatternByInts(new int[] {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0}).noteOffset(0);
			interphase.sequencerAt(5).setPatternByInts(new int[] {0,0,1,0,1,0,1,0,0,0,0,0,1,0,0,1}).noteOffset(8).attack(0).release(500);
			interphase.sequencerAt(6).setPatternByInts(new int[] {0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0}).noteOffset(4).attack(0).release(500);
			interphase.sequencerAt(7).setPatternByInts(new int[] {0,0,0,1,0,0,0,1,0,0,0,0,1,0,0,1}).noteOffset(1).attack(0).release(500);

			// alt!
//			UI.setValue("SAMPLE_1", 27);
//			UI.setValue("SAMPLE_2", 76);
//			UI.setValue("SAMPLE_3", 19);
//			UI.setValue("SAMPLE_4", 43);
//			UI.setValue("SAMPLE_5", 17);
//			UI.setValue("SAMPLE_6", 45);
//			UI.setValue("SAMPLE_7", 12);
//			UI.setValue("SAMPLE_8", 28);
//			
//			interphase.sequencerAt(0).setPatternByInts(new int[] {1,1,0,0,1,0,0,0,1,1,0,0,1,0,0,0}).noteOffset(0);
//			interphase.sequencerAt(1).setPatternByInts(new int[] {0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,1}).noteOffset(0);
//			interphase.sequencerAt(2).setPatternByInts(new int[] {0,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1}).noteOffset(0);
//			interphase.sequencerAt(3).setPatternByInts(new int[] {0,0,0,0,1,0,0,1,0,1,0,1,1,0,0,0}).noteOffset(0);
//			interphase.sequencerAt(4).setPatternByInts(new int[] {0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0}).noteOffset(0);
//			interphase.sequencerAt(5).setPatternByInts(new int[] {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1}).noteOffset(8).attack(0).release(500);
//			interphase.sequencerAt(6).setPatternByInts(new int[] {0,1,0,0,0,0,1,0,1,0,0,0,1,0,0,0}).noteOffset(4).attack(0).release(500);
//			interphase.sequencerAt(7).setPatternByInts(new int[] {0,0,0,1,0,0,0,1,0,0,0,1,0,1,0,1}).noteOffset(1).attack(0).release(500);

		}
		
		// update Interphase object every frame
		interphase.update(null);
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
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			// add delay - signals happen before audio is audible, so we delay slightly
			int vizTriggerDelay = 90;
			SystemUtil.setTimeout(new ActionListener() { public void actionPerformed(ActionEvent e) {
				int seqIndex = val.intValue();
				int curPitch = interphase.sequencerAt(seqIndex).pitchIndex1();
				if(seqIndex == 5 || seqIndex == 7) {
					sequencerHits[seqIndex].setTarget(curPitch);
					sequencerHits[seqIndex].setInc(0.75f);
				} else {
					sequencerHits[seqIndex].setInc(0.025f);
					sequencerHits[seqIndex].setCurrent(1).setTarget(0);
				}
			}}, vizTriggerDelay);
		}
	}
	public void updatedString(String key, String val) {
		if(key == PEvents.KEY_PRESSED && val.equals("b")) {
			SystemUtil.openWebPage("http://localhost:8080/ui");
		}
		if(key == PEvents.KEY_PRESSED && val.equals("o")) outputConfig(); 
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
