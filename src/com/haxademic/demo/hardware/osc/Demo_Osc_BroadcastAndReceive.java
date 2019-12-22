package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.osc.OscState;
import com.haxademic.core.hardware.shared.InputTrigger;

import oscP5.OscMessage;

public class Demo_Osc_BroadcastAndReceive 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected InputTrigger oscTrigger = new InputTrigger().addOscMessages(new String[] {"/framecount"});
	
	protected void firstFrame() {
		OscState.instance();

		DebugView.autoHide(false);
		DebugView.active(true);
	}

	public void drawApp() {
		p.background(0);
		
		// do something with the trigger
		if(oscTrigger.triggered()) {
			// draw a box when the OSC message is received
			p.fill(0, 255, 0);
			p.rect(10, 10, 50, 50);
			// log the OSC value
			P.out(oscTrigger.value());
		}
		
		// broadcast framecount (picked up by self since it's multicasted)
		if(frameCount % 50 == 0) {
			OscMessage heartbeatMessage = new OscMessage("/framecount");
			heartbeatMessage.add((float) p.frameCount);
			OscState.instance().oscP5().send(heartbeatMessage); 
		}
	}
	
	public void oscEvent(OscMessage theOscMessage) {
		if (theOscMessage.isPlugged()== false) {
			println("UNPLUGGED: " + theOscMessage);
		}
	}

	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			OscMessage message = new OscMessage("/video-start");
			message.add((float) p.frameCount);
			OscState.instance().oscP5().send(message);
			P.out("send OSC:", message.toString());
		}
		if(p.key == '2') {
//			OscMessage message = new OscMessage("/video-start");
			OscMessage message = new OscMessage("/centrifuge");
			message.add((float) p.frameCount);
			OscState.instance().oscP5().send(message);
			P.out("send OSC:", message.toString());
		}
		if(p.key == '3') {
//			OscMessage message = new OscMessage("/video-start");
			OscMessage message = new OscMessage("/victory");
			message.add((float) p.frameCount);
			OscState.instance().oscP5().send(message);
			P.out("send OSC:", message.toString());
		}
	}

}