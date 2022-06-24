package com.haxademic.demo.media.audio.vst;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Sequencer;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.synth.SynthDolphin;
import com.haxademic.core.ui.UI;

import beads.AudioContext;
import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VST_Interphase
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	
	protected VSTPlugin vstSynth;
	
	// break loop!
	protected String UI_BREAK_DIVIDER = "UI_BREAK_DIVIDER";
//	protected String beat1 = "data/audio/breakbeats/dnb_loop006.wav";
	protected String beat1 = "data/audio/breakbeats/fish-loop.wav";
	protected WavPlayer player;

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 780 );
		Config.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
	}
	
	protected void firstFrame() {
		SequencerConfig.setAbsolutePath();
		interphase = new Interphase(SequencerConfig.interphaseChannelsAlt());
		interphase.initUI();
		interphase.initLaunchpads(6, 9, 8, 11);
		MidiDevice.init(3, 6);
		interphase.initGlobalControlsUI(LaunchControlXL.KNOBS_ROW_1, LaunchControlXL.KNOBS_ROW_2, LaunchControlXL.KNOBS_ROW_3);
//		interphase.initGlobalControlsUI();
//		interphase = new Interphase(SequencerConfig.interphaseChannelsMinimal(), true);
		UI.launchWebUIWindow();
		P.store.addListener(this);
		
		// load VSTs
		vstSynth = new SynthDolphin(true, true);
		
		// beat loop
		AudioContext acInterphase = Metronome.ac;
		player = new WavPlayer(acInterphase);
		UI.addSlider(UI_BREAK_DIVIDER, 4, 1, 16, 1, false);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		interphase.update(p.g);
		
		// keep loop synced
		float bpm = P.store.getNumber(Interphase.BPM).floatValue();
		Metronome.shiftPitchToMatchBpm(player, beat1, bpm, UI.valueInt(UI_BREAK_DIVIDER));
		
		// draw sound?
//		player.drawWav(p.g, beat1);
		
		// add reverb
		for (int i = 0; i < interphase.numChannels(); i++) {
			Sequencer seq = interphase.sequencerAt(i);
			seq.reverb(10.0f, 0.75f);
			if(i == 0) seq.reverb(5f, 0.9f);
		}
	}
	

	protected void triggerLoop(float progress) {
		// set loop to global bpm
		float bpm = P.store.getNumber(Interphase.BPM).floatValue();
		// chop it up on the beat!
		player.stop(beat1);
		player.playWav(beat1);
		if(progress < 0) {
			player.seekToProgress(beat1, MathUtil.randRange(0, 3) * 0.25f);	
		} else {
			player.seekToProgress(beat1, progress);	
		}
		Metronome.shiftPitchToMatchBpm(player, beat1, bpm, UI.valueInt(UI_BREAK_DIVIDER));
	}
	
	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) {
//			triggerLoop(val.intValue());
//			vstSynth.playRandomNote(100);
		}
		if(key.equals(Interphase.CUR_STEP)) {
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER_VISUAL)) {
			int sequencerIndex = val.intValue();
			// vst bass mirroring
			int bassChannelIndex = 5;
			if(sequencerIndex == bassChannelIndex) {
				if(P.store.getInt(Interphase.CUR_STEP) <= 1) vstSynth.randomizeAllParams();
				int curSequencerNote = interphase.sequencerAt(bassChannelIndex).pitchIndex1();
//				vstSynth.playRandomNote(200);
				vstSynth.playMidiNote(36 + curSequencerNote, 100);
			}
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			// breakbeat chop on the kick!
			int sequencerIndex = val.intValue();
			if(sequencerIndex == 0) triggerLoop(0); 
			if(sequencerIndex == 1) triggerLoop(0.25f); 
			if(sequencerIndex == 2) triggerLoop(0.25f * 1.5f); 
		}
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) {
			if(p.key == 'm') vstSynth.randomizeAllParams();
//			if(p.key == '6') AudioUtil.buildRecorder(Metronome.ac, 1500);
//			if(p.key == '7') AudioUtil.finishRecording();
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

	
}
