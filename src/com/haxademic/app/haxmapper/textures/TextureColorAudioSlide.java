package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class TextureColorAudioSlide
extends BaseTexture {

	protected int _eqIndex;
	protected int _mode;
	protected float _lastAmp = 0;
	
	public TextureColorAudioSlide( int width, int height ) {
		super();

		buildGraphics( width, height );
		
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
		_texture.clear();
		
		_texture.noStroke();
		_texture.fill( _colorEase.colorInt() );
		float amp = P.p.audioIn.getEqAvgBand( _eqIndex ) * 0.15f;
		if( amp < _lastAmp ) amp = _lastAmp * 0.9f;
		if( _mode == 0 ) {
			_texture.rect(0, 0, _texture.width * amp, _texture.height );			
		} else if( _mode == 1 ) {
			_texture.rect(_texture.width, 0, -_texture.width * amp, _texture.height );			
		} else if( _mode == 2 ) {
			_texture.rect(0, 0, _texture.width, _texture.height * amp );			
		} else if( _mode == 3 ) {
			_texture.rect(0, _texture.height, _texture.width, -_texture.height * amp );			
		} 
		_lastAmp = amp;
	}
}
