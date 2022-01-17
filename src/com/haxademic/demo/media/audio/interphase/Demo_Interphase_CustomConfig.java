package com.haxademic.demo.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Interphase_CustomConfig
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected FloatBuffer[] sequencerAmps;
	protected LinearFloat[] sequencerTriggers;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 780 );
		Config.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
	}
	
	protected void firstFrame() {
		// load interphase
		SequencerConfig.BASE_AUDIO_PATH = "D:\\workspace\\interphase\\data\\";
		interphase = new Interphase(new SequencerConfig[] {
			new SequencerConfig(0, "audio/samples/01-kick", SequencerConfig.buildKickSnarePatterns(), 1f, false, false, false, false, false),
			new SequencerConfig(1, "audio/samples/02-snare", SequencerConfig.buildKickSnarePatterns(), 1f, false, false, false, false, false),
			new SequencerConfig(2, "audio/samples/03-hats", SequencerConfig.buildHatPatterns(), 1f, false, false, false, false, false),
			new SequencerConfig(3, "audio/samples/04-perc", SequencerConfig.buildHatPatterns(), 1f, false, false, false, false, false),
		});
		interphase.initUI();
		interphase.initLaunchpads(2, 5, 4, 7);
		interphase.initGlobalControlsUI();
		interphase.initAudioAnalysisPerChannel();
//		interphase.initAudioTexturesPerChannel();
		interphase.autoPlay();
		
		// build custom objects for tracking amplitude per sequencer
		sequencerAmps = new FloatBuffer[interphase.numChannels()];
		sequencerTriggers = new LinearFloat[interphase.numChannels()];
		for (int i = 0; i < sequencerAmps.length; i++) {
			sequencerAmps[i] = new FloatBuffer(3);
			sequencerTriggers[i] = new LinearFloat(0, 0.025f);
		}

		// set custom props
		UI.setValueToggle(Interphase.UI_GLOBAL_EVOLVES, false);
		
		// listen for events
		P.store.addListener(this);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCenter(p.g);

		// update the music engine
		interphase.update();
		
		// update audio effects
		for (int i = 0; i < sequencerAmps.length; i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.reverb(1.0f, 0.85f);
			if(i == 0) seq.reverb(0.01f, 0.85f);
			seq.attack(0).release(0);
		}
		
		// update audio amps per channel
		// and draw debug/test graphics
		for (int i = 0; i < sequencerAmps.length; i++) {
			// get FFT amplitude
			Sequencer seq = interphase.sequencerAt(i);
			sequencerAmps[i].update(seq.audioAmp());
			
			// draw debug
			float x = p.width / 2 - 150 + 100 * i;
			float y = p.height / 2;
			float size = 100 * sequencerAmps[i].average();
			p.ellipse(x, y - 100, size, size);
			
			// get beats
			sequencerTriggers[i].update();
			size = 100 * sequencerTriggers[i].value();
			p.ellipse(x, y + 100, size, size);
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
			sequencerTriggers[val.intValue()].setCurrent(1).setTarget(0);
		}
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) {
			if(p.key == '6') AudioUtil.buildRecorder(Metronome.ac, 1500);
			if(p.key == '7') AudioUtil.finishRecording();
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

	
}
