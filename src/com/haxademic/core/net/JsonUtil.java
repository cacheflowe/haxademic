package com.haxademic.core.net;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;

import processing.data.JSONObject;

public class JsonUtil {

	public static String jsonToSingleLine(JSONObject jsonObj) {
		return jsonObj.toString().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}
	
	public static String jsonToSingleLine(String jsonString) {
		return jsonString.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}
	
	public static boolean isValid(String jsonString) {
		JSONObject jsonData = jsonFromString(jsonString);
		return (jsonData != null);
	}
	
	public static JSONObject jsonFromString(String jsonStr) {
		try {
			return JSONObject.parse(jsonStr);
		} catch (Exception e) {
			P.out("JSON.parse() failed"); 
			return null;
		}
	}
	
	public static JSONObject jsonFromFile(String jsonPath) {
		String[] fileLines = FileUtil.readTextFromFile(jsonPath);
		String jsonStr = String.join("\n", fileLines);
		return jsonFromString(jsonStr);
	}

	public static void jsonToFile(JSONObject jsonData, String path) {
		FileUtil.writeTextToFile(path, jsonData.toString());
	}
	
	public enum Type {
		String,
		Number, 
		Boolean, 
		Unknown
	}
	
	public static Type getTypeForKey(JSONObject jsonData, String key) {
		Object val = jsonData.get(key);
		boolean isNull = jsonData.isNull(key);
		// check datatype & set proper values into AppStore
		if(val instanceof String) {
	    	return Type.String;
	    } else if (val instanceof Integer || val instanceof Long) {
	    	return Type.Number;
	    } else if(val instanceof Float || val instanceof Double) {
	    	return Type.Number;
	    } else if(val instanceof Boolean) {
	    	return Type.Boolean;
	    } else if(isNull) {
	    	return Type.String;
	    } else {
	    	return Type.Unknown;
	    }

	}
	
}
