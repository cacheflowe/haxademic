
void setup() {
  Serial.begin(115200);  // Start serial for output
}

void loop() {
  Serial.println(millis());
  delay(50);
}
