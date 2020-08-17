package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

import processing.data.JSONObject;

public class Demo_UI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "COLOR_R";
	protected String G = "COLOR_G";
	protected String B = "COLOR_B";
	protected String AUTO_ON = "AUTO_ON";
	protected String TEXT = "UI_TEXT";
	
	protected String VECTOR_3 = "VECTOR_3";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame () {
		// build UI
		UI.addTitle("Color");
		UI.addSlider(R, 255, 0, 255, 0.5f, false);
		UI.addSlider(G, 255, 0, 255, 0.5f, false);
		UI.addSlider(B, 255, 0, 255, 0.5f, false);
		UI.setEasingFactor(B, 0.01f);
		UI.addTitle("Rotation");
		UI.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false);
		UI.addTitle("Test buttons");
		UI.addButton("Button", false);
		UI.addButton("Button 2", true);
		UI.addButtons(new String[] {"1", "2", "3", "4"}, true);
		UI.addToggle(AUTO_ON, false, false);
		UI.addTitle("Editable text");
		UI.addTextfield(TEXT, "Test String", true);
		
		// write out config to json
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
			if(p.frameCount % 200 == 100) UI.get("1").set(0);
			// set textfield
			if(p.frameCount % 200 == 0) UI.get(TEXT).set("AUTOMATIC!");
		}
		
		// bg components
		p.background(
			UI.value(R),
			UI.value(G),
			UI.valueEased(B)
		);
		
		// live editable text
		p.text(UI.valueString(TEXT), 20, p.height - 30);
		
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
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') P.out(UI.valuesToJSON(new String[] {"COLOR_"}));
		if(p.key == '2') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED));
	}
	
	protected String CONFIG_SAVED = "{\r\n" + 
		"	\"COLOR_R\": 117.0,\r\n" + 
		"	\"COLOR_G\": 58.5,\r\n" + 
		"	\"COLOR_B\": 174.0\r\n" +
	"}";
}
