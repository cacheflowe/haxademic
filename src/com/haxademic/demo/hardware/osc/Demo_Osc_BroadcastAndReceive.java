package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscProperties;

public class Demo_Osc_BroadcastAndReceive 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected OscP5 oscP5;
	
	protected void overridePropsFile() {
		 p.appConfig.setProperty( AppSettings.OSC_ACTIVE, true );
	}
	
	protected void setupFirstFrame() {
		// init osc 
		OscProperties properties = new OscProperties();
		properties.setNetworkProtocol(OscProperties.MULTICAST);
//		properties.setRemoteAddress("224.0.1.0", 7777);			// this multicast address didn't work
//		properties.setRemoteAddress("239.0.0.1", 7777);			// this multicast address also didn't work w/parallel
		properties.setRemoteAddress("255.255.255.255", 7777);	// this multicast address works!!
//		properties.setRemoteAddress("192.168.1.51", 7777);		// direct to receiver works!
		P.out(properties.toString());
		oscP5 = new OscP5(this, properties);    

	}

	public void drawApp() {
		p.background(0);
		
		// show received OSC signals
		p.oscState.printButtons();
		
		// broadcast framecount
		if(frameCount % 50 == 0) {
			OscMessage heartbeatMessage = new OscMessage("/framecount");
			heartbeatMessage.add((float) p.frameCount);
			oscP5.send(heartbeatMessage); 
			// P.out("HEARTBEAT OSC", heartbeatMessage.toString(), heartbeatMessage.get(0).floatValue());
		}
	}

}