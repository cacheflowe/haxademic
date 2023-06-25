package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_FakeLightingFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage altMap;
	protected String ALT_MAP = "ALT_MAP";
	protected String FILTER_ACTIVE = "FILTER_ACTIVE";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}
	
	protected void firstFrame() {
		FakeLightingFilter.instance().buildUI("Demo", false);
		UI.addToggle(FILTER_ACTIVE, true, false);
		UI.addToggle(ALT_MAP, false, false);
	}

	protected void drawApp() {
		// lazy-init blurred map
		if(altMap == null) {
			int altMapW = DemoAssets.squareTexture().width;
			int altMapH = DemoAssets.squareTexture().height;
			altMap = ImageUtil.getScaledImage(DemoAssets.squareTexture(), altMapW, altMapH);
			PGraphics mapPG = PG.newPG(altMapW, altMapH);
			ImageUtil.copyImage(altMap, mapPG);
			BlurHFilter.instance().setBlurByPercent(1, altMapW);
			BlurVFilter.instance().setBlurByPercent(1, altMapH);
			BlurHFilter.instance().applyTo(mapPG);
			BlurVFilter.instance().applyTo(mapPG);
			altMap = mapPG;
			DebugView.setTexture("altMap", altMap);
		}
		
		// draw image to screen
		p.background(0);
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		
		// apply effect
		if(UI.valueToggle(FILTER_ACTIVE)) {
			if(UI.valueToggle(ALT_MAP)) {
				FakeLightingFilter.instance().setMap(altMap);
			} else {
				FakeLightingFilter.instance().setMap(p.g);
			}
			FakeLightingFilter.instance().setPropsFromUI("Demo");
			FakeLightingFilter.instance().applyTo(p.g);
		}
	}

}
