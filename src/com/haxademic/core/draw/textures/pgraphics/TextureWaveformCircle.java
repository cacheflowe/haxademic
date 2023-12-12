package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureWaveformCircle 
extends BaseTexture {

	protected float segmentRads;
	protected float _amp;
	protected float _strokeWeight;
	protected EasingFloat _radius = new EasingFloat(0, 6);
	protected boolean trailIn = true;

	public TextureWaveformCircle( int width, int height ) {
		super(width, height);
		
		// set some defaults
		newLineMode();
		_amp = width / 10f;
		_strokeWeight = 3.f;
	}
	
	public void newLineMode() {
		trailIn = MathUtil.randBoolean();
		if(trailIn) {
			_radius.setTarget(width * 0.4f); 
		} else {
			_radius.setTarget(width * 0.2f); 
		}
	}
	
	public void draw() {
		if(trailIn) {
			// feedback(-20, 0.1f);
			PG.feedback(_texture, 0, 0.1f, -10);
		} else {
			// feedback(30, 0.2f);
			PG.feedback(_texture, 0, 0.2f, 15);
		}
		
		_radius.update();
		
		PG.setCenterScreen( _texture );

		int audioPoints = AudioIn.waveform.length;
		int circlePoints = audioPoints * 2;
		segmentRads = P.TWO_PI / (float) circlePoints;
		
		// draw a circle
		_texture.noFill();
		_texture.stroke( _color );
		_texture.strokeWeight( _strokeWeight );
		_texture.beginShape();
		
		float radius;
		float radsOffset = -P.HALF_PI;
		for (int i = 0; i <= circlePoints; i++ ) {
			int loopI = i;
			if(loopI >= audioPoints) loopI = audioPoints - (i % audioPoints);
			radius = _radius.value() + AudioIn.audioWave(loopI) * _amp;
			// radius = width * 0.4f + AudioIn.audioWave(loopI) * _amp;
			float curRads = radsOffset + segmentRads * i;
			float x = P.cos(curRads) * radius;
			float y = P.sin(curRads) * radius;
			_texture.vertex(x, y);
		}

		// connect 1st and last points
//			radius = _radius.value() + AudioIn.waveform[0] * _amp;
//			_texture.vertex( P.sin( _circleInc * 0 ) * radius , P.cos( _circleInc * 0 ) * radius );
		_texture.endShape();
	}
	
}
