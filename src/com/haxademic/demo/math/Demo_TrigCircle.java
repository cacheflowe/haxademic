package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.ui.UI;

public class Demo_TrigCircle
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String radians = "radians";
	protected String radius = "radius";
	
	protected float _x = 0;
	protected float _y = 0;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	public void firstFrame() {
		UI.addSlider(radians, 0, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(radius, 0, 0, 300, 1f, false);
	}

	public void drawApp() {
		background(0);
		PG.setDrawCenter(p);

		_x = p.width / 2 + P.cos(UI.value(radians)) * UI.value(radius);
		_y = p.height / 2 + P.sin(UI.value(radians)) * UI.value(radius);
		
		p.fill(255);
		p.ellipse(_x, _y, 40, 40);
	}

}
