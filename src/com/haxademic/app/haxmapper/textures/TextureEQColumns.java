package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class TextureEQColumns 
extends BaseTexture {

	protected int _numLines = 40;
	protected boolean _hasStroke = true;
	
	public TextureEQColumns( int width, int height ) {
		super();

		buildGraphics( width, height );
	}
	
	public void newLineMode() {
		_numLines = MathUtil.randRange(20, 30);
		_hasStroke = !_hasStroke;
	}

	public void update() {
		super.update();
		
		float eqW = _texture.width / _numLines;
		float spectrumInterval = ( 512f / _numLines );
		float avergeInterval = ( 32f / _numLines );
		
		_texture.beginDraw();
		_texture.clear();
		
		if( _hasStroke == true ) {
			_texture.stroke(0);
			_texture.strokeWeight(1);
		} else {
			_texture.noStroke();
		}

		for( int i=0; i < _numLines; i++ ) {
			 _texture.fill( _colorEase.colorInt(), P.constrain( P.p.audioIn.getEqAvgBand( P.floor(i*avergeInterval) ) * 255, 0, 255 ) );
//			_texture.fill( _colorEase.colorInt(), P.constrain( P.p.audioIn.getEqBand( P.floor(i*spectrumInterval) ) * 255, 0, 255 ) );
			_texture.rect(i * eqW, 0, eqW, _texture.height );  //  P.p.audioIn.getEqBand( P.floor(i*spectrumInterval)%512 ) * 50
		}
		
		_texture.endDraw();
	}
}
