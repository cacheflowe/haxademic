package com.haxademic.core.hardware.http;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.shared.InputTrigger.InputState;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;

public class HttpInputState {

	protected WebServer server;
	public static boolean DEBUG = true;
	protected HashMap<String, Float> webControlValues;
	protected HashMap<String, InputState> webControlState;
	protected int lastUpdatedFrame = 0;
	
	// Singleton instance
	
	public static HttpInputState instance;
	
	public static HttpInputState instance() {
		if(instance != null) return instance;
		instance = new HttpInputState(defaultServer());
		return instance;
	}
	
	public static HttpInputState instance(WebServer server) {
		if(instance != null) return instance;
		instance = new HttpInputState(server);
		return instance;
	}
	
	public HttpInputState(WebServer server) {
		this.server = server;
		
		// local state storage
		webControlValues = new HashMap<String, Float>();
		webControlState = new HashMap<String, InputState>();
		
		// update!
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	public static WebServer defaultServer() {
		if(instance != null && instance.server != null) return instance.server;
		else return new WebServer(new UIControlsHandler(), true);
	}
	
	///////////////////////////////
	// PUBLIC INTERFACE
	///////////////////////////////

	public float getValue( String controlId ) {
		if( webControlValues.containsKey( controlId ) ) {
			return webControlValues.get(controlId);
		} else {
			return 0;
		}
	}
	
	public boolean isValueTriggered( String controlId ) {
		if( webControlState.containsKey( controlId ) ) {
			return webControlState.get(controlId) == InputState.TRIGGER;
		} else {
			return false;
		}
	}
	
	public boolean isValueOn( String controlId ) {
		if( webControlValues.containsKey( controlId ) ) {
			return webControlValues.get(controlId).floatValue() > 0;
		} else {
			return false;
		}
	}
	
	///////////////////////////////
	// INCOMING EVENT CALLBACK
	///////////////////////////////

	public void setControlValue(String controlId, float controlValue) {
		webControlValues.put(controlId, controlValue);
		InputState newState = (controlValue == 0) ? InputState.OFF : InputState.TRIGGER;
		webControlState.put(controlId, newState);
		lastUpdatedFrame = P.p.frameCount;
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void pre() {
		logValues();
	}
	
	public void post() {
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (String key : webControlState.keySet()) {
			if(webControlState.get(key) == InputState.TRIGGER) webControlState.put(key, InputState.ON);
		}
	}


	///////////////////////////////
	// DEBUG
	///////////////////////////////

	protected void logValues() {
		if(!DEBUG) return;
		// debug print values if debug window is showing
		if(DebugView.active()) {
			for (String key : webControlValues.keySet()) {
				DebugView.setValue("WebRequest ["+key+"]", webControlValues.get(key) + " | " + webControlState.get(key).name());
			}
		}
	}

}
