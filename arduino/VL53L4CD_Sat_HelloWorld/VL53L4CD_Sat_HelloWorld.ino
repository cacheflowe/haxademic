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
 * To use this sketch you need to connect the VL53L4CD satellite sensor directly to the Nucleo board with wires in this way:
 * pin 1 (GND) of the VL53L4CD satellite connected to GND of the Nucleo board
 * pin 2 (VDD) of the VL53L4CD satellite connected to 3V3 pin of the Nucleo board
 * pin 3 (SCL) of the VL53L4CD satellite connected to pin D15 (SCL) of the Nucleo board
 * pin 4 (SDA) of the VL53L4CD satellite connected to pin D14 (SDA) of the Nucleo board
 * pin 5 (GPIO1) of the VL53L4CD satellite connected to pin A2 of the Nucleo board
 * pin 6 (XSHUT) of the VL53L4CD satellite connected to pin A1 of the Nucleo board
 */

/* 
- Hello world code updated by @cacheflowe to achieve ~100fps updates
- References:
  - https://github.com/stm32duino/VL53L4CD/blob/main/src/vl53l4cd_api.cpp
- Board: Adafruit Metro
  - Install by adding https://adafruit.github.io/arduino-board-index/package_adafruit_index.json to the Arduino IDE Board Manager URLs
- TODO:
  - Get multiple sensors running in tandem
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

// Components.
VL53L4CD sensor_vl53l4cd_sat(&DEV_I2C, A1);

// FPS calculation variables
unsigned long lastFpsTime = 0;
const unsigned long FPS_UPDATE_INTERVAL = 1000; // Update FPS every second
unsigned long readingCount = 0;
float currentFps = 0.0;

/* Setup ---------------------------------------------------------------------*/

void setup()
{
  // Led.
  pinMode(LedPin, OUTPUT);

  // Initialize serial for output.
  SerialPort.begin(115200);
  SerialPort.println("Starting...");

  // Initialize I2C bus with higher speed
  DEV_I2C.begin();
  DEV_I2C.setClock(400000); // 400kHz I2C (Fast Mode)
  // Or try even faster if your board supports it (this doesn't seem to work)
  // DEV_I2C.setClock(1000000); // 1MHz I2C (Fast Mode Plus)

  // Configure VL53L4CD satellite component.
  sensor_vl53l4cd_sat.begin();

  // Switch off VL53L4CD satellite component.
  sensor_vl53l4cd_sat.VL53L4CD_Off();

  //Initialize VL53L4CD satellite component.
  sensor_vl53l4cd_sat.InitSensor();

  // Program the highest possible TimingBudget, without enabling the
  // low power mode. This should give the best accuracy
  sensor_vl53l4cd_sat.VL53L4CD_SetRangeTiming(10, 0); // Minimum 10ms timing budget

  // Start Measurements
  sensor_vl53l4cd_sat.VL53L4CD_StartRanging();
}

void loop() {
  uint8_t NewDataReady = 0;
  VL53L4CD_Result_t results;
  uint8_t status;
  char report[100];

  do {
    status = sensor_vl53l4cd_sat.VL53L4CD_CheckForDataReady(&NewDataReady);
  } while (!NewDataReady);

  if ((!status) && (NewDataReady != 0)) {
    sensor_vl53l4cd_sat.VL53L4CD_ClearInterrupt();
    sensor_vl53l4cd_sat.VL53L4CD_GetResult(&results);

    // Print results to Serial
    // `range_status`
    // Status 0: RANGE_VALID - Good, reliable reading
    // Status 2: SIGMA_FAIL - The measurement precision is too low (noisy signal)
    // Status 4: SIGNAL_FAIL - The signal is too weak for a reliable measurement
    // `signal_per_spad_kcps` 
    // - Signal per SPAD (Single Photon Avalanche Diode) in kcps/SPAD (kcps stands for Kilo Count Per Second)
    // - Under ~3 kcps/spad means the reading is probably unreliable (too far away rom a surface, shiny surfaces, too much of an angle) because of not enough data. But it still *might* be a good reading
    snprintf(report, sizeof(report), "Status = %2u, Distance = %5u mm, Signal = %5u kcps/spad, Ambient = %4u kcps\r\n",
      results.range_status,
      results.distance_mm,
      results.signal_per_spad_kcps,
      results.ambient_rate_kcps); 

    // super basic filtering
    if(results.signal_per_spad_kcps >= 3) {
      SerialPort.print(report);
      updateFPS();
    }
  }
}

void updateFPS() {
  // Increment reading counter
  readingCount++;

  // Calculate and display FPS every second
  unsigned long currentTime = millis();
  if (currentTime - lastFpsTime >= FPS_UPDATE_INTERVAL) {
    currentFps = (float)readingCount * 1000.0 / (currentTime - lastFpsTime);
    
    SerialPort.print("Sensor FPS: ");
    SerialPort.println(currentFps, 1);
    
    // Reset counters
    readingCount = 0;
    lastFpsTime = currentTime;
  }
}
