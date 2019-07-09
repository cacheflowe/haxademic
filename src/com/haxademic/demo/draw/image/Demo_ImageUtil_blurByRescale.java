package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_ImageUtil_blurByRescale 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		// set up context
		p.background(100);
		p.noStroke();

		// NOTE: this works on p.g, but does weird alpha stuff on a PGraphics
		// draw steps/results to screen
		pg.beginDraw();
		pg.fill(255);
//		pg.image(DemoAssets.textureJupiter(), 0, 0);
		ImageUtil.drawImageCropFill(DemoAssets.textureJupiter(), pg, true);
		pg.endDraw();
		ImageUtil.blurByRescale(pg, p.mousePercentX());

		// extra blur to smooth edges
		BlurHFilter.instance(p).setBlurByPercent(p.mousePercentY() * 2f, pg.width);
		BlurHFilter.instance(p).applyTo(pg);
		BlurVFilter.instance(p).setBlurByPercent(p.mousePercentY() * 2f, pg.height);
		BlurVFilter.instance(p).applyTo(pg);
		
		p.image(pg, 0, 0);
	}
}
