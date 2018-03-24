package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.GradientCoverWipe;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;

public class Demo_GradientCoverWipe
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		// draw image
		ImageUtil.cropFillCopyImage(DemoAssets.justin(), p.g, true);
		
		// apply blur
//		GradientCoverWipe.instance(p).set(mousePercent, img.width);
		GradientCoverWipe.instance(p).setColorTop(1f, 0f, 1f, 1f);
		GradientCoverWipe.instance(p).setColorBot(0f, 1f, 1f, 1f);
		GradientCoverWipe.instance(p).setProgress(p.mousePercentX());
		GradientCoverWipe.instance(p).setProgress(p.frameCount * 0.01f % 1f);
		GradientCoverWipe.instance(p).applyTo(p);
	}

}
