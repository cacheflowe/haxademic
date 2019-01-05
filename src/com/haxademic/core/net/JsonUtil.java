package com.haxademic.core.net;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.data.JSONObject;

public class JsonUtil {

	public static String jsonToSingleLine(JSONObject jsonObj) {
		return jsonObj.toString().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}
	
	public static boolean isValid(String jsonString) {
		try {
			@SuppressWarnings("unused")
			JSONObject jsonData = JSONObject.parse(jsonString);
			return true;
		} catch (Exception e) {
			P.out("JSON.parse() failed"); 
			return false;
		}
	}
	
	public static JSONObject jsonFromFile(String jsonPath) {
		String[] fileLines = FileUtil.readTextFromFile(jsonPath);
		String jsonStr = String.join("\n", fileLines);
		try {
			return JSONObject.parse(jsonStr);
		} catch (Exception e) {
			P.out("JSON.parse() failed"); 
			return null;
		}
	}

}
