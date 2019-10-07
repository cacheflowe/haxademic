package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;

import processing.core.PImage;
import processing.serial.Serial;

public class Demo_SerialDevice_NeoPixel
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SimplexNoiseTexture noiseTexture;

	protected SerialDevice serialDevice;
	protected int numLights = 60;
//	protected byte[] colorsOut = new byte[2 + numLights * 3]; // add one index to set the restart special character
	protected byte[] colorsOut = new byte[2 + numLights]; // add one index to set the restart special character
	protected byte START_BYTE = (byte) 255;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setupFirstFrame() {
		SerialDevice.printDevices();
		serialDevice = new SerialDevice(this, 0, 57600); 
		
		noiseTexture = new SimplexNoiseTexture(p.width, p.height);
	}

	public void drawApp() {
		background(0);
		updateLedLights();
	}
	
	protected void updateLedLights() {
		// Notes:
		// we can only write 64 bytes per frame: https://arduino.stackexchange.com/questions/14401/arduino-serial-write-sending-more-than-64-bytes
		// though by modifying the Arduino buffer size, we can go up to 256 bytes: http://www.hobbytronics.co.uk/arduino-serial-buffer-size
		// larger sets of data would need to be chunked...
		
		// trigonometry-based color cycling
//		for (float i = 0; i < numLights; i++) {
//			serialDevice.device().write(ConvertUtil.intToByte(30 + P.round(20f * P.sin((p.frameCount + i * 10f) * 0.07f))));
//			serialDevice.device().write(ConvertUtil.intToByte(30 + P.round(20f * P.sin((p.frameCount + i * 10f) * 0.02f))));
//			serialDevice.device().write(ConvertUtil.intToByte(30 + P.round(20f * P.sin((p.frameCount + i * 10f) * 0.03f))));
//		}
		
		// pixel-sampling color send
		noiseTexture.update(2f, 0, p.frameCount * 0.01f, 0);
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(noiseTexture.texture());
		PImage readTexture = noiseTexture.texture();
		p.image(readTexture, 0, 0);
		readTexture.loadPixels();
		
		// set start byte for arduino
		colorsOut[0] = (byte) '<';
		colorsOut[colorsOut.length - 1] = (byte) '>';
		
		float skipPixels = (readTexture.width - 40) / numLights;
		if(p.frameCount % 10 == 0) {
			for (int i = 0; i < numLights; i++) {
				// get pixel color from webcam
				int x = 20 + P.round(skipPixels * i);
				int y = readTexture.height / 2;
				int pixelColor = ImageUtil.getPixelColor(readTexture, x, y);
				
				p.fill(pixelColor);
				p.rect(x, y, 20, 20);
				
				// set color on LED strip - lights don't want to go above 127
				float bright = 0.75f * p.mousePercentX();
//				serialDevice.device().write(ConvertUtil.intToByte((int) (p.red(pixelColor) * bright)));
//				serialDevice.device().write(ConvertUtil.intToByte((int) (p.green(pixelColor) * bright)));
//				serialDevice.device().write(ConvertUtil.intToByte((int) (p.blue(pixelColor) * bright)));
				
				// write to byte array
				colorsOut[1 + i] = ConvertUtil.intToByte((int) (p.red(pixelColor) * bright));
//				colorsOut[1 + i * 3 + 0] = ConvertUtil.intToByte((int) (p.red(pixelColor) * bright));
//				colorsOut[1 + i * 3 + 1] = ConvertUtil.intToByte((int) (p.green(pixelColor) * bright));
//				colorsOut[1 + i * 3 + 2] = ConvertUtil.intToByte((int) (p.blue(pixelColor) * bright));
			}
			// do threaded write
			serialDevice.write(colorsOut);
		}
	}

	@Override
	public void newDataAvailable(Serial serialDevice) {
		// log incoming messages
		String serialIn = serialDevice.readString();
		p.debugView.setValue("[Serial in]", serialIn);
		P.out(serialIn);
	}

}

