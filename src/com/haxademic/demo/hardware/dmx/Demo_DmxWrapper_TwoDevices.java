package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.DMXWrapper;

public class Demo_DmxWrapper_TwoDevices
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXWrapper dmx1;
	protected DMXWrapper dmx2;
	
	protected void firstFrame() {
		dmx1 = new DMXWrapper("COM4", 9600);
		dmx2 = new DMXWrapper("COM7", 9600);
	}

	protected void drawApp() {
		background(0);
		for (int i = 0; i < 512; i++) {
			dmx1.setValue(i+1, P.round(127 + 127 * P.sin(p.frameCount * 0.01f + i)));
			dmx2.setValue(i+1, P.round(127 + 127 * P.sin(p.frameCount * 0.05f + i)));
		}
	}
}





