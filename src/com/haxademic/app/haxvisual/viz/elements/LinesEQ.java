package com.haxademic.app.haxvisual.viz.elements;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.color.TColor;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.ElementBase;
import com.haxademic.app.haxvisual.viz.IVizElement;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class LinesEQ
extends ElementBase 
implements IVizElement {
	
	protected float _width;
	protected float _height;
	protected int _numLines;
	protected float _rotationTarget;
	protected float _curRotation;
	protected float _rotXTarget;
	protected float _curRotX;
	protected TColor _baseColor;

	public LinesEQ( PApplet p, ToxiclibsSupport toxi, AudioInputWrapper audioData ) {
		super( p, toxi, audioData );
		init();
	}

	public void init() {
		// set some defaults
		_width = 1000;
		_height = 600;
		_numLines = 30;
		_curRotation = 0;
		_rotationTarget = 0;
		_curRotX = 0;
		_rotXTarget = 0;
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
		DrawUtil.setBasicLights( p );

		p.pushMatrix();
		p.rectMode(PConstants.CORNER);
		
		_width = p.width * 6;
		_height = p.height * 6;
		float lineH = _height / _numLines;
		
		// ease rotations
		_curRotation = MathUtil.easeTo(_curRotation, _rotationTarget, 5);
		_curRotX = MathUtil.easeTo(_curRotX, _rotXTarget, 5);
		
		// set colors and alphas
		p.noStroke();
		int spectrumInterval = p.round( _audioData.getFFT().spectrum.length / _numLines);
//		TColor fillColor = _baseColor;
		
		// double lines
		_height *= 2;
		lineH = _height / _numLines;
		DrawUtil.setCenter( p );
		p.translate( 0, -_height/2, -3000 );
		p.rotateX( _curRotX );
//		float rotation = _curRotation;//(float)(Math.PI*2f)/18f;
		
		p.pushMatrix();
		p.rotateY(-_curRotation);
		drawLines( _baseColor.toARGB(), lineH, spectrumInterval );
		p.popMatrix();
		
		p.pushMatrix();
		p.rotateY(_curRotation - (float)Math.PI);
		drawLines( _baseColor.toARGB(), lineH, spectrumInterval );
		p.popMatrix();

		
		p.popMatrix();
	}
	
	protected void drawLines( int fillColor, float lineH, int spectrumInterval ) {
		for( int i = 0; i < _numLines; i++ ) {
			float spectrumData = _audioData.getFFT().spectrum[i*spectrumInterval % 512];
			float alpha = spectrumData * .7f;	//  * 255
			p.fill( fillColor, alpha * 255 );
			p.rect( 0, i * lineH, _width, lineH );
		}
	}

	public void reset() {
		updateCamera();
		updateLineMode();
	}
	
	public void updateLineMode() {
		_numLines = (int) p.random( 50, 150 );
	}
	
	public void updateCamera() {
		float eighthPi = (float) Math.PI / 8f;
		_rotationTarget = p.random(-eighthPi,eighthPi);
		_rotXTarget = p.random(-eighthPi/2,eighthPi/2);
	}

	public void dispose() {
		_audioData = null;
	}

}
