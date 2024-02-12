package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.DemoAssets;

public class Demo_DmxFixture_Custom
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected FixtureCustom fixture1;
	protected FixtureCustom fixture2;
	
	protected void config() {
		Config.setAppSize(512, 512);
	}

	protected void firstFrame() {
		// use most basic singleton instance version of DMXUniverse
		DMXUniverse.instanceInit();
		
		// and an EasingFloat vs LinearFloat
		fixture1 = new FixtureCustom(1);
		// fixture2 = new FixtureCustom(11);
	}

	protected void drawApp() {
		fixture1.color().setTargetRGBANormalized(1, 0, 0, 0);
		// fixture1.color().setTargetRGBANormalized(Mouse.xNorm, 0, Mouse.yNorm, 1);
		fixture1.amber.setTarget(0);
		fixture1.uv.setTarget(0);
		fixture1.dimmer.setTarget(1);
		
		// draw on 2 halves of screen
		fill(fixture1.color().colorInt());
		rect(0, 0, p.width, p.height);

		// print 
		DemoAssets.setDemoFont(p.g);
		p.text("" +
			"fixture1.r: " + fixture1.colorR() + "\n" +
			"fixture1.g: " + fixture1.colorG() + "\n" +
			"fixture1.b: " + fixture1.colorB() + "\n" +
			"fixture1.a: " + fixture1.colorA() + "\n" +
			"fixture1.amber: " + fixture1.amber.value() + "\n" +
			"fixture1.uv: " + fixture1.uv.value() + "\n" +
			"fixture1.dimmer: " + fixture1.dimmer.value() + "\n"
		, 50, 50);
	}

	public class FixtureCustom
	extends DMXFixture {

		public EasingFloat amber = new EasingFloat(0, DEFAULT_EASING);
		public EasingFloat uv = new EasingFloat(0, DEFAULT_EASING);
		public EasingFloat dimmer = new EasingFloat(0, DEFAULT_EASING);

		public FixtureCustom(int address) {
			super(DMXUniverse.instance(), address, DMXMode.RGBW);
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
}
