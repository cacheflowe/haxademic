////////////////////////////////////////////////////////////////
// VL53L1X: Read distance from sensor in mm
// Uses Polulu's library: https://github.com/pololu/vl53l1x-arduino
// And the CQRobot sensor: http://www.cqrobot.wiki/index.php/VL53L1X_Distance_Ranging_Sensor_SKU:_CQRWX00744US
// ----------------------------------------
// from: https://github.com/pololu/vl53l1x-arduino/blob/master/examples/ContinuousMultipleSensors/ContinuousMultipleSensors.ino
// --------------
// Each sensor needs to have its XSHUT pin connected to a different Arduino pin, and you should change sensorCount and the xshutPins array below to match your setup.
// --------------
// VIN     ->  VIN (shared 5v)
// GND     ->  GND
// SCL     ->  SCL 1
// SDA     ->  SDA 1
// XSHUT   ->  Digital 6
// Sensor 2 - shared VIN & GND, and:
// SCL     ->  SCL 2
// SDA     ->  SDA 2
// XSHUT   ->  Digital 7
////////////////////////////////////////////////////////////////


// 2-sensor setup -------------------------------------
#include <Wire.h>
#include <VL53L1X.h>
const uint8_t sensorCount = 2;                    // The number of sensors in your system.
const uint8_t xshutPins[sensorCount] = { 6, 7 };  // The Arduino pin connected to the XSHUT pin of each sensor.
VL53L1X sensors[sensorCount];
static unsigned long lidarDetectTime = 0;
// 2-sensor setup -------------------------------------


// LED setup -------------------------------------
// NeoPixel library for RGBW LEDs
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif
#define DATA_PIN 2
#define NUM_LEDS 30
Adafruit_NeoPixel pixels(NUM_LEDS, DATA_PIN, NEO_GRBW + NEO_KHZ800);
int ledIndex = 0;
static unsigned long ledLastUpdate = 0;
// LED setup -------------------------------------


// Piezo setup -------------------------------------
const int knockSensor = A0;  // the piezo is connected to analog pin 0
// TODO: figure out threshold for weird voltage difference when plugged inot wall. test 50' USB extensiom
const int threshold = 100;   // threshold value to decide when the detected sound is a knock or not
int sensorReading = 0;  // variable to store the value read from the sensor pin
int lastReading = 0;
static unsigned long piezoLastUpdate = 0;
static unsigned long piezoDetectTime = 0;
// Piezo end ---------------------------------------



void setup() {
  analogReference(INTERNAL1V1);

  while (!Serial) {}
  Serial.begin(115200);

  initLidar();
  initLEDNeopixel();
}

void initLidar() {
  Wire.begin();
  Wire.setClock(400000);  // use 400 kHz I2C

  // Disable/reset all sensors by driving their XSHUT pins low.
  for (uint8_t i = 0; i < sensorCount; i++) {
    pinMode(xshutPins[i], OUTPUT);
    digitalWrite(xshutPins[i], LOW);
  }

  // Enable, initialize, and start each sensor, one by one.
  for (uint8_t i = 0; i < sensorCount; i++) {
    // Stop driving this sensor's XSHUT low. This should allow the carrier
    // board to pull it high. (We do NOT want to drive XSHUT high since it is
    // not level shifted.) Then wait a bit for the sensor to start up.
    pinMode(xshutPins[i], INPUT);
    delay(10);

    sensors[i].setTimeout(100);  // 500
    if (!sensors[i].init()) {
      Serial.print("Failed to detect and initialize sensor ");
      Serial.println(i);
      while (1)
        ;
    }

    // Each sensor must have its address changed to a unique value other than
    // the default of 0x29 (except for the last one, which could be left at
    // the default). To make it simple, we'll just count up from 0x2A.
    sensors[i].setAddress(0x2A + i);

    // super fast reading settings
    // not sure why these work so well, but they do
    sensors[i].setDistanceMode(VL53L1X::Short);
    sensors[i].setMeasurementTimingBudget(5000);  // 50000
    sensors[i].startContinuous(5);                // 50
  }
}

void loop() {
  updateLidar();
  updateLEDNeopixel();
  updatePiezo();
}

void updateLidar() {
  for (uint8_t i = 0; i < sensorCount; i++) {
    int val = sensors[i].read();
    if (val > 0 && val < 450) {
      lidarDetectTime = millis();
      // Serial.print("detect: ");
      // Serial.println(lidarDetectTime);
      // TODO: send several messages in a row, ensuring that they're received
    }
    // Serial.println(val);
    if (sensors[i].timeoutOccurred()) {
      Serial.println(" TIMEOUT");
      // TODO: Restart program if this happens, and send a serial message so we know!
    }
  }
}

void initLEDNeopixel() {
  pixels.begin();
}

void updateLEDNeopixel() {
  if (millis() < ledLastUpdate + 50) return;
  ledLastUpdate = millis();

  for (int i = 0; i < NUM_LEDS; i++) {
    int luma = (i == ledIndex) ? 20 : 0;
    if (millis() < lidarDetectTime + 150) luma = 100;
    int r = luma;
    int g = luma;
    int b = luma;
    int w = luma;
    if (millis() < piezoDetectTime + 150) {
      r = 0;
      g = 100;
      b = 0;
      w = 0;
    };
    pixels.setPixelColor(i, pixels.Color(r, g, b, w));
  }

  // pixels.clear();
  pixels.show();
  ledLastUpdate = millis();
  ledIndex++;
  ledIndex = ledIndex % NUM_LEDS;
}

void updatePiezo() {
  if (millis() < piezoLastUpdate + 20) return;
  piezoLastUpdate = millis();

  int sensorReading = 0; // create variable to store many different readings
  for (int i = 0; i < 16; i++) {
    sensorReading += analogRead(knockSensor);
  }
  if(sensorReading > threshold && lastReading != sensorReading) {
    Serial.print("Knock: ");
    Serial.println(sensorReading);
    piezoDetectTime = millis();
  }
  lastReading = sensorReading;
}
