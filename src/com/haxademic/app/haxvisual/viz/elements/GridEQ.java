package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.data.Point3D;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.util.DrawUtil;

public class GridEQ
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _height;
	protected float _amp;
	protected int _numLines;
	protected TColor _baseColor;
	protected boolean _is3D = false;
	
	protected float _cols = 32;
	protected float _rows = 16;

	protected Point3D _rotation = new Point3D( 0, 0, 0 );
	protected Point3D _rotationTarget = new Point3D( 0, 0, 0 );

	public GridEQ( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_width = p.width;
		_height = p.height;
		_amp = 20;
	}
	
	public void setDrawProps(float width, float height) {
		_width = width;
		_height = height;
	}

	public void updateColorSet( ColorGroup colors ) {
		_baseColor = colors.getRandomColor().copy();
	}

	public void update() {
		DrawUtil.resetGlobalProps( p );
		DrawUtil.setCenter( p );
		p.pushMatrix();
		
		// TODO: if background mode, draw back and huge
		setDrawProps(p.width*20, p.height*20);
		p.translate( 0f, 0f, -4000f );
		
		p.rectMode(PConstants.CENTER);
		p.noStroke();
		
		// ease tile of grid
		_rotation.easeToPoint( _rotationTarget, 5 );
		p.rotateX( _rotation.x );
		p.rotateY( _rotation.y );
		p.rotateZ( _rotation.z );

		// draw grid
		float cellW = _width/_cols;
		float cellH = _height/_rows;
		float startX = -_width/2;
		float startY = -_height/2;
		int spectrumIndex = 0;
		int fillColor = _baseColor.toARGB();
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				float alphaVal = _audioData.getFFT().spectrum[spectrumIndex];
				p.fill( fillColor, alphaVal * 255f );
				p.pushMatrix();
				if( _is3D ) {
					p.translate( 0, 0, cellH * alphaVal );
					p.rect( startX + i*cellW, startY + j*cellH, cellW, cellH );	
				} else {
					p.translate( startX + i*cellW, startY + j*cellH );
					p.box( cellW, cellH, cellH * alphaVal * 4 ); 
				}
				p.popMatrix();					
				spectrumIndex++;
			}
		}
		
		p.popMatrix();
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
		_rotationTarget.x = p.random( -circleSegment * 2, circleSegment * 2 );
		_rotationTarget.y = p.random( -circleSegment, circleSegment );
		_rotationTarget.z = p.random( -circleSegment, circleSegment );
	}
	
	public void dispose() {
		_audioData = null;
	}
	
}
