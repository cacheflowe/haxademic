package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.textures.PerlinTexture;
import com.haxademic.core.ui.UI;

public class PerlinTextureTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PerlinTexture _perlinTexture;
	
	protected String zoom = "zoom";
	protected String detail = "detail";
	protected String xProgress = "xProgress";
	protected String yProgress = "yProgress";

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_UI, true);
	}

	public void setupFirstFrame() {
		UI.addSlider(zoom, 0.01f, 0.001f, 0.1f, 0.0001f, false);
		UI.addSlider(detail, 0.1f, 0.0001f, 0.3f, 0.001f, false);
		UI.addSlider(xProgress, 0, 0, 10, 0.01f, false);
		UI.addSlider(yProgress, 0, 0, 10, 0.01f, false);

		_perlinTexture = new PerlinTexture(this, 200, 200);
	}

	public void drawApp() {
		p.background(0);
		_perlinTexture.update(UI.value(zoom), UI.value(detail), UI.value(xProgress), UI.value(yProgress));
		p.image(_perlinTexture.texture(), p.width - _perlinTexture.texture().width, 0);
	}
}
