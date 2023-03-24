package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.textures.SimplexNoise3dTexture;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.serial.LedStripLPD8806;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;

import processing.serial.Serial;

public class Demo_LedStripLPD8806
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SimplexNoise3dTexture noiseTexture;
	protected TextureShader textureShader;
	
	protected LedStripLPD8806 ledStrip;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
//		Config.setProperty( AppSettings.FPS, 30 );
	}

	protected void firstFrame() {
		SerialDevice.printDevices();
		ledStrip = new LedStripLPD8806(this, 0, 115200, 12); 
		
		noiseTexture = new SimplexNoise3dTexture(p.width, p.height);
		textureShader = new TextureShader(TextureShader.light_leak);
	}

	protected void drawApp() {
		background(0);
		updateLedLights();
	}
	
	protected void updateLedLights() {
		// update texture
		textureShader.setTime((float) p.frameCount * 0.03f);
		pg.filter(textureShader.shader());
		
		
		BrightnessFilter.instance().setBrightness(Mouse.xNorm);
		BrightnessFilter.instance().applyTo(pg);

		// copy to device buffer & send to hardware
		ledStrip.update(pg, 0.4f, 0.85f);
		p.image(pg, 0, 0);
	}

	@Override
	public void newDataAvailable(Serial serialDevice) {
		// log incoming messages
		String inputStr = serialDevice.readString();
		DebugView.setValue("[Serial in]", inputStr);
		P.out(inputStr);
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

