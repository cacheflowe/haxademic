package com.haxademic.demo.hardware.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

import ch.bildspur.artnet.ArtNetClient;

public class Demo_ArtNetTest_multipleUniversesInline
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected String controllerAddress = "192.168.1.101";
	protected int numPixels = 256;
	protected int universePixels = 170;
	protected int universeSize = universePixels * 3;
	protected ArtNetClient artnet;
	protected byte[] dmxData = new byte[numPixels * 3];	// pixels * RGB channels
	protected byte[] dmxDataCurUniverse = new byte[universeSize];

	protected void firstFrame() {
		// create artnet client without buffer (no receving needed)
		artnet = new ArtNetClient(null);
		artnet.start();
	}

	protected void drawApp() {
		createColors();
		sendColors();

	}
	
	protected void createColors() {
		// build entire LED data, to loop through afterwards
		int i = 0;
//		for(int i=0; i < numPixels; i++) {
		float colorSpeed = 0.3f;//FrameLoop.osc(0.003f, 0.4f, 0.6f);
		float colorFreq = 0.41f;//FrameLoop.osc(0.003f, 1.4f, 2.5f);
		while(i < numPixels) {
			// set rgb colors
//			float r = 127 + 127f * sin(0+(i/10f) + frameCount * 0.02f);
			float r = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(0+(i*colorFreq) + -frameCount * colorSpeed*1f));
			float g = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(1+(i*colorFreq) + -frameCount * colorSpeed*0.66f));
			float b = 255f * Penner.easeInOutExpo(0.5f + 0.5f * P.sin(2+(i*colorFreq) + -frameCount * colorSpeed*0.33f));
			
			// pixel index, stepping through single-row sequential layout, 1 by 1
			int pixelIndex = i * 3;
			
			// zigzag remap
			int matrixSize = 16;
			int rowStartI = P.floor(i / matrixSize) * matrixSize;
			int twoRowIndex = i % (matrixSize * 2);
			int zigZagRevIndex = 16 - 1 - (i % 16);
			if(twoRowIndex < matrixSize) {
				pixelIndex = (rowStartI + zigZagRevIndex) * 3;
			}
			
			// set data
			dmxData[pixelIndex + 0] = P.parseByte(r);
			dmxData[pixelIndex + 1] = P.parseByte(g);
			dmxData[pixelIndex + 2] = P.parseByte(b);
				
			// move index to next pixel
			i++;
		}
	}
	
	protected void sendColors() {
		// loop through multiple universes, sending out via artnet 
		// when we complete copying each universe to the current array
		int curUniverse = 0;
		int iCurUniverse = 0;
		int i = 0;
		int loopedIndex = 0;
		for(i=0; i < dmxData.length; i++) {
			loopedIndex = i % universeSize;
			dmxDataCurUniverse[loopedIndex] = dmxData[i];
			iCurUniverse++;
			
			// send dmx to localhost when looping to the next universe
			if(i > 0 && iCurUniverse % universeSize == 0) {
				artnet.unicastDmx(controllerAddress, 0, curUniverse, dmxDataCurUniverse);	// address, subnet, universe, data
				iCurUniverse = 0;
				curUniverse++;
			}
		}
		// if we didn't finish sending all in a universe (which is very likely)
		if(iCurUniverse > 0) {
			artnet.unicastDmx(controllerAddress, 0, curUniverse, dmxDataCurUniverse);	// address, subnet, universe, data
		}
	}
}