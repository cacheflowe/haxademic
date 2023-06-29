package com.haxademic.core.app.config;

import java.io.IOException;
import java.util.Properties;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;

/**
 * Helper object to extend the standard java.util.Properties 
 * class to return pre-typed data from .properties files
 */
public class Config {
	protected static Properties properties;
	
	/////////////////////////
	// Static PAppletHax `arguments` helpers
	/////////////////////////
	
	public static void printArgs() {
		String[] arguments = PAppletHax.arguments; 
		if(arguments == null || arguments.length == 0) return;
		// print command line arguments
		P.outInitLineBreak();
		P.outInit("main() args:");
		for (String string : arguments) {
			P.outInit("- " + string);
		}
		P.outInitLineBreak();
	}
	
	public static String getArgValue(String arg) {
		return getArgValue(arg, null);
	}
	
	public static String getArgValue(String arg, String defaultVal) {
		String[] arguments = PAppletHax.arguments; 
		for (String string : arguments) {
			if(string.indexOf(arg+"=") != -1) {
				return string.split("=")[1];
			}
		}
		return defaultVal;
	}
	
	/////////////////////////
	// Singleton instance
	/////////////////////////
	
	public static Config instance;
	
	public static Config instance() {
		if(instance != null) return instance;
		instance = new Config();
		return instance;
	}

	public Config() {
		properties = new Properties();
		loadDefaultPropsFile();
	}
	
	/////////////////////////
	// load .properties files
	/////////////////////////
	
	protected void loadDefaultPropsFile() {
		String defaultPropsFile = P.path("properties/run.properties");
		if(FileUtil.fileExists(defaultPropsFile)) {
			loadPropertiesFile(defaultPropsFile);
		}
	}
	
	public static void loadPropertiesFile(String file) {
		try {
			properties.load(P.p.createInput(file));
		} catch(IOException e) {
			DebugUtil.printErr("couldn't read " + file + " config file...");
		}
	}
 
	/////////////////////////
	// Data type getters/setters
	/////////////////////////
	
	// string helpers
	
	public static Object setProperty(String id, String state) {
		return properties.setProperty(id, state);
	}
	
	public static String getString(String id, String defState) {
		return properties.getProperty(id, defState);
	}
 
	// boolean helpers
	
	public static Object setProperty(String id, boolean state) {
		return properties.setProperty(id, ""+state);
	}
 
	public static boolean getBoolean(String id, boolean defState) {
		return Boolean.parseBoolean(properties.getProperty(id,""+defState));
	}
	
	// int helpers
	
	public static Object setProperty(String id, int val) {
		return properties.setProperty(id, ""+val);
	}
	
	public static int getInt(String id, int defVal) {
		return Integer.parseInt(properties.getProperty(id,""+defVal));
	}
 
	// float helpers
	
	public static Object setProperty(String id, float val) {
		return properties.setProperty(id, ""+val);
	}
	
	public static float getFloat(String id, float defVal) {
		return ConvertUtil.stringToFloat(properties.getProperty(id,""+defVal));
  	} 
	
	/////////////////////////
	// Special helper setters
	/////////////////////////
	
	public static void setAppSize(float w, float h) {
		Config.setProperty(AppSettings.WIDTH, P.round(w));
		Config.setProperty(AppSettings.HEIGHT, P.round(h));
	}
	
	public static void setPgSize(int w, int h) {
		Config.setProperty(AppSettings.PG_WIDTH, w);
		Config.setProperty(AppSettings.PG_HEIGHT, h);
	}
}