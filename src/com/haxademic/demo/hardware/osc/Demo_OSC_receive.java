package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;

import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscProperties;

public class Demo_OSC_receive 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	OscP5 oscP5;
	protected StringBufferLog logOut = new StringBufferLog(10);

	protected void firstFrame() {
		// was: 
		// oscP5 = new OscP5(this, 7400);
		// new initialization:
		OscProperties properties = new OscProperties();
		properties.setNetworkProtocol(OscProperties.MULTICAST);
		properties.setRemoteAddress("255.255.255.255", 7777);	// this multicast address works!!
		oscP5 = new OscP5(this, properties);    

		// add event listener methods
		oscP5.plug(this, "receive", "/video-start");

		// keep debug up
		DebugView.autoHide(false);
		DebugView.active(true);
	}

	public void drawApp() {
		p.background(0);
		logOut.printToScreen(p.g, 320, 20);
	}

	////////////////////////
	// OscP5 callbacks
	////////////////////////

	// plugged routes must mastch the incoming data type!
	
	public void receive(float i) {
		logOut.update("receive! " + i);
	}

	// common callback 
	
	void oscEvent(OscMessage theOscMessage) {
		if (theOscMessage.isPlugged() == false) {
			P.out("UNPLUGGED: " + theOscMessage);
		}
		
	    // check if the typetag is the right one.
	    if(theOscMessage.checkTypetag("f")) {
	    	logOut.update(theOscMessage.toString() + " = " + theOscMessage.get(0).floatValue());
	    }  
	}

}