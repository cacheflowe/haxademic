package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.Gradients;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;

public class TextureCyclingRadialGradient
extends BaseTexture {

	protected float time = 0;
	protected float speed = 0.1f;
	protected int size = 0;

	public TextureCyclingRadialGradient( int width, int height ) {
		super();

		buildGraphics( width, height );
		size = P.max(width, height);
		updateTimingSection();
	}
	
	public void updateTiming() {
		speed = MathUtil.randRangeDecimal(-0.1f, 0.1f);
	}
	
	public void updateDraw() {
		_texture.clear();
		
		time += speed;
		
		_texture.pushMatrix();
		_texture.translate(_texture.width/2, _texture.height/2);
		Gradients.radial(
				_texture, 
				size * 2, 
				size * 2, 
				P.p.color(127f + 127f * P.sin(time)), 
				P.p.color(127f + 127f * P.sin(P.PI + time)), 
				40
		);
		
		_texture.popMatrix();
	}
	
}
