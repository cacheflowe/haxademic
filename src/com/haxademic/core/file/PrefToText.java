package com.haxademic.core.file;

import java.io.File;

import com.haxademic.core.data.ConvertUtil;

public class PrefToText {

	// common 
	
	public static String getFilePath(String key) {
		FileUtil.createDir(FileUtil.getPath("text" + File.separator + "prefs"));
		return FileUtil.getPath("text" + File.separator + "prefs" + File.separator + key + ".txt");
	}
	
	// setters
	
	public static String setValue(String key, String value) {
		FileUtil.writeTextToFile(getFilePath(key), value);
		return value;
	}
	
	public static float setValue(String key, float value) {
		FileUtil.writeTextToFile(getFilePath(key), Float.toString(value));
		return value;
	}
	
	public static int setValue(String key, int value) {
		FileUtil.writeTextToFile(getFilePath(key), Integer.toString(value));
		return value;
	}
	
	// getters (write default if file doesn't exist)

	public static String getValueS(String key, String defaultVal) {
		String[] lines = FileUtil.readTextFromFile(getFilePath(key));
		return (lines == null || lines.length == 0) ? setValue(key, defaultVal) : lines[0];
	}
	
	public static float getValueF(String key, float defaultVal) {
		String[] lines = FileUtil.readTextFromFile(getFilePath(key));
		return (lines == null) ? setValue(key, defaultVal) : ConvertUtil.stringToFloat(lines[0]);
	}
	
	public static int getValueI(String key, int defaultVal) {
		String[] lines = FileUtil.readTextFromFile(getFilePath(key));
		return (lines == null) ? setValue(key, defaultVal) : ConvertUtil.stringToInt(lines[0]);
	}
}
