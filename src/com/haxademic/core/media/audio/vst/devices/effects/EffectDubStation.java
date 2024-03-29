package com.haxademic.core.media.audio.vst.devices.effects;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.vst.VSTPlugin;

public class EffectDubStation
extends VSTPlugin {

	public static String pluginPath = "vst/fx/DubStation_15.dll";
	
	public EffectDubStation() {
		super(pluginPath);
	}
	
	public EffectDubStation(boolean openVstUI, boolean buildUI, boolean startsAudioThread) {
		super(pluginPath, openVstUI, buildUI, startsAudioThread);
	}

	public void randomizeAllParams() {
		super.randomizeAllParams();
		// randomize some params that should stay within a specific range
		vst.setParameter(1, MathUtil.randBoolean() ? 0 : 1);	// Mult (x2 Time)
		vst.setParameter(2, MathUtil.randBoolean() ? 0 : 1);	// Sync
		vst.setParameter(3, P.p.random(0.2f, 0.8f));							// Regen
		// vst.setParameter(4, MathUtil.randBoolean() ? 0 : 1); 			// Loop
		vst.setParameter(4, 0); 																	// Loop -p robably don't want to turn this on because it never stops
		vst.setParameter(5, MathUtil.randBoolean() ? 0 : 1); 			// Reverse
		vst.setParameter(8, P.p.random(0.2f, 0.5f));							// Mix
		vst.setParameter(9, 1f);							// Drive
		vst.setParameter(10, 1f);							// Output
		// write to vst
		syncUIToVstUI();
	}
	
}
