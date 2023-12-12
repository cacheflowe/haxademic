package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureEQConcentricCircles 
extends BaseTexture {

	protected float _amp = 20;
	protected float _maxRadius = 0;
	protected int _numCircles = 50;
	protected EasingFloat[] amps = new EasingFloat[_numCircles];
	protected float _circleRadiusStep = 0;
	protected float _spectrumInterval = 512f / _numCircles;


	public TextureEQConcentricCircles( int width, int height ) {
		super(width, height);
		for (int i = 0; i < _numCircles; i++ ) {
			amps[i] = new EasingFloat(0, 0.8f);
		}

		_maxRadius = width;
		_circleRadiusStep = _maxRadius / _numCircles;
	}
	
	public void draw() {
		_spectrumInterval = 180 / _numCircles; // use lower end of spectrum
		pg.background(0);
		
		// draw grid
		float startX = width / 2f;
		float startY = height / 2f;
		int white = P.p.color(255);
		pg.noFill();
		pg.strokeWeight(_circleRadiusStep / 1.8f);
		PG.setDrawCenter(pg);
		for (int i = 0; i < _numCircles; i++) {
			amps[i].setEaseFactor(0.99f);
			amps[i].setTarget(AudioIn.audioFreq(P.floor(_spectrumInterval * i))).update();
			float alphaVal = amps[i].value() * 0.8f;
			pg.stroke( white, P.constrain( alphaVal * 255f, 0, 255 ) );
			pg.ellipse( startX, startY, i * _circleRadiusStep, i * _circleRadiusStep );	
		}
	}
}
