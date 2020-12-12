package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;

import processing.serial.Serial;

public class Demo_SerialDevice_Arduino_FasetLEDStrip
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SimplexNoise3dTexture noiseTexture;
	protected TextureShader textureShader;
	
	protected SerialDevice serialDevice;
	protected int numLights = 32;	// 5 bytes per rgb color cmd

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
	}

	protected void firstFrame() {
		SerialDevice.printDevices();
		serialDevice = new SerialDevice(this, 0, 115200); 
		
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height);
		textureShader = new TextureShader(TextureShader.cacheflowe_liquid_moire_camo_alt);
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
		
		// pixel-sampling color send
		noiseTexture.update(4f, p.frameCount * 0.007f, 0, 0, p.frameCount * 0.01f, false);
		ContrastFilter.instance(p).setContrast(2f);
		ContrastFilter.instance(p).applyTo(noiseTexture.texture());
		ImageUtil.copyImage(noiseTexture.texture(), pg);
		// if shader mode
		textureShader.setTime((float) p.frameCount * 0.03f);
		pg.filter(textureShader.shader());
		// prep for pixel reading
		pg.loadPixels();
		p.image(pg, 0, 0);

		// build one big array for the byte data
		byte[] colorsData = new byte[numLights * 5];	// 5 bytes per led color
		boolean allAtOnce = true;
		
		// sampling across screen, write Serial bytes
		float skipPixels = (pg.width - 40) / numLights;
		for (int i = 0; i < numLights; i++) {
			// get pixel color from webcam
			int x = 20 + P.round(skipPixels * i);
			int y = pg.height / 2;
			int pixelColor = ImageUtil.getPixelColor(pg, x, y);
			
			p.fill(pixelColor);
			p.rect(x, y, 20, 20);
			
			// set color on LED strip - lights don't want to go above 127
			float bright = 1f * Mouse.xNorm;
			
			///////////////////////////
			// write each LED at a time
			///////////////////////////
			if(allAtOnce == false) {
				// write each dang byte individually. slowest/worst option
//				serialDevice.device().write('s');
//				serialDevice.device().write('c');
//				serialDevice.device().write(i);
//				serialDevice.device().write(ConvertUtil.intToByte((int) (p.red(pixelColor) * bright)));
//				serialDevice.device().write(ConvertUtil.intToByte((int) (p.green(pixelColor) * bright)));
//				serialDevice.device().write(ConvertUtil.intToByte((int) (p.blue(pixelColor) * bright)));
				
				// write each color as a 5-member array. better, but not as good as one array per frame
				serialDevice.device().write(new byte[] {
						'c', 
						P.parseByte(i), 
						ConvertUtil.intToByte((int) (p.red(pixelColor) * bright)),
						ConvertUtil.intToByte((int) (p.green(pixelColor) * bright)),
						ConvertUtil.intToByte((int) (p.blue(pixelColor) * bright))
				});
			} else {
				///////////////////////////
				// or build the entire color array and send later! this is the fastest technique
				///////////////////////////
				colorsData[i * 5 + 0] = 'c';
				colorsData[i * 5 + 1] = P.parseByte(i);
				colorsData[i * 5 + 2] = ConvertUtil.intToByte((int) (p.green(pixelColor) * bright));
				colorsData[i * 5 + 3] = ConvertUtil.intToByte((int) (p.red(pixelColor) * bright));
				colorsData[i * 5 + 4] = ConvertUtil.intToByte((int) (p.blue(pixelColor) * bright));
			}
		}
		
		if(allAtOnce) {
			// write the larger data structure once
			serialDevice.device().write(colorsData);
		}
	}

	@Override
	public void newDataAvailable(Serial serialDevice) {
		// log incoming messages
		String inputStr = serialDevice.readString();
		DebugView.setValue("[Serial in]", inputStr);
	}

}



// ARDUINO CODE
/**

// Controls LPD8806-based RGB LED Modules in a strip
// Wiring diagram: https://learn.adafruit.com/digital-led-strip/wiring
// -5V  = 5V
// -GND = GND
// -DI  = Digital 2
// -CI  = Digital 3

// For led chips like WS2812, which have a data line, ground, and power, you just
// need to define DATA_PIN.  For led chipsets that are SPI based (four wires - data, clock,
// ground, and power), like the LPD8806 define both DATA_PIN and CLOCK_PIN
// Clock pin only needed for SPI based chipsets when not using hardware SPI

/////////////////////////////////////////////////////////////////////////////

// hardware setup
#include <FastLED.h>
#define DATA_PIN 2
#define CLOCK_PIN 3

// lights config
#define NUM_LEDS 32
CRGB leds[NUM_LEDS];

// state-machine for bufferless serial input
typedef enum {  NONE, GOT_COLOR, GOT_OTHER } states;
states state = NONE;
unsigned int currentValue;
int colorArrSize = 4;
int curRgbInput[] = {0, 0, 0, 0}; // [led_index, r, g, b]
int rgbInputValIndex = 0;

// start
void setup() { 
  // ## Clocked (SPI) initializer ##
  FastLED.addLeds<LPD8806, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);  // GRB ordering is typical

  // Start serial for input
  Serial.begin(115200);
  Serial.setTimeout(0);
  Serial.println("<Arduino is ready>");
  state = NONE;
}

int byteToInt(byte b) {
  return b;// & 0xFF;
}


void loop() { 
  // runSingleDotPulseDemo();
  runRgbSerial();
}

/////////////////////////////
// Serial input mode
/////////////////////////////

void runRgbSerial() {
  while (Serial.available()) {
    processIncomingByte(Serial.read());
  }
//  Serial.flush();
  FastLED.show();
//  delay(16);
}

void processIncomingByte (byte c) {
  // check for numeric values or new mode characters
  // idea from: http://www.gammon.com.au/forum/?id=11425&reply=1#reply1
  if (isCmd (c)) {
    processCmdByte(c);
  } else {
    processNumericByte(c);
  }
}

boolean isCmd(byte c) {
  return(c == 'c' || c == 's');
}

void processNumericByte (byte c) {
//  Serial.println (c);
  int val = byteToInt(c);
  switch (state) {
    case GOT_COLOR:
      processColorVal(val);
      break;
    case GOT_OTHER:
      processNumericVal(val);
      break;
    default:
      break;
  }
}

void processCmdByte (const unsigned int c) {
  switch (c) {
    case 'c':
      newColorMode();
      break;
    case 's':
      numericAddMode();
      break;
    default:
      state = NONE;
      break;
  }
}

void processColorVal (int val) {
  // set value in index
  curRgbInput[rgbInputValIndex] = val;
  
  // advance rgb array index 
  // and write to LEDs when the array is filled
  rgbInputValIndex++;
  if(rgbInputValIndex >= colorArrSize) {
    int ledIndex = curRgbInput[0];
    if(ledIndex < NUM_LEDS) {
      leds[ledIndex] = CRGB(curRgbInput[1], curRgbInput[2], curRgbInput[3]);
      rgbInputValIndex = 0;
    }
  }
}

void newColorMode() {
  state = GOT_COLOR;
  rgbInputValIndex = 0;
}

void numericAddMode() {
  state = GOT_OTHER;
}

void processNumericVal (int val) {
  currentValue *= 10;
  currentValue += val - '0';
}

/////////////////////////////
// Demo mode
/////////////////////////////

void runSingleDotPulseDemo() {
  for(int whiteLed = 0; whiteLed < NUM_LEDS; whiteLed = whiteLed + 1) {
    // Turn our current led on to white, then show the leds
    leds[whiteLed] = CRGB::White;
    
    // Show the leds (only one of which is set to white, from above)
    FastLED.show();
    
    // Wait a little bit (60fps)
    delay(16);
    
    // Turn our current led back to black for the next loop around
    leds[whiteLed] = CRGB::Black;
  }
}

*/

