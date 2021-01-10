#include "LPD8806.h"
#include "SPI.h" // Comment out this line if using Trinket or Gemma
#ifdef __AVR_ATtiny85__
 #include <avr/power.h>
#endif

// Example to control LPD8806-based RGB LED Modules in a strip
// Wiring diagram: https://learn.adafruit.com/digital-led-strip/wiring

/*****************************************************************************/

// Number of RGB LEDs in strand:
int nLEDs = 32;

// Chose 2 pins for output; can be any valid output pins:
int dataPin  = 2;
int clockPin = 3;

// First parameter is the number of LEDs in the strand.  The LED strips
// are 32 LEDs per meter but you can extend or cut the strip.  Next two
// parameters are SPI data and clock pins:
LPD8806 strip = LPD8806(nLEDs, dataPin, clockPin);

void setup() {
  // Start up the LED strip
  strip.begin();

  // Update the strip, to start they are all 'off'
  strip.show();
}


void loop() {
  // write new colors
  for (int i=0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, strip.Color(
//      round(150.0 + 60.0 * sin(millis() * 0.00007 + i*0.03)), 
//      round(150.0 + 60.0 * sin(millis() * 0.00006 + i*0.02)), 
//      round(150.0 + 60.0 * sin(millis() * 0.00005 + i*0.04))
        round(255),
        round(5),
        round(255)
    ));
  }  
  strip.show();   // write all the pixels out
}
