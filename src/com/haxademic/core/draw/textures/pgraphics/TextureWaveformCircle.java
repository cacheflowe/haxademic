package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureWaveformCircle 
extends BaseTexture {

//	protected WaveformData _waveformData;
	protected float _circleInc;
	protected float _amp;
	protected float _strokeWeight;
	protected EasingFloat _radius = new EasingFloat(0, 6);

	public TextureWaveformCircle( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		_texture = P.p.createGraphics( _texture.width, _texture.height, P.P3D );
		_texture.smooth(OpenGLUtil.SMOOTH_HIGH);
		_texture.beginDraw();
		_texture.background(0);
		_texture.endDraw();

		
//		_waveformData = P.p._waveformData;
		_circleInc = ( (float)Math.PI * 2.0f ) / P.p.audioData.waveform().length;
		
		// set some defaults
		newLineMode();
		_amp = _texture.width / 20f;
		_strokeWeight = 3.f;
	}
	
	public void newLineMode() {
		_radius.setTarget(MathUtil.randRangeDecimal(_texture.width / 10f, _texture.width / 2.7f)); 
	}
	
	public void updateDraw() {
		feedback(10, 0.12f);
		
		_radius.update();
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );

		int numPoints = P.p.audioData.waveform().length;
		
		// draw 3 concentric circles
		for (int j = 0; j < 3; j++) {
			
			// draw a circle
			_texture.noFill();
			_texture.stroke( _color );
			_texture.strokeWeight( _strokeWeight );
			_texture.strokeCap( P.ROUND );
			_texture.beginShape();
			
			float radius;
			for (int i = 0; i < numPoints; i++ ) {
				float concentricMult = 0.5f * (float) j;
				radius =   concentricMult * _radius.value() + P.p.audioData.waveform()[i] * _amp;
				_texture.vertex( P.sin( _circleInc * i ) * radius , P.cos( _circleInc * i ) * radius );
			}

			// connect 1st and last points
			radius = _radius.value() + P.p.audioData.waveform()[0] * _amp;
			_texture.vertex( P.sin( _circleInc * 0 ) * radius , P.cos( _circleInc * 0 ) * radius );
			_texture.endShape();
		}
	}
	
}
