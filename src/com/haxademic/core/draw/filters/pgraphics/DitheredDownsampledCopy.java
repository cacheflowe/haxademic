package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.DitherFilter;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

public class DitheredDownsampledCopy {
	
	protected PGraphics sourcePG;
	protected PGraphics destPG;
	protected int scaleDownDivisor;
	
	public DitheredDownsampledCopy(PGraphics sourcePG, int scaleDownDivisor) {
		this.sourcePG = sourcePG;
		this.scaleDownDivisor = scaleDownDivisor;
		buildDest();
	}
	
	public PGraphics image() {
		return destPG;
	}
	
	protected void buildDest() {
		// scaled-down buffer gets copied, dithered, then scaled back up
		// set low texture sampling for nearest-neighbor scaling on the way back up. this keeps the dither intact
		destPG = PG.newPG(sourcePG.width / scaleDownDivisor, sourcePG.height / scaleDownDivisor, true, false);
		((PGraphicsOpenGL)destPG).textureSampling(2);
	}
	
	public void update() {
		// downsample to small buffer 
		ImageUtil.copyImage(sourcePG, destPG);
		
		// apply dither
		DitherFilter.instance(P.p).setDitherMode8x8();
		DitherFilter.instance(P.p).applyTo(destPG);
	}	

}
