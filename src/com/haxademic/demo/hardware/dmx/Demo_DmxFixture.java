package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;

public class Demo_DmxFixture
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXFixture fixture;
	
	public void setupFirstFrame() {
		// use most basic singleton instance version of DMXUniverse
		DMXUniverse.instanceInit("COM3", 9600);
		fixture = (new DMXFixture(1)).setEaseFactor(0.1f);
	}

	public void drawApp() {
		fixture.color().setTargetRGBANormalized(p.mousePercentX(), p.mousePercentY(), p.mousePercentX(), 1);
		background(fixture.color().colorInt());
	}
}
