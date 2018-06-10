package com.haxademic.core.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.EasingFloat;

public class EasingColor {
	
	public static final float defaultEasing = 8f;
	public EasingFloat r;
	public EasingFloat g;
	public EasingFloat b;
	public EasingFloat a;
	
	// INIT
	
	public EasingColor( float r, float g, float b, float a, float easeFactor ) {
		this.r = new EasingFloat(r, easeFactor);
		this.g = new EasingFloat(g, easeFactor);
		this.b = new EasingFloat(b, easeFactor);
		this.a = new EasingFloat(a, easeFactor);
	} 
	
	public EasingColor( float r, float g, float b, float a ) {
		this(r, g, b, a, defaultEasing);
	} 
	
	public EasingColor( float r, float g, float b ) {
		this(r, g, b, 255, defaultEasing);
	} 
	
	public EasingColor( int color, float easeFactor ) {
		this(redFromColorInt(color), greenFromColorInt(color), blueFromColorInt(color), alphaFromColorInt(color), easeFactor);
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
	
	public void setTargetEasingColor( EasingColor color ) {
		this.r.setTarget( color.r() );
		this.g.setTarget( color.g() );
		this.b.setTarget( color.b() );
		this.a.setTarget( color.a() );
	}
	
	public void setTargetRGBA( float r, float g, float b, float a ) {
		this.r.setTarget( r );
		this.g.setTarget( g );
		this.b.setTarget( b );
		this.a.setTarget( a );
	}
	
	public void setTargetRGBANormalized( float r, float g, float b, float a ) {
		this.r.setTarget( r * 255f );
		this.g.setTarget( g * 255f );
		this.b.setTarget( b * 255f );
		this.a.setTarget( a * 255f );
	}
	
	public void setTargetHex( String hex ) {
		setTargetInt(ColorUtil.colorFromHex(hex));
	}
	
	public void setTargetInt( int color ) {
		this.r.setTarget( redFromColorInt(color) );
		this.g.setTarget( greenFromColorInt(color) );
		this.b.setTarget( blueFromColorInt(color) );
		this.a.setTarget( alphaFromColorInt(color) );
	}
	
	public void setTargetColorIntWithBrightness( int color, float brightness ) {
		this.r.setTarget( redFromColorInt(color) * brightness );
		this.g.setTarget( greenFromColorInt(color) * brightness );
		this.b.setTarget( blueFromColorInt(color) * brightness );
		this.a.setTarget( alphaFromColorInt(color) );
	}
	
	public void setTargetColorIntWithBrightnessAndSaturation( int color, float brightness ) {

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
	}
	
	// CURRENT
	
	public void setCurrentEasingColor( EasingColor color ) {
		this.r.setCurrent( color.r() );
		this.g.setCurrent( color.g() );
		this.b.setCurrent( color.b() );
		this.a.setCurrent( color.a() );
	}
	
	public void setCurrentRGBA( float r, float g, float b, float a ) {
		this.r.setCurrent( r );
		this.g.setCurrent( g );
		this.b.setCurrent( b );
		this.a.setCurrent( a );
	}
	
	public void setCurrentRGBANormalized( float r, float g, float b, float a ) {
		this.r.setCurrent( r * 255f );
		this.g.setCurrent( g * 255f );
		this.b.setCurrent( b * 255f );
		this.a.setCurrent( a * 255f );
	}
	
	public void setCurrentHex( String hex ) {
		setCurrentInt(ColorUtil.colorFromHex(hex));
	}

	public void setCurrentInt( int color ) {
		this.r.setCurrent( redFromColorInt(color) );
		this.g.setCurrent( greenFromColorInt(color) );
		this.b.setCurrent( blueFromColorInt(color) );
		this.a.setCurrent( alphaFromColorInt(color) );
	}
	
	// UPDATE
	
	public void update() {
		r.update(true);
		g.update(true);
		b.update(true);
		a.update(true);
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
	
	public int colorInt( float multiplier ) {
		return P.p.color(r.value() * multiplier, g.value() * multiplier, b.value() * multiplier, a.value());
	}
	
	// UTIL
	
	public static final int alphaFromColorInt( int c ) { return (c >> 24) & 0xFF; }
	public static final int redFromColorInt( int c ) { return (c >> 16) & 0xFF;	}
	public static final int greenFromColorInt( int c ) { return (c >> 8)  & 0xFF; }
	public static final int blueFromColorInt( int c ) { return c & 0xFF; }

}
