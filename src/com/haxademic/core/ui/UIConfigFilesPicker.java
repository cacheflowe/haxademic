package com.haxademic.core.ui;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.net.JsonUtil;

import processing.data.JSONObject;

public class UIConfigFilesPicker {
	
	protected String filesPath;
	protected String title;
	protected String sliderKey;
	protected String configFilesPath;
	protected JSONObject[] configs;
	protected int curIndex = 0;
	
	public UIConfigFilesPicker(String title, String sliderKey, String configFilesPath) {
		this.title = title;
		this.sliderKey = sliderKey;
		this.configFilesPath = configFilesPath;
		loadConfigFiles();
		buildUI();
		startUpdates();
	}
	
	protected void loadConfigFiles() {
		filesPath = configFilesPath;
		ArrayList<String> configFiles = FileUtil.getFilesInDirOfTypes(filesPath, "json");
		configs = new JSONObject[configFiles.size()];
		for (int i = 0; i < configFiles.size(); i++) {
			String jsonPath = configFiles.get(i);
			configs[i] = JsonUtil.jsonFromFile(jsonPath);
//			P.out("jsonPath:", jsonPath);
//			P.out("json obj:", configs[i].toString());
		}
	}
	
	protected void buildUI() {
		// maybe there are not config files
		if(configs.length == 0) {
			P.out("UIConfigFilesPicker Error: No config files found for "+title);
			return;
		}
		
		// build UI to attach configs to a slider
		UI.addTitle(title);
		UI.addSlider(sliderKey, 0, 0, configs.length - 1, 1);
		
		// make sure any saved value is stored for value changes
		// and load initial selection
		curIndex = UI.valueInt(sliderKey);
		UI.loadValuesFromJSON(configs[UI.valueInt(sliderKey)]);
	}
	
	protected void startUpdates() {
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public int numConfigs() {
		return configs.length;
	}
	
	public void loadConfigByIndex(int index) {
		UI.setValue(sliderKey, index);
	}
	
	public void pre() {
		// if no config files, bail
		if(configs.length == 0) return;
		
		// check for preset slider value change, and reload settings when that happens
		if(curIndex != UI.valueInt(sliderKey)) {
			curIndex = UI.valueInt(sliderKey);
			UI.loadValuesFromJSON(configs[curIndex]);
		}
	}
	
}