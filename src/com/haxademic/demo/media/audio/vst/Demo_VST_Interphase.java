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
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.SequencerConfig;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.synth.SynthCharlatan;
import com.haxademic.core.media.audio.vst.devices.synth.SynthYoozBL303;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VST_Interphase
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Interphase interphase;
	protected VSTPlugin vstSynth;

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
	}
	
	protected void drawApp() {
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		interphase.update(p.g);
	}
	

	/////////////////////////////////////////////////////////////////
	// IAppStoreListener
	/////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) {
//			vstSynth.playRandomNote(100);
		}
		if(key.equals(Interphase.CUR_STEP)) {
		}
		if(key.equals(Interphase.SEQUENCER_TRIGGER)) {
			int sequencerIndex = val.intValue();
			int bassChannelIndex = 5;
			if(sequencerIndex == bassChannelIndex) {
				if(P.store.getInt(Interphase.CUR_STEP) <= 1) vstSynth.randomizeAllParams();
				int curSequencerNote = interphase.sequencerAt(bassChannelIndex).pitchIndex1();
//				vstSynth.playRandomNote(200);
				vstSynth.playMidiNote(24 + curSequencerNote, 300);
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
