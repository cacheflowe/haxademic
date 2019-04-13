package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class TextureNoiseLines
extends BaseTexture {

	protected int maxRows = 15;
	protected int numRows = 5;
	protected LinesRow[] linesRows;
	
	public TextureNoiseLines( int width, int height ) {
		super();
		
		buildGraphics( width, height );
		
		// build pool
		linesRows = new LinesRow[maxRows];
		for (int i = 0; i < maxRows; i++) {
			linesRows[i] = new LinesRow();
		}
	}
	
	public void preDraw() {
	}
	
	public void updateDraw() {
		// draw transition result to texture
		_texture.background(0);
		_texture.noStroke();
		
		// draw rows
		for (int i = 0; i < numRows; i++) {
			float rowHeight = (float) _texture.height / (float) numRows;
			linesRows[i].update(rowHeight, i);
		}
	}

	public void updateTiming() {
		for (int i = 0; i < numRows; i++) {
			linesRows[i].newSpeed();
		}
	}
	
	public void updateTimingSection() {
		numRows = MathUtil.randRange(4, maxRows);
	}
	
	// LinesRow Object --------------------------

	public class LinesRow {
		
		float noiseStart = 1;
		EasingFloat noiseMult = new EasingFloat(1, 0.05f);
		EasingFloat speed = new EasingFloat(1, 0.15f);
		int randomInterval = 100;
		int lastRandomFrame = 1;
		
		public LinesRow() {
			randomize();
		}
		
		public void randomize() {
			noiseMult.setTarget(MathUtil.randRangeDecimal(0.01f, 0.2f));
			speed.setTarget(MathUtil.randRangeDecimal(-0.5f, 0.5f));
			noiseMult.setCurrent(noiseMult.target());
			speed.setCurrent(speed.target());
			randomInterval = MathUtil.randRange(40, 200);
			lastRandomFrame = P.p.frameCount;
		}
		
		public void newSpeed() {
			noiseMult.setTarget(MathUtil.randRangeDecimal(0.01f, 0.1f));
			speed.setTarget(MathUtil.randRangeDecimal(-0.5f, 0.5f));
		}
		
		public void update(float rowHeight, float index) {
			// draw noise
			noiseMult.update(true);
			speed.update(true);
			noiseStart += speed.value();
			for (int x = 0; x < _texture.width; x++) {
				float noiseX = P.p.noise(noiseStart + x * noiseMult.value());
				if(noiseX > 0.65f) {
					_texture.fill(255); //  * noiseX 
					_texture.rect(x, rowHeight * index, 1, rowHeight);
				}
			}

		}
	}

}
