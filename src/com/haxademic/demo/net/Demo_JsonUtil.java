package com.haxademic.demo.net;

import java.util.Iterator;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.net.HttpRequest;
import com.haxademic.core.net.JsonUtil;

import processing.data.JSONObject;

public class Demo_JsonUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected HttpRequest request;
	
	protected void firstFrame() {
		// create json object
		String json = "{" + 
				"			\"sessionMaxLength\": 120000," + 
				"			\"sessionWarningTime\": 10000," + 
				"			\"widgetQrLogo\": null," + 
				"			\"widgetScale\": 0.8," + 
				"			\"widgetLocationX\": 1760," + 
				"			\"widgetLocationY\": 880," + 
				"			\"pointerClickEvent\": \"click\"," + 
				"			\"pointerColor\": \"#5D47FF\"," + 
				"			\"touchpadHeaderLogo\": null," + 
				"			\"touchpadKeepInstructions\": true," + 
				"			\"touchpadHideClickButton\": false," + 
				"		}";
		JSONObject jsonObj = JsonUtil.jsonFromString(json);
		
		// loop through values
		for (@SuppressWarnings("unchecked")
		Iterator<String> i = jsonObj.keys().iterator(); i.hasNext(); ) {
			// get json entry
			String key = i.next();
			Object val = jsonObj.get(key);
			// boolean isNull = jsonObj.isNull(key);
			
			// check datatype & set proper values into AppStore
			JsonUtil.Type dataType = JsonUtil.getTypeForKey(jsonObj, key);
			P.out("[" + dataType + "]", key, " = ", val.toString());
		}
		
		// unescape escaped json
		String escapedJson = "\"{\\\"schema\\\":{\\\"fields\\\":[{\\\"name\\\":\\\"index\\\",\\\"type\\\":\\\"integer\\\"},{\\\"name\\\":\\\"ranked_colors\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"sentiment\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"word\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"x\\\",\\\"type\\\":\\\"number\\\"},{\\\"name\\\":\\\"y\\\",\\\"type\\\":\\\"number\\\"},{\\\"name\\\":\\\"z\\\",\\\"type\\\":\\\"number\\\"},{\\\"name\\\":\\\"time\\\",\\\"type\\\":\\\"number\\\"}],\\\"primaryKey\\\":[\\\"index\\\"],\\\"pandas_version\\\":\\\"0.20.0\\\"},\\\"data\\\":[]}\"";
		String unescapedJson = JsonUtil.unescape(escapedJson, true);
		P.out("");
		P.out("");
		P.out(JsonUtil.formatJsonString(unescapedJson));
		
		p.exit();
	}

	protected void drawApp() {
		
	}
	
}
