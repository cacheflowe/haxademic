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
* @attention
*
* <h2><center>&copy; COPYRIGHT(c) 2021 STMicroelectronics</center></h2>
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
*   1. Redistributions of source code must retain the above copyright notice,
*      this list of conditions and the following disclaimer.
*   2. Redistributions in binary form must reproduce the above copyright notice,
*      this list of conditions and the following disclaimer in the documentation
*      and/or other materials provided with the distribution.
*   3. Neither the name of STMicroelectronics nor the names of its contributors
*      may be used to endorse or promote products derived from this software
*      without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
******************************************************************************
*/

/*
* IMPORTANT: You CANNOT simply daisy chain two VL53L4CD sensors without XSHUT control!
* Both sensors boot with the same I2C address (0x29), causing conflicts.
* 
* WIRING FOR DUAL SENSORS:
* 
* SENSOR 1:
* - STEMMA QT connector to Arduino I2C (SDA/SCL shared)
* - XSHUT pin connected to Arduino pin A1
* 
* SENSOR 2: 
* - STEMMA QT connector to Arduino I2C (SDA/SCL shared via daisy chain)
* - XSHUT pin connected to Arduino pin A0
* 
* The XSHUT pins allow us to control which sensor is active during initialization,
* preventing I2C address conflicts during startup.
*/

/* 
- Hello world code updated by @cacheflowe to achieve ~100fps updates
- Modified to support 2 sensors running in tandem
- References:
  - https://github.com/stm32duino/VL53L4CD/blob/main/src/vl53l4cd_api.cpp
- TODO:
  - Better filtering of bad data
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

// Sensor addresses (default is 0x29, we'll change sensor 2 to 0x2A)
#define SENSOR1_ADDRESS 0x29
#define SENSOR2_ADDRESS 0x2A

// Number of sensors
const uint8_t NUM_SENSORS = 2;

// Sensor configuration
struct SensorConfig {
  VL53L4CD* sensor;
  uint8_t xshutPin;
  uint8_t i2cAddress;
  const char* name;
};

VL53L4CD sensor1(&DEV_I2C, A0);
VL53L4CD sensor2(&DEV_I2C, A1);

SensorConfig sensors[NUM_SENSORS] = {
  {&sensor1, A0, SENSOR1_ADDRESS, "S1"},
  {&sensor2, A1, SENSOR2_ADDRESS, "S2"}
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
const uint16_t MIN_STRENGTH = 5; // kcps/spad

// Detection zone parameters (tune these based on your setup)
const uint16_t MIN_DETECTION_DISTANCE = 100;   // mm - closer than this is ignored (mounting surface)
const uint16_t MAX_DETECTION_DISTANCE = 500; // mm - farther than this is ignored (background)

/* Setup ---------------------------------------------------------------------*/

void setup()
{
  pinMode(LedPin, OUTPUT);
  SerialPort.begin(115200);
  SerialPort.println("VL53L4CD Dual Sensor Test");

  DEV_I2C.begin();
  DEV_I2C.setClock(400000); // 400kHz I2C (Fast Mode)

  initializeSensors();
}

void initializeSensors() {
  SerialPort.println("=== Initializing Dual Sensors ===");
  
  // Set up all XSHUT pins and shut down all sensors
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    pinMode(sensors[i].xshutPin, OUTPUT);
    digitalWrite(sensors[i].xshutPin, LOW);
  }
  delay(100);
  
  // Initialize each sensor one at a time
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    SerialPort.print("Initializing ");
    SerialPort.print(sensors[i].name);
    SerialPort.println("...");
    
    // Power up this sensor only
    digitalWrite(sensors[i].xshutPin, HIGH);
    delay(100);
    
    // Initialize sensor
    sensors[i].sensor->begin();
    if (sensors[i].sensor->InitSensor() != 0) {
      SerialPort.print("ERROR: ");
      SerialPort.print(sensors[i].name);
      SerialPort.println(" init failed!");
      if (i == 0) while(1); // Halt on first sensor failure
      continue;
    }
    
    // Change address if not the first sensor
    if (i > 0) {
      if (sensors[i].sensor->VL53L4CD_SetI2CAddress(sensors[i].i2cAddress) != 0) {
        SerialPort.print("ERROR: ");
        SerialPort.print(sensors[i].name);
        SerialPort.println(" address change failed!");
        continue;
      }
    }
    
    // Shut down this sensor before next one (except last sensor)
    if (i < NUM_SENSORS - 1) {
      digitalWrite(sensors[i].xshutPin, LOW);
      delay(100);
    }
    
    SerialPort.print(sensors[i].name);
    SerialPort.print(" initialized at 0x");
    SerialPort.println(sensors[i].i2cAddress, HEX);
  }
  
  // Power all sensors back up
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    digitalWrite(sensors[i].xshutPin, HIGH);
  }
  delay(100);
  
  // Configure and start all sensors
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    sensors[i].sensor->VL53L4CD_SetRangeTiming(10, 0);
    sensors[i].sensor->VL53L4CD_StartRanging();
  }
  delay(100);
  
  SerialPort.println("All sensors ready!\n");
}

void loop() {
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
    // Check if any sensor has a non-zero distance
    bool hasValidDetection = false;
    for (uint8_t i = 0; i < NUM_SENSORS; i++) {
      if (sensorStates[i].distance > 0) {
        hasValidDetection = true;
        break;
      }
    }
    
    if (hasValidDetection) {
      char report[200];
      int offset = 0;
      
      for (uint8_t i = 0; i < NUM_SENSORS; i++) {
        offset += snprintf(report + offset, sizeof(report) - offset,
          "%s: %5u mm (%2u kcps)",
          sensors[i].name,
          sensorStates[i].distance,
          sensorStates[i].signal);
        
        if (i < NUM_SENSORS - 1) {
          offset += snprintf(report + offset, sizeof(report) - offset, " | ");
        }
        
        sensorStates[i].hasNewData = false;
      }
      
      snprintf(report + offset, sizeof(report) - offset, "\r\n");
      SerialPort.print(report);
      readingCount++;
    } else {
      // Reset hasNewData flags even if we didn't print
      for (uint8_t i = 0; i < NUM_SENSORS; i++) {
        sensorStates[i].hasNewData = false;
      }
    }
  }
  
  updateFPS();
}

void updateFPS() {
  unsigned long currentTime = millis();
  if (currentTime - lastFpsTime >= FPS_UPDATE_INTERVAL) {
    currentFps = (float)readingCount * 1000.0 / (currentTime - lastFpsTime);
    SerialPort.print("FPS: ");
    SerialPort.println(currentFps, 1);
    readingCount = 0;
    lastFpsTime = currentTime;
  }
}
