package com.haxademic.core.draw.color;

import toxi.color.TColor;

public class EasingTColor {
	
	public TColor curColor;
	public TColor targetColor;
	protected float _easing;
	
	/**
	 * Eases a TColor towards another.
	 * @param color	
	 * @param ease	should be < 1.0. smaller numbers ease slower
	 */
	public EasingTColor( TColor color, float ease ) {
		this.curColor = color.copy();
		this.targetColor = color.copy();
		_easing = ease;
	}
	
	public void setCurColor( TColor color ) {
		curColor.setARGB( color.toARGB() );
	}
	
	public void setTargetColor( TColor color ) {
		targetColor.setARGB( color.toARGB() );
	}
	
	public void setCurAndTargetColors( TColor cur, TColor target ) {
		curColor.setARGB( cur.toARGB() );
		targetColor.setARGB( target.toARGB() );
	}
	
	public void update() {
		curColor.blend( targetColor, _easing );
	}
	
	public TColor color() {
		return curColor;
	}
}
