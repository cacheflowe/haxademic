////////////////////////////////////////////////////////////////
// Button wiring:
// - GND
// - D3
// - When you touch 3 to ground or trigger momentary button, the buttons are triggered
// Relay wiring for control:
// - VCC to 5V
// - GND to GND
// - IN1 to D6
// Wire electrical component to relay module
// - VCC to NO 
// - GND to GND
////////////////////////////////////////////////////////////////

#include <ezButton.h>

// initialize button objects with pin numbers
const int NUM_BUTTONS = 1;
ezButton buttons[] = {
  ezButton(3) // D3
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
// Relay Module config
// -------------------------------------------------------
// 5V   ->  5V
// GND  ->  GND
// DIN  ->  Digital 2
////////////////////////////////////////////////////////////////

// config
const int relayPin = 6;

// interval between hardware updates
static unsigned long lastRelayTime = 0;
const long relayInterval = 16;

// relay state
unsigned long relayOnTime = 0;     // time when relay was turned on
unsigned long relayTimeout = 30000; // relay timeout in milliseconds
bool relayState = false;           // current state of the relay

////////////////////////////
// Relay communication
////////////////////////////

void initRelay() {
  pinMode(relayPin, OUTPUT);
  turnOffRelay();
}

void updateRelay() {
  // set delay between updates
  if (now < lastRelayTime + relayInterval) return;
  lastRelayTime = now;

  // check relay timeout to turn off
  if (relayState == true && now > relayOnTime + relayTimeout) {
    turnOffRelay();
  }
}


////////////////////////////
// Main app
////////////////////////////

void setup() {
  Serial.begin(115200);
  Serial.println("Buttons started");
  initButtons();
  initRelay();
}

void loop() {
  now = millis(); // get durrent time. usually a single device interface would use something like `delay(100)`
  updateButtons();
  updateRelay();
}

void turnOnRelay() {
  digitalWrite(relayPin, HIGH);
  relayOnTime = now;
  relayState = true;
}

void turnOffRelay() {
  digitalWrite(relayPin, LOW);
  relayState = false;
}

void pressed(int index) {
  // Serial.print("The button "); 
  // Serial.print(index + 1); 
  // Serial.println(" is pressed");
  // toggle relay on button release
  if(relayState == true) turnOffRelay();
  else turnOnRelay();
}

void released(int index) {
  // Serial.print("The button "); 
  // Serial.print(index + 1); 
  // Serial.println(" is released");
  // toggle relay on button release
  // turnOffRelay();
}
