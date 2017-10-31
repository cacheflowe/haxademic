package com.haxademic.app.haxvisual.viz.elements;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.color.TColorBlendBetween;
import com.haxademic.core.draw.context.DrawUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

public class BarsModEQ
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _height;
	protected float _amp;
	protected int _numLines;
	protected TColorBlendBetween _color;
	protected boolean _is3D = false;
	
	protected float _cols = 32;
	protected float _rows = 16;

	protected PVector _rotation = new PVector( 0, 0, 0 );
	protected PVector _rotationTarget = new PVector( 0, 0, 0 );

	public BarsModEQ( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_width = p.width;
		_height = p.height;
		_amp = 20;
		_color = new TColorBlendBetween( TColor.BLACK.copy(), TColor.BLACK.copy() );
	}
	
	public void setDrawProps(float width, float height) {
		_width = width;
		_height = height;
	}

	public void updateColorSet( ColorGroup colors ) {
		_color.setColors( TColor.BLACK.copy(), colors.getRandomColor() );
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		
		int scaleMult = 3;
		setDrawProps(p.width*scaleMult, p.height*scaleMult);
		
		
		p.pushMatrix();
		p.translate( 0f, p.height * 1.1f, -p.height * 1.8f );
		drawBars();
		p.popMatrix();
		
		p.pushMatrix();
		p.translate( 0f, - p.height * 1.1f, -p.height * 1.8f );
		p.rotateX( P.PI );
		p.rotateY( P.PI );
		drawBars();
		p.popMatrix();

				
	}
	
	protected void drawBars() {
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		
		// ease tilt of grid
		_rotation.lerp( _rotationTarget, 0.2f );
		p.rotateX( _rotation.x );//+ (float) Math.PI );
		//		p.rotateY( 0 );	// _rotation.y
		//		p.rotateZ( _rotation.z );

		// draw grid
		float cellW = _width/_cols;
		float cellH = _height/_rows;
		float startX = -_width/2;
		float row = 0;
		int col = 0;
		

		for (int i = 0; i < _cols * 4; i++) {
			float eqAmp = _audioData.getFFT().spectrum[i];
			col = (int) ( i % _cols );
			row = eqAmp * 2.0f * _rows;
				
			if( eqAmp > 0.01f ) {
				p.fill( _color.argbWithPercent( eqAmp * 0.85f ) );
				p.pushMatrix();
	
				p.translate( startX + col*cellW, 0, 0 );
				p.rotateX( -eqAmp * (float) Math.PI / 5f );
				p.rect( 0, 0, cellW, cellH + row*cellH );	
	
				p.popMatrix();					
			}
		}

	}

	public void reset() {
		updateLineMode();
		updateCamera();
	}

	public void updateLineMode() {
		_is3D = ( p.random(0f,2f) >= 1 ) ? false : true;
	}
	
	public void updateCamera() {
		float circleSegment = (float) ( Math.PI * 2f ) / 16f;
		_rotationTarget.x = -circleSegment * 3f + p.random( -circleSegment * 2, 0 );
		_rotationTarget.y = p.random( -circleSegment, circleSegment );
		_rotationTarget.z = p.random( -circleSegment, circleSegment );
	}
	
	public void dispose() {
		_audioData = null;
	}
	
}
