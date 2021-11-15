 package com.haxademic.core.hardware.dmx.fixtures;

import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;

public class DMXFixtureBsyun
extends DMXFixture {

	public DMXFixtureBsyun(int dmxChannel) {
		this(DMXUniverse.instance(), dmxChannel, DMXMode.RGB);	// default mode requires calling `DMXUniverse.instanceInit()` on app init to use the singleton
	}
		
	public DMXFixtureBsyun(DMXUniverse universe, int dmxChannel, DMXMode mode) {
		super(universe, dmxChannel, mode);
	}
	
	public void update() {
		color.update();
		
		// send dmx signals
		if(this.universe == null) return;
		
		// 7-channel DMX mode
		this.universe.setValue(dmxChannel + 0, 255);		// channel 1 = RGB brightness for channel 2-4
		this.universe.setValue(dmxChannel + 1, colorR());	// channel 2 = R
		this.universe.setValue(dmxChannel + 2, colorG());	// channel 3 = G
		this.universe.setValue(dmxChannel + 3, colorB());	// channel 4 = B
		this.universe.setValue(dmxChannel + 4, 0);			// channel 5 = [0-7] - no strobe, [8-255] - Ascending strobe amplitude
		this.universe.setValue(dmxChannel + 5, 0);			// channel 6 = [0-10] - manual control, [11-60] - Color combo, [61-110] - gradual fade, [161-210] - Color jump, [211-255] - sound activated mode 
		this.universe.setValue(dmxChannel + 6, 0);			// channel 7 = color combination (for automated modes)
	}
}
