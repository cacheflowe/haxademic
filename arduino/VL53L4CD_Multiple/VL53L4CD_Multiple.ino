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
* WIRING FOR MULTIPLE SENSORS:
* 
* SENSOR 1:
* - STEMMA QT connector to Arduino I2C (SDA/SCL shared)
* - XSHUT pin connected to Arduino pin A0
* 
* SENSOR 2: 
* - STEMMA QT connector to Arduino I2C (SDA/SCL shared via daisy chain)
* - XSHUT pin connected to Arduino pin A1
* 
* The XSHUT pins allow us to control which sensor is active during initialization,
* preventing I2C address conflicts during startup.
*/

/* 
- Hello world code updated by @cacheflowe to achieve ~100fps updates
- Modified to support 2+ sensors running in tandem
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
  SerialPort.println("VL53L4CD Dual Sensor Test");
  SerialPort.println("Commands:");
  SerialPort.println("  min <value> - Set minimum detection distance (mm)");
  SerialPort.println("  max <value> - Set maximum detection distance (mm)");
  SerialPort.println("  strength <value> - Set minimum signal strength (kcps)");
  SerialPort.println("  show - Show current settings");
  SerialPort.println();

  DEV_I2C.begin();
  DEV_I2C.setClock(400000); // 400kHz I2C (Fast Mode)

  initializeSensors();
}

void initializeSensors() {
  SerialPort.println("=== Initializing Sensors (Robust Mode) ===");
  
  // 1. Turn EVERYTHING OFF first
  // This ensures the bus is completely silent
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    pinMode(sensors[i].xshutPin, OUTPUT);
    digitalWrite(sensors[i].xshutPin, LOW);
  }
  delay(100);
  
  // 2. Initialize one by one
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    SerialPort.print("Configuring ");
    SerialPort.print(sensors[i].name);
    SerialPort.println("...");
    
    // Power up ONLY this sensor
    // Since all others are LOW, this is the ONLY device at 0x29
    digitalWrite(sensors[i].xshutPin, HIGH);
    delay(50); // Give it time to boot
    
    // Initialize sensor (it talks at 0x29)
    sensors[i].sensor->begin();
    if (sensors[i].sensor->InitSensor() != 0) {
      SerialPort.print("  ERROR: ");
      SerialPort.print(sensors[i].name);
      SerialPort.println(" init failed!");
      // If it fails, turn it off so it doesn't mess up the next one
      digitalWrite(sensors[i].xshutPin, LOW);
      continue;
    }
    
    // Change address immediately
    // We move it from 0x29 to its assigned address (e.g., 0x30)
    uint8_t newAddress = sensors[i].i2cAddress;
    if (sensors[i].sensor->VL53L4CD_SetI2CAddress(newAddress) != 0) {
      SerialPort.print("  ERROR: ");
      SerialPort.print(sensors[i].name);
      SerialPort.println(" address change failed!");
      digitalWrite(sensors[i].xshutPin, LOW);
      continue;
    }
    
    SerialPort.print("  Address set to 0x");
    SerialPort.println(newAddress, HEX);
    
    // IMPORTANT: We leave this sensor ON.
    // Since it is now at 0x30 (or 31/32), it will NOT conflict 
    // with the next sensor which will boot at 0x29.
  }
  
  SerialPort.println("All sensors addressed. Starting ranging...");
  delay(100);
  
  // 3. Start Ranging for all active sensors
  for (uint8_t i = 0; i < NUM_SENSORS; i++) {
    // Only start if the sensor is actually powered
    if (digitalRead(sensors[i].xshutPin) == HIGH) {
      sensors[i].sensor->VL53L4CD_SetRangeTiming(10, 0);
      sensors[i].sensor->VL53L4CD_StartRanging();
    }
  }
  
  SerialPort.println("All sensors ready!\n");
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
        SerialPort.print("MIN set to ");
        SerialPort.print(MIN_DETECTION_DISTANCE);
        SerialPort.println(" mm");
      } else {
        SerialPort.println("ERROR: Invalid MIN value");
      }
    }
  } else if (strcmp(token, "max") == 0) {
    token = strtok(NULL, " ");
    if (token != NULL) {
      uint16_t newMax = atoi(token);
      if (newMax > MIN_DETECTION_DISTANCE && newMax <= 1300) {
        MAX_DETECTION_DISTANCE = newMax;
        SerialPort.print("MAX set to ");
        SerialPort.print(MAX_DETECTION_DISTANCE);
        SerialPort.println(" mm");
      } else {
        SerialPort.println("ERROR: Invalid MAX value");
      }
    }
  } else if (strcmp(token, "strength") == 0) {
    token = strtok(NULL, " ");
    if (token != NULL) {
      uint16_t newStrength = atoi(token);
      if (newStrength >= 0 && newStrength <= 100) {
        MIN_STRENGTH = newStrength;
        SerialPort.print("MIN_STRENGTH set to ");
        SerialPort.print(MIN_STRENGTH);
        SerialPort.println(" kcps");
      } else {
        SerialPort.println("ERROR: Invalid STRENGTH value (0-100)");
      }
    }
  } else if (strcmp(token, "show") == 0) {
    SerialPort.println("Current settings:");
    SerialPort.print("  MIN: ");
    SerialPort.print(MIN_DETECTION_DISTANCE);
    SerialPort.println(" mm");
    SerialPort.print("  MAX: ");
    SerialPort.print(MAX_DETECTION_DISTANCE);
    SerialPort.println(" mm");
    SerialPort.print("  MIN_STRENGTH: ");
    SerialPort.print(MIN_STRENGTH);
    SerialPort.println(" kcps");
  } else {
    SerialPort.println("Unknown command. Available: min, max, strength, show");
  }
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
