package com.haxademic.core.hardware.dmx.artnet;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.math.MathUtil;

import ch.bildspur.artnet.ArtNetClient;
import processing.core.PGraphics;
import processing.core.PImage;

public class ArtNetDataSender {

	/**
	 * Creates a data structure and loops through it to properly
	 * create and send sequential universes of ArtNet data.
	 * One key feature is that there are 170 RGB lights per universe,
	 * and the last two channels are *not* transmitted. This solves 
	 * some strange behavior and is probably the expected ArtNet 
	 * standard, sending 510 channels, not 512.
	 */
	
	public static ArtNetClient artnet;
	public static final int universePixels = 170;
	public static final int universeSize = universePixels * 3;
	
	protected String controllerAddress;
	protected int universeStart;
	protected int numPixels = 0;
	protected byte[] dmxData;
	protected byte[] dmxDataCurUniverse = new byte[universeSize];	// data per univers is copied into this array and sent out, one universe at a time in sequence

	public ArtNetDataSender(String controllerAddress, int universeStart, int numPixels) {
		this.controllerAddress = controllerAddress;
		this.universeStart = universeStart;
		this.numPixels = numPixels;
		dmxData = new byte[numPixels * 3];	// pixels * RGB channels
		
		// only use a single artnet client
		if(artnet == null) {
			artnet = new ArtNetClient(null);
			artnet.start();
		}
	}
	
	public void setColorAtIndex(int pixelIndex, float r, float g, float b) {
		dmxData[pixelIndex + 0] = P.parseByte(r);
		dmxData[pixelIndex + 1] = P.parseByte(g);
		dmxData[pixelIndex + 2] = P.parseByte(b);
	}
	
	public void send() {
		// loop through multiple universes, sending out via artnet 
		// when we complete copying each universe to the current array
		int curUniverse = universeStart;
		int iCurUniverse = 0;
		int loopedIndex = 0;
		for(int i=0; i < dmxData.length; i++) {
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

	public void sendMatrixFromBuffer(PImage texture, boolean shouldLoadPixels) {
		if(shouldLoadPixels) texture.loadPixels();
		int matrixSize = texture.width;
		
		// build entire LED data, to loop through afterwards
		for(int i=0; i < numPixels; i++) {
			// get texture pixel color components
			int x = MathUtil.gridColFromIndex(i, matrixSize);
			int y = MathUtil.gridRowFromIndex(i, matrixSize);
			int pixelColor = ImageUtil.getPixelColor(texture, x, y);
			float r = ColorUtil.redFromColorInt(pixelColor);
			float g = ColorUtil.greenFromColorInt(pixelColor);
			float b = ColorUtil.blueFromColorInt(pixelColor);
			
			// pixel index, stepping through single-row sequential layout, 1 by 1
			// zigzag remap
			int pixelIndex = i * 3;
			int rowStartI = P.floor(i / matrixSize) * matrixSize;
			int twoRowIndex = i % (matrixSize * 2);
			int zigZagRevIndex = matrixSize - 1 - (i % matrixSize);
			if(twoRowIndex < matrixSize) {
				pixelIndex = (rowStartI + zigZagRevIndex) * 3;
			}
			
			// set data
			setColorAtIndex(pixelIndex, r, g, b);
		}
		send();
	}
	
	public void drawDebug(PGraphics pg) {
		pg.push();
		pg.noStroke();
		int pixSize = 4;
		int x = 0;
		int y = 0;
		int debugMult = 80;
		for(int i=0; i < dmxData.length/3; i+=3) {
			pg.fill(debugMult*dmxData[i + 0], debugMult*dmxData[i + 1], debugMult*dmxData[i + 2]);
			pg.rect(x, y, pixSize, pixSize);
			x += pixSize;
			if(x >= pg.width) {
				x = 0;
				y += pixSize;
			}
		}
		pg.pop();
	}
}
