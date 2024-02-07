package com.haxademic.core.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.IEasingValue;
import com.haxademic.core.math.easing.LinearFloat;

public class EasingColor {
	
	public static final float defaultEasing = 0.125f;
	public IEasingValue r;
	public IEasingValue g;
	public IEasingValue b;
	public IEasingValue a;
	
	// INIT
	
	public EasingColor( float r, float g, float b, float a, float easeFactor ) {
		this(r, g, b, a, easeFactor, true);
	}

	public EasingColor( float r, float g, float b, float a, float easeFactor, boolean isEasingFloat) {
		if(isEasingFloat) {
			this.r = new EasingFloat(r, easeFactor);
			this.g = new EasingFloat(g, easeFactor);
			this.b = new EasingFloat(b, easeFactor);
			this.a = new EasingFloat(a, easeFactor);
		} else {
			this.r = new LinearFloat(r, easeFactor);
			this.g = new LinearFloat(g, easeFactor);
			this.b = new LinearFloat(b, easeFactor);
			this.a = new LinearFloat(a, easeFactor);
		}
	} 
	
	public EasingColor( float r, float g, float b, float a ) {
		this(r, g, b, a, defaultEasing);
	} 
	
	public EasingColor( float r, float g, float b ) {
		this(r, g, b, 255, defaultEasing);
	} 
	
	public EasingColor( int color, float easeFactor, boolean isEasingFloat ) {
		this(redFromColorInt(color), greenFromColorInt(color), blueFromColorInt(color), alphaFromColorInt(color), easeFactor, isEasingFloat);
	}

	public EasingColor( int color, float easeFactor ) {
		this(color, easeFactor, true);
	}

	public EasingColor( int color ) {
		this(color, defaultEasing);
	}
	
	public EasingColor( String hex, float easeFactor ) {
		this(ColorUtil.colorFromHex(hex), easeFactor);
	}
	
	public EasingColor( String hex ) {
		this(hex, defaultEasing);
	}
	
	// TARGET
	
	public int targetInt() {
		return P.p.color(r.target(), g.target(), b.target(), a.target());
	}
	
	public EasingColor setDelay(int delay) {
		this.r.setDelay(delay);
		this.g.setDelay(delay);
		this.b.setDelay(delay);
		this.a.setDelay(delay);
		return this;
	}
	
	public EasingColor setTargetEasingColor( EasingColor color ) {
		this.r.setTarget( color.r() );
		this.g.setTarget( color.g() );
		this.b.setTarget( color.b() );
		this.a.setTarget( color.a() );
		return this;
	}
	
	public EasingColor setTargetRGBA( float r, float g, float b, float a ) {
		this.r.setTarget( r );
		this.g.setTarget( g );
		this.b.setTarget( b );
		this.a.setTarget( a );
		return this;
	}
	
	public EasingColor setTargetR(float r) { this.r.setTarget(r); return this; }
	public EasingColor setTargetG(float g) { this.g.setTarget(g); return this; }
	public EasingColor setTargetB(float b) { this.b.setTarget(b); return this; }
	public EasingColor setTargetA(float a) { this.a.setTarget(a); return this; }
	
	public EasingColor setTargetRGBANormalized( float r, float g, float b, float a ) {
		this.r.setTarget( r * 255f );
		this.g.setTarget( g * 255f );
		this.b.setTarget( b * 255f );
		this.a.setTarget( a * 255f );
		return this;
	}
	
	public EasingColor setTargetRNorm(float r) { this.r.setTarget(r * 255f); return this; }
	public EasingColor setTargetGNorm(float g) { this.g.setTarget(g * 255f); return this; }
	public EasingColor setTargetBNorm(float b) { this.b.setTarget(b * 255f); return this; }
	public EasingColor setTargetANorm(float a) { this.a.setTarget(a * 255f); return this; }
	
	public EasingColor setTargetHex( String hex ) {
		setTargetInt(ColorUtil.colorFromHex(hex));
		return this;
	}
	
	public EasingColor setTargetInt( int color ) {
		this.r.setTarget( redFromColorInt(color) );
		this.g.setTarget( greenFromColorInt(color) );
		this.b.setTarget( blueFromColorInt(color) );
		this.a.setTarget( alphaFromColorInt(color) );
		return this;
	}
	
	public EasingColor setTargetColorIntWithBrightness( int color, float brightness ) {
		this.r.setTarget( redFromColorInt(color) * brightness );
		this.g.setTarget( greenFromColorInt(color) * brightness );
		this.b.setTarget( blueFromColorInt(color) * brightness );
		this.a.setTarget( alphaFromColorInt(color) );
		return this;
	}
	
	public EasingColor setTargetColorIntWithBrightnessAndSaturation( int color, float brightness ) {

		this.r.setTarget( redFromColorInt(color) * brightness );
		this.g.setTarget( greenFromColorInt(color) * brightness );
		this.b.setTarget( blueFromColorInt(color) * brightness );
		this.a.setTarget( alphaFromColorInt(color) );
		
		// tone down other colors to give more saturation
		float saturationAmount = 0.75f;
		if( this.r.target() >= this.g.target() && this.r.target() >= this.b.target() ) {
			this.g.setTarget( greenFromColorInt(color) * brightness * saturationAmount );
			this.b.setTarget( blueFromColorInt(color) * brightness * saturationAmount );
		} else if( this.g.target() >= this.r.target() && this.g.target() >= this.b.target() ) {
			this.r.setTarget( redFromColorInt(color) * brightness * saturationAmount );
			this.b.setTarget( blueFromColorInt(color) * brightness * saturationAmount );
		} else if( this.b.target() >= this.r.target() && this.b.target() >= this.g.target() ) {
			this.r.setTarget( redFromColorInt(color) * brightness * saturationAmount );
			this.g.setTarget( greenFromColorInt(color) * brightness * saturationAmount );
		}
		
		return this;
	}
	
	// CURRENT
	
	public EasingColor setCurrentEasingColor( EasingColor color ) {
		this.r.setCurrent( color.r() );
		this.g.setCurrent( color.g() );
		this.b.setCurrent( color.b() );
		this.a.setCurrent( color.a() );
		return this;
	}
	
	public EasingColor setCurrentRGBA( float r, float g, float b, float a ) {
		this.r.setCurrent( r );
		this.g.setCurrent( g );
		this.b.setCurrent( b );
		this.a.setCurrent( a );
		return this;
	}
	
	public EasingColor setCurrentR(float r) { this.r.setCurrent(r); return this; }
	public EasingColor setCurrentG(float g) { this.g.setCurrent(g); return this; }
	public EasingColor setCurrentB(float b) { this.b.setCurrent(b); return this; }
	public EasingColor setCurrentA(float a) { this.a.setCurrent(a); return this; }
	
	public EasingColor setCurrentRGBANormalized( float r, float g, float b, float a ) {
		this.r.setCurrent( r * 255f );
		this.g.setCurrent( g * 255f );
		this.b.setCurrent( b * 255f );
		this.a.setCurrent( a * 255f );
		return this;
	}
	
	public EasingColor setCurrentRNorm(float r) { this.r.setCurrent(r * 255f); return this; }
	public EasingColor setCurrentGNorm(float g) { this.g.setCurrent(g * 255f); return this; }
	public EasingColor setCurrentBNorm(float b) { this.b.setCurrent(b * 255f); return this; }
	public EasingColor setCurrentANorm(float a) { this.a.setCurrent(a * 255f); return this; }

	public EasingColor setCurrentHex( String hex ) {
		setCurrentInt(ColorUtil.colorFromHex(hex));
		return this;
	}

	public EasingColor setCurrentInt( int color ) {
		this.r.setCurrent( redFromColorInt(color) );
		this.g.setCurrent( greenFromColorInt(color) );
		this.b.setCurrent( blueFromColorInt(color) );
		this.a.setCurrent( alphaFromColorInt(color) );
		return this;
	}
	
	// UPDATE
	
	public void update() {
		if(r instanceof EasingFloat) {
			((EasingFloat) r).update(true);
			((EasingFloat) g).update(true);
			((EasingFloat) b).update(true);
			((EasingFloat) a).update(true);
		} else {
			r.update();
			g.update();
			b.update();
			a.update();
		}
	}
	
	// OTHER SETTERS
	
	public EasingColor setEaseFactor(float easeFactor) {
		this.r.setEaseFactor(easeFactor);
		this.g.setEaseFactor(easeFactor);
		this.b.setEaseFactor(easeFactor);
		this.a.setEaseFactor(easeFactor);
		return this;
	}
	
	// GETTERS
	
	public float r() { return r.value(); }
	public float g() { return g.value(); }
	public float b() { return b.value(); }
	public float a() { return a.value(); }

	public float rNorm() { return r.value() / 255f; }
	public float gNorm() { return g.value() / 255f; }
	public float bNorm() { return b.value() / 255f; }
	public float aNorm() { return a.value() / 255f; }
	
	public int colorInt() {
		return P.p.color(r.value(), g.value(), b.value(), a.value());
	}
	
	public int colorIntRGB() {
		return P.p.color(r.value(), g.value(), b.value());
	}
	
	public int colorInt( float multiplier ) {
		return P.p.color(r.value() * multiplier, g.value() * multiplier, b.value() * multiplier, a.value());
	}
	
	public boolean isComplete() {
		return r.isComplete() && g.isComplete() && b.isComplete() && a.isComplete();
	}
	
	public int colorIntMixedWith( EasingColor color2, float mix ) {
		return P.p.lerpColor(colorInt(), color2.colorInt(), mix);
	}
	
	// comparison helpers
	
	protected float RGB_TOTAL = 255 * 3;
	public float distanceToColor(int color) {
		float totalEasing = r() + g() + b();
		float totalColor = P.p.red(color) + P.p.green(color) + P.p.blue(color);
		return P.abs(totalEasing - totalColor) / RGB_TOTAL;
	}
	
	public float distanceToColor(EasingColor otherColor) {
		float total1 = r() + g() + b();
		float total2 = otherColor.r() + otherColor.g() + otherColor.b();
		return P.abs(total1 - total2) / RGB_TOTAL;
	}
	
	// UTIL
	
	public static final int alphaFromColorInt( int c ) { return (c >> 24) & 0xFF; }
	public static final int redFromColorInt( int c ) { return (c >> 16) & 0xFF;	}
	public static final int greenFromColorInt( int c ) { return (c >> 8)  & 0xFF; }
	public static final int blueFromColorInt( int c ) { return c & 0xFF; }
	public static final float alphaFromColorIntNorm( int c ) { return alphaFromColorInt(c) / 255f; }
	public static final float redFromColorIntNorm( int c ) { return redFromColorInt(c) / 255f; }
	public static final float greenFromColorIntNorm( int c ) { return greenFromColorInt(c) / 255f; }
	public static final float blueFromColorIntNorm( int c ) { return blueFromColorInt(c) / 255f; }

}
