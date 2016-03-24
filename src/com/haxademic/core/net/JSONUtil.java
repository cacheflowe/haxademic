package com.haxademic.core.net;

import processing.data.JSONObject;

public class JSONUtil {

	public static String jsonToSingleLine(JSONObject jsonObj) {
		return jsonObj.toString().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
	}
	
}
