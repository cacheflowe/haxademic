package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;

public class TextureScrollingColumns
extends BaseTexture {

	public TextureScrollingColumns( int width, int height ) {
		super();

		buildGraphics( width, height );
	}
	
	public void update() {
		int barW = 20;
		int x = P.p.frameCount % (barW * 2);
		
		_texture.beginDraw();
		_texture.clear();
		
		for( int i=x - barW*2; i < _texture.width; i+=barW*2 ) {
			_texture.fill( 0 );
			_texture.rect(i, 0, barW, _texture.height );
			_texture.fill( 255 );
			_texture.rect(i+barW, 0, barW, _texture.height );
		}
		
		_texture.endDraw();
	}
}
