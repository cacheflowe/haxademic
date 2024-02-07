package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_DmxFixture
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXFixture fixture1;
	protected DMXFixture fixture2;
	
	protected void firstFrame() {
		// use most basic singleton instance version of DMXUniverse
		DMXUniverse.instanceInit();
		
		// and an EasingFloat vs LinearFloat
		fixture1 = (new DMXFixture(1)).setEaseFactor(0.1f);
		fixture2 = (new DMXFixture(4, false)).setEaseFactor(100f);
	}

	protected void drawApp() {
		fixture1.color().setTargetRGBANormalized(Mouse.xNorm, Mouse.yNorm, Mouse.xNorm, 1);
		fixture2.color().setTargetRGBANormalized(Mouse.xNorm, Mouse.yNorm, Mouse.xNorm, 1);
		
		// draw on 2 halves of screen
		fill(fixture1.color().colorInt());
		rect(0, 0, p.width/2, p.height);
		fill(fixture2.color().colorInt());
		rect(p.width/2, 0, p.width/2, p.height);
	}
}
