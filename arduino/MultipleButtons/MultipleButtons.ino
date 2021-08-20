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

ezButton button1(6);  // create ezButton object that attach to pin 6;
ezButton button2(7);  // create ezButton object that attach to pin 7;
ezButton button3(8);  // create ezButton object that attach to pin 8;

void setup() {
  Serial.begin(115200);
  Serial.println("Buttons started");
  button1.setDebounceTime(50); // set debounce time to 50 milliseconds
  button2.setDebounceTime(50); // set debounce time to 50 milliseconds
  button3.setDebounceTime(50); // set debounce time to 50 milliseconds
}

void loop() {
  // update button objects & respond to events
  button1.loop();
  if(button1.isPressed()) Serial.println("The button 1 is pressed");
  if(button1.isReleased()) Serial.println("The button 1 is released");
  button2.loop();
  if(button2.isPressed()) Serial.println("The button 2 is pressed");
  if(button2.isReleased()) Serial.println("The button 2 is released");
  button3.loop();
  if(button3.isPressed()) Serial.println("The button 3 is pressed");
  if(button3.isReleased()) Serial.println("The button 3 is released");

  // get current state - probably unnecessary
  int btn1State = button1.getState();
  int btn2State = button2.getState();
  int btn3State = button3.getState();
  // stream state - 1=off, 0=on
  //  Serial.print("button 1 state: ");
  //  Serial.println(btn1State);
  //  Serial.print("button 2 state: ");
  //  Serial.println(btn2State);
  //  Serial.print("button 3 state: ");
  //  Serial.println(btn3State);

}
