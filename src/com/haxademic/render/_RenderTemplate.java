package com.haxademic.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;

public class _RenderTemplate
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int FRAMES = 180;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
		Config.setProperty(AppSettings.WIDTH, 960);
		Config.setProperty(AppSettings.HEIGHT, 960);
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}

	protected void firstFrame() {
	}

	protected void drawApp() {
		// set up context
		background(0);
		PG.setDrawCenter(p);
		p.fill(255);
		p.noStroke();
	}
}
