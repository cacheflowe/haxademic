package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.math.MathUtil;

public class TextureTwistingSquares
extends BaseTexture {
	
	public TextureTwistingSquares( int width, int height ) {
		super();

		buildGraphics( width, height );
		updateTimingSection();
	}
	
	public void updateDraw() {
//		_texture.clear();

		DrawUtil.setDrawCenter(_texture);
		_texture.translate( _texture.width/2, _texture.height/2, 0 );

		_texture.background(0);
		
		int steps = 50;
		float oscInc = P.TWO_PI / (float)steps;
		float lineSize = 25f + 1f * P.sin( ( P.p.frameCount ) * oscInc );
		
		for( int i=100; i > 0; i-- ) {
			
			float rot = P.sin( ( P.p.frameCount + i ) * oscInc );
			
			if( i % 2 == 0 ) {
				_texture.fill( 0 );
				_texture.stroke( 0 );
			} else {
				_texture.fill( _colorEase.colorInt() );
				_texture.stroke( _colorEase.colorInt() );
			}
			
			_texture.pushMatrix();
			_texture.rotate( rot * 0.4f );
			_texture.rect( 0, 0, i * lineSize, i * lineSize );
			_texture.popMatrix();
		}
	}
	
	public void updateTimingSection() {
	}

}
