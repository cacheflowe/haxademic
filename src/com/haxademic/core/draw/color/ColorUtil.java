package com.haxademic.core.draw.color;

import java.util.Random;

import com.haxademic.core.app.P;

public class ColorUtil {
	
	public static Random random = new Random();
	
	public static int colorWithIntAndAlpha(int color, int alpha ) {
		// from: http://processing.org/discourse/beta/num_1261125421.html
		return (color & 0xffffff) | (alpha << 24); 
	}
	
	public static int colorFromHex( String hex ) {
		if(hex.length() == 9) hex = hex.substring(1);
		if(hex.length() == 7) hex = hex.substring(1);
		if(hex.length() == 6) hex = "FF" + hex;
		return colorFromHex(hex, false);
	}
	
	public static int colorFromHex( String hex, boolean hasHash ) {
		if(hasHash) return P.unhex(hex.substring(1));
		else return P.unhex(hex);
	}
	
	public static float componentByPercent( float percent ) {
		return percent * 255f;
	}
	
	public static String randomHex() {
		// from: https://stackoverflow.com/a/35459935
        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(256*256*256);
        // return String.format("%06x", nextInt);
        return "#ff"+ String.format("%06x", nextInt);	// leading #ff for alpha value
	}
	
	// concept from: http://dev.thi.ng/gradients/
	// returns 0-255 w/default params: gradientComponent(0, 255f, 1f, 0, rads);
	public static float gradientComponent(float dcOffset, float amp, float freq, float phase, float position) {
		float color = dcOffset + (amp/2f) + (amp/2f) * P.sin((freq * position) + phase);
		return P.constrain(color, 0f, 255f);
	}

	public final static void printColor(int c) {
		P.out(
				"RGBA: " +
				redFromColorInt(c) + ", " +
				greenFromColorInt(c) + ", " +
				blueFromColorInt(c) + ", " +
				alphaFromColorInt(c)
		);
	}
	
	public final static int alphaFromColorInt( int c ) { return (c >> 24) & 0xFF; }
	public final static int redFromColorInt( int c ) { return (c >> 16) & 0xFF;	}
	public final static int greenFromColorInt( int c ) { return (c >> 8)  & 0xFF; }
	public final static int blueFromColorInt( int c ) { return c & 0xFF; }
	public final static int[] rgbFromColorInt( int c ) { return new int[]{redFromColorInt(c), greenFromColorInt(c), blueFromColorInt(c)}; }
	
}
