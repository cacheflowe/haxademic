package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;

public class TextureEQBandDistribute 
extends BaseTexture {

	public TextureEQBandDistribute( int width, int height ) {
		super();
		buildGraphics( width, height );
	}

	public void newLineMode() {

	}

	public void updateDraw() {
//		_texture.clear();
		feedback(6f,0.2f);
		_texture.noStroke();
		float numEQ = 128f;
		float numElements = 300f;

		float eqStep = numEQ / numElements;
		float barW = _texture.width / numElements;
		int eqIndex = 0;
		for(int i=0; i < numElements; i++) {
			eqIndex = P.floor(i * eqStep);
			float eq = P.p.audioFreq(eqIndex);
			_texture.fill(_colorEase.colorInt(eq));
			_texture.rect(i * barW, 0, barW, _texture.height);
		}
		
		

	}
}
