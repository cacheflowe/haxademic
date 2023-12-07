package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureEQTextLog 
extends BaseTexture {

	protected StringBufferLog log;
	protected StringBufferLog log2;
	protected int eq1 = 10;
	protected int eq2 = 30;

	public TextureEQTextLog( int width, int height ) {
		super(width, height);
		log = new StringBufferLog(height / 13);
		log2 = new StringBufferLog(height / 13);
	}
	
	public void newMode() {
		eq1 = MathUtil.randIndex(180);
		eq2 = MathUtil.randIndex(180);
	}
	
	public void updateDraw() {
		_texture.background(0);
		// if(AudioIn.isBeat()) {
		// 	log.update("BEAT");
		// 	log2.update("BEAT");
		// } else {
			log.update("freq["+eq1+"] = " + MathUtil.roundToPrecision(AudioIn.frequencies[eq1], 4));
			log.printToScreen(_texture, 20, 10);
			log2.update("freq["+eq2+"] = " + MathUtil.roundToPrecision(AudioIn.frequencies[eq2], 4));
			log2.printToScreen(_texture, 10 + width / 2, 10);
		// }
	}
	
}
