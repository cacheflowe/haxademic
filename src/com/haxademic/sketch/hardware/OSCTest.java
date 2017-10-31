package com.haxademic.sketch.hardware;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

import netP5.NetAddress;
import oscP5.OscArgument;
import oscP5.OscMessage;
import oscP5.OscP5;

public class OSCTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	OscP5 oscP5;
	NetAddress myRemoteLocation;

	protected void overridePropsFile() {
		 p.appConfig.setProperty( AppSettings.OSC_ACTIVE, true );
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