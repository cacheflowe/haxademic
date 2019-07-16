package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.PAppletHax;

import oscP5.OscMessage;
import oscP5.OscP5;

public class Demo_OSC_receive 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	OscP5 oscP5;

	protected void setupFirstFrame() {
	  oscP5 = new OscP5(this, 7400);
	  oscP5.plug(this, "receive", "/video-start");
	}

	public void drawApp() {
		p.background(0);
	}

	public void receive(int i) {
		println("receive!", i);
	}

	void oscEvent(OscMessage theOscMessage) {
		if (theOscMessage.isPlugged() == false) {
			println("UNPLUGGED: " + theOscMessage);
		}
	}

}