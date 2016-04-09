package com.haxademic.app.haxmapper.textures;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class TextureWaveformSimple 
extends BaseTexture {

	protected int _numLines = 40;
	protected boolean _hasStroke = true;
	
	public TextureWaveformSimple( int width, int height ) {
		super();
		buildGraphics( width, height );
	}
	
	public void newLineMode() {
		_numLines = MathUtil.randRange(20, 30);
		_hasStroke = !_hasStroke;
	}

	public void updateDraw() {
		feedback(10f, 0.12f);
		
		int waveformDataLength = P.p._waveformData._waveform.length;
		float widthStep = (float) _texture.width / waveformDataLength;
		float startY = _texture.height * 0.5f;
		float amp = _texture.height * 0.4f;
		
		_texture.stroke(_color);
		_texture.strokeWeight(0.7f);

		for(int i = 1; i < waveformDataLength; i++) {
			_texture.line( i * widthStep, startY + P.p._waveformData._waveform[i-1] * amp, (i+1) * widthStep, startY + P.p._waveformData._waveform[i] * amp );
		}
	}
}
