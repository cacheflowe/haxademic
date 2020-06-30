package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.net.JsonConfig;

public class Demo_JsonConfig
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// run with args like this to override with CLI args:
	// numericOption=1.75
	
	protected JsonConfig jsonConfig;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		jsonConfig = new JsonConfig(FileUtil.getPath("text/json/config.json"), true);
		jsonConfig.json().setString("manualAddition", "Another value!");
		jsonConfig.overrideConfigWithArgs(arguments);
		jsonConfig.copyConfigToAppStore();
	}
	
	protected void drawApp() {
		background(0);
		P.store.showStoreValuesInDebugView();
	}
	
}
