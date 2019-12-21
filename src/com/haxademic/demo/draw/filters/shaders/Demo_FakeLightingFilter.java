package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;

public class Demo_FakeLightingFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String AMBIENT = "AMBIENT";
	protected String GRAD_AMP = "GRAD_AMP";
	protected String GRAD_BLUR = "GRAD_BLUR";
	protected String SPEC_AMP = "SPEC_AMP";
	protected String DIFF_DARK = "DIFF_DARK";
	
	protected String FILTER_ACTIVE = "FILTER_ACTIVE";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SHOW_UI, true );
	}
	
	public void setupFirstFrame() {
		p.ui.addSlider(AMBIENT, 2f, 0.3f, 6f, 0.01f, false);
		p.ui.addSlider(GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f, false);
		p.ui.addSlider(GRAD_BLUR, 1f, 0.1f, 6f, 0.01f, false);
		p.ui.addSlider(SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f, false);
		p.ui.addSlider(DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f, false);

		p.ui.addSlider(FILTER_ACTIVE, 1f, 0f, 1f, 1f, false);
	}

	public void drawApp() {
		p.background(0);
		ImageUtil.drawImageCropFill(DemoAssets.squareTexture(), p.g, true);
		
		// apply effect
		FakeLightingFilter.instance(p).setAmbient(p.ui.value(AMBIENT));
		FakeLightingFilter.instance(p).setGradAmp(p.ui.value(GRAD_AMP));
		FakeLightingFilter.instance(p).setGradBlur(p.ui.value(GRAD_BLUR));
		FakeLightingFilter.instance(p).setSpecAmp(p.ui.value(SPEC_AMP));
		FakeLightingFilter.instance(p).setDiffDark(p.ui.value(DIFF_DARK));
		
		if(p.ui.value(FILTER_ACTIVE) > 0.5f) {
			FakeLightingFilter.instance(p).applyTo(p.g);
		}
	}

}
