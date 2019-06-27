package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureWaveformCircle 
extends BaseTexture {

	protected float _circleInc;
	protected float _amp;
	protected float _strokeWeight;
	protected EasingFloat _radius = new EasingFloat(0, 6);

	public TextureWaveformCircle( int width, int height ) {
		super(width, height);
//		_waveformData = P.p._waveformData;
		
		// set some defaults
		newLineMode();
		_amp = width / 20f;
		_strokeWeight = 3.f;
	}
	
	public void newLineMode() {
		_radius.setTarget(MathUtil.randRangeDecimal(width / 10f, width / 2.7f)); 
	}
	
	public void updateDraw() {
		feedback(10, 0.12f);
		
		_radius.update();
		
		PG.setCenterScreen( _texture );

		_circleInc = P.TWO_PI / (float) P.p.audioData.waveform().length;
		int numPoints = P.p.audioData.waveform().length;
		
		// draw 3 concentric circles
		for (int j = 0; j < 4; j++) {
			
			// draw a circle
			_texture.noFill();
			_texture.stroke( _color );
			_texture.strokeWeight( _strokeWeight );
			_texture.strokeCap( P.ROUND );
			_texture.beginShape();
			
			float radius;
			for (int i = 0; i <= numPoints; i++ ) {
				int loopI = i % numPoints;
				float concentricMult = 0.5f * (float) j;
				radius = concentricMult * _radius.value() + P.p.audioData.waveform()[loopI] * _amp;
				_texture.vertex( P.sin( _circleInc * loopI ) * radius , P.cos( _circleInc * loopI ) * radius );
			}

			// connect 1st and last points
//			radius = _radius.value() + P.p.audioData.waveform()[0] * _amp;
//			_texture.vertex( P.sin( _circleInc * 0 ) * radius , P.cos( _circleInc * 0 ) * radius );
			_texture.endShape();
		}
	}
	
}
