package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

public class RotatorShapes 
extends ElementBase 
implements IVizElement {
	
	RotatorShape _rotator;
	RotatorShape _rotatorBG;

	public RotatorShapes( PApplet p, ToxiclibsSupport toxi ) {
		super( p, toxi );
		init();
	}
	
	public void init() {
		_rotator = new RotatorShape( p, toxi, 12 );
		_rotatorBG = new RotatorShape( p, toxi, 12 );
		reset();
	}

	public void updateColorSet( ColorGroup colors ) {
		_rotator.updateColorSet( colors );
		_rotatorBG.updateColorSet( colors );
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		DrawUtil.setBasicLights( p );
		p.strokeWeight(1);

		p.pushMatrix();
		p.translate( 0, 0, -2000 );
		_rotatorBG.update();
		p.popMatrix();

	}
	
	public void reset() {
		_rotator.reset();
		_rotatorBG.reset();
	}
	
	public void updateLineMode() {
		_rotator.updateLineMode();
		_rotatorBG.updateLineMode();
	}
	
	public void updateCamera() {
		_rotator.updateCamera();
		_rotatorBG.updateCamera();
	}

	
	public void dispose() {
		super.dispose();
		_rotator.dispose();
		_rotator = null;
		_rotatorBG.dispose();
		_rotatorBG = null;
	}
	
}
