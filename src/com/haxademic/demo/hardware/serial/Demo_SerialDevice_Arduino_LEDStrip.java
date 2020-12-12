package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;

import processing.core.PImage;
import processing.serial.Serial;

public class Demo_SerialDevice_Arduino_LEDStrip
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SimplexNoiseTexture noiseTexture;

	protected SerialDevice serialDevice;
	protected int numLights = 32;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	protected void firstFrame() {
		SerialDevice.printDevices();
		serialDevice = new SerialDevice(this, 0, 115200); 
		
		noiseTexture = new SimplexNoiseTexture(p.width, p.height);
	}

	protected void drawApp() {
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
		
		// show debug texture sampling points and 
		// send the Serial messages
		float skipPixels = (readTexture.width - 40) / numLights;
		for (int i = 0; i < numLights; i++) {
			// get pixel color from webcam
			int x = 20 + P.round(skipPixels * i);
			int y = readTexture.height / 2;
			int pixelColor = ImageUtil.getPixelColor(readTexture, x, y);
			
			p.fill(pixelColor);
			p.rect(x, y, 20, 20);
			
			// set color on LED strip - lights don't want to go above 127
			float bright = 0.5f * Mouse.xNorm;
			serialDevice.device().write(ConvertUtil.intToByte((int) (p.red(pixelColor) * bright)));
			serialDevice.device().write(ConvertUtil.intToByte((int) (p.green(pixelColor) * bright)));
			serialDevice.device().write(ConvertUtil.intToByte((int) (p.blue(pixelColor) * bright)));
		}
	}

	@Override
	public void newDataAvailable(Serial serialDevice) {
		// log incoming messages
		DebugView.setValue("[Serial in]", P.trim(serialDevice.readString()));
	}

}



// ARDUINO CODE
/**

#include "LPD8806.h"
#include "SPI.h" // Comment out this line if using Trinket or Gemma
#ifdef __AVR_ATtiny85__
 #include <avr/power.h>
#endif

// Example to control LPD8806-based RGB LED Modules in a strip
// Wiring diagram: https://learn.adafruit.com/digital-led-strip/wiring
// -5V  = 5V
// -GND = GND
// -DI  = Digital 2
// -CI  = Digital 3

// Number of RGB LEDs in strand:
int numLights = 32;

// Chose 2 pins for output; can be any valid output pins:
int dataPin  = 2;
int clockPin = 3;

// First parameter is the number of LEDs in the strand.  The LED strips
// are 32 LEDs per meter but you can extend or cut the strip.  Next two
// parameters are SPI data and clock pins:
LPD8806 strip = LPD8806(numLights, dataPin, clockPin);

int incomingByte = 0;   // for incoming serial data
int incomingInt = 0;

void setup() {
  // Start up the LED strip
  // Update the strip, to start they are all 'off'
  strip.begin();
  strip.show();

  // Start serial for input
  Serial.begin(345600);
  Serial.setTimeout(0);
  Serial.println("<Arduino is ready>");
}

int byteToInt(byte b) {
  return b;// & 0xFF;
}

void loop() {
  // read incoming serial data
  int numColorsSet = 0;
  if (Serial.available() >= numLights * 3) {
    int lightIndex = 0;
    for (int i=0; i < numLights * 3; i+=3) {
      // read the incoming byte:
      strip.setPixelColor(lightIndex, strip.Color(
        byteToInt(Serial.read()), 
        byteToInt(Serial.read()), 
        byteToInt(Serial.read())
       ));
      lightIndex++;
      numColorsSet += 3;
    }
    Serial.flush();
    Serial.println(numColorsSet); // tell the Java app we've completed sending out light messages
    strip.show();   // write all the pixels out
  }
}

*/