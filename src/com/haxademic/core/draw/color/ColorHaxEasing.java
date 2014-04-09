package com.haxademic.core.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.EasingFloat;

public class ColorHaxEasing {
	public EasingFloat r;
	public EasingFloat g;
	public EasingFloat b;
	public EasingFloat a;
	
	public ColorHaxEasing( float r, float g, float b, float a, float easeFactor ) {
		this.r = new EasingFloat(r, easeFactor);
		this.g = new EasingFloat(g, easeFactor);
		this.b = new EasingFloat(b, easeFactor);
		this.a = new EasingFloat(a, easeFactor);
	} 
	
	public ColorHaxEasing( String hex, float easeFactor ) {
		int color = ColorUtil.colorFromHex(hex);
		this.r = new EasingFloat(redFromColorInt(color), easeFactor);
		this.g = new EasingFloat(greenFromColorInt(color), easeFactor);
		this.b = new EasingFloat(blueFromColorInt(color), easeFactor);
		this.a = new EasingFloat(255, easeFactor);
	} 
	
	public void setTargetRGBA( float r, float g, float b, float a ) {
		this.r.setTarget( r );
		this.g.setTarget( g );
		this.b.setTarget( b );
		this.a.setTarget( a );
	}
	
	public void setTargetColorInt( int color ) {
		this.r.setTarget( redFromColorInt(color) );
		this.g.setTarget( greenFromColorInt(color) );
		this.b.setTarget( blueFromColorInt(color) );
		this.a.setTarget( 255 );
	}
	
	public void setTargetColorIntWithBrightness( int color, float brightness ) {
		this.r.setTarget( redFromColorInt(color) * brightness );
		this.g.setTarget( greenFromColorInt(color) * brightness );
		this.b.setTarget( blueFromColorInt(color) * brightness );
		this.a.setTarget( 255 );
	}
	
	public void update() {
		r.update();
		g.update();
		b.update();
		a.update();
	}

	public final static int alphaFromColorInt( int c ) { return (c >> 24) & 0xFF; }
	public final static int redFromColorInt( int c ) { return (c >> 16) & 0xFF;	}
	public final static int greenFromColorInt( int c ) { return (c >> 8)  & 0xFF; }
	public final static int blueFromColorInt( int c ) { return c & 0xFF; }

	public int colorInt() {
		return P.p.color(r.value(), g.value(), b.value(), a.value());
	}
	
}
