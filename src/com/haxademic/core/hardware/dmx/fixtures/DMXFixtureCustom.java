package com.haxademic.core.hardware.dmx.fixtures;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.math.easing.EasingFloat;

public class DMXFixtureCustom
	extends DMXFixture {

	public EasingFloat amber = new EasingFloat(0, DEFAULT_EASING);
	public EasingFloat uv = new EasingFloat(0, DEFAULT_EASING);
	public EasingFloat dimmer = new EasingFloat(0, DEFAULT_EASING);

	public DMXFixtureCustom(int dmxChannel) {
		super(DMXUniverse.instance(), dmxChannel, DMXMode.RGBW);
	}

	public void update() {
		super.update();

		// update colors
		amber.update();
		uv.update();
		dimmer.update();

		// send to DMX output
		universe.setValue(dmxChannel + 4, P.round(amber.target() * 255));
		universe.setValue(dmxChannel + 5, P.round(uv.target() * 255));
		universe.setValue(dmxChannel + 6, P.round(dimmer.target() * 255));
	}
}
