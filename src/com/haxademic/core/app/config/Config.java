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
 * class to return pre-typed data from .properties files. 
 * Also merges in command line arguments on launch, and is also
 * commonly overridden by an app's own config() function on launch.
 * Steps on launch (see in Demo_Config.java):
 * - Load run.properties file into Properties object
 * - Optionally load a custom properties file into Properties object
 * - Parse command line arguments & save into Properties object
 * - Manually set any further properties in PAppletHax's config() function. See AppSettings for common keys
 */
public class Config {
	protected static Properties properties;
	
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
		storeCommandLineArgs();
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
	// Static PAppletHax `arguments` helpers
	/////////////////////////
	
	public static void printArgs() {
		String[] arguments = PAppletHax.arguments; 
		if(arguments == null || arguments.length == 0) return;
		// print command line arguments
		P.outInitLineBreak();
		P.outInit("main() command-line arguments:");
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

	public void storeCommandLineArgs() {
		String[] arguments = PAppletHax.arguments;
		if(arguments == null) {
			P.error("PAppletHax.arguments is null");
			P.error("Add: public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }");
		}
		for (String string : arguments) {
			if(string.indexOf("=") != -1) {
				String[] parts = string.split("=");
				String key = parts[0];
				String val = parts[1];
				properties.put(key, val);
			}
		}
	}

	/////////////////////////
	// Print collection
	/////////////////////////

	public static void printProperties() {
		P.outInitLineBreak();
		P.outInit("Config properties:");
		for (Object key : properties.keySet()) {
			P.outInit("- " + key + ": " + properties.get(key));
		}
		P.outInitLineBreak();
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
	
	public static void setAppLocation(float x, float y) {
		Config.setProperty(AppSettings.SCREEN_X, P.round(x));
		Config.setProperty(AppSettings.SCREEN_Y, P.round(y));
	}
	
	public static void setPgSize(int w, int h) {
		Config.setProperty(AppSettings.PG_WIDTH, w);
		Config.setProperty(AppSettings.PG_HEIGHT, h);
	}
}