package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;

import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscProperties;

public class Demo_Osc_Broadcast_vanilla 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected OscP5 oscP5;
	
	protected void firstFrame() {
		initOSC();
		DebugView.autoHide(false);
		DebugView.active(true);
	}
	
	protected void initOSC() {
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
	}

	protected void drawApp() {
		p.background(0);
		
		// broadcast framecount (picked up by self since it's multicasted)
		if(frameCount % 50 == 0) {
			OscMessage heartbeatMessage = new OscMessage("/framecount");
			heartbeatMessage.add((float) p.frameCount);
			oscP5.send(heartbeatMessage); 
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			OscMessage message = new OscMessage("/video-start");
			message.add((float) p.frameCount);
			oscP5.send(message);
			P.out("send OSC:", message.toString());
		}
		if(p.key == '2') {
//			OscMessage message = new OscMessage("/video-start");
			OscMessage message = new OscMessage("/centrifuge");
			message.add((float) p.frameCount);
			oscP5.send(message);
			P.out("send OSC:", message.toString());
		}
		if(p.key == '3') {
//			OscMessage message = new OscMessage("/video-start");
			OscMessage message = new OscMessage("/victory");
			message.add((float) p.frameCount);
			oscP5.send(message);
			P.out("send OSC:", message.toString());
		}
	}

}