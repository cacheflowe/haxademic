package com.haxademic.demo.media.audio.vst;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.audio.vst.VSTPlugin;
import com.haxademic.core.media.audio.vst.devices.synth.SynthYoozBL303;
import com.haxademic.core.render.FrameLoop;

public class Demo_VSTSynth 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VSTPlugin vstSynth;
	protected VSTPlugin vstSynth2;

	protected void firstFrame() {
		// these are happy with their window being opened
//		String vstFile = FileUtil.getPath("vst/synth/Zebra2(x64).dll");
//		String vstFile = FileUtil.getPath("vst/synth/PG-8X.dll");
//		vstSynth = new SynthCharlatan();
		vstSynth = new SynthYoozBL303();
//		vstSynth2 = new VSTPlugin("vst/synth/PG-8X.dll");
		
		// these ones don't like their window opened, or at least opened automatically:
//		String vstFile = FileUtil.getPath("vst/synth/JuceOPLVSTi_ax64.dll");
//		String vstFile = FileUtil.getPath("vst/synth/synister64.dll");

	}

	protected void drawApp() {
		background(0);
		// play notes
		if(FrameLoop.frameMod(13) == 1) {
			vstSynth.playRandomNote(120);
//			vstSynth.playMidiNote(48, 300);
		}
//		if(FrameLoop.frameMod(60) == 30) vstSynth.playRandomNote(400);
//		if(FrameLoop.frameMod(60) == 1) vstSynth2.playRandomNote(400);
//		if(FrameLoop.frameMod(60) == 1) vstSynth.playMidiNote(48, 400);

		// extract & draw waveform
		// TODO: CAN'T DO THIS HERE! 
		// it messes up the output. we need to grab from the audio thread?
//		float[][] waveform = vstSynth.updateWaveform();
//		drawWaveform(waveform);

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
	}

}
