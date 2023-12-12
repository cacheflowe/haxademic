package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureEQRadialLollipops 
extends BaseTexture {

	protected float _strokeWeight;
	protected boolean trailIn = true;
	protected int circlePoints = 60;
	protected EasingFloat[] amps = new EasingFloat[circlePoints];

	public TextureEQRadialLollipops( int width, int height ) {
		super(width, height);
		
		for (int i = 0; i < circlePoints; i++ ) {
			amps[i] = new EasingFloat(0, 0.2f);
		}

		// set some defaults
		newLineMode();
		_strokeWeight = width * 0.02f;
	}
	
	public void newLineMode() {
		trailIn = MathUtil.randBoolean();
		if(trailIn) {
			
		} else {
			
		}
	}
	
	public void draw() {
		_texture.background(0);
		PG.setCenterScreen(_texture);
		PG.setDrawCenter(_texture);

		int audioPoints = AudioIn.waveform.length;
		audioPoints = circlePoints / 2; // divide by 2 to mirroe
		float segmentRads = P.TWO_PI / (float) circlePoints;
		
		// draw a circle
		_texture.fill(0);
		_texture.stroke(_color);
		_strokeWeight = width * 0.005f;
		_texture.strokeWeight(_strokeWeight);
		
		float radsOffset = -P.HALF_PI;
		for (int i = 0; i < circlePoints; i++ ) {
			int loopI = i;
			if(loopI >= audioPoints) loopI = audioPoints - (i % audioPoints);
			float radius = width * 0.2f + width * 0.13f * AudioIn.audioFreq(1 + loopI);
			amps[i].setTarget(radius).update();
			amps[i].setEaseFactor(0.9f);

			float curRads = radsOffset + segmentRads * i;
			float x = P.cos(curRads) * amps[i].value();
			float y = P.sin(curRads) * amps[i].value();
			_texture.line(0, 0, x, y);
			_texture.circle(x, y, width * 0.01f);
		}
	}
	
}
