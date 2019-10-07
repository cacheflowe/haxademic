package com.haxademic.demo.media.audio.analysis;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.media.audio.analysis.AudioStreamData;
import com.haxademic.core.media.audio.playback.AudioPlayerMinim;

import ddf.minim.Minim;

public class Demo_AudioLooperWithAnalysisMinim
extends PAppletHax { public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Minim minim;
	protected AudioPlayerMinim[] loops;
	protected int[] knobs;
	protected boolean midiActive = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 900);
		p.appConfig.setProperty(AppSettings.HEIGHT, 600);
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}
	
	public void setupFirstFrame() {
		minim = new Minim(P.p);
	
		// oad samples
		loops = new AudioPlayerMinim[] {
				new AudioPlayerMinim(minim, "audio/crusher-loops/kicks.wav"),
				new AudioPlayerMinim(minim, "audio/crusher-loops/snares.wav"),
				new AudioPlayerMinim(minim, "audio/crusher-loops/bass-selekta.wav"),
				new AudioPlayerMinim(minim, "audio/crusher-loops/fnc-01.wav"),
				new AudioPlayerMinim(minim, "audio/crusher-loops/fx05.wav"),
				new AudioPlayerMinim(minim, "audio/crusher-loops/contender.wav"),
		};
		
		knobs = new int[] {
				LaunchControl.KNOB_01,
				LaunchControl.KNOB_02,
				LaunchControl.KNOB_03,
				LaunchControl.KNOB_04,
				LaunchControl.KNOB_05,
				LaunchControl.KNOB_06,
		};
		
		// enable knobs if MIDI input active 
		if(p.appConfig.getInt(AppSettings.MIDI_DEVICE_IN_INDEX, -1) == 0) midiActive = true;
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') relaunchAllLoops();
	}
	
	protected void relaunchAllLoops() {
		for(int i=0; i < loops.length; i++) {
			loops[i].start();
		}	
	}
	
	protected void mapMidiKnobsToVolume() {
		float vol = 1;
		for(int i=0; i < knobs.length; i++) {
			vol = p.midiState.midiCCPercent(knobs[i]);
			loops[i].setVolume(vol);
			loops[i].audioData().setGain(vol);
		}
	}
	
	protected void checkClipRelaunch() {
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_01)) loops[0].start();
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_02)) loops[1].start();
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_03)) loops[2].start();
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_04)) loops[3].start();
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_05)) loops[4].start();
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_06)) loops[5].start();
		
		if(p.midiState.isMidiButtonTriggered(LaunchControl.PAD_08)) relaunchAllLoops();
	}
	
	public void drawApp() {
		background(0);
		stroke(255);
		
		// set volume if needed
		if(midiActive == true) {
			mapMidiKnobsToVolume();
			checkClipRelaunch();
		}
		
		// update analysis
		for(int i=0; i < loops.length; i++) {
			loops[i].update();
			if(loops[i].looped()) P.println("LOOPED:", i);
		}
		
		// draw debug
		loops[0].audioData().drawDebug(p.g);
		p.translate(AudioStreamData.debugW, 0);
		loops[1].audioData().drawDebug(p.g);
		p.translate(AudioStreamData.debugW, 0);
		loops[2].audioData().drawDebug(p.g);
		p.translate(-AudioStreamData.debugW * 2, AudioStreamData.debugH);
		loops[3].audioData().drawDebug(p.g);
		p.translate(AudioStreamData.debugW, 0);
		loops[4].audioData().drawDebug(p.g);
		p.translate(AudioStreamData.debugW, 0);
		loops[5].audioData().drawDebug(p.g);
	}

}

