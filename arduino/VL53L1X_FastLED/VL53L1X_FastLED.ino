////////////////////////////////////////////////////////////////
// FastLED: Control (5v) WS2812 RGB LED Modules in a strip
// Uses FastLED's library: https://github.com/FastLED/FastLED
// -------------------------------------------------------
// Wiring info: https://learn.adafruit.com/digital-led-strip/wiring
// LED  ->  Arduino
// --------------
// 5V   ->  5V
// GND  ->  GND
// DIN  ->  Digital 2
////////////////////////////////////////////////////////////////

// LED library includes
#include <FastLED.h>
#define DATA_PIN 2

// lights config
#define NUM_LEDS 100
CRGB leds[NUM_LEDS];

// interval between hardware updates
static unsigned long lastLEDTime = 0;
const long ledInterval = 16;


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

// sensor library includes
#include <Wire.h>
#include <VL53L1X.h>

// sensor object
VL53L1X sensor;
int sensorMM = 0;

// interval between hardware updates
static unsigned long lastSensorTime = 0;
const long sensorInterval = 100;


////////////////////////////////////////////////////////////////
// Main app:
// Read VL53L1X data and use it to light up WS2812 LEDs
////////////////////////////////////////////////////////////////

unsigned long now;

void setup() {
  initSerial();
  initSensor();
  initLEDs();
}

void loop() {
  now = millis(); // get durrent time. usually a single device interface would use something like `delay(100)`
  updateSensor();
  updateLEDs();
}

////////////////////////////
// Make serial connection for logging to Serial Monitor
////////////////////////////

void initSerial() {
  // Connect to serial so we can watch debug values
  Serial.begin(115200);
  Serial.setTimeout(0);
  Serial.println("<Arduino is ready>");
}

////////////////////////////
// Sensor communication
////////////////////////////

void initSensor() {
  Serial.println("mVL53L1X initializing");
  Wire.begin();
  Wire.setClock(400000); // use 400 kHz I2C

  // Attempt to initialize the sensor library
  sensor.setTimeout(500);
  if (!sensor.init()) {
    Serial.println(F("Failed to detect and initialize VL53L1X"));
    while (1);
  } else {
    Serial.println("VL53L1X API Started"); 
  }

  // Configure the sensor 
  sensor.setDistanceMode(VL53L1X::Long);
  sensor.setMeasurementTimingBudget(50000);
  sensor.startContinuous(50);
  Serial.println("VL53L1X API Configured"); 
}

void updateSensor() {
  // set delay between updates
  if (now < lastSensorTime + sensorInterval) return;
  lastSensorTime = now;

  int distanceMM = sensor.read();
  if (sensor.timeoutOccurred()) { 
    Serial.println(F("VL53L0X error: Timeout"));
  } else {
    Serial.println("Dist (mm): "+String(distanceMM));
    sensorMM = distanceMM;
  }
}

////////////////////////////
// LED communication
////////////////////////////

void initLEDs() {
  FastLED.addLeds<WS2812B, DATA_PIN>(leds, NUM_LEDS);
}

void updateLEDs() {
  // set delay between updates
  if (now < lastLEDTime + ledInterval) return;
  lastLEDTime = now;

  // update all LEDs every frame
  int numToLight = sensorMM / 10;
  for(int i = 0; i < NUM_LEDS; i++) {
    // are we below threshold or not?
    if(i < numToLight) {
      leds[i] = CRGB(10, 10, 10); // CRGB::White;
    } else {
      leds[i] = CRGB::Black;
    }
  }

  // Show the leds (only one of which is set to white, from above)
  FastLED.show();
}
