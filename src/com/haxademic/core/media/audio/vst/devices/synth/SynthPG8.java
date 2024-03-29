package com.haxademic.core.media.audio.vst.devices.synth;

import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.vst.VSTPlugin;

public class SynthPG8
extends VSTPlugin {

	public static String pluginPath = "vst/synth/PG-8X.dll";
	
	public SynthPG8() {
		super(pluginPath);
	}
	
	public SynthPG8(boolean openVstUI, boolean buildUI, boolean startsAudioThread) {
		super(pluginPath, openVstUI, buildUI, startsAudioThread);
	}

	public void randomizeAllParams() {
		super.randomizeAllParams(new int[]{ 55, 58, 59, 68, 69 });
		// randomize some params that should stay within a specific range
		vst.setParameter(14, 1);	// MIX DCO1 ()
		vst.setParameter(15, 1);	// MIX DCO2 ()
		vst.setParameter(27, 1);	// VCA LEVEL ()
		vst.setParameter(35, 1);	// VCA LEVEL ()
		vst.setParameter(40, MathUtil.randRangeDecimal(0, 0.2f));	// ENV1 ATT  ()
		vst.setParameter(47, MathUtil.randRangeDecimal(0, 0.2f));	// ENV2 ATT  ()
		// write to vst
		syncUIToVstUI();
	}
	
}
