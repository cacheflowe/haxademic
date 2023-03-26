package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

public class Demo_RotateFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void drawApp() {
		if(p.frameCount == 1) PG.setTextureRepeat(p.g, true);
		p.background(0);
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		
		RotateFilter.instance().setRotation(Mouse.xNorm * 10f);
		RotateFilter.instance().setZoom(Mouse.yNorm * 10f);
		RotateFilter.instance().setOffset(Mouse.xNorm * 10f, Mouse.xNorm * 10f);
		RotateFilter.instance().applyTo(p.g);
	}

}
