package com.haxademic.core.draw.color;

import com.haxademic.core.math.easing.EasingFloat;

public class EasedRGBColor {

	protected EasingFloat r;
	protected EasingFloat g;
	protected EasingFloat b;
	
	public EasedRGBColor(float r, float g, float b, float easeFactor) {
		this.r = new EasingFloat(r, easeFactor);
		this.g = new EasingFloat(g, easeFactor);
		this.b = new EasingFloat(b, easeFactor);
	}
	
	public float r() { return r.value(); }
	public float g() { return g.value(); }
	public float b() { return b.value(); }
	
	public void setRGB(float r, float g, float b) {
		this.r.setTarget(r);
		this.g.setTarget(g);
		this.b.setTarget(b);
	}
	
	public void update() {
		r.update(true);
		g.update(true);
		b.update(true);
	}
	
}
