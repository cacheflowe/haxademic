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

	protected String AMBIENT = "AMBIENT";
	protected String GRAD_AMP = "GRAD_AMP";
	protected String GRAD_BLUR = "GRAD_BLUR";
	protected String SPEC_AMP = "SPEC_AMP";
	protected String DIFF_DARK = "DIFF_DARK";
	protected String FILTER_ACTIVE = "FILTER_ACTIVE";
	
	protected PImage altMap;
	protected String ALT_MAP = "ALT_MAP";
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}
	
	protected void firstFrame() {
		UI.addSlider(AMBIENT, 2f, 0.3f, 6f, 0.01f, false);
		UI.addSlider(GRAD_AMP, 0.66f, 0f, 6f, 0.005f, false);
		UI.addSlider(GRAD_BLUR, 1f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f, false);
		UI.addSlider(DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f, false);

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
			BlurHFilter.instance(p).setBlurByPercent(1, altMapW);
			BlurVFilter.instance(p).setBlurByPercent(1, altMapH);
			BlurHFilter.instance(p).applyTo(mapPG);
			BlurVFilter.instance(p).applyTo(mapPG);
			altMap = mapPG;
			DebugView.setTexture("altMap", altMap);
		}
		
		// draw image to screen
		p.background(0);
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		
		// apply effect
		FakeLightingFilter.instance(p).setAmbient(UI.value(AMBIENT));
		FakeLightingFilter.instance(p).setGradAmp(UI.value(GRAD_AMP));
		FakeLightingFilter.instance(p).setGradBlur(UI.value(GRAD_BLUR));
		FakeLightingFilter.instance(p).setSpecAmp(UI.value(SPEC_AMP));
		FakeLightingFilter.instance(p).setDiffDark(UI.value(DIFF_DARK));
		if(UI.valueToggle(ALT_MAP)) {
			FakeLightingFilter.instance(p).setMap(altMap);
		} else {
			FakeLightingFilter.instance(p).setMap(p.g);
		}
		if(UI.valueToggle(FILTER_ACTIVE)) {
			FakeLightingFilter.instance(p).applyTo(p.g);
		}
	}

}
