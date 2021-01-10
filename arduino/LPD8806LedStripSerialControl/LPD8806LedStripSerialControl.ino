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


/*****************************************************************************/

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
  Serial.begin(115200);
  Serial.setTimeout(0);
  Serial.println("<Arduino is ready>");
}

int byteToInt(byte b) {
  return b;// & 0xFF;
}

void loop() {
  // send back bytes to sender
//  if (Serial.available() > 0) {
//    // read the incoming byte:
//    int incomingByte = Serial.read();
//
//    // say what you got:
//    Serial.println(incomingByte, DEC);
//  }

  
  // read incoming serial data
//  strip.setPixelColor(0, strip.Color(126, 20, 126));
//  strip.show();   // write all the pixels out


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
//  delay(16);
}
