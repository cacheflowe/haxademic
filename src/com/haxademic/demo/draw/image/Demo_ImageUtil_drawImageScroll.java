package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_ImageUtil_drawImageScroll
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setAppSize(512, 512);
	}

	protected void drawApp() {
		background(0);
		ImageUtil.drawImageScroll(DemoAssets.squareTexture(), p.g);
	}
}
