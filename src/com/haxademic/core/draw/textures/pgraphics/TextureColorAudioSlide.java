package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

public class TextureColorAudioSlide
extends BaseTexture {

	protected int _eqIndex;
	protected int _mode;
	protected float _lastAmp = 0;
	
	public TextureColorAudioSlide( int width, int height ) {
		super(width, height);

		
		
		randomize();
	}
	
	public BaseTexture setActive( boolean isActive ) {
		boolean wasActive = _active;
		super.setActive(isActive);
		if( isActive != wasActive ) {
			randomize();
		}
		return this;
	}
	
	public void randomize() {
		_eqIndex = MathUtil.randRange(3, 31);
		_mode = MathUtil.randRange(0, 3);
	}
	
	public void updateDraw() {
//		_texture.clear();
		_texture.background(0);
		
		_texture.noStroke();
		_texture.fill( _colorEase.colorInt() );
		float amp = P.p.audioFreq( _eqIndex ) * 0.15f;
		if( amp < _lastAmp ) amp = _lastAmp * 0.9f;
		if( _mode == 0 ) {
			_texture.rect(0, 0, width * amp, height );			
		} else if( _mode == 1 ) {
			_texture.rect(width, 0, -width * amp, height );			
		} else if( _mode == 2 ) {
			_texture.rect(0, 0, width, height * amp );			
		} else if( _mode == 3 ) {
			_texture.rect(0, height, width, -height * amp );			
		} 
		_lastAmp = amp;
	}
}
