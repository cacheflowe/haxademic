package com.haxademic.app.haxvisual.viz.elements;

import java.util.ArrayList;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

public class AppFrame2d
extends ElementBase 
implements IVizElement {
	
	protected float _amp;
	
	protected TColor _baseColor = null;
	protected TColor _fillColor = null;
	
	protected ArrayList<EasingFloat> _radii;


	public AppFrame2d( PApplet p, ToxiclibsSupport toxi ) {
		super( p, toxi );
		init();
	}

	public void init() {
		// set some defaults
		_radii = new ArrayList<EasingFloat>();
		for( int i=0; i < 8; i++ ) {
			_radii.add( new EasingFloat( 1f, 4 ) );
		}
	}
	
	public void setDrawProps(float width, float height) {

	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
		_fillColor = _baseColor.copy();
		_fillColor.alpha = 0.2f;
	}
	
	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
				
		// ease vertex multipliers
		for( int i=0; i < 8; i++ ) {
			_radii.get( i ).update();
		}
		
		p.translate(0,0,-400);
		
		// start outer for wraparound
		float outerMult = 3;
		float halfW = p.width/5f;
		float halfH = p.height/5f;
		p.fill( 0 );
		p.noStroke();
		
		// draw in halves - drawing all at once wasn't filling properly
		// draw frame right side		
		p.beginShape();
		p.vertex( 0, -p.height * outerMult );
		p.vertex( 0, -halfH * _radii.get( 0 ).value() );
		p.vertex( halfW * _radii.get( 1 ).value(), -halfH * _radii.get( 1 ).value() );
		p.vertex( halfW * _radii.get( 2 ).value(), 0 );
		p.vertex( halfW * _radii.get( 3 ).value(), halfH * _radii.get( 3 ).value() );
		p.vertex( 0, halfH * _radii.get( 4 ).value() );
		p.vertex( 0, p.height * outerMult );
		p.vertex( p.width * outerMult, p.height * outerMult );
		p.vertex( p.width * outerMult, -p.height * outerMult );
		p.vertex( 0, -p.height * outerMult );
		p.endShape(P.CLOSE);

		// draw frame left side		
		p.beginShape();
		p.vertex( 0, -p.height * outerMult );
		p.vertex( 0, -halfH * _radii.get( 0 ).value() );
		p.vertex( -halfW * _radii.get( 7 ).value(), -halfH * _radii.get( 7 ).value() );
		p.vertex( -halfW * _radii.get( 6 ).value(), 0 );
		p.vertex( -halfW * _radii.get( 5 ).value(), halfH * _radii.get( 5 ).value() );
		p.vertex( 0, halfH * _radii.get( 4 ).value() );
		p.vertex( 0, p.height * outerMult );
		p.vertex( -p.width * outerMult, p.height * outerMult );
		p.vertex( -p.width * outerMult, -p.height * outerMult );
		p.vertex( 0, -p.height * outerMult );
		p.endShape(P.CLOSE);


		
		p.popMatrix();
	}

	public void updateLineMode() {
		float cornerMult = p.random( 0.8f, 1.3f );
		float sideMult = ( cornerMult >= 1f ) ? p.random( 0.8f, 1.0f ) : p.random( 1.0f, 1.3f );
		for( int i=0; i < 8; i++ ) {
			if( i % 2 == 0 )
				_radii.get( i ).setTarget( cornerMult );
			else
				_radii.get( i ).setTarget( sideMult );
		}

	}
	
	public void reset() {
		
	}

	public void dispose() {
	}
	
}
