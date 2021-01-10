#include <HCSR04.h>

// Initialize sensor that uses digital pins 13 (trigger) and 12 (echo).
UltraSonicDistanceSensor distanceSensor(13, 12);
UltraSonicDistanceSensor distanceSensor2(11, 10);

void setup () {
    Serial.begin(115200);  // We initialize serial connection so that we could print values from sensor.
}

void loop () {
    double distance = distanceSensor.measureDistanceCm();
    double distance2 = distanceSensor2.measureDistanceCm();
    Serial.println('a'+String(distance));
    delay(20);
    Serial.println("b"+String(distance2));
    delay(20);
}
