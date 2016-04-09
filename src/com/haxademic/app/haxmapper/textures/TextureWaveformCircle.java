package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureWaveformCircle 
extends BaseTexture {

	protected WaveformData _waveformData;
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

		
		_waveformData = P.p._waveformData;
		_circleInc = ( (float)Math.PI * 2.0f ) / _waveformData._waveform.length;
		
		// set some defaults
		newLineMode();
		_amp = _texture.width / 20f;
		_strokeWeight = 1.5f;
	}
	
	public void newLineMode() {
		_radius.setTarget(MathUtil.randRangeDecimal(_texture.width / 10f, _texture.width / 2.7f)); 
	}

	public void feeback() {
		// texture feedback
		float feedback = 10f;// * P.sin(percentComplete * P.TWO_PI);
		_texture.copy(
			_texture, 
			0, 
			0, 
			_texture.width, 
			_texture.height, 
			P.round(-feedback/2f), 
			P.round(-feedback/2f), 
			P.round(_texture.width + feedback), 
			P.round(_texture.height + feedback)
		);
		_texture.fill(0, 20);
		_texture.noStroke();
		_texture.rect(_texture.width/2, _texture.height/2, _texture.width, _texture.height);
	}
	
	public void updateDraw() {
		feeback();
		
		_radius.update();
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );

		int numPoints = _waveformData._waveform.length;
		_texture.noFill();
		_texture.stroke( _color );
		_texture.strokeWeight( _strokeWeight );
		_texture.strokeCap( P.ROUND );
		_texture.beginShape();
		
//		int iNext = 0;
		float radius;//, radiusNext;
		for (int i = 0; i < numPoints; i++ ) {
//			iNext = (i == numPoints - 1) ? 0 : i+1;	// makes sure we wrap around at the end
			radius =     _radius.value() + _waveformData._waveform[i] * _amp;
//			radiusNext = _radius.value() + _waveformData._waveform[iNext] * _amp;
//			p.line( p.sin( _circleInc * i ) * radius , p.cos( _circleInc * i ) * radius, p.sin( _circleInc * iNext ) * radiusNext, p.cos( _circleInc * iNext ) * radiusNext );
			_texture.vertex( P.sin( _circleInc * i ) * radius , P.cos( _circleInc * i ) * radius );
		}
		// connect 1st and last points
		radius = _radius.value() + _waveformData._waveform[0] * _amp;
		_texture.vertex( P.sin( _circleInc * 0 ) * radius , P.cos( _circleInc * 0 ) * radius );
		_texture.endShape();
	}
	
}
