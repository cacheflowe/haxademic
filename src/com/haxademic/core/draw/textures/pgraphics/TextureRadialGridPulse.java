package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.Penner;

public class TextureRadialGridPulse
extends BaseTexture {

	protected float time = 0;
	protected float speed = 1;
	protected EasingFloat gridDivisions = new EasingFloat(25, 8);
	protected EasingFloat pulseFreq = new EasingFloat(3f, 8);
	protected EasingFloat pulseWidth = new EasingFloat(1.5f, 8);
	protected EasingFloat noiseCutoff = new EasingFloat(0.5f, 8);


	public TextureRadialGridPulse( int width, int height ) {
		super(width, height);

		
		updateTimingSection();
	}
	
	public void newRotation() {
	}
	
	public void updateTiming() {
		speed = MathUtil.randRangeDecimal(-2f, 2f);
		pulseFreq.setTarget(MathUtil.randRangeDecimal(1, 8));
	}
	
	public void updateTimingSection() {
		gridDivisions.setTarget(MathUtil.randRangeDecimal(15, 30));
		noiseCutoff.setTarget(MathUtil.randRangeDecimal(0f, 0.5f));
		pulseWidth.setTarget(MathUtil.randRangeDecimal(0.5f, 4f));
	}

	public void updateDraw() {
		// context & camera
		_texture.background(0);
		_texture.noStroke();
		_texture.blendMode(PBlendModes.BLEND);
		PG.setCenterScreen(_texture);
		PG.setDrawCenter(_texture);

		// time
		time += speed;
		float progressRads = (time % 100f) / 100f;
		progressRads = P.abs(progressRads) * P.TWO_PI;
		
		// calc grid values
		gridDivisions.update(true);
		pulseFreq.update(true);
		pulseWidth.update(true);
		noiseCutoff.update(true);
		float tileSize = (float) height / gridDivisions.value();
		float cols = width / tileSize;
		float rows = height / tileSize;
		float startX = -width / 2 + tileSize / 2;
		float startY = -height / 2 + tileSize / 2;
		float centerX = 0;
		float centerY = 0;
		float noiseScrollX = P.p.frameCount * 0.01f;
		float noiseScrollY = P.p.frameCount * 0.01f;
		boolean isRect = (P.round(pulseFreq.value()) % 2 == 0);
		
		// draw grid of tiles
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float tileX = startX + x * tileSize;
				float tileY = startY + y * tileSize;
				
				// map distance to sin offset
				float distanceFromCenter = MathUtil.getDistance(tileX, tileY, centerX, centerY);
				float distanceToRadians = P.map(distanceFromCenter, 0, 1000, 0, P.TWO_PI * pulseFreq.value());
				
				// calc wave
				float curOsc = 0.5f + 0.5f * P.sin(-progressRads + distanceToRadians);
				curOsc = Penner.easeInOutQuad(curOsc, 0, 1, 1);
				float tileOscSize = tileSize * (0.7f + 0.2f * curOsc);

				// position and draw
				_texture.pushMatrix();
				_texture.translate(tileX, tileY);
				
				if(isRect && noiseCutoff.value() < 0.2f) _texture.rotate(progressRads + distanceToRadians);
//				_texture.translate(0, 0, curOsc * 10f);
				
				// tint it in a skinnier pulse
				float tintOsc = (-1f + pulseWidth.value()) + P.sin(-progressRads + distanceToRadians);
				if(tintOsc > 0) {
					tintOsc *= 1f / pulseWidth.value();
//					float whiteAlpha = tintOsc;
					
					tintOsc = Penner.easeInOutQuad(tintOsc, 0, 1, 1);
					float cellNoiseVal = P.p.noise(noiseScrollX + startX + tileX * 0.02f, -noiseScrollY + startY + tileY * 0.02f); 
					if(cellNoiseVal < noiseCutoff.value()) tintOsc = 0;
					tintOsc *= cellNoiseVal;

					// draw 
//					int curColor = lerpColor(white, orange, tintOsc);
					_texture.fill(tintOsc * 255f);
					if(isRect) {
						_texture.rect(0, 0, tileOscSize, tileOscSize);
					} else {
						_texture.ellipse(0, 0, tileOscSize, tileOscSize);
					}
				}
				_texture.popMatrix();
			}
		}
	}
	
}
