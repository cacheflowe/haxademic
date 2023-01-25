#include "Servo.h"

int diodes = 8;
int startPin = 22;
int frameCount = 1;
int lastTimeLaser = 0;

int motorPin = 2;
Servo myServo = Servo();
int lastTimeServo = 0;


void setup() {
  for(int i=0; i < diodes; i++) {
    pinMode(startPin + i, OUTPUT);
  }
  // pinMode(motorPin, OUTPUT);
  myServo.attach(motorPin);
}

void loop() {
  updateLasers();
  updateServo();
}

void updateLasers() {
  if(millis() > lastTimeLaser + 100) {
    lastTimeLaser = millis();

    frameCount++;

    for(int i=0; i < diodes; i++) {
      int offIndex = frameCount % diodes;
      if(i == offIndex) {
        digitalWrite(startPin + i, LOW);
      } else {
        digitalWrite(startPin + i, HIGH);
      } 
    }
  }
}

void updateServo() {
  if(millis() > lastTimeServo + 150) {
    lastTimeServo = millis();

    // analogWrite(motorPin, 3);
    // myServo.write(round(92.0 + 8.0 * sin(millis() * 0.003)));
    myServo.write(round(92.0 + 3.));
  }
}