package com.haxademic.core.media.audio.vst.devices.synth;

import com.haxademic.core.media.audio.vst.VSTPlugin;

public class SynthYoozBL303
extends VSTPlugin {

	public SynthYoozBL303() {
		// NOTE! Use velocities < 127. The synth stops working if so.
		// From the docs: https://yoozmusic.com/yooz-bl-303/
		/*
		    Shorten the note length a little bit, so it ends slightly before the next note (like the TB).
		    Use the Velocity to control the Accent and the Glide.
		    Velocity 0 – 9: Slide On
		    Velocity 10 – 100: Regular note
		    Velocity 101 – 126: Accent On
		    Velocity 127: Accent & Slide On
		*/

		super("vst/synth/YoozBL303_x64.dll");
	}
	
//	public void randomizeAllParams() {
//		super.randomizeAllParams(new int[]{ 5, 9, 10, 11, 23, 49 });
//		// randomize some params that should stay within a specific range
//		vst.setParameter(3, P.p.random(0.5f, 1));	// Osc1 Volume (dB}
//		vst.setParameter(9, P.p.random(0.5f, 1));	// Osc2 Volume (dB}
//		vst.setParameter(23, P.p.random(0.3f, 1));	// Filter Cutoff (Hz}
//		vst.setParameter(35, P.p.random(0, 0.4f));	// Amp Attack (ms}
//	}
	
}
