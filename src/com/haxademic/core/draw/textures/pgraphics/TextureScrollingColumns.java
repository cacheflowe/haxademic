package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureScrollingColumns
extends BaseTexture {

	protected EasingFloat _barW = new EasingFloat(20, 8);
	protected float time = 0;
	protected float speed = 1;
	protected EasingFloat rotation = new EasingFloat(0, 8);

	public TextureScrollingColumns( int width, int height ) {
		super();

		buildGraphics( width, height );
		updateTimingSection();
	}
	
	public void updateTiming() {
		speed = MathUtil.randRangeDecimal(-2f, 2f);
	}
	
	public void newRotation() {
		if(MathUtil.randBoolean(P.p) == true) {
			rotation.setTarget( P.TWO_PI * MathUtil.randRangeDecimal(0, 2) );
		} else {
			rotation.setTarget( P.PI/4f * MathUtil.randRange(0, 8) );
		}
	}
	
	public void updateDraw() {
		_texture.clear();
		
		rotation.update();
		_barW.update();
		float barW = _barW.value();

		time += speed;
		float x = time % (barW * 2f);
		
		_texture.pushMatrix();
		_texture.translate(_texture.width/2, _texture.height/2);
		_texture.rotate(rotation.value());
		
		DrawUtil.setDrawCenter(_texture);
		for( float i=x - _texture.width - barW*2f; i < _texture.width * 2; i+=barW*2f ) {
			_texture.fill( 0 );
			_texture.rect(i, 0, barW, _texture.height * 2 );
			_texture.fill( _colorEase.colorInt() );
			_texture.rect(i+barW, 0, barW, _texture.height * 2 );
		}
		
		_texture.popMatrix();
	}
	
	public void updateTimingSection() {
		_barW.setTarget( 20f + 3f * MathUtil.randRange(1, 6) );
	}

}
