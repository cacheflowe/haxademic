// hardware assembly: https://learn.adafruit.com/adafruit-vl53l0x-micro-lidar-distance-sensor-breakout/arduino-code
// VIN -> VIN
// GND -> GND
// SCL -> SCL || A5
// SDA -> SDA || A4

#include "Adafruit_VL53L0X.h"

Adafruit_VL53L0X lox = Adafruit_VL53L0X();

void setup() {
  // Connect to serial
  Serial.begin(115200);
  Serial.println("mAdafruit VL53L0X initializing");
  delay(100);
  
  // Wait until serial port opens to connect to the sensor
  while (! Serial) {
    delay(100);
    Serial.println("mSerial connecting");
  }

  // Attempt to initialize the sensor library
  Serial.println("mAdafruit VL53L0X waiting");
  if (!lox.begin()) {
    Serial.println(F("mFailed to boot VL53L0X"));
    while(100);
  } else {
    // power 
    Serial.println("mVL53L0X API Started!"); 
  }
}


void loop() {
  // Prep to read a measurement
  VL53L0X_RangingMeasurementData_t measure;
  lox.rangingTest(&measure, false); // pass in 'true' to get debug data printout!

  // Send data back up through serial, prepended with 'a'
  if (measure.RangeStatus != 4) {  // phase failures have incorrect data
    Serial.println('a'+String(measure.RangeMilliMeter));
  } else {
    Serial.println('a'+String(0));  // 
  }

  // 10 readings per second
  delay(100);
}
