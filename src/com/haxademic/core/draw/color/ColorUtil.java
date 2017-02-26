package com.haxademic.core.draw.color;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class ColorUtil {
	
	public static int colorWithIntAndAlpha( PApplet p, int color, int alpha ) {
		// from: http://processing.org/discourse/beta/num_1261125421.html
		return (color & 0xffffff) | (alpha << 24); 
	}
	
	public static int colorFromHex( String hex ) {
		return P.unhex("FF"+hex.substring(1));
	}
	
	public static float componentByPercent( float percent ) {
		return percent * 255f;
	}
	
	// concept from: http://dev.thi.ng/gradients/
	// returns 0-255 w/default params: gradientComponent(0, 255f, 1f, 0, rads);
	public static float gradientComponent(float dcOffset, float amp, float freq, float phase, float position) {
		float color = dcOffset + (amp/2f) + (amp/2f) * P.sin((freq * position) + phase);
		return P.constrain(color, 0f, 255f);
	}

	
	public final static int alphaFromColorInt( int c ) { return (c >> 24) & 0xFF; }
	public final static int redFromColorInt( int c ) { return (c >> 16) & 0xFF;	}
	public final static int greenFromColorInt( int c ) { return (c >> 8)  & 0xFF; }
	public final static int blueFromColorInt( int c ) { return c & 0xFF; }
	public final static int[] rgbFromColorInt( int c ) { return new int[]{redFromColorInt(c), greenFromColorInt(c), blueFromColorInt(c)}; }
	
}
