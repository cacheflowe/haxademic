package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.mouse.Mouse;

import ch.bildspur.artnet.ArtNetClient;

public class Demo_ArtNet_ZigZagMatrix
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetClient artnet;
	protected byte[] dmxData = new byte[512];
	protected byte[] dmxData2 = new byte[512];

	protected void firstFrame() {
		// create artnet client without buffer (no receiving needed)
		artnet = new ArtNetClient(null);
		artnet.start();
	}

	protected void drawApp() {
		for(int i=0; i < 256; i++) {
			// fill data array
			int dmxChannel = i * 3;
			byte[] universeData = (i < 170) ? dmxData : dmxData2;
			if(i == Mouse.x) {
				universeData[i % 170 + 0] = P.parseByte(127 + 127f * sin(0+(i/50f) + frameCount * 0.02f));
				universeData[i % 170 + 1] = P.parseByte(127 + 127f * sin(1+(i/50f) + frameCount * 0.02f));
				universeData[i % 170 + 2] = P.parseByte(127 + 127f * sin(2+(i/50f) + frameCount * 0.02f));
			} else {
				universeData[i % 170 + 0] = P.parseByte(0);
				universeData[i % 170 + 1] = P.parseByte(0);
				universeData[i % 170 + 2] = P.parseByte(0);
			}
			
			universeData[i % 170 + 0] = P.parseByte(0);
			universeData[i % 170 + 1] = P.parseByte(0);
			universeData[i % 170 + 2] = P.parseByte(0);

		}

		// send dmx to localhost
		artnet.unicastDmx("192.168.1.101", 0, 0, dmxData);
		artnet.unicastDmx("192.168.1.101", 0, 1, dmxData2);
	}
}