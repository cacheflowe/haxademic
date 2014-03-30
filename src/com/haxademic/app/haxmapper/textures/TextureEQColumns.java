package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;

public class TextureEQColumns 
extends BaseTexture {

	public TextureEQColumns( int width, int height ) {
		super();

		buildGraphics( width, height );
	}
	
	public void update() {
		int numBands = 32;
		float eqW = P.ceil( _texture.width / numBands );
		float spectrumInterval = (int) ( 256 / numBands );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		
		_texture.beginDraw();
		_texture.clear();
		
		for( int i=0; i < numBands; i++ ) {
			_texture.fill( 255 * P.p._audioInput.getFFT().spectrum[P.floor(i*spectrumInterval)] * 2, 255 );
			_texture.rect(i * eqW, 0, eqW, _texture.height );
		}
		
		_texture.endDraw();
	}
}
