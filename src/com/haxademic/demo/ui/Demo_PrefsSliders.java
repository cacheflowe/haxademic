package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class Demo_PrefsSliders 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}
	
	public void setupFirstFrame () {
		p.prefsSliders.addSlider(R, 255, 0, 255, 0.5f);
		p.prefsSliders.addSlider(G, 255, 0, 255, 0.5f);
		p.prefsSliders.addSlider(B, 255, 0, 255, 0.5f);
		P.out(p.prefsSliders.toJSON());
	}
	
	public void drawApp() {
		p.background(
			p.prefsSliders.value(R),
			p.prefsSliders.value(G),
			p.prefsSliders.value(B)
		);
	}
	
}
