package com.haxademic.core.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.EasingColor;

public class DMXFixture {

	public enum DMXMode {
		SINGLE_CHANNEL,
		RGB,
		RGBW,
	}
	protected DMXMode mode;

	protected DMXUniverse universe;
	protected int dmxChannel;
	protected EasingColor color; 
	
	public DMXFixture(int dmxChannel) {
		this(DMXUniverse.instance(), dmxChannel, DMXMode.RGB);	// default mode requires calling `DMXUniverse.instanceInit()` on app init to use the singleton
	}
		
	public DMXFixture(int dmxChannel, boolean easingFloatMode) {
		this(DMXUniverse.instance(), dmxChannel, DMXMode.RGB, easingFloatMode);
	}
		
	public DMXFixture(int dmxChannel, DMXMode mode, boolean easingFloatMode) {
		this(DMXUniverse.instance(), dmxChannel, mode, easingFloatMode);
	}
	
	public DMXFixture(DMXUniverse universe, int dmxChannel, DMXMode mode) {
		this(DMXUniverse.instance(), dmxChannel, DMXMode.RGB, true);
	}

	public DMXFixture(DMXUniverse universe, int dmxChannel, DMXMode mode, boolean easingFloatMode) {
		this.universe = universe;
		this.dmxChannel = dmxChannel;
		this.mode = mode;
		this.color = new EasingColor(0xffffffff, 0.3f, easingFloatMode);
		if(this.universe != null) this.universe.addFixture(this);
		else P.error("DMXFixture initialized without a DMXUniverse");
	}
	
	public DMXMode mode() { return mode; }
	public int dmxChannel() { return dmxChannel; }
	public void dmxChannel(int channel) { dmxChannel = channel; }
	public EasingColor color() { return color; }
	public DMXFixture setEaseFactor(float easeFactor) { color.setEaseFactor(easeFactor); return this; }
	public int colorR() { return P.round(color.r()); }
	public int colorG() { return P.round(color.g()); }
	public int colorB() { return P.round(color.b()); }
	public int colorA() { return P.round(color.a()); }
	public int colorLuma() { return (int) P.p.brightness(color.colorInt()); }
	
	// called automatically by the DMXUniverse
	public void update() {
		color.update();
		
		// send dmx signals
		if(this.universe == null) return;
		this.universe.setValue(dmxChannel + 0, colorR());			// single channel uses just red channel
		if(mode == DMXMode.RGB || mode == DMXMode.RGBW) {
			this.universe.setValue(dmxChannel + 1, colorG());		// rgb & rgbw uses 3 RGB values
			this.universe.setValue(dmxChannel + 2, colorB());
		}
		if(mode == DMXMode.RGBW) {
			this.universe.setValue(dmxChannel + 3, colorA());		// rgbw uses alpha value for W
		}
	}
}
