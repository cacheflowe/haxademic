/*
======================================================================================================
Notes:
- Be sure to install the M5Stick/etc libraries
- Needs the ESP32 platform sources: 
  - https://randomnerdtutorials.com/installing-the-esp32-board-in-arduino-ide-windows-instructions/
- In Arduino's "Board Manager" tab, install the esp32 library by Expressif Systems
- Install the "Smoothed" library by Matthew Fryer

======================================================================================================
Links:
- Device:
  - https://shop.m5stack.com/collections/m5-controllers/products/m5stickc-plus-esp32-pico-mini-iot-development-kit
- Official examples:
  - https://github.com/m5stack/M5StickC-Plus/blob/master/examples/Basics/MPU6886/MPU6886.ino
- Other:
  - https://www.hackster.io/shasha-liu/magic-wand-752f52
  - https://github.com/m5stack/MagicWand/blob/master/src/capture.cpp

======================================================================================================
*/

#include <M5StickCPlus.h>

float accX = 0;
float accY = 0;
float accZ = 0;

float gyroX = 0;
float gyroY = 0;
float gyroZ = 0;

void setup() {
    M5.begin();                // Init M5StickC Plus.  初始化 M5StickC Plus
    M5.Imu.Init();             // Init IMU.  初始化IMU
    M5.Lcd.setRotation(3);     // Rotate the screen. 将屏幕旋转
    M5.Lcd.setCursor(50, 15);  // set the cursor location.  设置光标位置
    M5.Lcd.println("MPU6886 Accelerometer");
    M5.Lcd.setCursor(30, 30);
    M5.Lcd.println("  X       Y       Z");
}

void loop() {
    static float temp = 0;
    M5.Imu.getGyroData(&gyroX, &gyroY, &gyroZ);
    M5.Imu.getAccelData(&accX, &accY, &accZ);
    M5.Imu.getTempData(&temp);

    M5.Lcd.setCursor(30, 45);
    M5.Lcd.printf("%.2f   %.2f   %.2f      ", gyroX, gyroY, gyroZ);
    M5.Lcd.setCursor(170, 45);
    M5.Lcd.print("o/s");
    M5.Lcd.setCursor(30, 60);
    M5.Lcd.printf("%.2f   %.2f   %.2f      ", accX * 1000, accY * 1000,
                  accZ * 1000);
    M5.Lcd.setCursor(185, 60);
    M5.Lcd.print("mg");
    M5.Lcd.setCursor(30, 75);
    M5.Lcd.printf("Temperature : %.2f C", temp);
    delay(100);
}

