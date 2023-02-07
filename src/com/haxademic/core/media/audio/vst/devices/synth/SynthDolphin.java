package com.haxademic.core.media.audio.vst.devices.synth;

import com.haxademic.core.app.P;
import com.haxademic.core.media.audio.vst.VSTPlugin;

public class SynthDolphin
extends VSTPlugin {

	public static String pluginPath = "vst/synth/Dolphin-x64.dll";
	
	public SynthDolphin() {
		super(pluginPath);
	}
	
	public SynthDolphin(boolean openVstUI, boolean buildUI) {
		super(pluginPath, openVstUI, buildUI);
	}

	public void randomizeAllParams() {
		super.randomizeAllParams();
		// randomize some params that should stay within a specific range
		vst.setParameter(1, 0.5f);					// tune ()
		vst.setParameter(2, P.p.random(0.2f, 1));	// cutoff (%)
	}
	
}
