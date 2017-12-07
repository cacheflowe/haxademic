package com.haxademic.core.hardware.osc;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.InputState;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

public class OscWrapper {
	
	protected OscP5 _oscP5;
	protected NetAddress _remoteLocation;
	
	protected HashMap<String, Float> oscMsgMap;
	protected HashMap<String, InputState> oscMsgState;
	
	public OscWrapper() {
		_oscP5 = new OscP5(this, 12000);
		_remoteLocation = new NetAddress("127.0.0.1",12000);
		oscMsgMap = new HashMap<String, Float>();
		oscMsgState = new HashMap<String, InputState>();
	}
	
	///////////////////////////////
	// PUBLIC INTERFACE
	///////////////////////////////

	public float getValue( String oscMessage ) {
		if( oscMsgMap.containsKey( oscMessage ) ) {
			return oscMsgMap.get(oscMessage);
		} else {
			return 0;
		}
	}
	
	public boolean isValueTriggered( String oscMessage ) {
		if( oscMsgState.containsKey( oscMessage ) ) {
			return oscMsgState.get(oscMessage) == InputState.TRIGGER;
		} else {
			return false;
		}
	}
	
	public boolean isValueOn( String oscMessage ) {
		if( oscMsgMap.containsKey( oscMessage ) ) {
			return oscMsgMap.get(oscMessage).floatValue() > 0;
		} else {
			return false;
		}
	}
	
	///////////////////////////////
	// INCOMING EVENT CALLBACK
	///////////////////////////////

	public void oscEvent(OscMessage theOscMessage) {
		float oscValue = theOscMessage.get(0).floatValue();
		String oscMsg = theOscMessage.addrPattern();
		if(P.p.showDebug) P.println(oscMsg+": "+oscValue);
		oscMsgMap.put(oscMsg, oscValue);
		InputState newState = (oscValue == 0) ? InputState.OFF : InputState.TRIGGER;
		oscMsgState.put(oscMsg, newState);
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void update() {
		for (String key : oscMsgState.keySet()) {
			if(oscMsgState.get(key) == InputState.TRIGGER) oscMsgState.put(key, InputState.ON);
		}
	}

	///////////////////////////////
	// DEBUG
	///////////////////////////////

	public void printButtons() {
		P.p.noStroke();
		P.p.fill(255);
		String debugStr = "";
		for (String key : oscMsgMap.keySet()) {
			if(oscMsgState.get(key) == InputState.TRIGGER) {
				debugStr += key + ": TRIGGER \n";
			} else {
				debugStr += key + ": " + oscMsgMap.get(key) + "\n";
			}
		}
		P.p.text(debugStr, 420, 20, P.p.width - 40, P.p.height - 40);
	}


}
