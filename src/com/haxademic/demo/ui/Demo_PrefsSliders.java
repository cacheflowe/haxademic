package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;

public class Demo_PrefsSliders 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	
	protected String VECTOR_3 = "VECTOR_3";
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_SLIDERS, true);
	}
	
	public void setupFirstFrame () {
		p.prefsSliders.addSlider(R, 255, 0, 255, 0.5f);
		p.prefsSliders.addSlider(G, 255, 0, 255, 0.5f);
		p.prefsSliders.addSlider(B, 255, 0, 255, 0.5f);
		p.prefsSliders.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false);
		P.out(p.prefsSliders.toJSON());
	}
	
	public void drawApp() {
		// bg components
		p.background(
			p.prefsSliders.value(R),
			p.prefsSliders.value(G),
			p.prefsSliders.value(B)
		);
		
		// 3d rotation
		p.lights();
		DrawUtil.setCenterScreen(p.g);
		DrawUtil.setDrawCenter(p.g);
		p.rotateX(p.prefsSliders.value(VECTOR_3 + "_X"));
		p.rotateY(p.prefsSliders.value(VECTOR_3 + "_Y"));
		p.rotateZ(p.prefsSliders.value(VECTOR_3 + "_Z"));
		p.fill(255);
		p.stroke(0);
		p.box(100);
	}
	
}
