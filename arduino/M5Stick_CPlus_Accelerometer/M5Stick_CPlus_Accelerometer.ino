/*
======================================================================================================
Notes:
- Be sure to install the M5Stick/etc libraries
- Arduino IDE needs the ESP32 platform sources: 
  - https://randomnerdtutorials.com/installing-the-esp32-board-in-arduino-ide-windows-instructions/
- In Arduino's "Boards Manager" tab
  - Install the esp32 library by Expressif Systems
- In Arduino's "Library Manager" tab
  - Install the "Smoothed" library by Matthew Fryer
  - Install the "ArduinoWebsockets" library from gilmaimon
  - Install the "WebSockets" library by Markus Sattler

======================================================================================================
Links:
- Device:
  - https://shop.m5stack.com/collections/m5-controllers/products/m5stickc-plus-esp32-pico-mini-iot-development-kit
- Official examples / docs:
  - https://github.com/m5stack/M5StickC-Plus/blob/master/examples/Basics/MPU6886/MPU6886.ino
  - https://github.com/m5stack/m5-docs/tree/master/docs/en/api
  - https://github.com/m5stack/M5StickC/blob/master/examples/Basics/FactoryTest
- Wifi / WebSockets
  - Use this one:
    - https://github.com/Links2004/arduinoWebSockets/blob/master/examples/esp32/WebSocketClient/WebSocketClient.ino
  - Not this one: (it doesn't have auto-reconnect)
    - https://github.com/gilmaimon/ArduinoWebsockets/blob/master/examples/Esp32-Client/Esp32-Client.ino
    - https://github.com/gilmaimon/ArduinoWebsockets/blob/master/examples/Minimal-Esp32-Client/Minimal-Esp32-Client.ino
    - https://github.com/gilmaimon/ArduinoWebsockets
    - https://github.com/gilmaimon/ArduinoWebsockets/issues/75 - reconnection strategies
  - https://m5stack.hackster.io/katsushun89/m5stack-synchronizing-the-colors-with-unity-fb8422
- Other:
  - https://www.hackster.io/shasha-liu/magic-wand-752f52
  - https://github.com/m5stack/MagicWand/blob/master/src/capture.cpp

======================================================================================================
*/

////////////////////////////////////////////////////
// includes
////////////////////////////////////////////////////

#include <Arduino.h>
#include <M5StickCPlus.h>
#include <Smoothed.h>
#include <WebSocketsClient.h>
#include <WiFi.h>
#include <WiFiMulti.h>

////////////////////////////////////////////////////
// network config
////////////////////////////////////////////////////

// - Wifi auth
const char* ssid = "TechHouse";
const char* password = "birdmagnet";

// - ws:// server 
const char* websockets_server_host = "192.168.1.155";
const uint16_t websockets_server_port = 8080;
const char* websockets_server_path = "/ws";

////////////////////////////////////////////////////
// network objects
////////////////////////////////////////////////////

WiFiMulti WiFiMulti;
WebSocketsClient webSocket;
bool wsConnected = false;
long lastNetworkPollTime = 0;

////////////////////////////////////////////////////
// force reboot helper
////////////////////////////////////////////////////

void(* Reboot)(void) = 0;

////////////////////////////////////////////////////
// sensor values & smoothed values
////////////////////////////////////////////////////

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

////////////////////////////////////////////////////
// swing detection
////////////////////////////////////////////////////

float motionTotal = 0;
const int motionBufferSize = 15;
float motionBuffer[motionBufferSize] = { 0 };
int motionIndex = 0;
int swingMax = 12000;
int swingThreshold = swingMax / 2;
int swingTimeout = 1000;
int swingTime = 0;
long lastSendTime = 0;

////////////////////////////////////////////////////
// bat position
////////////////////////////////////////////////////

enum Position {
  UP, 
  FLAT, 
  DOWN, 
  SWING
};
Position position = FLAT;

////////////////////////////////////////////////////
// LCD screen refresh interval
////////////////////////////////////////////////////

long lastDrawTime = 0;

////////////////////////////////////////////////////
// device active/inactive - toggle with large button
////////////////////////////////////////////////////

bool active = true;


void setup() {
  Serial.begin(115200);
  initLCD();
  initSmoothedSensorData();
  initNetwork();
}

void initLCD() {
  M5.begin();                // Init M5StickC Plus
  M5.Imu.Init();             // Init IMU
  M5.Lcd.setRotation(0);     // Rotate the screen
}

void initSmoothedSensorData() {
  accX_.begin(SMOOTHED_AVERAGE, 50);
  accY_.begin(SMOOTHED_AVERAGE, 50);
  accZ_.begin(SMOOTHED_AVERAGE, 50);
  
  gyroX_.begin(SMOOTHED_AVERAGE, 3);
  gyroY_.begin(SMOOTHED_AVERAGE, 3);
  gyroZ_.begin(SMOOTHED_AVERAGE, 3);

  temp_.begin(SMOOTHED_AVERAGE, 10);
}

void loop() {
  if(active) {
    updateSensors();
    checkPosition();
    checkMotion();
    sendReadings();
    drawReadings();
    // soundTest();
  }
  networkLoop();
  checkButtonClick();
  delay(20); // 50fps
}

void updateSensors() {
  // get raw sensor values
  M5.Imu.getGyroData(&gyroX, &gyroY, &gyroZ);
  M5.Imu.getAccelData(&accX, &accY, &accZ);
  M5.Imu.getTempData(&temp);

  // update Smoothed values with raw values
  accX_.add(accX);
  accY_.add(accY);
  accZ_.add(accX);
  
  gyroX_.add(gyroX);
  gyroY_.add(gyroY);
  gyroZ_.add(gyroZ);

  temp_.add(temp);
}

void checkPosition() {
  // y axis is the simple way to figure out bat orientation (up/flat/down)
  float yPos = accY_.get();
  if(position != UP && yPos > 0.7 && millis() > swingTime + swingTimeout) {
    // reset bat for next swing by pointing it up
    position = UP;
    webSocket.sendTXT("{\"position\": \"up\"}");
  } else if(motionTotal < 1500) {
    // if bat is not swinging, check for other orientations
    /*
    if(position != FLAT && abs(yPos) < 0.3) {
      position = FLAT;
      webSocket.sendTXT("{\"position\": \"flat\"}");
    } else if(position != DOWN && yPos < -0.6) {
      position = DOWN;
      webSocket.sendTXT("{\"position\": \"down\"}");
    }
    */
  }
}

void checkMotion() {
  // check gyro for swing motion
  motionBuffer[motionIndex] = abs(gyroX_.get()) + abs(gyroY_.get()) + abs(gyroZ_.get());
  motionIndex++;
  motionIndex = motionIndex % motionBufferSize;
  
  // sum motion array  
  motionTotal = 0;
  for(int i=0; i < motionBufferSize; i++) {
    motionTotal += motionBuffer[i];
  }

  // is it a swing?
  if(position == UP) {
    if(motionTotal > swingThreshold) {
      position = SWING;
      swingTime = millis();
      float normalizedSwingAmp = motionTotal / (float) swingMax;
      String msg = "{\"cmd\":\"swing\",\"sender\":\"client\",\"data\":{\"velocity\":" + String(normalizedSwingAmp) + "}}";
      webSocket.sendTXT(msg);
    }
  }
}

void sendReadings() {
  if(!updateAllowed(lastSendTime, 250)) return;
  webSocket.sendTXT("{\"motionTotal\":" + String(motionTotal) + "}");
}

void drawReadings() {
  // only draw on a reasonable interval
  if(!updateAllowed(lastDrawTime, 50)) return;

  // screen measurements
  int centerX = M5.Lcd.width() / 2;

  // title
  M5.Lcd.setTextColor(WHITE, BLACK);
  M5.Lcd.setCursor(10, 10);
  M5.Lcd.println("MPU6886 Accel/Gyro");
  
  // sensors
  drawReading(30, "Gyro X", gyroX_.get(), 1, RED);
  drawReading(45, "Gyro Y", gyroY_.get(), 1, GREEN);
  drawReading(60, "Gyro Z", gyroZ_.get(), 1, YELLOW);

  drawReading(90, "Accel X", accX_.get(), 30, RED);
  drawReading(105, "Accel Y", accY_.get(), 30, GREEN);
  drawReading(120, "Accel Z", accZ_.get(), 30, YELLOW);

  drawReading(210, "Motion", (int) motionTotal, 0.01, GREEN, 10);  

  // bat state
  M5.Lcd.setCursor(10, 150);
  M5.Lcd.print("Position");
  M5.Lcd.setCursor(80, 150);
  if(position == UP) M5.Lcd.print("UP  ");
  if(position == FLAT) M5.Lcd.print("FLAT");
  if(position == DOWN) M5.Lcd.print("DOWN");

  // network connectivity
  drawConnectionStatus(10, 165, "Wifi ", (WiFi.status() == WL_CONNECTED));
  drawConnectionStatus(10, 180, "ws://", wsConnected);
  M5.Lcd.setTextColor(WHITE, BLACK);

  // M5.Lcd.setCursor(10, 230);
  // M5.Lcd.printf("Temperature : %.2f C", temp_.get());
}

void drawReading(int y, String label, float value, float amp, int color) {
  drawReading(y, label, value, amp, color, M5.Lcd.width() / 2);
}

void drawReading(int y, String label, float value, float amp, int color, int lineX) {
  M5.Lcd.setCursor(10, y);
  M5.Lcd.print(label);
  M5.Lcd.setCursor(60, y);
  M5.Lcd.print(value);
  drawLine(lineX, y + 11, value, amp, color);
}

void drawLine(int x, int y, float width, float amp, int color) {
  float finalW = abs(width * amp);
  float xOffset = (width < 0) ? -finalW : 0;
  M5.Lcd.drawFastHLine(0, y, M5.Lcd.width(), BLACK);
  M5.Lcd.drawFastHLine(x + xOffset, y, finalW, color);
}

void drawConnectionStatus(int x, int y, String connectionLabel, bool isConnected) {
  int statusColor = (isConnected) ? GREEN : RED;
  String statusStr = (isConnected) ? " CONNECTED   " : " DISCONNECTED";
  M5.Lcd.setTextColor(statusColor, BLACK);
  M5.Lcd.setCursor(x, y);
  M5.Lcd.println(connectionLabel + statusStr);
}

void checkButtonClick() {
  M5.update();  // Read the press state of the key
  if (M5.BtnA.wasReleased()) {
    active = !active;
    int bgColor = (active) ? BLACK : RED;
    M5.Lcd.fillScreen(bgColor);
  }
  if (M5.BtnB.wasReleased()) {
    Reboot();
  }
}


void initNetwork() {
  initWifi();
  initWebSockets();
}

void initWifi() {
  // Connect to wifi
  WiFiMulti.addAP(ssid, password);
  while(WiFiMulti.run() != WL_CONNECTED) {
    delay(100);
  }
  Serial.println("Connected to Wifi, Connecting to server.");
}

void initWebSockets() {
  // // try to connect to Websockets server
  webSocket.begin(websockets_server_host, websockets_server_port, websockets_server_path);
  webSocket.onEvent(webSocketEvent);
  webSocket.setReconnectInterval(5000);
}

void webSocketEvent(WStype_t type, uint8_t * payload, size_t length) {
  switch(type) {
    case WStype_DISCONNECTED:
      wsConnected = false;
      Serial.println("[WSc] Disconnected!\n");
      break;
    case WStype_CONNECTED:
      wsConnected = true;
      webSocket.sendTXT("{\"status\": \"bat connected\"}");
      Serial.printf("[WSc] Connected to url: %s\n", payload);
      break;
    case WStype_TEXT:
      Serial.printf("[WSc] get text: %s\n", payload);
      break;
    case WStype_ERROR:	
      wsConnected = false;		
      break;
  }
}


void networkLoop() {
  // only update on a reasonable interval
  if(!updateAllowed(lastNetworkPollTime, 200)) return;

  // let the websockets client check for incoming messages
  webSocket.loop();
}

bool updateAllowed(long& lastExecTime, int interval) {
  int now = millis();
  if (now < lastExecTime + interval) {
    return false;
  } else {
    lastExecTime = now;
    return true;
  }
}


int curTone = 100;
long lastToneTime = 0;
void soundTest() {
  // only update on a reasonable interval
  if(!updateAllowed(lastToneTime, 50)) return;
  curTone += 100;
  if(curTone > 1000) curTone = 100;
  M5.Beep.tone(curTone);
  M5.Beep.update();
}

