package com.haxademic.core.draw.color;

import processing.core.PApplet;

import com.haxademic.core.app.P;

public class ColorUtil {
	
	public static int colorWithIntAndAlpha( PApplet p, int color, int alpha ) {
//		float a = (color >> 24) & 0xFF;
//		float r = color >> 16 & 0xFF;  // Faster way of getting red(argb)
//		float g = color >> 8 & 0xFF;   // Faster way of getting green(argb)
//		float b = color & 0xFF;          // Faster way of getting blue(argb)

//		float r = color >> 16 & 0xFF;  // Faster way of getting red(argb)
//		float g = color >> 8 & 0xFF;   // Faster way of getting green(argb)
//		float b = color & 0xFF;          // Faster way of getting blue(argb)

//		float r = p.red(color);
//		float g = p.green(color);
//		float b = p.blue(color);
		
//		return p.color(r, g, b, alpha);
		
		// from: http://processing.org/discourse/beta/num_1261125421.html
		return (color & 0xffffff) | (alpha << 24); 
	}
	
	public static int colorFromHex( String hex ) {
		return P.unhex("FF"+hex.substring(1));
	}
}
