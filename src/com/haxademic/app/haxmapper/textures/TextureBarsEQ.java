package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;

public class TextureBarsEQ 
extends BaseTexture {

	protected float _barHeight;
	protected float _amp;
	
	protected float _cols;

	public TextureBarsEQ( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		_cols = P.p.width * 0.1f;
		_barHeight = P.p.height * 0.1f;
	}
	
	public void newLineMode() {
	}

	public void updateDraw() {
//		_texture.clear();
		feedback(7f, 0.15f);
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );
		_texture.pushMatrix();
		
		_texture.rectMode(PConstants.CORNER);
		_texture.noStroke();
		
		_texture.fill( _color );
		
		// draw bars
		_texture.translate( 0, -_texture.height/2, 0 );
		drawBars();
		_texture.translate( 0, _texture.height, 0 );
		_texture.rotateX( P.PI );
		drawBars();
		
		_texture.popMatrix();
	}
	
	public void drawBars() {
		// draw bars
		float cellW = P.p.width / _cols;
		float cellH = _barHeight;
		float startX = -P.p.width/2f;
		int spectrumInterval = (int) ( 256f / _cols );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		for (int i = 0; i < _cols; i++) {
			_texture.rect( startX + i * cellW, 0, cellW, P.p._audioInput.getFFT().spectrum[i*spectrumInterval] * cellH );
		}		
	}

}
