package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class TextureColorAudioFade 
extends BaseTexture {

	protected int _eqIndex;
	
	public TextureColorAudioFade( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		_eqIndex = MathUtil.randRange(0, 255);
	}
	
	public BaseTexture setActive( boolean isActive ) {
		super.setActive(isActive);
		_eqIndex = MathUtil.randRange(3, 31);
		return this;
	}
	
	public void updateDraw() {
		_texture.clear();
		
		_texture.fill( _colorEase.colorInt(), P.constrain( P.p.audioIn.getEqAvgBand( _eqIndex ) * 255, 0, 255 ) );
		_texture.rect(0, 0, _texture.width, _texture.height );
	}
}
