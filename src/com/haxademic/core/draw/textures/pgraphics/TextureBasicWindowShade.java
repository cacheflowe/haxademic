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

	public void draw() {
//		_texture.clear();
		pg.background(0);
		
		float eqVal;
		float eqBands = 256f;
		float eqStep = width / eqBands; // (float) width
		
//		_texture.background(255);
		pg.rectMode( P.CENTER );
		pg.noStroke();
		for( int i=0; i < eqBands; i++ ) {
			eqVal = AudioIn.audioFreq(Math.round( i ) % 256);
			pg.fill( _color );
			pg.rect( i * eqStep, height/2, eqStep, height * eqVal);
		}
	}
	
}
