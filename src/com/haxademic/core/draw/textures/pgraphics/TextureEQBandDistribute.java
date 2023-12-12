package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureEQBandDistribute 
extends BaseTexture {

	public TextureEQBandDistribute( int width, int height ) {
		super(width, height);
		
	}

	public void newLineMode() {

	}

	public void draw() {
//		_texture.clear();
		PG.feedback(pg, 0xff000000, 0.2f, 3);
		pg.noStroke();
		float numEQ = 128f;
		float numElements = 300f;

		float eqStep = numEQ / numElements;
		float barW = width / numElements / 2;
		int eqIndex = 0;
		for(int i=0; i < numElements; i++) {
			eqIndex = P.floor(i * eqStep);
			float eq = AudioIn.audioFreq(eqIndex) * 3f;
			pg.fill(_colorEase.colorInt(eq));
			pg.rect(width/2 + i * barW, 0, barW, height);
			pg.rect(width/2 - i * barW, 0, barW, height);
		}
		
	}
}
