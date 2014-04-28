package com.haxademic.sketch.test;

import netP5.NetAddress;
import oscP5.OscArgument;
import oscP5.OscMessage;
import oscP5.OscP5;

import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class OSCTest 
extends PAppletHax {

	OscP5 oscP5;
	NetAddress myRemoteLocation;


	protected void overridePropsFile() {
		// _appConfig.setProperty( "width", "1200" );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		background(0);
	}

	public void oscEvent(OscMessage theOscMessage) 
	{  
		// get the first value as an integer
		OscArgument firstValue = theOscMessage.get(0);
		// print out the message
		print("OSC Message Recieved: ");
		print(theOscMessage.addrPattern() + " ");
		println(firstValue.floatValue());
	}
}