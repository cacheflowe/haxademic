////////////////////////////////////////////////////////////////
// (placeholder) Adafruit_NeoPixel: Instead of Neopixel
// ----------------------------------------
// (placeholder) from: https://github.com/pololu/vl53l1x-arduino/blob/master/examples/ContinuousMultipleSensors/ContinuousMultipleSensors.ino
// --------------
// LED wiring:
// --------------
// 'Red'    ->  5V
// 'Green'  ->  Digital 2
// 'White'  ->  GND
////////////////////////////////////////////////////////////////


// LED setup -------------------------------------
// NeoPixel library for RGBW LEDs
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif
#define DATA_PIN 2
#define NUM_LEDS 30
Adafruit_NeoPixel pixels(NUM_LEDS, DATA_PIN, NEO_GRBW + NEO_KHZ800);
int ledIndex = 0;
static unsigned long ledLastUpdate = 0;
// LED setup -------------------------------------

// Serial control --------------------------------
const char START_CHAR = 'n'; // your special character
int receivedNumber = 100;
bool numberStarted = false;
// Serial control --------------------------------

// force reboot helper ---------------------------------------
void(* Reboot)(void) = 0;


void setup() {
  while (!Serial) {}
  Serial.begin(115200);
  Serial.setTimeout(0);
  Serial.println("- Arduino is ready -");
  initLEDNeopixel();
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


void initLEDNeopixel() {
  pixels.begin();
}

void updateLEDNeopixel() {
  if (millis() < ledLastUpdate + 200) return;
  ledLastUpdate = millis();

  for (int i = 0; i < NUM_LEDS; i++) {
    int luma = (i == ledIndex) ? 20 : 0;
    int r = luma;
    int g = luma;
    int b = luma;
    int w = luma;
    pixels.setPixelColor(i, pixels.Color(r, g, b, w));
  }

  // pixels.clear();
  pixels.show();
  ledIndex++;
  ledIndex = ledIndex % NUM_LEDS;
}

void updateLEDNeopixelByIndex() {
  if (millis() < ledLastUpdate + 30) return;
  ledLastUpdate = millis();

  // set all LEDs
  for (int i = 0; i < NUM_LEDS; i++) {
    int luma = (i == receivedNumber) ? 50 : 0;
    int r = luma;
    int g = luma;
    int b = luma;
    int w = luma;
    pixels.setPixelColor(i, pixels.Color(r, g, b, w));
  }
  pixels.show();
}
