package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
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
		super(width, height);

		
		updateTimingSection();
	}
	
	public void updateTiming() {
		speed = MathUtil.randRangeDecimal(-3f, 3f);
	}
	
	public void newRotation() {
		if(MathUtil.randBoolean() == true) {
			rotation.setTarget( P.TWO_PI * MathUtil.randRangeDecimal(0, 2) );
		} else {
			rotation.setTarget( P.PI/4f * MathUtil.randRange(0, 8) );
		}
	}
	
	public void draw() {
		pg.clear();
		
		rotation.update();
		_barW.update();
		float barW = _barW.value();

		time += speed;
		float x = time % (barW * 2f);
		
		pg.pushMatrix();
		pg.translate(width/2, height/2);
		pg.rotate(rotation.value());
		
		PG.setDrawCenter(pg);
		for( float i=x - width - barW*2f; i < width * 2; i+=barW*2f ) {
			pg.fill( 0 );
			pg.rect(i, 0, barW, height * 2 );
			pg.fill( _colorEase.colorInt() );
			pg.rect(i+barW, 0, barW, height * 2 );
		}
		
		pg.popMatrix();
	}
	
	public void updateTimingSection() {
		_barW.setTarget( 20f + 3f * MathUtil.randRange(1, 6) );
	}

}
