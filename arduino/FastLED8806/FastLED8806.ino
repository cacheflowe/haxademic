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

/*****************************************************************************/

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
