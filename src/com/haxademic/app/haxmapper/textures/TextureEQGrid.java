package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;

public class TextureEQGrid 
extends BaseTexture {

	protected int _numLines;
	protected boolean _is3D = false;
	
	protected float _cols = 32;
	protected float _rows = 16;
	protected float _amp = 20;
	protected float _spectrumInterval = 256f / (_cols * _rows);	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
	protected float _cellW;
	protected float _cellH;


	public TextureEQGrid( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		_cellW = P.ceil( _texture.width/_cols );
		_cellH = P.ceil( _texture.height/_rows );
	}
	
	public void update() {
		
		_texture.beginDraw();
		_texture.clear();
		
		// draw grid
		float startX = 0;
		float startY = 0;
		int spectrumIndex = 0;
		int fillColor = _texture.color(255);
		_texture.noStroke();
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				float alphaVal = P.p._audioInput.getFFT().spectrum[P.floor(_spectrumInterval * spectrumIndex)];
				_texture.fill( fillColor, alphaVal * 255f );
				_texture.rect( startX + i*_cellW, startY + j*_cellH, _cellW, _cellH );	
				spectrumIndex++;
			}
		}
		
		_texture.endDraw();
	}
}
