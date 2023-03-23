package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.Pixelate2Filter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

public class Demo_PImageShaderContained
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
	}

	protected void drawApp() {
		p.background(0);
		
		// draw bg image with saturation
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		SaturationFilter.instance().setSaturation(FrameLoop.osc(0.02f, 0, 2));
		SaturationFilter.instance().applyTo(p.g);
		
		// draw PImage with shader
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
	    Pixelate2Filter.instance().setDivider(FrameLoop.osc(0.1f, 20, 1));
	    Pixelate2Filter.instance().setOnContext(p);
		p.image(DemoAssets.smallTexture(), 0, 0);
		Pixelate2Filter.instance().resetContext(p);
	}

}
