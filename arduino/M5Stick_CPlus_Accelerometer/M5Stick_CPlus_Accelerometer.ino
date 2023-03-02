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
- Official examples / docs:
  - https://github.com/m5stack/M5StickC-Plus/blob/master/examples/Basics/MPU6886/MPU6886.ino
  - https://github.com/m5stack/m5-docs/tree/master/docs/en/api
- Wifi / WebSockets
  - https://m5stack.hackster.io/katsushun89/m5stack-synchronizing-the-colors-with-unity-fb8422
  - https://github.com/gilmaimon/ArduinoWebsockets
- Other:
  - https://www.hackster.io/shasha-liu/magic-wand-752f52
  - https://github.com/m5stack/MagicWand/blob/master/src/capture.cpp

======================================================================================================
TODO:
- Add swing detection & threshold
- Add WebSockets & test w/local server

*/

#include <M5StickCPlus.h>
#include <Smoothed.h>

// sensor values
float accX = 0;
float accY = 0;
float accZ = 0;
Smoothed <float> accX_;
Smoothed <float> accY_;
Smoothed <float> accZ_;

float gyroX = 0;
float gyroY = 0;
float gyroZ = 0;
Smoothed <float> gyroX_;
Smoothed <float> gyroY_;
Smoothed <float> gyroZ_;

float temp = 0;
Smoothed <float> temp_;

// screen update interval
static unsigned long lastDrawTime = 0;

// device active/inactive - toggle with large button
bool active = true;

// bat position
enum Position {UP, FLAT, DOWN, SWING};
Position position = FLAT;

void setup() {
  initLCD();
  initSmoothedSensorData();
}

void initLCD() {
  M5.begin();                // Init M5StickC Plus
  M5.Imu.Init();             // Init IMU
  M5.Lcd.setRotation(0);     // Rotate the screen
}

void initSmoothedSensorData() {
  accX_.begin(SMOOTHED_AVERAGE, 10);
  accY_.begin(SMOOTHED_AVERAGE, 10);
  accZ_.begin(SMOOTHED_AVERAGE, 10);
  
  gyroX_.begin(SMOOTHED_AVERAGE, 10);
  gyroY_.begin(SMOOTHED_AVERAGE, 10);
  gyroZ_.begin(SMOOTHED_AVERAGE, 10);

  temp_.begin(SMOOTHED_AVERAGE, 10);
}

void loop() {
  if(active) {
    updateSensors();
    checkPosition();
    drawReadings();
  }
  checkButtonClick();
  delay(20);
}

void updateSensors() {
  M5.Imu.getGyroData(&gyroX, &gyroY, &gyroZ);
  M5.Imu.getAccelData(&accX, &accY, &accZ);
  M5.Imu.getTempData(&temp);

  accX_.add(accX);
  accY_.add(accY);
  accZ_.add(accX);
  
  gyroX_.add(gyroX);
  gyroY_.add(gyroY);
  gyroZ_.add(gyroZ);

  temp_.add(temp);
}

void checkPosition() {
  float yPos = accY_.get();
  if(position != UP && yPos > 0.7) {
    position = UP;    
  } else if(position != FLAT && abs(yPos) < 0.3) {
    position = FLAT;
  } else if(position != DOWN && yPos < -0.6) {
    position = DOWN;
  }
}

void drawReadings() {
  // only draw on a reasonable interval
  int now = millis();
  if (now < lastDrawTime + 50) return;
  lastDrawTime = now;

  // screen measurements
  int centerX = M5.Lcd.width() / 2;

  // M5.Lcd.fillScreen(BLACK);

  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("MPU6886 Accel/Gyro");
  
  M5.Lcd.setCursor(10, 30);
  M5.Lcd.println("Gyroscope");
  drawReading(50, "Gyro X", gyroX_.get(), 1, RED);
  drawReading(70, "Gyro Y", gyroY_.get(), 1, GREEN);
  drawReading(90, "Gyro Z", gyroZ_.get(), 1, YELLOW);

  M5.Lcd.setCursor(10, 130);
  M5.Lcd.println("Accelerometer");
  drawReading(150, "Accel X", accX_.get(), 30, RED);
  drawReading(170, "Accel Y", accY_.get(), 30, GREEN);
  drawReading(190, "Accel Z", accZ_.get(), 30, YELLOW);

  M5.Lcd.setCursor(10, 210);
  M5.Lcd.print("Position");
  M5.Lcd.setCursor(80, 210);
  if(position == UP) M5.Lcd.print("UP  ");
  if(position == FLAT) M5.Lcd.print("FLAT");
  if(position == DOWN) M5.Lcd.print("DOWN");

  M5.Lcd.setCursor(10, 230);
  M5.Lcd.printf("Temperature : %.2f C", temp_.get());
}

void drawReading(int y, String label, float value, float amp, int color) {
  M5.Lcd.setCursor(10, y);
  M5.Lcd.print(label);
  M5.Lcd.setCursor(60, y);
  M5.Lcd.print(value);
  int centerX = M5.Lcd.width() / 2;
  drawLine(centerX, y + 12, value, amp, color);
}

void drawLine(int x, int y, float width, float amp, int color) {
  float finalW = abs(width * amp);
  float xOffset = (width < 0) ? -finalW : 0;
  M5.Lcd.drawFastHLine(0, y, M5.Lcd.width(), BLACK);
  M5.Lcd.drawFastHLine(x + xOffset, y, finalW, color);
}

void checkButtonClick() {
  M5.update();  // Read the press state of the key
  if (M5.BtnA.wasReleased()) {
    active = !active;
  }
}
