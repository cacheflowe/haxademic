package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneCamoFilter;
import com.haxademic.core.draw.image.ImageUtil;

public class HalftoneCamo
extends BaseVideoFilter {
	
	public HalftoneCamo( int width, int height ) {
		super(width, height);
	}
	
	public void update() {
		ImageUtil.copyImage(sourceBuffer, destBuffer);
		float osc = P.sin(P.p.frameCount * 0.01f);
//		ContrastFilter.instance(p).setContrast(1.4f);
//		ContrastFilter.instance(p).applyTo(p);
		HalftoneCamoFilter.instance(P.p).setTime(P.PI + osc);
		HalftoneCamoFilter.instance(P.p).setScale(1.5f + 0.5f * osc);
		HalftoneCamoFilter.instance(P.p).applyTo(destBuffer);
	}
}
