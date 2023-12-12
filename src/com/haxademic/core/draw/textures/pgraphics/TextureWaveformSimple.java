package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureWaveformSimple 
extends BaseTexture {

	protected int _numLines = 40;
	protected boolean _hasStroke = true;
	
	public TextureWaveformSimple( int width, int height ) {
		super(width, height);
		
	}
	
	public void newLineMode() {
		_numLines = MathUtil.randRange(20, 30);
		_hasStroke = !_hasStroke;
	}

	public void draw() {
		PG.feedback(pg, 0xff000000, 0.15f, 5);
		int waveformDataLength = AudioIn.waveform.length;
		float widthStep = (float) width / (float) waveformDataLength;
		float startY = height * 0.5f;
		float amp = height * 0.4f;
		
		pg.stroke(_color);
		pg.strokeWeight(3.f);

		for(int i = 1; i < waveformDataLength; i++) {
			pg.line( i * widthStep, startY + AudioIn.waveform[i-1] * amp, (i+1) * widthStep, startY + AudioIn.waveform[i] * amp );
		}
	}
}
