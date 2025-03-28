package com.haxademic.demo.media.audio.vst;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.interphase.Interphase;
import com.haxademic.core.media.audio.interphase.Metronome;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.synth.SynthRagnarok;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_VSTSynth 
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Metronome metronome;
	
	protected VSTPlugin vstSynth;
	protected VSTPlugin vstSynth2;
	
	protected LinearFloat trigger = new LinearFloat(0, 0.03f);

	protected void firstFrame() {
		AudioUtil.setPrimaryMixer();
		
		metronome = new Metronome(true);
		P.store.setNumber(Interphase.BPM, 96);
		P.store.addListener(this);
		UI.addSlider(Interphase.BPM, P.store.getInt(Interphase.BPM), 20, 240, 1, false);
		
		// these are happy with their window being opened
//	    vstSynth = new VSTPlugin("vst/synth/Zebra2(x64).dll");
//	    vstSynth = new VSTPlugin("vst/synth/PG-8X.dll");
		// vstSynth = new VSTPlugin("vst/synth/ragnarok64.dll");
//		String vstFile = FileUtil.getPath("vst/synth/PG-8X.dll");
		// vstSynth = new SynthPG8(true, true, true);
//		vstSynth = new SynthCharlatan(true, true, true);
		// vstSynth = new SynthYoozBL303(true, true, true);
		vstSynth = new SynthRagnarok(true, true, true);
		// vstSynth2 = new VSTPlugin("vst/synth/Synsonic_BD-909.dll");
		// vstSynth2 = new VSTPlugin("vst/synth/kern64.dll");
		// vstSynth2 = new VSTPlugin("vst/synth/ragnarok64.dll");
		
		
		// these ones don't like their window opened, or at least opened automatically:
//		String vstFile = FileUtil.getPath("vst/synth/JuceOPLVSTi_ax64.dll");
//		String vstFile = FileUtil.getPath("vst/synth/synister64.dll");
	}

	protected void drawApp() {
		trigger.update();
		background(30 * trigger.value());
		// if(p.frameCount == 100) vstSynth.toggleVstUI();
		// play notes
//		if(FrameLoop.frameMod(60) == 30) vstSynth.playRandomNote(400);
//		if(FrameLoop.frameMod(60) == 1) vstSynth2.playRandomNote(400);
//		if(FrameLoop.frameMod(60) == 1) vstSynth.playMidiNote(48, 400);

		// extract & draw waveform
		// TODO: CAN'T DO THIS HERE! 
		// it messes up the output. we need to grab from the audio thread?
//		float[][] waveform = vstSynth.updateWaveform();
//		drawWaveform(waveform);
	}
	
	protected void trigger() {
	}

	protected void drawWaveform(float[][] waveform) {
		p.fill(0);
		p.stroke(255);
		for (int i = 0; i < P.min(waveform[0].length, p.width); i++) {
			p.point(i, p.height / 2 + waveform[0][i] * 300f);
		}
		for (int i = 0; i < P.min(waveform[1].length, p.width); i++) {
			p.point(i, p.height / 2 + waveform[1][i] * 300f);
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') vstSynth.randomizeAllParams();
		if(p.key == 'v') vstSynth.toggleVstUI();
		if(p.key == 'V' && vstSynth2 != null) vstSynth2.toggleVstUI();
		if(p.key == 'm' && vstSynth2 != null) vstSynth2.randomizeAllParams();
		if(p.key == 'p' && vstSynth != null) {
			vstSynth.playRandomNote(300);
			trigger.setTarget(0).setCurrent(1);
		}
	}

		@Override
		public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.CUR_STEP)) {
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
					trigger.setTarget(0).setCurrent(1);
					// play double synth
					vstSynth.playMidiNote(curNote - 12, MathUtil.randRange(150, 200));
				}
			}
		}
		}
		public void updatedString(String key, String val) {}
		public void updatedBoolean(String key, Boolean val) {}
		public void updatedImage(String key, PImage val) {}
		public void updatedBuffer(String key, PGraphics val) {}

}
