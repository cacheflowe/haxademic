// from: https://randomnerdtutorials.com/guide-for-microphone-sound-sensor-with-arduino/
// wiring:
// vcc - 3v
// gnd - gnd
// out - 4 (digital in)

// default value is HIGH< when mic is triggered, is switches to LOW, momentarily
int sensorPin = 4;
bool val = 0;
bool lastVal = 0;

void setup() {
  pinMode(sensorPin, INPUT);
  Serial.begin(115200);
}

void loop() {
  val = digitalRead(sensorPin);
  if(val != lastVal) {
    if (val == HIGH) {
      Serial.println("HIGH");
    }
    else {
      Serial.println("LOW");
    }
  }
  lastVal = val;
  // delay(20); // delay will make it miss the signals
}
