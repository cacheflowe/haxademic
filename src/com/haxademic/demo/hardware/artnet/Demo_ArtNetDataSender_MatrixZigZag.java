package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.dmx.artnet.ArtNetDataSender;
import com.haxademic.core.math.easing.Penner;

public class Demo_ArtNetDataSender_MatrixZigZag
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArtNetDataSender artNetDataSender;
	protected int numPixels = 256;

	protected void firstFrame() {
		artNetDataSender = new ArtNetDataSender("192.168.1.101", 0, numPixels);
	}

	protected void drawApp() {
		createColors();
		artNetDataSender.send();
	}
	
	protected void createColors() {
		// build entire LED data, to loop through afterwards
		float colorSpeed = 0.1f;
		float colorFreq = 0.4f;
		for(int i=0; i < numPixels; i++) {
			// set rgb colors
			float r = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(0+(i*colorFreq) + -frameCount * colorSpeed*1f));
			float g = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(1+(i*colorFreq) + -frameCount * colorSpeed*0.66f));
			float b = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(2+(i*colorFreq) + -frameCount * colorSpeed*0.33f));
			
			// pixel index, stepping through single-row sequential layout, 1 by 1
			int pixelIndex = i;
			
			// zigzag remap
			int matrixSize = 16;
			int rowStartI = P.floor(i / matrixSize) * matrixSize;
			int twoRowIndex = i % (matrixSize * 2);
			int zigZagRevIndex = 16 - 1 - (i % 16);
			if(twoRowIndex < matrixSize) {	// flip even rows
				pixelIndex = (rowStartI + zigZagRevIndex);
			}
			
			// set data
			artNetDataSender.setColorAtIndex(pixelIndex, r, g, b);
		}
	}
}