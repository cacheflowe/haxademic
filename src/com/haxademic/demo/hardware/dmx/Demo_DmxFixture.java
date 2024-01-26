package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_DmxFixture
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXFixture fixture;
	
	protected void firstFrame() {
		// use most basic singleton instance version of DMXUniverse
		DMXUniverse.instanceInit();
		fixture = (new DMXFixture(1)).setEaseFactor(0.1f);
	}

	protected void drawApp() {
		fixture.color().setTargetRGBANormalized(Mouse.xNorm, Mouse.yNorm, Mouse.xNorm, 1);
		background(fixture.color().colorInt());
	}
}
