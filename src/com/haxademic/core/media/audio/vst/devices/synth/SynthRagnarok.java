package com.haxademic.core.media.audio.vst.devices.synth;

import com.haxademic.core.app.P;
import com.haxademic.core.media.audio.vst.VSTPlugin;

public class SynthRagnarok
extends VSTPlugin {

	public static String pluginPath = "vst/synth/ragnarok64.dll";
	
	public SynthRagnarok() {
		super(pluginPath);
	}
	
	public SynthRagnarok(boolean openVstUI, boolean buildUI, boolean startsAudioThread) {
		super(pluginPath, openVstUI, buildUI, startsAudioThread);
	}

	public void randomizeAllParams() {
		super.randomizeAllParams();
		super.randomizeAllParams(new int[]{ 4, 5, 6, 7, 25, 28, 44, 45, 49,  });
		// randomize some params that should stay within a specific range
		vst.setParameter(4, 1);	// Volume
		vst.setParameter(5, P.p.random(0.4f, 0.6f));	// Micro Tune
		vst.setParameter(6, 0.5f);	// Detune
		vst.setParameter(7, 0.5f);	// Master Tune
		vst.setParameter(25, P.p.random(0, 0.3f)); // NoiseMix
		vst.setParameter(28, P.p.random(0.3f, 1)); // FltFreq
		vst.setParameter(44, P.p.random(0, 0.3f)); // Env Attack
		vst.setParameter(45, P.p.random(0, 0.4f)); // Env Delay
		vst.setParameter(49, 0.5f); // EQ 100
		vst.setParameter(50, 0.5f); // EQ 200
		vst.setParameter(51, 0.5f); // EQ 400
		vst.setParameter(52, 0.5f); // EQ 1K
		vst.setParameter(53, 0.5f); // EQ 2.5K
		vst.setParameter(54, 0.5f); // EQ 5K
		vst.setParameter(55, 0.5f); // EQ 12K
		// write to vst
		syncUIToVstUI();
	}
	
}
