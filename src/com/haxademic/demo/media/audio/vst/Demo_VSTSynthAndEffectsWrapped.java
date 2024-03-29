package com.haxademic.demo.media.audio.vst;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.effects.EffectDubStation;
import com.haxademic.core.media.audio.vst.devices.synth.SynthCharlatan;
import com.haxademic.core.media.audio.vst.devices.synth.SynthDolphin;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VSTSynthAndEffectsWrapped 
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Metronome metronome;
	
	protected VSTPlugin vstSynth;
	protected VSTPlugin vstAcid;
	protected VSTPlugin vstFX;
	
	protected LinearFloat trigger = new LinearFloat(0, 0.03f);

	protected WavPlayer player;

	protected void config() {
		Config.setAppSize(512, 512);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		player = new WavPlayer();

		// build metronome & bpm slider
		metronome = new Metronome(true);
		P.store.setNumber(Interphase.BPM, 96);
		P.store.addListener(this);
		UI.addSlider(Interphase.BPM, P.store.getInt(Interphase.BPM), 20, 240, 1, false);
		
		// build synth & fx
		vstAcid = new SynthDolphin(true, true, false);
		vstSynth = new SynthCharlatan(false, true, true);
		vstFX = new EffectDubStation(true, true, false);
		// vstFX = new VSTPlugin("vst/fx/DubStation_15.dll", true, true, false);

		// wait for async audio threads to be ready
		SystemUtil.setTimeout(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vstAcid.startAudioThreadWithFX(vstFX.vst());
			}
		}, 100);
	}

	protected void drawApp() {
		trigger.update();
		background(30 * trigger.value());
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') vstAcid.randomizeAllParams();
		if(p.key == 'o') vstFX.setRandomProgram();
		if(p.key == 'i') vstSynth.randomizeAllParams();
		if(p.key == 'v') vstAcid.toggleVstUI();
		if(p.key == 'p' && vstAcid != null) {
			vstAcid.playRandomNote(300);
			trigger.setTarget(0).setCurrent(1);
		}
	}

	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.CUR_STEP)) {
			if(Mouse.xNorm > 0.1f) {
				int curStep = val.intValue() % 8;
				if(curStep != -1) {
					int[] notes = new int[] {
						Scales.CUR_SCALE[0], 
						0, 
						Scales.CUR_SCALE[0], 
						Scales.CUR_SCALE[3], 
						Scales.CUR_SCALE[2], 
						0, 
						0, 
						Scales.CUR_SCALE[5]
					};
					int curNote = 36 + notes[curStep];
					if(curNote > 36) {
						if(MathUtil.randBooleanWeighted(0.2f)) curNote += 12;
						vstAcid.playMidiNote(curNote, MathUtil.randRange(150, 200));
						trigger.setTarget(0).setCurrent(1);
						// play double synth
						vstSynth.playMidiNote(curNote - 12, MathUtil.randRange(150, 200));
					}
				}
				if(curStep == 0) {
					player.playWav("data/audio/kit808/kick.wav");
				}
				if(curStep == 2 || curStep == 6) {
					player.playWav("data/audio/kit808/hi-hat.wav");
				}
				if(curStep == 4) {
					// player.playWav("data/audio/kit808/kick.wav");
					player.playWav("data/audio/kit808/snare.wav");
				}
			}
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
