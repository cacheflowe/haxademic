package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.math.easing.Penner;

import ch.bildspur.artnet.ArtNetClient;

public class Demo_ArtNet_BasicTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetClient artnet;
	protected byte[] dmxData = new byte[512];
	protected byte[] dmxData2 = new byte[512];

	protected void firstFrame() {
		// create artnet client without buffer (no receving needed)
		artnet = new ArtNetClient(null);
		artnet.start();
	}

	protected void drawApp() {
		// max of 170 RGB lights in 1 universe
		for(int i=0; i < 170; i++) {
			int indx = i * 3;
//			float r = 127 + 127f * sin(0+(i/10f) + frameCount * 0.02f);
			float r = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(0+(i/10f) + frameCount * 0.2f));
			float g = 0; // 127 + 127f * sin(1+(i/10f) + frameCount * 0.02f);
			float b = 0; // 127 + 127f * sin(2+(i/10f) + frameCount * 0.02f);
			dmxData[indx + 0] = P.parseByte(r);
			dmxData[indx + 1] = P.parseByte(g);
			dmxData[indx + 2] = P.parseByte(b);
		}

		// send dmx to localhost: address, subnet, universe, data
		artnet.unicastDmx("192.168.1.101", 0, 0, dmxData);

		// send DMX data out of extra port (set to universe `1`)
		for(int i=0; i < 100; i++) {
			// fill data array
			int indx = i * 3;
			dmxData2[indx + 0] = P.parseByte(127 + 127f * sin(0+(i/10f) + frameCount * 0.01f));
			dmxData2[indx + 1] = P.parseByte(127 + 127f * sin(1+(i/10f) + frameCount * 0.04f));
			dmxData2[indx + 2] = P.parseByte(127 + 127f * sin(2+(i/10f) + frameCount * 0.02f));
		}
		artnet.unicastDmx("192.168.1.101", 0, 1, dmxData2);
	}
}