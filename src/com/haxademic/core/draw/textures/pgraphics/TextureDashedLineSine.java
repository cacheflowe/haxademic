package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureDashedLineSine
extends BaseTexture {

	protected int maxRows = 6;
	protected int numRows = 5;
	protected WaveOscillator[] waves;
	protected float frames = 0;
	protected float loopProgress = 0;
	protected EasingFloat lineWeight = new EasingFloat(2, 6);

	
	public TextureDashedLineSine( int width, int height ) {
		super();
		
		buildGraphics( width, height );
		
		// build pool
		waves = new WaveOscillator[maxRows];
		for (int i = 0; i < maxRows; i++) {
			waves[i] = new WaveOscillator();
		}

	}
	
	public void updateDraw() {
		// update loop
		frames++;
		loopProgress = (frames % 100f) / 100f;
		loopProgress = P.abs(loopProgress) * P.TWO_PI;

		// draw transition result to texture
		_texture.background(0);
		_texture.stroke(255);
		lineWeight.update();
		_texture.strokeWeight(lineWeight.value());
		
		// draw rows
		for (int i = 0; i < numRows; i++) {
			float rowHeight = (float) _texture.height / (float) numRows;
			waves[i].update(rowHeight, i);
		}
	}

	public void updateTiming() {
		waves[MathUtil.randRange(0, waves.length - 1)].randomize();
	}
	
	public void updateTimingSection() {
		numRows = MathUtil.randRange(3, maxRows);
		
		for (int i = 0; i < waves.length; i++) {
			waves[i].randomize();
		}
	}
	
	public void newLineMode() {
		lineWeight.setTarget(MathUtil.randRange(1, 8));
	}
	
	// LinesRow Object --------------------------

	public class WaveOscillator {
		
		float spacing = 1;
		float scrollMult = 1;
		float ampOscSpeed = 0.1f;
		float freqBase = 0.0075f;
		float freqBase2 = 0.0075f;
		float freqBase3 = 0.0075f;
		float freqMultRange = 0.0015f;
		
		public WaveOscillator() {
			randomize();
		}
		
		public void randomize() {
			// 20, 3, 0.15f, 0.0025f, 0.002f
			spacing = MathUtil.randRangeDecimal(4, 10);
			scrollMult = MathUtil.randRangeDecimal(-4, 4);
			while(P.abs(scrollMult) < 0.2f) scrollMult = MathUtil.randRangeDecimal(-4, 4);
			ampOscSpeed = MathUtil.randRangeDecimal(0.05f, 0.4f);
			freqBase = MathUtil.randRangeDecimal(0.0005f, 0.005f);
			freqBase2 = MathUtil.randRangeDecimal(0.005f, 0.015f);
			freqBase3 = MathUtil.randRangeDecimal(0.005f, 0.015f);
			freqMultRange = MathUtil.randRangeDecimal(0.001f, 0.3f);
		}
		
		public void update(float rowHeight, float i) {
			float waveAmp = rowHeight * 0.5f; // * (0.25f + ampOscSpeed * P.sin(loopProgress));
			float waveFreqMult = freqBase + freqMultRange;// * P.sin(loopProgress);
//			float waveFreqMult2 = freqBase2 + freqMultRange;// * P.sin(loopProgress);
//			float waveFreqMult3 = freqBase3 + freqMultRange;// * P.sin(loopProgress);

			float centerY = rowHeight * i + rowHeight / 2f;
			for (int x = 0; x < _texture.width; x += spacing) {
				float oscValue = P.sin((frames * 0.04f) * (float)scrollMult + x * waveFreqMult);
//				float oscValue2 = P.sin(loopProgress * (float)scrollMult + P.p.noise(x * waveFreqMult2));
//				float oscValue3 = P.sin(loopProgress * (float)scrollMult + P.p.noise(x * waveFreqMult3));
//				oscValue *= oscValue2;
//				oscValue *= oscValue3;
				float waveY = centerY + oscValue * waveAmp;
//				p.point(x, waveY);
				_texture.line(x, centerY, 0, x, waveY, 0);
			}
		}
		
	}

}
