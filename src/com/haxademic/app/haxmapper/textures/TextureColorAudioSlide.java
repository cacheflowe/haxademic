package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class TextureColorAudioSlide
extends BaseTexture {

	protected int _eqIndex;
	protected int _mode;
	
	public TextureColorAudioSlide( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		randomize();
	}
	
	public void setActive( boolean isActive ) {
		super.setActive(isActive);
		randomize();
	}
	
	public void randomize() {
		_eqIndex = MathUtil.randRange(0, 255);
		_mode = MathUtil.randRange(0, 3);
	}
	
	public void update() {
		super.update();

		_texture.beginDraw();
		_texture.clear();
		
		_texture.fill( _colorEase.colorInt() );
		float amp = P.p.audioIn.getEqBand( _eqIndex );
		if( _mode == 0 ) {
			_texture.rect(0, 0, _texture.width * amp, _texture.height );			
		} else if( _mode == 1 ) {
			_texture.rect(_texture.width, 0, -_texture.width * amp, _texture.height );			
		} else if( _mode == 2 ) {
			_texture.rect(0, 0, _texture.width, _texture.height * amp );			
		} else if( _mode == 3 ) {
			_texture.rect(0, _texture.height, _texture.width, -_texture.height * amp );			
		} 
		
		_texture.endDraw();
	}
}
