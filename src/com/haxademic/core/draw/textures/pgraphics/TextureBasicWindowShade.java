package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureBasicWindowShade 
extends BaseTexture {

	protected float _amp;
	
	public TextureBasicWindowShade( int width, int height ) {
		super(width, height);

		
	}
	
	public void newLineMode() {
	}

	public void updateDraw() {
//		_texture.clear();
		_texture.background(0);
		
		float eqVal;
		float eqBands = 256f;
		float eqStep = width / eqBands; // (float) width
		
//		_texture.background(255);
		_texture.rectMode( P.CENTER );
		_texture.noStroke();
		for( int i=0; i < eqBands; i++ ) {
			eqVal = AudioIn.audioFreq(Math.round( i ) % 256);
			_texture.fill( _color );
			_texture.rect( i * eqStep, height/2, eqStep, height * eqVal);
		}
	}
	
}
