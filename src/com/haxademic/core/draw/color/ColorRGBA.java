package com.haxademic.core.draw.color;

import com.haxademic.core.app.PAppletHax;

public class ColorRGBA {
	public float r;
	public float g;
	public float b;
	public float a;
	
	// Cell Constructor
	public ColorRGBA( float r, float g, float b, float a ) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	} 
	
	public int colorInt()
	{
		return PAppletHax.getInstance().color(r, g, b, a);
	}
	
	public int colorIntWithAlpha( float alpha, float offset )
	{
		return PAppletHax.getInstance().color(r + offset, g + offset, b + offset, alpha);
	}
}
