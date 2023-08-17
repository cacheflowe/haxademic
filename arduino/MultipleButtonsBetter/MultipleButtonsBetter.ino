/*
 * Created by ArduinoGetStarted.com
 *
 * This example code is in the public domain
 *
 * Tutorial page: https://arduinogetstarted.com/tutorials/arduino-button-library
 *
 * This example:
 *   + uses debounce for multiple buttons.
 *   + reads state of multiple buttons
 *   + detects the pressed and released events of multiple buttons
 */
// Wiring:
// - Put a wire in ground
// - Put wires in Digital 6,7,8
// - When you touch 6,7,8 to ground, the buttons are triggered

#include <ezButton.h>

// initialize button objects with pin numbers
const int NUM_BUTTONS = 3;
ezButton buttons[] = {
  ezButton(6),
  ezButton(7),
  ezButton(8)
};

unsigned long now;

void initButtons() {
  for(int i=0; i < NUM_BUTTONS; i++) {
    buttons[i].setDebounceTime(50); // set debounce time to 50 milliseconds
  }
}

void updateButtons() {
  for(int i=0; i < NUM_BUTTONS; i++) buttons[i].loop();
  for(int i=0; i < NUM_BUTTONS; i++) {
    if(buttons[i].isPressed()) pressed(i);
    if(buttons[i].isReleased()) released(i);
  }
}


////////////////////////////////////////////////////////////////
// FastLED: Control (5v) WS2812 RGB LED Modules in a strip
// Uses FastLED's library: https://github.com/FastLED/FastLED
// -------------------------------------------------------
// Wiring info: https://learn.adafruit.com/digital-led-strip/wiring
// LED  ->  Arduino
// --------------
// 5V   ->  5V
// GND  ->  GND
// DIN  ->  Digital 2
////////////////////////////////////////////////////////////////

// LED library includes
#include <FastLED.h>
#define DATA_PIN 2

// lights config
#define NUM_LEDS 100
CRGB leds[NUM_LEDS];

// interval between hardware updates
static unsigned long lastLEDTime = 0;
const long ledInterval = 16;


////////////////////////////
// LED communication
////////////////////////////

void initLEDs() {
  FastLED.addLeds<WS2812B, DATA_PIN>(leds, NUM_LEDS);
}

void updateLEDs() {
  // set delay between updates
  if (now < lastLEDTime + ledInterval) return;
  lastLEDTime = now;

  // update all LEDs every frame
  CRGB curColor = CRGB(10, 10, 10);
  int numLights = 10;
  if(buttons[0].getState() == 0) { numLights = 20; curColor = CRGB(10, 10, 80); }
  if(buttons[1].getState() == 0) { numLights = 30; curColor = CRGB(80, 10, 10); }
  if(buttons[2].getState() == 0) { numLights = 40; curColor = CRGB(10, 80, 10); }
  for(int i = 0; i < NUM_LEDS; i++) {
    if(i < numLights) {
      leds[i] = curColor; // CRGB::White;
    } else {
      leds[i] = CRGB::Black;
    }
  }

  // Show the leds (only one of which is set to white, from above)
  FastLED.show();
}


////////////////////////////
// Main app
////////////////////////////

void setup() {
  Serial.begin(115200);
  Serial.println("Buttons started");
  initButtons();
  initLEDs();
}

void loop() {
  now = millis(); // get durrent time. usually a single device interface would use something like `delay(100)`
  updateButtons();
  updateLEDs();
}

void pressed(int index) {
  Serial.print("The button "); 
  Serial.print(index + 1); 
  Serial.println(" is pressed");
}

void released(int index) {
  Serial.print("The button "); 
  Serial.print(index + 1); 
  Serial.println(" is released");
}
