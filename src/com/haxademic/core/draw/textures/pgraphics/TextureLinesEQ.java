package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

import processing.core.PConstants;

public class TextureLinesEQ 
extends BaseTexture {

	protected float _width;
	protected float _height;
	protected int _numLines;
	protected float _rotationTarget;
	protected float _curRotation;
	protected float _rotXTarget;
	protected float _curRotX;

	public TextureLinesEQ( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		// set some defaults
		_width = width;
		_height = height;
//		_numLines = 30;
		_curRotation = 0;
		_rotationTarget = 0;
		_curRotX = 0;
		_rotXTarget = 0;
		newLineMode();
	}
	
	public void newLineMode() {
		_numLines = (int) MathUtil.randRangeDecimal( 100, 250 );
	}
	
	public void newRotation() {
		float eighthPi = (float) Math.PI / 8f;
		_rotationTarget = MathUtil.randRangeDecimal(-eighthPi,eighthPi);
		_rotXTarget = MathUtil.randRangeDecimal(-eighthPi/2f,eighthPi/2f);
	}

	public void updateDraw() {
		_texture.clear();
		
		PG.resetGlobalProps( _texture );
		PG.setBasicLights( _texture );

		_texture.pushMatrix();
		_texture.rectMode(PConstants.CORNER);
		
		_width = _texture.width * 6;
		_height = _texture.height * 6;
		float lineH = _height / _numLines;
		
		// ease rotations
		_curRotation = MathUtil.easeTo(_curRotation, _rotationTarget, 5);
		_curRotX = MathUtil.easeTo(_curRotX, _rotXTarget, 5);
		
		// set colors and alphas
		_texture.noStroke();
		int spectrumInterval = P.round( P.p.audioData.frequencies().length / _numLines);
		
		// double lines
		lineH = _height / _numLines;
		PG.setCenterScreen( _texture );
		_texture.translate( 0, -_height/2, 0 );
		_texture.rotateX( _curRotX );
		
		_texture.pushMatrix();
		_texture.rotateY(-_curRotation);
		drawLines( _color, lineH, spectrumInterval );
		_texture.popMatrix();
		
		_texture.pushMatrix();
		_texture.rotateY(_curRotation - (float)Math.PI);
		drawLines( _color, lineH, spectrumInterval );
		_texture.popMatrix();

		
		_texture.popMatrix();
	}
	
	protected void drawLines( int fillColor, float lineH, int spectrumInterval ) {
		for( int i = 0; i < _numLines; i++ ) {
			float alpha = 2f * P.p.audioFreq(i*spectrumInterval);
			_texture.fill( fillColor, alpha * 255 );
			_texture.rect( 0, i * lineH, _width, lineH );
		}
	}		
	
}
