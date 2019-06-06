package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;

import processing.serial.Serial;

public class Demo_SerialDevice_Arduino_Altimeter
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String serialInputString = null;
	protected SerialDevice serialDevice;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		SerialDevice.printDevices();
		serialDevice = new SerialDevice(this, 0, 9600); 
	}

	public void drawApp() {
		background(0);
		
		// test draw to make sure serial communication doesn't hurt framerate
		PG.setDrawCenter(p);
		p.fill(255);
		p.stroke(127);
		p.strokeWeight(5);
		p.rect(p.frameCount % p.width, p.height * 0.5f, 100, 100);
	}
	

	@Override
	public void newDataAvailable(Serial serialDevice) {
		String serialInputString = P.trim(serialDevice.readStringUntil(SerialDevice.cr));
		if(serialInputString != null) {
			p.debugView.setValue("[Serial in]", p.frameCount + " | " + serialInputString);
			serialDevice.clear();
		}
	}

}


// ARDUINO CODE
/**

/*
 MPL3115A2 Barometric Pressure Sensor Library Example Code
 By: Nathan Seidle
 SparkFun Electronics
 Date: September 24th, 2013
 License: This code is public domain but you buy me a beer if you use this and we meet someday (Beerware license).
 
 Uses the MPL3115A2 library to display the current altitude and temperature
 
 Hardware Connections (Breakoutboard to Arduino):
 -VCC = 3.3V
 -SDA = A4 (use inline 10k resistor if your board is 5V)
 -SCL = A5 (use inline 10k resistor if your board is 5V)
 -INT pins can be left unconnected for this demo
 
 During testing, GPS with 9 satellites reported 5393ft, sensor reported 5360ft (delta of 33ft). Very close!
 During testing, GPS with 8 satellites reported 1031ft, sensor reported 1021ft (delta of 10ft).
 
 Available functions:
 .begin() Gets sensor on the I2C bus.
 .readAltitude() Returns float with meters above sealevel. Ex: 1638.94
 .readAltitudeFt() Returns float with feet above sealevel. Ex: 5376.68
 .readPressure() Returns float with barometric pressure in Pa. Ex: 83351.25
 .readTemp() Returns float with current temperature in Celsius. Ex: 23.37
 .readTempF() Returns float with current temperature in Fahrenheit. Ex: 73.96
 .setModeBarometer() Puts the sensor into Pascal measurement mode.
 .setModeAltimeter() Puts the sensor into altimetery mode.
 .setModeStandy() Puts the sensor into Standby mode. Required when changing CTRL1 register.
 .setModeActive() Start taking measurements!
 .setOversampleRate(byte) Sets the # of samples from 1 to 128. See datasheet.
 .enableEventFlags() Sets the fundamental event flags. Required during setup.
 
 /

#include <Wire.h>
#include "SparkFunMPL3115A2.h"

//Create an instance of the object
MPL3115A2 myPressure;

void setup()
{
  Wire.begin();        // Join i2c bus
  Serial.begin(9600);  // Start serial for output
  Serial.println("<Arduino is ready>");

  myPressure.begin(); // Get sensor online

  //Configure the sensor
  myPressure.setModeAltimeter(); // Measure altitude above sea level in meters
  //myPressure.setModeBarometer(); // Measure pressure in Pascals from 20 to 110 kPa

  myPressure.setOversampleRate(7); // Set Oversample to the recommended 128
  myPressure.enableEventFlags(); // Enable all three pressure and temp event flags 
}

void loop()
{
  //float altitude = myPressure.readAltitude();
  //Serial.print("Altitude(m):");
  //Serial.print(altitude, 2);

  float altitude = myPressure.readAltitudeFt();
  
  // Serial.print("Altitude(ft):");
//  Serial.write(altitude, 2);

  //float pressure = myPressure.readPressure();
  //Serial.print("Pressure(Pa):");
  //Serial.print(pressure, 2);

  //float temperature = myPressure.readTemp();
  //Serial.print(" Temp(c):");
  //Serial.print(temperature, 2);

  float temperature = myPressure.readTempF();
//  Serial.print("|Temp(f):");
//  Serial.print(temperature);
//  Serial.write(temperature, 2);

  String out = "Altitude(ft):" + String(altitude, 6) + "| Temp(f):" + String(temperature, 6);
  
  Serial.println(out);
  
//  Serial.println();

  delay(200);
}
*/