// Wiring:
// - Put a wire in ground
// - Put wires in Digital 6,7,8
// - When you touch 6,7,8 to ground, the buttons are triggered

// Keyboard library
// - Select Arduino Micro board, even if publishing to Arduino Nano, as the Keyboard library only work with certain boards

#include <ezButton.h>
#include <Keyboard.h>

// initialize button objects with pin numbers
const int NUM_BUTTONS = 3;
ezButton buttons[] = {
  ezButton(25),
  ezButton(26),
  ezButton(27)
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

////////////////////////////
// Main app
////////////////////////////

void setup() {
  Serial.begin(115200);
  Serial.println("Buttons started");
  initButtons();
  Keyboard.begin();
}

void loop() {
  now = millis(); // get durrent time. usually a single device interface would use something like `delay(100)`
  updateButtons();
}

void pressed(int index) {
  Serial.print("The button "); 
  Serial.print(index + 1); 
  Serial.println(" is pressed");
  doKeyPress(index);
}

void released(int index) {
  Serial.print("The button "); 
  Serial.print(index + 1); 
  Serial.println(" is released");
}

void doKeyPress(int index) {
  if(index == 0) {
    Keyboard.write(32);
  }
}