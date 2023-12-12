package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;

import processing.core.PConstants;

public class TextureAppFrameEq2d 
extends BaseTexture {

	protected float _cols = 32;

	public TextureAppFrameEq2d( int width, int height ) {
		super(width, height);

		
	}
	
	public void newLineMode() {
	}

	public void draw() {
		pg.clear();
		
		PG.resetGlobalProps( pg );
		pg.pushMatrix();
		
		pg.noStroke();
		pg.fill( 0 );
		
		pg.rectMode(PConstants.CENTER);

		// draw bars
		pg.pushMatrix();
		drawBars();
		pg.popMatrix();

		pg.pushMatrix();
		pg.translate( 0, height );
		pg.rotateX( (float) Math.PI );

		drawBars();
		pg.popMatrix();
		
		pg.popMatrix();
	}
	
	public void drawBars() {
		// draw bars
		float halfH = height * 0.5f;
		float halfW = width * 0.5f;
		float cellW = (float)width/(float)_cols;
		float cellX = 0;
		float cellH = height/6f;
		int spectrumInterval = (int) ( 128f / _cols );	// 128 keeps it in the bottom quarter of the spectrum since the high ends is so overrun
		
		pg.beginShape();
		pg.vertex( cellX, -halfH );
		for (int i = 0; i < _cols; i++) {
			float eqAmp = AudioIn.audioFreq(i*spectrumInterval) * cellH;
			pg.vertex( cellX, eqAmp );
			cellX += cellW;
		}		
		pg.vertex( cellX, 0 );
		pg.vertex( cellX, -halfH );
		pg.vertex( -halfW, -halfH );
		pg.endShape(P.CLOSE);
	}

}
