package com.haxademic.core.net;

import java.util.Iterator;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;

import processing.data.JSONObject;

public class JsonConfig {

	protected JSONObject jsonObj;
	
	public JsonConfig(String filePath) {
		this(filePath, false);
	}
	
	public JsonConfig(String filePath, boolean quitOnFailure) {
		// get config file. exit if not valid
		jsonObj = FileUtil.fileExists(filePath) ? 
				  JsonUtil.jsonFromFile(filePath) : 
				  null;
		if(quitOnFailure && jsonObj == null) {
			DebugUtil.alert("Error: config json is not valid. Exiting...");
			P.p.exit();
		}
	}
	
	public JSONObject json() {
		return jsonObj;
	}

	public void overrideConfigWithArgs(String[] arguments) {
		// loop through command line args and override any config.json keys that match
		for(String arg : arguments) {
			if(arg.indexOf("=") != -1) {
				// get key/val from arg
				String argKey = arg.split("=")[0];
				String argVal = arg.split("=")[1];
				
				// check datatypes and override in json object
				if(jsonObj.hasKey(argKey)) {
					// get datatype from json object, and override it
					JsonUtil.Type dataType = JsonUtil.getTypeForKey(jsonObj, argKey);
					switch (dataType) {
						case String:
							jsonObj.setString(argKey, argVal);
							break;
						case Number:
							jsonObj.setFloat(argKey, ConvertUtil.stringToFloat(argVal));
							break;
						case Boolean:
							jsonObj.setBoolean(argKey, ConvertUtil.stringToBoolean(argVal));
							break;
						case Unknown:
						default:
							break;
					}
				}
			}
		}
	}
	
	public void copyConfigToAppStore() {
		if(jsonObj == null) return;
		
		for (@SuppressWarnings("unchecked")
		Iterator<String> i = jsonObj.keys().iterator(); i.hasNext(); ) {
			// get json entry
			String key = i.next();
			Object val = jsonObj.get(key);
			if(jsonObj.isNull(key)) val = null;
			
			// check datatype & set proper values into AppStore
			JsonUtil.Type dataType = JsonUtil.getTypeForKey(jsonObj, key);
			switch (dataType) {
				case String:
					String strVal = (val != null) ? (String) val : null;
					P.store.setString(key, strVal); 
					break;
				case Number:
					P.store.setNumber(key, (Number) val); 
					break;
				case Boolean:
					P.store.setBoolean(key, (Boolean) val);
					break;
				case Unknown:
				default:
					break;
			}
		}
	}
}
