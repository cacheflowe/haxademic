package com.haxademic.core.hardware.osc;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.shared.InputState;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscProperties;

public class OscState {
	
	protected OscP5 oscP5;
	protected NetAddress _remoteLocation;
	
	protected HashMap<String, Float> oscMsgValues;
	protected HashMap<String, InputState> oscMsgState;
	protected int lastUpdatedFrame = 0;

	// Singleton instance
	
	public static OscState instance;
	
	public static OscState instance() {
		if(instance != null) return instance;
		instance = new OscState();
		return instance;
	}
	
	public OscState() {
		// hardware interface
		OscProperties properties = new OscProperties();
		properties.setNetworkProtocol(OscProperties.MULTICAST);
//		properties.setRemoteAddress("224.0.1.0", 7777);			// this multicast address didn't work
//		properties.setRemoteAddress("239.0.0.1", 7777);			// this multicast address also didn't work w/parallel
		properties.setRemoteAddress("255.255.255.255", 7777);	// this multicast address works!!
//		properties.setRemoteAddress("192.168.1.51", 7777);		// direct to receiver works!
//		_remoteLocation = new NetAddress("127.0.0.1",12000);
		P.out(properties.toString());
		oscP5 = new OscP5(this, properties);    

		// local state storage
		oscMsgValues = new HashMap<String, Float>();
		oscMsgState = new HashMap<String, InputState>();
		
		// update!
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}
	
	public OscP5 oscP5() {
		return oscP5;
	}
	
	///////////////////////////////
	// PUBLIC INTERFACE
	///////////////////////////////

	public float getValue( String oscMessage ) {
		if( oscMsgValues.containsKey( oscMessage ) ) {
			return oscMsgValues.get(oscMessage);
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
		if( oscMsgValues.containsKey( oscMessage ) ) {
			return oscMsgValues.get(oscMessage).floatValue() > 0;
		} else {
			return false;
		}
	}
	
	///////////////////////////////
	// INCOMING EVENT CALLBACK
	///////////////////////////////

	public void oscEvent(OscMessage theOscMessage) {
		if(theOscMessage.typetag().equals("f")) {
			float oscValue = theOscMessage.get(0).floatValue();
			String oscMsg = theOscMessage.addrPattern();
			oscMsgValues.put(oscMsg, oscValue);
			InputState newState = (oscValue == 0) ? InputState.OFF : InputState.TRIGGER;
			oscMsgState.put(oscMsg, newState);
			lastUpdatedFrame = P.p.frameCount;
		}
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void pre() {
		logValues();
	}
	
	public void post() {
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (String key : oscMsgState.keySet()) {
			if(oscMsgState.get(key) == InputState.TRIGGER) oscMsgState.put(key, InputState.ON);
		}
	}

	///////////////////////////////
	// DEBUG
	///////////////////////////////

	protected void logValues() {
		// debug print values if debug window is showing
		if(DebugView.active()) {
			for (String key : oscMsgValues.keySet()) {
				DebugView.setValue("OSC ["+key+"]", oscMsgValues.get(key) + " | " + oscMsgState.get(key).name());
			}
		}
	}

}
