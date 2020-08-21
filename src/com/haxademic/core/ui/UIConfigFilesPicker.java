package com.haxademic.core.ui;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.net.JsonUtil;

import processing.data.JSONObject;

public class UIConfigFilesPicker {
	
	protected String filesPath;
	protected String title;
	protected String sliderKey;
	protected String configFilesPath;
	protected ArrayList<String> configFilePaths;
	protected JSONObject[] configs;
	protected int curIndex = 0;
	public static boolean DEBUG = false;
	
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
		configFilePaths = FileUtil.getFilesInDirOfTypes(filesPath, "json");
		configs = new JSONObject[configFilePaths.size()];
		for (int i = 0; i < configFilePaths.size(); i++) {
			String jsonPath = configFilePaths.get(i);
			configs[i] = JsonUtil.jsonFromFile(jsonPath);
			if(DEBUG == true) {
				P.out("loadConfigFiles ["+i+"] ===============================");
				P.out("jsonPath:", jsonPath);
				P.out("json obj:", configs[i].toString());
				P.out("===================================================");
			}
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
	
	public int curIndex() {
		return curIndex;
	}
	
	public String curConfigPath() {
		return configFilePaths.get(curIndex);
	}
	
	public int numConfigs() {
		return configs.length;
	}

	public void goNextConfig() {
		int nextIndex = (curIndex + 1) % numConfigs();
		UI.setValue(sliderKey, nextIndex);
	}
	
	public void goPrevConfig() {
		int prevIndex = curIndex - 1;
		if(prevIndex < 0) prevIndex = numConfigs() - 1;
		UI.setValue(sliderKey, prevIndex);
	}
	
	public void goRandomConfig() {
		UI.setValue(sliderKey, MathUtil.randIndex(numConfigs()));
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
			if(DEBUG == true) P.out("UIConfigFilesPicker :: new config index:", "[" + curIndex + "]", configFilePaths.get(curIndex));
			UI.loadValuesFromJSON(configs[curIndex]);
		}
	}
	
}