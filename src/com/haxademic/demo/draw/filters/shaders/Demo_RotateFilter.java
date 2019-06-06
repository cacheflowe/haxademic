package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_RotateFilter
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void drawApp() {
		if(p.frameCount == 1) DrawUtil.setTextureRepeat(p.g, true);
		p.background(0);
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		
		RotateFilter.instance(p).setRotation(p.mousePercentX() * 10f);
		RotateFilter.instance(p).setZoom(p.mousePercentY() * 10f);
		RotateFilter.instance(p).setOffset(p.mousePercentX() * 10f, p.mousePercentX() * 10f);
		RotateFilter.instance(p).applyTo(p.g);
	}

}
