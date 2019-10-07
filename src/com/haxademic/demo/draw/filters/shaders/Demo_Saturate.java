package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.filters.pshader.SaturateHSVFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_Saturate
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void drawApp() {
		p.background(0);
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		
		// apply saturation methods
		//		if(p.mousePercentY() > 0.5f) {
		//			SaturationFilter.instance(p).setSaturation(p.mousePercentY() * 3f);
		//			SaturationFilter.instance(p).applyTo(p.g);
		//		} else {
		//			SaturateHSVFilter.instance(p).setSaturation(p.mousePercentY() * 3f);
		//			SaturateHSVFilter.instance(p).applyTo(p.g);
		//		}
		
		// use old saturation for < 1, and HSV saturation for > 1
		float saturate = p.mousePercentY() * 3f;
		if(saturate < 1f) {
			SaturationFilter.instance(p).setSaturation(saturate);
			SaturationFilter.instance(p).applyTo(p.g);
		} else {
			SaturateHSVFilter.instance(p).setSaturation(saturate);
			SaturateHSVFilter.instance(p).applyTo(p.g);
		}
	}

}
