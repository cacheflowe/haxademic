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
	protected byte[] dmxDataCurUniverse = new byte[universeSize];	// data per universe is copied into this array and sent out, one universe at a time in sequence
	
	public static boolean DEBUG = true;

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
	
	public String controllerAddress() { return controllerAddress; }
	public int universeStart() { return universeStart; }
	public int numPixels() { return numPixels; }
	
	public void setColorAtIndex(int pixelIndex, float r, float g, float b) {
		// assuming RGB LEDs, map dmx channels to pixel index
//		pixelIndex *= 3; 
		// prevent out of range errors... why is it doing this?
		if(pixelIndex >= dmxData.length) {
			if(DEBUG) P.out("Bad pixelIndex in ArtNetDataSender.setColorAtIndex()");
			return;
		}
		// https://processing.org/reference/byte.html
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

	public void sendMatrixFromBuffer(PImage texture) {
		sendMatrixFromBuffer(texture, texture.width, texture.height, 0, 0, 0, true, true);
	}
	
	public void sendMatrixFromBuffer(PImage texture, int matrixSize) {
		sendMatrixFromBuffer(texture, matrixSize, matrixSize, 0, 0, 0, true, true);
	}
	
	public void sendMatrixFromBuffer(PImage texture, int matrixW, int matrixH) {
		sendMatrixFromBuffer(texture, matrixW, matrixH, 0, 0, 0, true, true);
	}
	
	public void sendMatrixFromBuffer(PImage texture, int matrixW, int matrixH, int pixelIndexStart, int offsetX, int offsetY, boolean shouldLoadPixels, boolean shouldSend) {
		if(shouldLoadPixels) texture.loadPixels();
		
		// check for out of bound color setting
		int oobIndex = -1;
		
		// build entire LED data, to loop through afterwards
		int numPixelsPerMatrix = matrixW * matrixH;
		for(int i=0; i < numPixelsPerMatrix; i++) {
			
			// get texture pixel color components
			int x = MathUtil.gridXFromIndex(i, matrixW);
			int y = MathUtil.gridYFromIndex(i, matrixW);
			int pixelColor = ImageUtil.getPixelColor(texture, offsetX + x, offsetY + y);
			float r = ColorUtil.redFromColorInt(pixelColor);
			float g = ColorUtil.greenFromColorInt(pixelColor);
			float b = ColorUtil.blueFromColorInt(pixelColor);
			
			// calculate pixel index, stepping through single-row sequential layout, 1 by 1
			// zigzag remap
			int pixelIndex = i * 3;
			int rowStartI = P.floor(i / matrixW) * matrixW;
			int twoRowIndex = i % (matrixW * 2);
			int zigZagRevIndex = matrixW - 1 - (i % matrixW);
			if(twoRowIndex < matrixW) {
				pixelIndex = (rowStartI + zigZagRevIndex) * 3;
			}
			
			// set data
			int artNetPixelIndex = (pixelIndexStart * 3) + pixelIndex;
			if(artNetPixelIndex <= dmxData.length - 3) setColorAtIndex(artNetPixelIndex, r, g, b);
			else {
				oobIndex = artNetPixelIndex;
			}
		}
		if(shouldSend) send();
		
		// announce any out of bounds errors
		if(oobIndex > -1 && P.p.frameCount % 60 == 1) {
			if(DEBUG) P.out("ERROR: ArtNet data index is past array length in sendMatrixFromBuffer(): ", oobIndex);
		}
	}
	
	public void drawDebug(PGraphics pg) {
		drawDebug(pg, false);
	}
	
	public void drawDebug(PGraphics pg, boolean openContext) {
		if(openContext) {
			pg.beginDraw();
			pg.background(0);
		}
		pg.push();
		pg.noStroke();
		int pixSize = 4;
		int x = 0;
		int y = 0;
		// need to properly convert byte back to int, because of weird byte value range without conversion
		for(int i=0; i < dmxData.length/3; i+=3) {
			pg.fill(P.parseInt(dmxData[i + 0]), P.parseInt(dmxData[i + 1]), P.parseInt(dmxData[i + 2]));
			pg.rect(x, y, pixSize, pixSize);
			x += pixSize;
			if(x >= pg.width) {
				x = 0;
				y += pixSize;
			}
		}
		pg.pop();
		if(openContext) pg.endDraw();
	}
}
