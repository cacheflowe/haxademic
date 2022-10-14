////////////////////////////////////////////////////////////////
// Piezo: Buzzer noises!
// No library needed
// ----------------------------------------
// Wiring info (2 wires):
// - Digital pin #9 & #7
// - GND
////////////////////////////////////////////////////////////////

const int buzzer1 = 9; // buzzer to arduino pin 9
const int buzzer2 = 7; // buzzer to arduino pin 7
const int ledPin = 13; // onboard LED pin
const int ledPinR = 4; // onboard LED pin
const int ledPinG = 5; // onboard LED pin

static unsigned long lastPiezoTime = 0;
int piezoInterval = 100;
int flip = 0;
int onOffMode = 0;
int onTime = 100;
int offTime = 1000;
int time1 = 0; 
int time2 = 0;

////////////////////////////////////////////////////////////////
// VL53L0X: Read distance from sensor in mm
// Uses Adafruit's library: Adafruit_VL53L0X
// ----------------------------------------
// Wiring info: https://learn.adafruit.com/adafruit-vl53l0x-micro-lidar-distance-sensor-breakout/arduino-code
// Sensor  ->  Arduino
// --------------
// VIN     ->  VIN
// GND     ->  GND
// SCL     ->  SCL (aka A5)
// SDA     ->  SDA (aka A4)
////////////////////////////////////////////////////////////////

// sensor library includes
#include "Adafruit_VL53L0X.h"

// sensor object
Adafruit_VL53L0X lox = Adafruit_VL53L0X();
int sensorMM = 0;
static unsigned long lastSensorTime = 0;
const long sensorInterval = 100;

// global 
unsigned long now;

void setup(){
  Serial.begin(115200);
  initPiezos();
  initSensor();
}

////////////////////////////
// Main loop
////////////////////////////

void loop(){
  now = millis(); // get durrent time. usually a single device interface would use something like `delay(100)`
  updatePiezos();
  updateSensor();
}

////////////////////////////
// Piezo communication
////////////////////////////

void initPiezos() {
  pinMode(buzzer1, OUTPUT); // Set buzzer - pin 9 as an output
  pinMode(buzzer2, OUTPUT);
  pinMode(ledPin, OUTPUT);
  pinMode(ledPinR, OUTPUT);
  pinMode(ledPinG, OUTPUT);
}

void updatePiezos() {
  // set delay between updates
  if (now < lastPiezoTime + piezoInterval) return;
  lastPiezoTime = now;

  // get oscillation values
  int distToBuzzTime = constrain(sensorMM, 0, 300);
  time1 += distToBuzzTime; // millis() / 5;
  time2 += distToBuzzTime * 2; // millis() / 2;
  while(time1 > 1000) time1 -= 1000;
  while(time2 > 1000) time2 -= 1000;

  // turn on or off!
  onOffMode++;
  if(onOffMode % 2 == 0) {
    // flip back & forth between piezos
    flip++;
    if(flip % 2 == 0) {
      tone(buzzer1, 100 + time1 % 500);
      digitalWrite(ledPinR, HIGH);
    } else {
      tone(buzzer2, 100 + time2 % 1000);  // random(100, 5000)
      digitalWrite(ledPin, HIGH);         // onboard LED metronome
      digitalWrite(ledPinG, HIGH);
    }
  } else {
    // silence & delay
    noTone(buzzer1);  
    noTone(buzzer2);           
    digitalWrite(ledPin, LOW);   
    digitalWrite(ledPinR, LOW);
    digitalWrite(ledPinG, LOW);
  }
  piezoInterval = sensorMM;
}

////////////////////////////
// Sensor communication
////////////////////////////

void initSensor() {
  Serial.println("mAdafruit VL53L0X initializing");

  // Attempt to initialize the sensor library
  if (!lox.begin()) {
    Serial.println(F("Failed to boot VL53L0X"));
    while(1);
  }
  // got power!
  Serial.println("VL53L0X API Started"); 
}

void updateSensor() {
  // set delay between updates
  if (now < lastSensorTime + sensorInterval) return;
  lastSensorTime = now;

  // grab & store sensor data
  VL53L0X_RangingMeasurementData_t measure;
  lox.rangingTest(&measure, false); // pass in 'true' to get debug data printout!

  if (measure.RangeStatus != 4) {  // phase failures have incorrect data
    Serial.println("Distance (mm): " + String(measure.RangeMilliMeter)); 
    sensorMM = measure.RangeMilliMeter;
  } else {
    // Serial.println(" out of range ");
  }
}
