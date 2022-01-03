package com.haxademic.core.hardware.midi.devices;

import com.haxademic.core.app.P;

public class NovationColorsMK3 {

	public static final int[] colors = new int[] {
			0,
			5,  	// red
			6,
			45,		// purple
			49,
			38,		// aqua
			37,
			73,		// yellow
			74,
			123,
			122,	// green
	};
	
	public static int colorByPercent(float percent) {
		return colors[P.round((colors.length - 1) * P.constrain(percent, 0, 1))];
	}
}
