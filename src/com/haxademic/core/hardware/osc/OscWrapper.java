package com.haxademic.core.hardware.osc;

import java.util.Hashtable;

import com.haxademic.core.app.P;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class OscWrapper {
	
	protected OscP5 _oscP5;
	protected NetAddress _remoteLocation;
	
	protected Hashtable<String, Float> oscMsgMap;
	
	public static String MSG_COLOR = "/osc/color";
	public static String MSG_CAMERA = "/osc/camera";
	public static String MSG_MODE = "/osc/mode";
	public static String MSG_FOLLOW = "/osc/follow";
	public static String MSG_BLOCKSIZE = "/osc/blocksize";
	public static String MSG_LINES = "/osc/lines";
	
	public OscWrapper(PApplet pApp) {
		_oscP5 = new OscP5(this, 12000);
		_remoteLocation = new NetAddress("127.0.0.1",12000);
		initOscMessages();
	}
	
	protected void initOscMessages()
	{
		oscMsgMap = new Hashtable<String, Float>();
		oscMsgMap.put(MSG_COLOR, 0f);
		oscMsgMap.put(MSG_CAMERA, 0f);
		oscMsgMap.put(MSG_MODE, 0f);
		oscMsgMap.put(MSG_FOLLOW, 0f);
		oscMsgMap.put(MSG_BLOCKSIZE, 0f);
		oscMsgMap.put(MSG_LINES, 0f);
	}
	
	public void setOscMapItem( String oscMessage, float floatValue ) {
		oscMsgMap.put( oscMessage, floatValue );
	}
	
	public int oscMsgIsOn( String oscMessage ) {
//		P.print("check: "+oscMessage);
		if( oscMsgMap.containsKey( oscMessage ) ) {
			if( oscMsgMap.get( oscMessage ) > 0 ) {
				oscMsgMap.put( oscMessage, 0.0f );
				return 1;
			}
		}
		return 0; 
	}
	
	/**
	 * listener for incoming OSC data
	 */
	public void oscEvent(OscMessage theOscMessage) {
		float oscValue = theOscMessage.get(0).floatValue();
		String oscMsg = theOscMessage.addrPattern();
		// PAppletHax.println(oscMsg+": "+oscValue);
		setOscMapItem(oscMsg, oscValue);

		try {
			if( oscValue > 0 ) {
				P.p.handleInput( true );
			}
		}
		catch( ArrayIndexOutOfBoundsException e ){P.println("noteOn BROKE!");}
	}

}
