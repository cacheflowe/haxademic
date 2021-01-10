// Controls WS2812 RGB LED Modules in a strip
// Wiring diagram: https://learn.adafruit.com/digital-led-strip/wiring
// -5V  = 5V
// -GND = GND
// -DIN = Digital 2

// For led chips like WS2812, which have a data line, ground, and power, you just
// need to define DATA_PIN. 

/*****************************************************************************/

// hardware setup
#include <FastLED.h>
#define DATA_PIN 2

// lights config
#define NUM_LEDS 128
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
//  FastLED.addLeds<LPD8806, DATA_PIN, CLOCK_PIN, RGB>(leds, NUM_LEDS);  // GRB ordering is typical
  FastLED.addLeds<WS2812B, DATA_PIN>(leds, NUM_LEDS);
  
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
//   runSingleDotPulseDemo();
  runRgbSerial();
}

/////////////////////////////
// Serial input mode
/////////////////////////////

void runRgbSerial() {
  while (Serial.available()) {
    processIncomingByte(Serial.read());
  }
  FastLED.show();
  clearBytes();
  delay(16);
  
//  Serial.println ("...");
}

void clearBytes() {
  while(Serial.read() >= 0);
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
//  Serial.flush();
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

void processCmdByte (byte c) {
  switch (c) {
    case 'n':
      clearBytes();
      break;
    case 'c':
//      Serial.println ("C");
//      Serial.flush();
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
  // if we get a 'c' command out of line, start over
  if(val == 'c') {
    clearBytes();
    return newColorMode();
  }
  
  // set value in index
  curRgbInput[rgbInputValIndex] = val;
  
  // advance rgb array index 
  // and write to LEDs when the array is filled
  rgbInputValIndex++;
  if(rgbInputValIndex == colorArrSize) {
    int ledIndex = curRgbInput[0];
    if(ledIndex < NUM_LEDS) {
      leds[ledIndex] = CRGB(curRgbInput[1], curRgbInput[2], curRgbInput[3]);
      rgbInputValIndex = 0;
      // if(ledIndex == NUM_LEDS - 1) clearBytes(); // clear bytes on last light?
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
