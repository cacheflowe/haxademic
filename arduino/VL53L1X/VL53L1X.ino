/*
This example shows how to take simple range measurements with the VL53L1X. The
range readings are in units of mm.
*/

#include <Wire.h>
#include <VL53L1X.h>

VL53L1X sensor;

void setup()
{
  // Connect to serial
  Serial.begin(115200);
  Serial.println("mVL53L1X initializing");
  Wire.begin();
  Wire.setClock(400000); // use 400 kHz I2C

  // Attempt to initialize the sensor library
  Serial.println("mSerial connecting");
  sensor.setTimeout(500);
  if (!sensor.init()) {
    Serial.println(F("mFailed to detect and initialize VL53L1X"));
    while (1);
  } else {
    Serial.println("mVL53L1X API Started"); 
  }

  // Configure the sensor
  // Use long distance mode and allow up to 50000 us (50 ms) for a measurement.
  // You can change these settings to adjust the performance of the sensor, but
  // the minimum timing budget is 20 ms for short distance mode and 33 ms for
  // medium and long distance modes. See the VL53L1X datasheet for more
  // information on range and timing limits.
  sensor.setDistanceMode(VL53L1X::Long);
  sensor.setMeasurementTimingBudget(50000);

  // Start continuous readings at a rate of one measurement every 50 ms (the
  // inter-measurement period). This period should be at least as long as the
  // timing budget.
  sensor.startContinuous(50);
  Serial.println("mVL53L1X API Configured"); 
}

void loop() {
  int distanceMM = sensor.read();
  if (sensor.timeoutOccurred()) { 
    Serial.println(F("mVL53L0X error: Timeout"));
  } else {
    Serial.println('a'+String(distanceMM));
  }

  // 10 readings per second
  delay(100);
}
