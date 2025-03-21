//Compatible with the Arduino IDE 1.0
//Library version:1.1
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

// LiquidCrystal_I2C lcd(0x27,20,4);  // set the LCD address to 0x27 for a 16 chars and 2 line display
LiquidCrystal_I2C lcd(0x27,16,2);  // set the LCD address to 0x27 for a 16 chars and 2 line display

int count = 0;

void setup() {
  lcd.init();                      // initialize the lcd 
  // lcd.setBacklight(1);

  // Print a message to the LCD.
  lcd.backlight();
  lcd.setCursor(0,0);
  lcd.print(" HOVERCRAFT  STUDIO ");
  // lcd.setCursor(0,2);
  // lcd.print("    2024 >> 2032    ");
  // lcd.setCursor(0,3);
  // lcd.print("   CUSTOM CONTENT   ");
}


void loop() {
  delay(300);
  // lcd.scrollDisplayLeft();
  lcd.setCursor(0,3);
  lcd.print(count);
  count++;
}
