package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.math.easing.Penner;

public class Demo_ArtNetDataSender_LongStrip
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected int numPixels = 400;

	protected void firstFrame() {
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 0, numPixels);
	}

	protected void drawApp() {
		background(0);
		createColors();
		artNetDataSender.send();
		artNetDataSender.drawDebug(p.g);
	}
	
	protected void createColors() {
		// build entire LED data, to loop through afterwards
		float colorSpeed = 0.02f;
		float colorFreq = 0.04f;
		for(int i=0; i < numPixels; i++) {
			// set rgb colors
			float r = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(0+(i*colorFreq) + -frameCount * colorSpeed*1f));
			float g = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(1+(i*colorFreq) + -frameCount * colorSpeed*0.8f));
			float b = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(2+(i*colorFreq) + -frameCount * colorSpeed*0.6f));
			
			// set data
			int pixelIndex = i * 3;
			artNetDataSender.setColorAtIndex(pixelIndex, r, g, b);
		}
	}
}