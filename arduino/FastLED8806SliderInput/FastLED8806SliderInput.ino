////////////////////////////////////////////////////////////////
// Controls LPD8806-based RGB LED Modules in a strip
////////////////////////////////////////////////////////////////
// Wiring diagram: https://learn.adafruit.com/digital-led-strip/wiring
// -5V  = 5V
// -GND = GND
// -DI  = Digital 2
// -CI  = Digital 3
////////////////////////////////////////////////////////////////

// For led chips like WS2812, which have a data line, ground, and power, you just
// need to define DATA_PIN.  For led chipsets that are SPI based (four wires - data, clock,
// ground, and power), like the LPD8806 define both DATA_PIN and CLOCK_PIN
// Clock pin only needed for SPI based chipsets when not using hardware SPI

////////////////////////////////////////////////////////////////

// hardware setup --------------------------------
#include <FastLED.h>
#define DATA_PIN 2
#define CLOCK_PIN 3

// lights config --------------------------------
#define NUM_LEDS 99
CRGB leds[NUM_LEDS];

// LED state --------------------------------
int ledIndex = 0;
static unsigned long ledLastUpdate = 0;

// Serial control --------------------------------
const char START_CHAR = 'n'; // your special character
int receivedNumber = 100;
bool numberStarted = false;

// start
void setup() {
  while (!Serial) {}
  Serial.begin(9600);
  Serial.setTimeout(0);
  Serial.println("- Arduino is ready -");
  initLED();
}

void initLED() { 
  // ## Clocked (SPI) initializer ##
  FastLED.addLeds<LPD8806, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);  // GRB ordering is typical
  // FastLED.addLeds<WS2801, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);   // GRB ordering is typical
}

void loop() { 
  checkInput();
  if(receivedNumber == 100) {
    updateLEDNeopixel();
  } else {
    updateLEDNeopixelByIndex();
  }
}

void checkInput() {
  if (Serial.available() > 0) {
    char c = Serial.read();
    if (c == START_CHAR) {
      numberStarted = true;
      receivedNumber = 0;
    } else if (numberStarted) {
      if (c >= '0' && c <= '9') { // check if c is a digit character
        receivedNumber = receivedNumber * 10 + (c - '0');
      } else if (c == '\n' || c == '\r') { // assume newline or carriage return marks the end of the number
        numberStarted = false;
        // process the received number here
        Serial.print("Received number: ");
        Serial.println(receivedNumber);
      } else {
        // handle unexpected characters (e.g., errors or noise)
        Serial.print("Error: unexpected character '");
        Serial.print(c);
        Serial.println("'");
      }
    }
  }
}

void updateLEDNeopixel() {
  if (millis() < ledLastUpdate + 200) return;
  ledLastUpdate = millis();

  for (int i = 0; i < NUM_LEDS; i++) {
    if(i == ledIndex) {
      leds[i] = CRGB(100, 100, 100); // CRGB::White;
    } else {
      leds[i] = CRGB::Black;
    }
  }

  ledIndex++;
  ledIndex = ledIndex % NUM_LEDS;
  FastLED.show();
}

void updateLEDNeopixelByIndex() {
  if (millis() < ledLastUpdate + 30) return;
  ledLastUpdate = millis();

  // set all LEDs
  for (int i = 0; i < NUM_LEDS; i++) {
    if(i == receivedNumber) {
      leds[i] = CRGB(100, 100, 100); // CRGB::White;
    } else {
      leds[i] = CRGB::Black;
    }
  }
  FastLED.show();
}

