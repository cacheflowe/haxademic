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
//		ContrastFilter.instance().setContrast(1.4f);
//		ContrastFilter.instance().applyTo(p);
		HalftoneCamoFilter.instance().setTime(P.PI + osc);
		HalftoneCamoFilter.instance().setScale(1.5f + 0.5f * osc);
		HalftoneCamoFilter.instance().applyTo(destBuffer);
	}
}
