package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

public class TextureEQGrid 
extends BaseTexture {

	protected float _cols = 32;
	protected float _rows = 32;
	protected float _spectrumInterval = 512f / (_cols * _rows);	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
	protected float _cellW;
	protected float _cellH;
	protected boolean _boxesGrow = false;


	public TextureEQGrid( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		_cellW = P.ceil( _texture.width/_cols );
		_cellH = P.ceil( _texture.height/_rows );
	}
	
	public void newLineMode() {
		_boxesGrow = MathUtil.randBoolean(P.p);
	}

	public void updateDraw() {
//		_texture.clear();
		feedback(6f,0.1f);
		
		// draw grid
		float startX = 0;
		float startY = 0;
		int spectrumIndex = 0;
		_texture.noStroke();
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				if( _boxesGrow ) {
					float scaleVal = P.constrain( 0.05f * P.p.audioFreq( P.floor(_spectrumInterval * spectrumIndex) ), 0, 1 );
					scaleVal = P.p.audioFreq(P.floor(i * _spectrumInterval));

					_texture.fill( _colorEase.colorInt() );
					_texture.rect( 
						startX + i*_cellW + _cellW * 0.5f - (_cellW * scaleVal * 0.5f), 
						startY + j*_cellH + _cellH * 0.5f - (_cellH * scaleVal * 0.5f), 
						_cellW * scaleVal, 
						_cellH * scaleVal 
					);	
					spectrumIndex++;
				} else {
					float alphaVal = 0.25f * P.p.audioFreq( P.floor(_spectrumInterval * spectrumIndex) );
					alphaVal = P.p.audioFreq(P.floor(i * _spectrumInterval));

					_texture.fill( _colorEase.colorInt(), P.constrain( alphaVal * 255f, 0, 255 ) );
					_texture.rect( startX + i*_cellW, startY + j*_cellH, _cellW, _cellH );	
					spectrumIndex++;
				}
			}
		}
	}
}
