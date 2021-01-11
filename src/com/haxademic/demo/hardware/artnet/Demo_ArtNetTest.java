package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import ch.bildspur.artnet.ArtNetClient;

public class Demo_ArtNetTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetClient artnet;
	protected byte[] dmxData = new byte[512];

	protected void firstFrame() {
		// create artnet client without buffer (no receving needed)
		artnet = new ArtNetClient(null);
		artnet.start();
	}

	protected void drawApp() {
		for(int i=0; i < 100; i++) {
			// fill data array
			int indx = i * 3;
			dmxData[indx + 0] = P.parseByte(127 + 127f * sin(0+(i/10f) + frameCount * 0.02f));
			dmxData[indx + 1] = P.parseByte(127 + 127f * sin(1+(i/10f) + frameCount * 0.02f));
			dmxData[indx + 2] = P.parseByte(127 + 127f * sin(2+(i/10f) + frameCount * 0.02f));
		}

		// send dmx to localhost
		artnet.unicastDmx("192.168.1.100", 0, 0, dmxData);
	}
}