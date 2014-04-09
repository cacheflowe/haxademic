package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class TextureScrollingColumns
extends BaseTexture {

	protected int _barW = 20;
	
	public TextureScrollingColumns( int width, int height ) {
		super();

		buildGraphics( width, height );
		updateTimingSection();
	}
	
	public void update() {
		super.update();

		int x = P.p.frameCount % (_barW * 2);
		
		_texture.beginDraw();
		_texture.clear();
		
		for( int i=x - _barW*2; i < _texture.width; i+=_barW*2 ) {
			_texture.fill( 0 );
			_texture.rect(i, 0, _barW, _texture.height );
			_texture.fill( _colorEase.colorInt() );
			_texture.rect(i+_barW, 0, _barW, _texture.height );
		}
		
		_texture.endDraw();
	}
	
	public void updateTimingSection() {
		_barW = P.round( 20f / MathUtil.randRange(1, 2) );
	}

}
