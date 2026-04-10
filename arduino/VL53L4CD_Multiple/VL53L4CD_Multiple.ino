/**
******************************************************************************
* @file    VL53L4CD_Sat_HelloWorld.ino
* @author  STMicroelectronics
* @version V1.0.0
* @date    29 November 2021
* @brief   Arduino test application for the STMicrolectronics VL53L4CD
*          proximity sensor satellite based on FlightSense.
*          This application makes use of C++ classes obtained from the C
*          components' drivers.
******************************************************************************
*
*
* IMPORTANT: You CANNOT simply daisy chain two VL53L4CD sensors without XSHUT control!
* Both sensors boot with the same I2C address (0x29), causing conflicts.
*
* WIRING FOR MULTIPLE SENSORS:
*
* SENSOR 1:
* - STEMMA QT connector to Arduino I2C (SDA/SCL shared)
* - XSHUT pin connected to Arduino pin A0
*
* SENSOR 2,3,4:
* - STEMMA QT connector to Arduino I2C (SDA/SCL shared via daisy chain)
* - XSHUT pin connected to Arduino pin A1, A2, A3 respectively
*
* The XSHUT pins allow us to control which sensor is active during initialization,
* preventing I2C address conflicts during startup.
*
* Next time use this stemma qt hub: https://learn.adafruit.com/adafruit-pca9548-8-channel-stemma-qt-qwiic-i2c-multiplexer/arduino
*
* Info:
* - Hello world code updated by @cacheflowe to achieve ~100fps updates
* - Modified to support 2+ sensors running in tandem
* - Runs on an Adafruit Metro Mini w/Stemma QT connectors
*   - Select Adafruit AVR Boards -> Adafruit Metro
* - Uses robust sensor initialization to avoid I2C conflicts
* - References:
*   - https://github.com/stm32duino/VL53L4CD/blob/main/src/vl53l4cd_api.cpp
*/

/* Includes ------------------------------------------------------------------*/
#include <Arduino.h>
#include <Wire.h>
#include <vl53l4cd_class.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <assert.h>
#include <stdlib.h>

#define DEV_I2C Wire
#define SerialPort Serial

#ifndef LED_BUILTIN
  #define LED_BUILTIN 13
#endif
#define LedPin LED_BUILTIN


// Number of sensors
const uint8_t NUM_SENSORS = 4;

// Sensor configuration
struct SensorConfig {
  VL53L4CD* sensor;
  uint8_t xshutPin;
  uint8_t i2cAddress;
  const char* name;
};

VL53L4CD sensor1(&DEV_I2C, A0);
VL53L4CD sensor2(&DEV_I2C, A1);
VL53L4CD sensor3(&DEV_I2C, A2);
VL53L4CD sensor4(&DEV_I2C, A3);

SensorConfig sensors[NUM_SENSORS] = {
  {&sensor1, A0, 0x30, "S1"}, // Sensor addresses (default is 0x29, we'll change sensor 2 to 0x2A, etc)
  {&sensor2, A1, 0x2F, "S2"},
  {&sensor3, A2, 0x2A, "S3"},
  {&sensor4, A3, 0x3A, "S4"},
};

// Sensor state
struct SensorState {
  uint16_t distance;
  uint8_t signal;
  bool hasNewData;
};

SensorState sensorStates[NUM_SENSORS];

// FPS calculation variables
unsigned long lastFpsTime = 0;
const unsigned long FPS_UPDATE_INTERVAL = 1000; // Update FPS every second
unsigned long readingCount = 0;
float currentFps = 0.0;

// Minimum signal strength to consider a valid reading
uint16_t MIN_STRENGTH = 5; // kcps/spad - make this non-const so it can be changed

// Detection zone parameters (tune these based on your setup)
uint16_t MIN_DETECTION_DISTANCE = 100;   // mm - closer than this is ignored (mounting surface)
uint16_t MAX_DETECTION_DISTANCE = 500; // mm - farther than this is ignored (background)

/* Setup ---------------------------------------------------------------------*/

void setup()
{
  pinMode(LedPin, OUTPUT);
  SerialPort.begin(115200);
  SerialPort.println("INFO: VL53L4CD Dual Sensor Test");
  SerialPort.println("INFO: Commands: min <value>, max <value>, strength <value>, show");

  DEV_I2C.begin();
  DEV_I2C.setClock(400000); // 400kHz I2C (Fast Mode)

  initializeSensors();
}

void initializeSensors() {
  SerialPort.println("INFO: Initializing Sensors (Robust Mode)");
  
  // 1. Turn EVERYTHING OFF first
  // This ensures the bus is completely silent
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    pinMode(sensors[i].xshutPin, OUTPUT);
    digitalWrite(sensors[i].xshutPin, LOW);
  }
  delay(100);
  
  // 2. Initialize one by one
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    SerialPort.println("INFO: Configuring " + String(sensors[i].name) + "...");
    
    // Power up ONLY this sensor
    // Since all others are LOW, this is the ONLY device at 0x29
    digitalWrite(sensors[i].xshutPin, HIGH);
    delay(50); // Give it time to boot
    
    // Initialize sensor (it talks at 0x29)
    sensors[i].sensor->begin();
    if (sensors[i].sensor->InitSensor() != 0) {
      SerialPort.println("INFO: ERROR: " + String(sensors[i].name) + " init failed!");
      // If it fails, turn it off so it doesn't mess up the next one
      digitalWrite(sensors[i].xshutPin, LOW);
      continue;
    }
    
    // Change address immediately
    // We move it from 0x29 to its assigned address (e.g., 0x30)
    uint8_t newAddress = sensors[i].i2cAddress;
    if (sensors[i].sensor->VL53L4CD_SetI2CAddress(newAddress) != 0) {
      SerialPort.println("INFO: ERROR: " + String(sensors[i].name) + " address change failed!");
      digitalWrite(sensors[i].xshutPin, LOW);
      continue;
    }
    
    SerialPort.println("INFO: " + String(sensors[i].name) + " address set to 0x" + String(newAddress, HEX));
    
    // IMPORTANT: We leave this sensor ON.
    // Since it is now at 0x30 (or 31/32), it will NOT conflict 
    // with the next sensor which will boot at 0x29.
  }
  
  SerialPort.println("INFO: All sensors addressed. Starting ranging...");
  delay(100);
  
  // 3. Start Ranging for all active sensors
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    // Only start if the sensor is actually powered
    if (digitalRead(sensors[i].xshutPin) == HIGH) {
      sensors[i].sensor->VL53L4CD_SetRangeTiming(10, 0);
      sensors[i].sensor->VL53L4CD_StartRanging();
    }
  }
  
  SerialPort.println("INFO: All sensors ready!");
}

void loop() {
  // Check for serial commands
  checkSerialCommands();
  
  static VL53L4CD_Result_t results;
  bool anyNewData = false;
  
  // Check all sensors
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    uint8_t ready = 0;
    sensors[i].sensor->VL53L4CD_CheckForDataReady(&ready);
    
    if (ready) {
      sensors[i].sensor->VL53L4CD_ClearInterrupt();
      sensors[i].sensor->VL53L4CD_GetResult(&results);
      
      // Filter by distance range AND signal strength
      if (results.signal_per_spad_kcps >= MIN_STRENGTH && 
          results.distance_mm >= MIN_DETECTION_DISTANCE && 
          results.distance_mm <= MAX_DETECTION_DISTANCE) {
        sensorStates[i].distance = results.distance_mm;
      } else {
        sensorStates[i].distance = 0; // Outside detection zone
      }
      sensorStates[i].signal = results.signal_per_spad_kcps;
      sensorStates[i].hasNewData = true;
      anyNewData = true;
    }
  }
  
  // Print if any sensor has new data AND at least one has a valid detection
  if (anyNewData) {
    readingCount++;

    // Check if any sensor has a non-zero distance
    bool hasValidDetection = false;
    for (uint8_t i = 0; i < NUM_SENSORS; i++) {
      if (sensorStates[i].distance > 0) {
        hasValidDetection = true;
        break;
      }
    }
    
    if (hasValidDetection) {
      SerialPort.print("DATA:");
      
      for (uint8_t i = 0; i < NUM_SENSORS; i++) {
        SerialPort.print(sensorStates[i].distance);
        SerialPort.print(',');
        SerialPort.print(sensorStates[i].signal);
        
        if (i < NUM_SENSORS - 1) {
          SerialPort.print(',');
        }
        
        sensorStates[i].hasNewData = false;
      }
      
      SerialPort.println();
    } else {
      // Reset hasNewData flags even if we didn't print
      for (uint8_t i = 0; i < NUM_SENSORS; i++) {
        sensorStates[i].hasNewData = false;
      }
    }
  }
  
  updateFPS();
}

void checkSerialCommands() {
  static char commandBuffer[32];
  static uint8_t bufferIndex = 0;
  
  while (SerialPort.available() > 0) {
    char c = SerialPort.read();
    
    if (c == '\n' || c == '\r') {
      if (bufferIndex > 0) {
        commandBuffer[bufferIndex] = '\0';
        processCommand(commandBuffer);
        bufferIndex = 0;
      }
    } else if (bufferIndex < sizeof(commandBuffer) - 1) {
      commandBuffer[bufferIndex++] = c;
    }
  }
}

/*
Type min 200 to set minimum detection distance to 200mm
Type max 800 to set maximum detection distance to 800mm
Type strength 10 to set minimum signal strength to 10 kcps
Type show to see current settings
*/
void processCommand(char* command) {
  char* token = strtok(command, " ");
  
  if (token == NULL) return;
  
  if (strcmp(token, "min") == 0) {
    token = strtok(NULL, " ");
    if (token != NULL) {
      uint16_t newMin = atoi(token);
      if (newMin >= 0 && newMin < MAX_DETECTION_DISTANCE) {
        MIN_DETECTION_DISTANCE = newMin;
        SerialPort.println("INFO: MIN set to " + String(MIN_DETECTION_DISTANCE) + " mm");
      } else {
        SerialPort.println("INFO: ERROR: Invalid MIN value");
      }
    }
  } else if (strcmp(token, "max") == 0) {
    token = strtok(NULL, " ");
    if (token != NULL) {
      uint16_t newMax = atoi(token);
      if (newMax > MIN_DETECTION_DISTANCE && newMax <= 1300) {
        MAX_DETECTION_DISTANCE = newMax;
        SerialPort.println("INFO: MAX set to " + String(MAX_DETECTION_DISTANCE) + " mm");
      } else {
        SerialPort.println("INFO: ERROR: Invalid MAX value");
      }
    }
  } else if (strcmp(token, "strength") == 0) {
    token = strtok(NULL, " ");
    if (token != NULL) {
      uint16_t newStrength = atoi(token);
      if (newStrength >= 0 && newStrength <= 100) {
        MIN_STRENGTH = newStrength;
        SerialPort.println("INFO: MIN_STRENGTH set to " + String(MIN_STRENGTH) + " kcps");
      } else {
        SerialPort.println("INFO: ERROR: Invalid STRENGTH value (0-100)");
      }
    }
  } else if (strcmp(token, "show") == 0) {
    SerialPort.println("INFO: MIN=" + String(MIN_DETECTION_DISTANCE) + "mm, MAX=" + String(MAX_DETECTION_DISTANCE) + "mm, MIN_STRENGTH=" + String(MIN_STRENGTH) + "kcps");
  } else {
    SerialPort.println("INFO: Unknown command. Available: min, max, strength, show");
  }
}

void updateFPS() {
  unsigned long currentTime = millis();
  if (currentTime - lastFpsTime >= FPS_UPDATE_INTERVAL) {
    currentFps = (float)readingCount * 1000.0 / (currentTime - lastFpsTime);
    SerialPort.print("FPS:");
    SerialPort.println(currentFps, 1);
    readingCount = 0;
    lastFpsTime = currentTime;
  }
}
