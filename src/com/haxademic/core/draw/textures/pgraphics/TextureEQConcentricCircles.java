package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;

public class TextureEQConcentricCircles 
extends BaseTexture {

	protected boolean _is3D = false;
	
	protected float _amp = 20;
	protected float _maxRadius = 0;
	protected float _numCircles = 50;
	protected float _circleRadiusStep = 0;
	protected float _spectrumInterval = 512f / _numCircles;


	public TextureEQConcentricCircles( int width, int height ) {
		super(width, height);
		
		_maxRadius = width;
		_circleRadiusStep = _maxRadius / _numCircles;
	}
	
	public void updateDraw() {
//		_texture.clear();
		_texture.background(0);
		
		// draw grid
		float startX = width / 2f;
		float startY = height / 2f;
		int white = P.p.color(255);
		_texture.noFill();
//		_texture.noStroke();
		_texture.stroke(255);
		_texture.strokeWeight(_circleRadiusStep);
		PG.setDrawCenter(_texture);
		for (int i = 0; i < _numCircles; i++) {
			float alphaVal = 0.1f * P.p.audioFreq( P.floor(_spectrumInterval * i) );
			alphaVal = P.p.audioFreq(P.floor(i * _spectrumInterval)) * 4f;
//			_texture.stroke( _colorEase.colorInt(), P.constrain( alphaVal * 255f, 0, 255 ) );
			if( i % 2 == 0 ) _texture.stroke( white, P.constrain( alphaVal * 255f, 0, 255 ) );
			_texture.ellipse( startX, startY, i * _circleRadiusStep, i * _circleRadiusStep );	
		}
	}
}
