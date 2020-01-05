package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

public class Demo_UI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	protected String AUTO_ON = "AUTO_ON";
	
	protected String VECTOR_3 = "VECTOR_3";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame () {
		UI.addTitle("Color");
		UI.addSlider(R, 255, 0, 255, 0.5f);
		UI.addSlider(G, 255, 0, 255, 0.5f);
		UI.addSlider(B, 255, 0, 255, 0.5f);
		UI.addTitle("Rotation");
		UI.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false);
		UI.addTitle("Test buttons");
		UI.addButton("Button", false);
		UI.addButton("Button 2", true);
		UI.addButtons(new String[] {"1", "2", "3", "4"}, true);
		UI.addToggle(AUTO_ON, false, false);
		P.out(UI.configToJSON());
		P.out(UI.valuesToJSON());
	}
	
	protected void drawApp() {
		// test setting of components
		if(UI.valueToggle(AUTO_ON) == true) {
			// override slider
			UI.setValue(R, 127 + 127f * P.sin(p.frameCount * 0.04f));
			// set a button's value
			if(p.frameCount % 200 == 0) UI.get("1").set(1);
		}
		
		// bg components
		p.background(
			UI.value(R),
			UI.value(G),
			UI.value(B)
		);
		
		// 3d rotation
		p.lights();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.rotateX(UI.valueX(VECTOR_3));
		p.rotateY(UI.valueY(VECTOR_3));
		p.rotateZ(UI.valueZ(VECTOR_3));
		p.fill(255);
		p.stroke(0);
		p.box(100);
	}
	
	public void uiButtonClicked(UIButton button) {
		P.out(button.id(), button.value());
	}
}
