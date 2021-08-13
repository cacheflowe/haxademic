////////////////////////////////////////////////////////////////
// VL53L1X: Read distance from sensor in mm
// Uses Polulu's library: https://github.com/pololu/vl53l1x-arduino
// ----------------------------------------
// Wiring info: https://learn.adafruit.com/adafruit-vl53l0x-micro-lidar-distance-sensor-breakout/arduino-code
// Sensor  ->  Arduino
// --------------
// VIN     ->  VIN
// GND     ->  GND
// SCL     ->  SCL (aka A5)
// SDA     ->  SDA (aka A4)
////////////////////////////////////////////////////////////////

#include <Wire.h>
#include <VL53L1X.h>

VL53L1X sensor;
int frameCount = 0;

void setup()
{
  // Connect to serial
  Serial.begin(115200);
  Serial.println("mVL53L1X initializing");
  Wire.begin();
  Wire.setClock(400000); // use 400 kHz I2C

  // Attempt to initialize the sensor library
  Serial.println("mSerial connecting");
  sensor.setTimeout(2000);  // was 500
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
  sensor.startContinuous(100);
  Serial.println("mVL53L1X API Configured"); 
}

void loop() {
  frameCount++;
  frameCount = frameCount % 36000;  // loop every hour @ 10fps
  
  int distanceMM = sensor.read();
  if (sensor.timeoutOccurred()) { 
    Serial.println(F("mVL53L0X error: Timeout"));
    // restart program if timeout happens, for self-recovery
    asm volatile ("  jmp 0");
  } else {
    // send value - add delay before writing serial
    delay(20);
    Serial.println('a'+String(distanceMM));
    
    // print uptime - add another delay
    if(frameCount % 10 == 0) {
      delay(20);
      Serial.println("mUptime:"+String(frameCount/10/60.)+"m");
    }
  }

  // 10 readings per second
  delay(80);
}
