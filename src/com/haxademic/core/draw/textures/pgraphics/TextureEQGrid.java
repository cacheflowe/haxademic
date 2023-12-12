package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureEQGrid 
extends BaseTexture {

	protected float _cols = 32;
	protected float _rows = 16;
	protected float _cellW;
	protected float _cellH;
	protected boolean _boxesGrow = false;


	public TextureEQGrid( int width, int height ) {
		super(width, height);
		
		_cellW = P.ceil( width/_cols );
		_cellH = P.ceil( height/_rows );
	}
	
	public void newLineMode() {
		_boxesGrow = MathUtil.randBoolean();
	}

	public void draw() {
//		_texture.clear();
		PG.feedback(pg, 0xff000000, 0.1f, 1);
		
		// draw grid
		float startX = 0;
		float startY = 0;
		float spectrumIndex = 0;
		pg.noStroke();
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				if( _boxesGrow ) {
					float scaleVal = AudioIn.audioFreq((int)spectrumIndex % 128) / 10f;
					scaleVal = P.min(1, scaleVal);
					
					pg.fill( _colorEase.colorInt() );
					pg.rect( 
						startX + i*_cellW + _cellW * 0.5f - (_cellW * scaleVal * 0.5f), 
						startY + j*_cellH + _cellH * 0.5f - (_cellH * scaleVal * 0.5f), 
						_cellW * scaleVal, 
						_cellH * scaleVal 
					);	
				} else {
					float alphaVal = AudioIn.audioFreq((int)spectrumIndex % 128) / 10f;
					alphaVal = P.min(1, alphaVal);
					
					pg.fill( _colorEase.colorInt(), P.constrain( alphaVal * 255f, 0, 255 ) );
					pg.rect( startX + i*_cellW, startY + j*_cellH, _cellW, _cellH );	
				}
				spectrumIndex += 0.18f;
			}
		}
	}
}
