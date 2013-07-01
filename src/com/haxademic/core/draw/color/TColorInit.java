package com.haxademic.core.draw.color;

import toxi.color.TColor;

public class TColorInit {
	
	public static final float MODE_255_BASE = 255f;
	
	public static TColor newRGBA( float r, float g, float b, float a ) {
		r += 0.00001f;
		g += 0.00001f;
		b += 0.00001f;
		a += 0.00001f;
		return TColor.newRGBA( r / MODE_255_BASE, g / MODE_255_BASE, b / MODE_255_BASE, a / MODE_255_BASE );
	}

}
