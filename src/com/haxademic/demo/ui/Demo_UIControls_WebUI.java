package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UIButton;

public class Demo_UIControls_WebUI 
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
		p.ui.addSlider(R, 255, 0, 255, 0.5f);
		p.ui.addSlider(G, 255, 0, 255, 0.5f);
		p.ui.addSlider(B, 255, 0, 255, 0.5f);
		p.ui.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false);
		p.ui.addButton("Button", false);
		p.ui.addButton("Button 2", true);
		p.ui.addButtons(new String[] {"1", "2", "3", "4"}, true);
		for (int i = 0; i < 30; i++) p.ui.addSlider("Test slider " + i, 255, 0, 255, 0.5f, false);
		p.ui.addWebInterface(false);
		P.out(p.ui.configToJSON());
		P.out(p.ui.valuesToJSON());
	}
	
	public void drawApp() {
		if(p.frameCount == 200) SystemUtil.openWebPage(WebServer.getServerAddress() + "ui/");

		// bg components
		p.background(
			p.ui.value(R),
			p.ui.value(G),
			p.ui.value(B)
		);
		
		// 3d rotation
		p.lights();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.rotateX(p.ui.value(VECTOR_3 + "_X"));
		p.rotateY(p.ui.value(VECTOR_3 + "_Y"));
		p.rotateZ(p.ui.value(VECTOR_3 + "_Z"));
		p.fill(255);
		p.stroke(0);
		p.box(200);
	}
	
	public void uiButtonClicked(UIButton button) {
		P.out(button.id(), button.value());
	}
}
