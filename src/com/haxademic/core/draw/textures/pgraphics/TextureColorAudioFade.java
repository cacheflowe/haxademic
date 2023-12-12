package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureColorAudioFade 
extends BaseTexture {

	protected int _eqIndex;
	
	public TextureColorAudioFade( int width, int height ) {
		super(width, height);

		
		
		_eqIndex = MathUtil.randRange(0, 255);
	}
	
	public BaseTexture setActive( boolean isActive ) {
		super.setActive(isActive);
		_eqIndex = MathUtil.randRange(3, 31);
		return this;
	}
	
	public void draw() {
//		_texture.clear();
		pg.background(0);
		
		pg.fill( _colorEase.colorInt(), P.constrain( AudioIn.audioFreq( _eqIndex ) * 255, 0, 255 ) );
		pg.rect(0, 0, width, height );
	}
}
