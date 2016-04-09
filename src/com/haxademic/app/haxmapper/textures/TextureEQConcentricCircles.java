package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;

public class TextureEQConcentricCircles 
extends BaseTexture {

	protected boolean _is3D = false;
	
	protected float _amp = 20;
	protected float _maxRadius = 0;
	protected float _numCircles = 20;
	protected float _circleRadiusStep = 0;
	protected float _spectrumInterval = 512f / _numCircles;


	public TextureEQConcentricCircles( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		_maxRadius = width + height;
		_circleRadiusStep = _maxRadius / _numCircles;
	}
	
	public void updateDraw() {
		_texture.clear();
		
		// draw grid
		float startX = _texture.width / 2f;
		float startY = _texture.height / 2f;
		int white = P.p.color(255);
		_texture.noFill();
		_texture.noStroke();
		_texture.strokeWeight(_circleRadiusStep);
		DrawUtil.setDrawCenter(_texture);
		for (int i = 0; i < _numCircles; i++) {
			float alphaVal = 0.1f * P.p.audioIn.getEqBand( P.floor(_spectrumInterval * i) );
//			_texture.stroke( _colorEase.colorInt(), P.constrain( alphaVal * 255f, 0, 255 ) );
			if( i % 2 == 0 ) _texture.fill( white, P.constrain( alphaVal * 255f, 0, 255 ) );
			_texture.ellipse( startX, startY, i * _circleRadiusStep, i * _circleRadiusStep );	
		}
	}
}
