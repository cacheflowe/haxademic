/* Arduino tutorial - Buzzer / Piezo Speaker
   More info and circuit: http://www.ardumotive.com/how-to-use-a-buzzer-en.html
   Dev: Michalis Vasilakis // Date: 9/6/2015 // www.ardumotive.com */

const int buzzer1 = 9; // buzzer to arduino pin 9
const int buzzer2 = 7; // buzzer to arduino pin 7
const int ledPin = 13; // buzzer to arduino pin 7

int flip = 0;
int onTime = 100;
int offTime = 10;

void setup(){
  pinMode(buzzer1, OUTPUT); // Set buzzer - pin 9 as an output
  pinMode(buzzer2, OUTPUT);
  pinMode(ledPin, OUTPUT);
}

void loop(){
  int time1 = millis() / 5;
  int time2 = millis() / 2;
  
  // flip back & forth between piezos
  flip++;
  if(flip % 2 == 0) {
    tone(buzzer1, 100 + time1 % 500);
  } else {
    tone(buzzer2, 100 + time2 % 1000);  // random(100, 5000)
    digitalWrite(ledPin, HIGH);         // onboard LED metronome
  }
  delay(onTime);

  // silence & delay
  noTone(buzzer1);  
  noTone(buzzer2);           
  digitalWrite(ledPin, LOW);   
  delay(offTime); 
}
