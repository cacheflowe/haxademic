package com.haxademic.app.haxvisual.viz.elements;


import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.draw.color.ColorGroup;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

public class _ElementTemplate
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _height;

	public _ElementTemplate( PApplet p, ToxiclibsSupport toxi ) {
		super( p, toxi );
		init();
	}

	public void init() {
		// set some defaults
	}
	
	public void setDrawProps(float width, float height, int numLines) {
		_width = width;
		_height = height;
	}

	public void update() {
		
	}

	public void reset() {
		
	}

	public void dispose() {
	}

	public void updateColorSet( ColorGroup colors ) {
	}

}

