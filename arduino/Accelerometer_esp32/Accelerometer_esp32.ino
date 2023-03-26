// ESP-WROOM-32 is the model I have 3 of, when selecting from the device list
// - https://www.amazon.com/gp/product/B08D5ZD528/
// 
// To add esp32 devices, add this to the "alternate board manager URLs" in Arduino IDE:
// - https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json
// 
// How to talk to the GY-61 accelerometer sensor:
// - https://peppe8o.com/accelerometer-with-arduino-uno-adxl335-gy-61-wiring-and-code/
//
// esp32 pinout:
// - https://www.upesy.com/blogs/tutorials/esp32-pinout-reference-gpio-pins-ultimate-guide
// 
// Look up WebSockets
// - https://iotdesignpro.com/projects/real-time-data-transfer-between-two-esp32-using-websocket-client-on-arduino-ide

// const int VCCPin = A0;
const int xPin = 12;
const int yPin = 13;
const int zPin = 14;
// const int GNDPin = A4;

// variables
int x = 0;
int y = 0;
int z = 0;


void setup() {
  // pin A0 (pin14) is VCC and pin A4 (pin18) in GND to activate the GY-61-module
  // pinMode(A0, OUTPUT);
  // pinMode(A4, OUTPUT);
  // digitalWrite(14, HIGH);
  // digitalWrite(18, LOW);

  // activating debugging for arduino UNO
  Serial.begin(115200);
}

void loop() {
  // let's compare against last frame
  int lastX = x;
  int lastY = y;
  int lastZ = z;

  // get current readings
  x = analogRead(xPin);
  y = analogRead(yPin);
  z = analogRead(zPin);

  // "normalize" to -100 - 100
  x = map(x, 1450, 2350, -100, 100);
  y = map(y, 1450, 2350, -100, 100);
  z = map(z, 1450, 2350, -100, 100);

  // sum up speed after normalizing
  int totalChange = abs(lastX - x) + abs(lastZ - z) + abs(lastZ - z);

  // show x, y and z-values
  Serial.print("x = ");
  Serial.print(x);
  Serial.print(", y = ");
  Serial.print(y);
  Serial.print(", z = ");
  Serial.print(z);
  Serial.print(", totalChange = ");
  Serial.println(totalChange);
	delay(50);
}

