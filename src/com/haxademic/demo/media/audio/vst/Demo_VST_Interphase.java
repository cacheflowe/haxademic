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
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.synth.SynthCharlatan;
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
	
	protected String beat1 = "data/audio/breakbeats/dnb_loop006.wav";
	protected WavPlayer player;

	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 780 );
		Config.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
	}
	
	protected void firstFrame() {
//		SequencerConfig.BASE_AUDIO_PATH = FileUtil.getHaxademicDataPath();
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
		vstSynth = new SynthCharlatan(true, true);
		
		// beat loop
		AudioContext acInterphase = Metronome.ac;
		player = new WavPlayer(acInterphase);
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		interphase.update(p.g);
		
		// keep loop synced
		float bpm = P.store.getNumber(Interphase.BPM).floatValue();
		Metronome.shiftPitchToMatchBpm(player, beat1, bpm, 4f);
	}
	

	protected void triggerLoop(int beat) {
		// set loop to global bpm
		float bpm = P.store.getNumber(Interphase.BPM).floatValue();
		// chop it up on the beat!
		player.stop(beat1);
		player.playWav(beat1);
		player.seekToProgress(beat1, MathUtil.randRange(0, 3) * 0.25f);
//		player.seekToProgress(beat1, 0);
		Metronome.shiftPitchToMatchBpm(player, beat1, bpm, 4f);
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
				vstSynth.playMidiNote(24 + curSequencerNote, 300);
			}
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			// breakbeat chop on the kick!
			int sequencerIndex = val.intValue();
			if(sequencerIndex == 0) {
				triggerLoop(-1);
			}
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
