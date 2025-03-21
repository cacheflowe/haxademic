////////////////////////////////////////////////////////////////
// VL53L1X: Read distance from sensor in mm
// Uses Polulu's library: https://github.com/pololu/vl53l1x-arduino
// And the CQRobot sensor: http://www.cqrobot.wiki/index.php/VL53L1X_Distance_Ranging_Sensor_SKU:_CQRWX00744US
// ----------------------------------------
// from: https://github.com/pololu/vl53l1x-arduino/blob/master/examples/ContinuousMultipleSensors/ContinuousMultipleSensors.ino
// --------------
// LIDAR wiring:
// Each sensor needs to have its XSHUT pin connected to a different Arduino pin, and you should change sensorCount and the xshutPins array below to match your setup.
// --------------
// VIN     ->  VIN (shared VIN)
// GND     ->  GND
// SCL     ->  SCL 1
// SDA     ->  SDA 1
// XSHUT   ->  Digital 6
// Sensor 2 - shared VIN & GND:
// VIN     ->  VIN (shared VIN)
// GND     ->  GND
// SCL     ->  SCL 2
// SDA     ->  SDA 2
// XSHUT   ->  Digital 7
// --------------
// Piezo wiring:
// '-'     ->  GND
// '+'     ->  VIN / 5V
// 'S'     ->  A0
////////////////////////////////////////////////////////////////


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



// Helpers for calculating fps & heartbeats ------------------
RunningAverage fps(5);
static unsigned long lastUpdateTime = 0;

uint8_t heartbeatInterval = 2; // seconds
unsigned long heartbeatLastUpdate = 0;
// Helpers end -----------------------------------------------


// force reboot helper ---------------------------------------
void(* Reboot)(void) = 0;


void setup() {
  analogReference(INTERNAL1V1);

  while (!Serial) {}
  Serial.begin(115200);

  initLidar();
  // initLEDNeopixel();

  Serial.println("Arduino Ready!");
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
      Serial.print("Failed to de_ec_ and ini_ialize sensor");
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
    // Despite the min timing budget being 20K, setting it to 5K and checking the value it reports as 5016 so it is being accepted
    sensors[i].setMeasurementTimingBudget(5000);  // 50000
    auto timingBudget = sensors[i].getMeasurementTimingBudget();  // 50000

    uint8_t roiW, roiH = 0;
    // 12 x 12 seems to work well without issue of timeouts at
    // [not true!] 10 x 10 also seems to not have timeout issues, AND it seems less noisy
    // specifically, 12x12 or bigger will flicker between 65535 and lower vals. Solid at 10x10
    sensors[i].setROISize(12, 12);
    sensors[i].getROISize(&roiW, &roiH);
    auto roiCenter = sensors[i].getROICenter();
    Serial.print("Sensor number: ");  Serial.println(i);
    Serial.print("_iming budget: ");  Serial.println(timingBudget);
    Serial.print("ROI wid_h: ");      Serial.println(roiW);
    Serial.print("ROI heigh_: ");     Serial.println(roiH);
    Serial.print("ROI cen_er: ");     Serial.println(roiCenter);
    Serial.println();

    sensors[i].startContinuous(5);                // 50
  }
}

void loop() {
  updateLidar();
  // updateLEDNeopixel();
  updatePiezo();
  updateHeartbeat();

  // logSerialData();

}

void logSerialData() {
  auto now = millis();
  auto frameDelta = now - lastUpdateTime;
  lastUpdateTime = now;
  fps.addValue(frameDelta);

  // Log all sensor data
  Serial.print("d\t");

  // Averaged distance sensor readings
  for (auto i = 0; i < sensorCount; i++) {
    Serial.print(distanceSensorReadings[i].getAverage()); Serial.print("\t");
  }

  // Piezo reading
  Serial.print(piezoLastReading); Serial.print("\t");

  // Distance sensor status & raw value
  for (auto i = 0; i < sensorCount; i++) {
    auto& sensorData = sensors[i].ranging_data;
    Serial.print(sensorData.range_status); Serial.print("\t");
    Serial.print(sensorData.range_mm); Serial.print("\t");
  }

  // Frame elapsed time & average fps
  Serial.print("elapsed: "); Serial.print(frameDelta); Serial.print(" ms\t");
  Serial.print("fps: "); Serial.print(1000 / fps.getAverage()); Serial.println("");
}

void updateLidar() {

  bool presenceDetected = false;

  for (uint8_t i = 0; i < sensorCount; i++) {
    // Call read() first so that ranging_data & range_status get updated
    uint16_t distance = sensors[i].read();
    auto& status = sensors[i].ranging_data.range_status;

    // Overwrite invalid reads with a high distance value
    // Using 0 as a throwaway value will lower our resulting
    // averaged value closer to our target range of 0-450,
    // so use a high value instead of low
    if (status == VL53L1X::SigmaFail ||
       status == VL53L1X::SignalFail ||
       status == VL53L1X::OutOfBoundsFail) {

        distance = sensorMaxValue;
    }

    distanceSensorReadings[i].addValue(distance);

    auto averageReading = distanceSensorReadings[i].getAverage();
    if (averageReading > distanceMinThreshold && averageReading < distanceMaxThreshold) {
      presenceDetected = true;
    }

    if (sensors[i].timeoutOccurred()) {
      // Restart program if this happens, and send a serial message so we know!
      Serial.println("t"); // timeout
      Reboot();
    }
  }

  if (presenceDetected) {
    Serial.println("p");
    lidarDetectTime = millis();
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
  if(sensorReading > piezoThreshold && piezoLastReading != sensorReading) {
    Serial.println("v"); // vibration
    // Serial.println(sensorReading); // remove for production
    piezoDetectTime = millis();
  }
  piezoLastReading = sensorReading;
}

void updateHeartbeat() {
  auto now = millis();
  if (now - heartbeatLastUpdate > (heartbeatInterval * 1000)) {
    heartbeatLastUpdate = now;
    Serial.println("h");
  }
}
