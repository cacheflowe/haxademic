package com.haxademic.core.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class LedStripLPD8806
extends SerialDevice {
	
	protected PGraphics buffer;
	protected byte[] colorsData;
	protected int numLights = 32;

	public LedStripLPD8806(ISerialDeviceDelegate delegate, int serialPortIndex, int baudRate, int numLights) {
		super(delegate, serialPortIndex, baudRate);
		this.numLights = numLights;
		colorsData = new byte[numLights * 5];	// 5 bytes per led color
		this.buffer = PG.newPG(256, 64, true, false);
	}
	
	public void update(PImage img) {
		update(img, 1, 1);
	}
	
	public void update(PImage img, float brightness, float smoothing) {
		// copy frame - lerp it!
		BlendTowardsTexture.instance().setSourceTexture(img);
		BlendTowardsTexture.instance().setBlendLerp(smoothing);
		BlendTowardsTexture.instance().applyTo(buffer);
		buffer.loadPixels();
		
//		device().write('n');
		
		// sampling across screen, write Serial bytes
		float skipPixels = (buffer.width - 40) / numLights;
		int y = buffer.height / 2;
		for (int i = 0; i < numLights; i++) {
			// get pixel color from webcam
			int x = 20 + P.round(skipPixels * i);
			int pixelColor = ImageUtil.getPixelColor(buffer, x, y);
			
			///////////////////////////
			// build the entire color array and send later! this is the fastest technique
			///////////////////////////
			colorsData[i * 5 + 0] = 'c';
			colorsData[i * 5 + 1] = P.parseByte(i);
			colorsData[i * 5 + 2] = ConvertUtil.intToByte((int) (P.p.green(pixelColor) * brightness));
			colorsData[i * 5 + 3] = ConvertUtil.intToByte((int) (P.p.red(pixelColor) * brightness));
			colorsData[i * 5 + 4] = ConvertUtil.intToByte((int) (P.p.blue(pixelColor) * brightness));
		}
		// write the larger data structure once
		write(colorsData);
	}

}
