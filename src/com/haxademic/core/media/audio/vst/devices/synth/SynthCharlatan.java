package com.haxademic.core.media.audio.vst.devices.synth;

import com.haxademic.core.app.P;
import com.haxademic.core.media.audio.vst.VSTPlugin;

public class SynthCharlatan
extends VSTPlugin {

	public SynthCharlatan() {
		super("vst/synth/Charlatan.dll");
//		allowsWindowOpen = false;
	}
	
	public void randomizeAllParams() {
		super.randomizeAllParams(new int[]{ 5, 9, 10, 11, 23, 49 });
		// randomize some params that should stay within a specific range
		vst.setParameter(3, P.p.random(0.5f, 1));	// Osc1 Volume (dB}
		vst.setParameter(9, P.p.random(0.5f, 1));	// Osc2 Volume (dB}
		vst.setParameter(23, P.p.random(0.3f, 1));	// Filter Cutoff (Hz}
		vst.setParameter(35, P.p.random(0, 0.4f));	// Amp Attack (ms}
	}
	
}
