package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PConstants;

public class TextureBarsEQ 
extends BaseTexture {

	protected float _barHeight;
	protected float _amp;
	
	protected float _cols;

	public TextureBarsEQ( int width, int height ) {
		super(width, height);

		
		
		_cols = P.p.width * 0.1f;
		_barHeight = P.p.height * 0.1f;
	}
	
	public void newLineMode() {
	}

	public void draw() {
//		_texture.clear();
		PG.feedback(pg, 0xff000000, 0.15f, 3);
		
//		PG.resetGlobalProps( _texture );
		PG.setCenterScreen( pg );
		pg.pushMatrix();
		
		pg.rectMode(PConstants.CORNER);
		pg.noStroke();
		
		pg.fill( _color );
		
		// draw bars
		pg.translate( 0, -height/2, 0 );
		drawBars();
		pg.translate( 0, height, 0 );
		pg.rotateX( P.PI );
		drawBars();
		
		pg.popMatrix();
	}
	
	public void drawBars() {
		// draw bars
		float cellW = P.p.width / _cols;
		float cellH = _barHeight;
		float startX = -P.p.width/2f;
		int spectrumInterval = (int) ( 256f / _cols );	// 256 keeps it in the bottom half of the spectrum since the high ends is so overrun
		for (int i = 0; i < _cols; i++) {
			pg.rect( startX + i * cellW, 0, cellW, AudioIn.audioFreq(i*spectrumInterval) * cellH );
		}		
	}

}
