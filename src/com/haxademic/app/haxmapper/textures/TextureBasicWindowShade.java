package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;

public class TextureBasicWindowShade 
extends BaseTexture {

	protected float _amp;
	
	public TextureBasicWindowShade( int width, int height ) {
		super();

		buildGraphics( width, height );
	}
	
	public void newLineMode() {
	}

	public void updateDraw() {
		_texture.clear();
		
		float eqVal;
		float eqBands = 256f;
		float eqStep = _texture.width / eqBands; // (float) _texture.width
		
//		_texture.background(255);
		_texture.rectMode( P.CENTER );
		_texture.noStroke();
		_texture.noSmooth();
		for( int i=0; i < eqBands; i++ ) {
			eqVal = P.p._audioInput.getFFT().spectrum[ Math.round( i ) % 256 ];			
			_texture.fill( _color );
			_texture.rect( i * eqStep, _texture.height/2, eqStep, _texture.height * eqVal);
		}
	}
	
}
