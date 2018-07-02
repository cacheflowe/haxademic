package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureTwistingSquares
extends BaseTexture {
	
	protected EasingFloat _steps = new EasingFloat(50f, 20);
	
	public TextureTwistingSquares( int width, int height ) {
		super();

		buildGraphics( width, height );
		updateTimingSection();
	}
	
	public void updateDraw() {
//		_texture.clear();
		_steps.update();

		DrawUtil.setDrawCenter(_texture);
		_texture.translate( _texture.width/2, _texture.height/2, 0 );

		_texture.background(0);
		
		float steps = _steps.value();
		float oscInc = P.TWO_PI / steps;
		float lineSize = (steps/2) + 1f * P.sin( ( P.p.frameCount ) * oscInc );
		
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
		_steps.setTarget(MathUtil.randRangeDecimal(30, 80));
	}

}
