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
		pg.background(0);
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		
		int audioPoints = AudioIn.waveform.length;
		audioPoints = circlePoints / 2; // divide by 2 to mirroe
		float segmentRads = P.TWO_PI / (float) circlePoints;
		
		// draw a circle
		float minDim = Math.min(width, height);
		float circleSize = minDim * 0.01f;
		pg.fill(0);
		pg.stroke(_color);
		_strokeWeight = minDim * 0.005f;
		pg.strokeWeight(_strokeWeight);
		
		float radsOffset = -P.HALF_PI;
		for (int i = 0; i < circlePoints; i++ ) {
			int loopI = i;
			if(loopI >= audioPoints) loopI = audioPoints - (i % audioPoints);
			float radius = minDim * 0.2f + minDim * 0.1f * AudioIn.audioFreq(1 + loopI);
			amps[i].setTarget(radius).update();
			amps[i].setEaseFactor(0.9f);
			
			// draw line
			float curRads = radsOffset + segmentRads * i;
			float x = P.cos(curRads) * amps[i].value();
			float y = P.sin(curRads) * amps[i].value();
			pg.line(0, 0, x, y);

			// make sure circle is in fron of line
			pg.push();
			pg.translate(0, 0, 1);
			pg.circle(x, y, circleSize);
			pg.pop();
		}
	}
	
}
