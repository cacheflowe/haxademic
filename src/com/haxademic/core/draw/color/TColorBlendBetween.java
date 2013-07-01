package com.haxademic.core.draw.color;

import toxi.color.TColor;

public class TColorBlendBetween {
	
	public TColor _startColor;
	public TColor _endColor;
	public TColor _blendColor;

	public TColorBlendBetween( TColor startColor, TColor endColor ) {
		_startColor = startColor.copy();
		_endColor = endColor.copy();
		_blendColor = _startColor.copy();
	}
	
	public void setColors( TColor startColor, TColor endColor ) {
		_startColor.setARGB( startColor.toARGB() );
		_endColor.setARGB( endColor.toARGB() );
	}
	
	public void lightenColor( float lighten ) {
		_endColor.adjustRGB( lighten, lighten, lighten );
	}
	
	public int argbWithPercent( float percent ) {
		_blendColor.setARGB( _startColor.toARGB() );
		_blendColor.blend( _endColor, percent );
		return _blendColor.toARGB();
	}
}
