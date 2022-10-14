/* Original code from: http://www.ardumotive.com/how-to-use-a-buzzer-en.html
   Modified by @cacheflowe */

const int buzzer1 = 9; // buzzer to arduino pin 9
const int ledPin = 13; // on-board LED

int flip = 0;
int onTime = 100;
int offTime = 100;
int toneHz = 100;
bool gotNewInput = false;

void setup(){
  pinMode(buzzer1, OUTPUT); // Set buzzer - pin 9 as an output
  pinMode(ledPin, OUTPUT);
  Serial.begin(115200); // opens serial port, sets data rate to 115200 bps
  Serial.setTimeout(10);
}

void loop(){
  if (Serial.available() > 0) {
    // read the incoming string
    String inStr = Serial.readString();
    if(inStr.charAt(0) == 'a') {
      // if starting with 'a', we know it's
      // what we're looking for, so parse the value
      int value = inStr.substring(1).toInt();
      toneHz = value;
      gotNewInput = true;
    }
  }

  // send new tone value back to host app
  if(gotNewInput == true) {
    gotNewInput = false;
    Serial.println(toneHz);
  }
  
  // flip back & forth between piezos
  tone(buzzer1, toneHz);
  digitalWrite(ledPin, HIGH);         // onboard LED metronome
  delay(onTime);

  // silence & delay
  noTone(buzzer1);  
  digitalWrite(ledPin, LOW);   
  delay(offTime); 
}
