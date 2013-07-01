package com.haxademic.core.hardware.osc;

import java.util.Hashtable;

import netP5.NetAddress;
import oscP5.OscP5;
import processing.core.PApplet;

import com.haxademic.core.app.P;

public class OscWrapper {
	
	protected PApplet p;
	protected OscP5 _oscP5;
	protected NetAddress _remoteLocation;
	
	protected Hashtable<String, Integer> oscMsgMap;
	
	public static String MSG_COLOR = "/osc/color";
	public static String MSG_CAMERA = "/osc/camera";
	public static String MSG_MODE = "/osc/mode";
	public static String MSG_FOLLOW = "/osc/follow";
	public static String MSG_BLOCKSIZE = "/osc/blocksize";
	public static String MSG_LINES = "/osc/lines";
	
	public OscWrapper(PApplet pApp) {
		p = pApp;

		init();
	}
	
	public void init() {
		/* start oscP5, listening for incoming messages at port 12000 */
		_oscP5 = new OscP5(p,12000);
		_remoteLocation = new NetAddress("127.0.0.1",12000);
		
		initOscMessages();
	}
	
	protected void initOscMessages()
	{
		oscMsgMap = new Hashtable<String, Integer>();
		oscMsgMap.put(MSG_COLOR, 0);
		oscMsgMap.put(MSG_CAMERA, 0);
		oscMsgMap.put(MSG_MODE, 0);
		oscMsgMap.put(MSG_FOLLOW, 0);
		oscMsgMap.put(MSG_BLOCKSIZE, 0);
		oscMsgMap.put(MSG_LINES, 0);
	}
	
	public void setOscMapItem( String oscMessage, int intValue ) {
		if( oscMsgMap.containsKey( oscMessage ) ) {
			oscMsgMap.put( oscMessage, intValue );
		}
	}
	
	public int oscMsgIsOn( String oscMessage ) {
		P.print("check: "+oscMessage);
		if( oscMsgMap.containsKey( oscMessage ) ) {
			if( oscMsgMap.get( oscMessage ) == 1 ) {
				return 1;
			}
		}
		return 0; 
	}
}
