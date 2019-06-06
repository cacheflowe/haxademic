package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.textures.PerlinTexture;

public class PerlinTextureTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture _perlinTexture;
	
	protected String zoom = "zoom";
	protected String detail = "detail";
	protected String xProgress = "xProgress";
	protected String yProgress = "yProgress";

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}

	public void setupFirstFrame() {
		p.ui.addSlider(zoom, 0.01f, 0.001f, 0.1f, 0.0001f, false);
		p.ui.addSlider(detail, 0.1f, 0.0001f, 0.3f, 0.001f, false);
		p.ui.addSlider(xProgress, 0, 0, 10, 0.01f, false);
		p.ui.addSlider(yProgress, 0, 0, 10, 0.01f, false);

		_perlinTexture = new PerlinTexture(this, 200, 200);
	}

	public void drawApp() {
		p.background(0);
		_perlinTexture.update(p.ui.value(zoom), p.ui.value(detail), p.ui.value(xProgress), p.ui.value(yProgress));
		p.image(_perlinTexture.texture(), p.width - _perlinTexture.texture().width, 0);
	}
}
