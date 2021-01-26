package com.haxademic.core.hardware.dmx.artnet;

import com.haxademic.core.app.P;

import ch.bildspur.artnet.ArtNetClient;

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
