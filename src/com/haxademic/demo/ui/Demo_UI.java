package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIConfigFilesPicker;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_UI 
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "COLOR_R";
	protected String G = "COLOR_G";
	protected String B = "COLOR_B";
	protected String AUTO_ON = "AUTO_ON";
	protected String TEXT = "UI_TEXT";
	protected String VECTOR_3 = "VECTOR_3";
	
	protected UIConfigFilesPicker configPicker;
	protected String configsPath = "text/json/ui-configs";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame () {
		// build UI
		UI.addTitle("Color");
		UI.addSlider(R, 255, 0, 255, 0.5f, false);
		UI.addSlider(G, 255, 0, 255, 0.5f, false);
		UI.addSlider(B, 255, 0, 255, 0.5f, false);
		UI.setEasingFactor(R, 0.05f);
		UI.setEasingFactor(G, 0.05f);
		UI.setEasingFactor(B, 0.05f);
		UI.addTitle("Rotation");
		UI.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false);
		UI.setEasingFactor(VECTOR_3+"_X", 0.05f);
		UI.setEasingFactor(VECTOR_3+"_Y", 0.05f);
		UI.setEasingFactor(VECTOR_3+"_Z", 0.05f);
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
		
		// load config files picker
		UIConfigFilesPicker.DEBUG = true;
		configPicker = new UIConfigFilesPicker("UI Configs", "RGB_AND_ROT_CONFIG", FileUtil.getPath(configsPath));
		
		// subscribe to updates
		P.store.addListener(this);
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
			UI.valueEased(R),
			UI.valueEased(G),
			UI.valueEased(B)
		);
		
		// live editable text
		p.text(UI.valueString(TEXT), 20, p.height - 30);
		
		// 3d rotation
		p.lights();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.rotateX(UI.valueXEased(VECTOR_3));
		p.rotateY(UI.valueYEased(VECTOR_3));
		p.rotateZ(UI.valueZEased(VECTOR_3));
		p.fill(255);
		p.stroke(0);
		p.box(100);
	}
	
	protected void pickRandomValues() {
		UI.setRandomValue(R);
		UI.setRandomValue(G);
		UI.setRandomValue(B);
		UI.setRandomValue(VECTOR_3+"_X");
		UI.setRandomValue(VECTOR_3+"_Y");
		UI.setRandomValue(VECTOR_3+"_Z");
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == '1') P.out(UI.valuesToJSON(new String[] {"COLOR_", "VECTOR_"}));
		if(p.key == '2') UI.loadValuesFromJSON(JSONObject.parse(CONFIG_SAVED));
		if(p.key == '3') saveJsonFile();
		if(p.key == '4') loadJsonFile();
		if(p.key == '5') updateJsonFile();
		if(p.key == 'r') pickRandomValues();
		if(p.key == '8') configPicker.goPrevConfig();
		if(p.key == '9') configPicker.goNextConfig();
		if(p.key == '0') configPicker.goRandomConfig();
	}
	
	//////////////////////////
	// Saving & recalling json configs
	//////////////////////////
	
	protected String savedConfigFile() {
		return FileUtil.getPath(configsPath + "/config_1.json");
	}
	
	protected void saveJsonFile() {
		String jsonOutput = UI.valuesToJSON(new String[] {"COLOR_", "VECTOR_"});
		JsonUtil.jsonToFile(jsonOutput, FileUtil.getPath(configsPath + "/color_rot_" + SystemUtil.getTimestamp() + ".json"));
	}
	
	protected void updateJsonFile() {
		String jsonOutput = UI.valuesToJSON(new String[] {"COLOR_", "VECTOR_"});
		JsonUtil.jsonToFile(jsonOutput, configPicker.curConfigPath());
	}
	
	protected void loadJsonFile() {
		JSONObject jsonObj = JsonUtil.jsonFromFile(savedConfigFile());
		UI.loadValuesFromJSON(jsonObj);
	}
	
	protected String CONFIG_SAVED = "{\r\n" + 
		"	\"COLOR_R\": 117.0,\r\n" + 
		"	\"COLOR_G\": 58.5,\r\n" + 
		"	\"COLOR_B\": 174.0\r\n" +
	"}";
	
	//////////////////////////
	// IAppStoreListener updates
	//////////////////////////
	
	public void updatedNumber(String key, Number val) {
		if(key == R || key == G || key == B || key.equals("Button")) P.out(key, "=", val);
	}
	public void updatedString(String key, String val) {
		P.out(key, "=", val);
	}
	public void updatedBoolean(String key, Boolean val) {
	}	
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
