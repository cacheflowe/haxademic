package com.haxademic.app.haxvisual.viz;

import com.haxademic.core.draw.color.ColorGroup;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

public class ElementBase {

	protected PApplet p;
	protected ToxiclibsSupport toxi;

	public ElementBase( PApplet p5, ToxiclibsSupport toxiclibs ) {
		p = p5;
		toxi = toxiclibs;
	}
	
	public void dispose() {
		p = null;
		toxi = null;
	}

	public void pause() {}
	public void updateColorSet( ColorGroup colors ){}
	public void updateLineMode(){}
	public void updateCamera(){}
	public void updateTiming(){};
	public void updateSection(){};
}
