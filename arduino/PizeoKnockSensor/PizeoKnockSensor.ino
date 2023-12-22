
// these constants won't change:
const int knockSensor = A0;  // the piezo is connected to analog pin 0
const int threshold = 100;   // threshold value to decide when the detected sound is a knock or not

// these variables will change:
int sensorReading = 0;  // variable to store the value read from the sensor pin
int lastReading = 0;

void setup() {
  Serial.begin(115200);       // use the serial port
}

void loop() {
  sensorReading = analogRead(knockSensor);  
  if(sensorReading > threshold && lastReading != sensorReading) {
    Serial.println(sensorReading);
  }
  lastReading = sensorReading;
  delay(20);  // delay to avoid overloading the serial port buffer
}
