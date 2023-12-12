package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureTwistingSquares
extends BaseTexture {
	
	protected EasingFloat _steps = new EasingFloat(50f, 20);
	
	public TextureTwistingSquares( int width, int height ) {
		super(width, height);

		
		updateTimingSection();
	}
	
	public void draw() {
//		_texture.clear();
		_steps.update();

		PG.setDrawCenter(pg);
		pg.translate( width/2, height/2, 0 );

		pg.background(0);
		
		float steps = 80; // _steps.value();
		float oscInc = P.TWO_PI / steps;
		float lineSize = (steps/2f) + 1f * P.sin( ( P.p.frameCount ) * oscInc );
		
		for( int i=100; i > 0; i-- ) {
			
			float rot = P.sin( ( P.p.frameCount + i ) * oscInc );
			
			if( i % 2 == 0 ) {
				pg.fill( 0 );
				pg.stroke( 0 );
			} else {
				pg.fill( _colorEase.colorInt() );
				pg.stroke( _colorEase.colorInt() );
			}
			
			pg.pushMatrix();
			pg.rotate( rot * 0.4f );
			pg.rect( 0, 0, i * lineSize, i * lineSize );
			pg.popMatrix();
		}
	}
	
	public void updateTimingSection() {
		_steps.setTarget(MathUtil.randRangeDecimal(30, 80));
	}

}
