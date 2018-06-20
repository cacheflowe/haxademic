package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;

public class TextureAppFrameEq2d 
extends BaseTexture {

	protected float _cols = 32;

	public TextureAppFrameEq2d( int width, int height ) {
		super();

		buildGraphics( width, height );
	}
	
	public void newLineMode() {
	}

	public void updateDraw() {
		_texture.clear();
		
		DrawUtil.resetGlobalProps( _texture );
		_texture.pushMatrix();
		
		_texture.noStroke();
		_texture.fill( 0 );
		
		_texture.rectMode(PConstants.CENTER);

		// draw bars
		_texture.pushMatrix();
		drawBars();
		_texture.popMatrix();

		_texture.pushMatrix();
		_texture.translate( 0, _texture.height );
		_texture.rotateX( (float) Math.PI );

		drawBars();
		_texture.popMatrix();
		
		_texture.popMatrix();
	}
	
	public void drawBars() {
		// draw bars
		float halfH = _texture.height * 0.5f;
		float halfW = _texture.width * 0.5f;
		float cellW = (float)_texture.width/(float)_cols;
		float cellX = 0;
		float cellH = _texture.height/6f;
		int spectrumInterval = (int) ( 128f / _cols );	// 128 keeps it in the bottom quarter of the spectrum since the high ends is so overrun
		
		_texture.beginShape();
		_texture.vertex( cellX, -halfH );
		for (int i = 0; i < _cols; i++) {
			float eqAmp = P.p.audioFreq(i*spectrumInterval) * cellH;
			_texture.vertex( cellX, eqAmp );
			cellX += cellW;
		}		
		_texture.vertex( cellX, 0 );
		_texture.vertex( cellX, -halfH );
		_texture.vertex( -halfW, -halfH );
		_texture.endShape(P.CLOSE);
	}

}
