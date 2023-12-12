package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

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
		super(width, height);

		
		
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

	public void draw() {
//		_texture.clear();
		pg.background(0);
		
//		PG.resetGlobalProps( _texture );
		PG.setBasicLights( pg );

		pg.pushMatrix();
		pg.rectMode(PConstants.CORNER);
		
		_width = width * 6;
		_height = height * 6;
		float lineH = _height / _numLines;
		
		// ease rotations
		_curRotation = MathUtil.easeTo(_curRotation, _rotationTarget, 5);
		_curRotX = MathUtil.easeTo(_curRotX, _rotXTarget, 5);
		
		// set colors and alphas
		pg.noStroke();
		int spectrumInterval = P.round( AudioIn.frequencies.length / _numLines);
		
		// double lines
		lineH = _height / _numLines;
		PG.setCenterScreen( pg );
		pg.translate( 0, -_height/2, 0 );
		pg.rotateX( _curRotX );
		
		pg.pushMatrix();
		pg.rotateY(-_curRotation);
		drawLines( _color, lineH, spectrumInterval );
		pg.popMatrix();
		
		pg.pushMatrix();
		pg.rotateY(_curRotation - (float)Math.PI);
		drawLines( _color, lineH, spectrumInterval );
		pg.popMatrix();

		
		pg.popMatrix();
	}
	
	protected void drawLines( int fillColor, float lineH, int spectrumInterval ) {
		for( int i = 0; i < _numLines; i++ ) {
			float alpha = 2f * AudioIn.audioFreq(i*spectrumInterval);
			pg.fill( fillColor, alpha * 255 );
			pg.rect( 0, i * lineH, _width, lineH );
		}
	}		
	
}
