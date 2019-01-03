package com.haxademic.core.net;

import com.haxademic.core.app.P;

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
}
