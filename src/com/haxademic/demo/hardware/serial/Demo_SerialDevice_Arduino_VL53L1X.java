package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.ui.UI;
import processing.serial.Serial;

public class Demo_SerialDevice_Arduino_VL53L1X
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String serialInputString = null;
	protected SerialDevice serialDevice;
	protected String DIST_MIN = "DIST_MIN";
	protected String DIST_MAX = "DIST_MAX";
	protected FloatBuffer lidarAvg = new FloatBuffer(3);
	protected EasingFloat lidarVal = new EasingFloat(0, 0.3f);

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// set up arduino
		SerialDevice.printDevices();
		// serialDevice = new SerialDevice(this, 0, 115200);
		serialDevice = new SerialDevice(this, "COM9", 115200);
		
		// set up UI for min/max
		UI.addTitle("VL53L1X Controls");
		UI.addSlider(DIST_MIN, 0, 0, 1000, 1, false);
		UI.addSlider(DIST_MAX, 1000, 0, 3000, 1, false);
	}

	protected void drawApp() {
		background(0);

		// draw eased reading
		lidarVal.update(true);
		p.fill(255);
		float barH = P.map(lidarVal.value(), UI.value(DIST_MIN), UI.value(DIST_MAX), 0, p.height);
		p.rect(0, p.height, p.width, -barH);
	}


	@Override
	public void newDataAvailable(Serial serialDevice) {
		String serialInputString = P.trim(serialDevice.readStringUntil(SerialDevice.cr));
		if(serialInputString != null) {
			// log incoming value
			DebugView.setValue("[Serial in]", p.frameCount + " | " + serialInputString);
			serialDevice.clear();

			// update easing value. good readings start with `a`
			if(serialInputString.charAt(0) == 'a') {
				float newVal = ConvertUtil.stringToFloat(serialInputString.substring(1));	// use rest of string after `a`
				if(newVal >= 0) {
					lidarAvg.update(newVal);
					lidarVal.setTarget(lidarAvg.average());
				}
			}
		}
	}

}


// ARDUINO CODE
/**
////////////////////////////////////////////////////////////////
// VL53L1X: Read distance from sensor in mm
// Uses Polulu's library: https://github.com/pololu/vl53l1x-arduino
// ----------------------------------------
// Wiring info: https://learn.adafruit.com/adafruit-vl53l0x-micro-lidar-distance-sensor-breakout/arduino-code
// Sensor  ->  Arduino
// --------------
// VIN     ->  VIN
// GND     ->  GND
// SCL     ->  SCL (aka A5)
// SDA     ->  SDA (aka A4)
////////////////////////////////////////////////////////////////

#include <Wire.h>
#include <VL53L1X.h>

VL53L1X sensor;
int frameCount = 0;

void setup()
{
  // Connect to serial
  Serial.begin(115200);
  Serial.println("mVL53L1X initializing");
  Wire.begin();
  Wire.setClock(400000); // use 400 kHz I2C

  // Attempt to initialize the sensor library
  Serial.println("mSerial connecting");
  sensor.setTimeout(2000);  // was 500
  if (!sensor.init()) {
    Serial.println(F("mFailed to detect and initialize VL53L1X"));
    while (1);
  } else {
    Serial.println("mVL53L1X API Started"); 
  }

  // Configure the sensor
  // Use long distance mode and allow up to 50000 us (50 ms) for a measurement.
  // You can change these settings to adjust the performance of the sensor, but
  // the minimum timing budget is 20 ms for short distance mode and 33 ms for
  // medium and long distance modes. See the VL53L1X datasheet for more
  // information on range and timing limits.
  sensor.setDistanceMode(VL53L1X::Long);
  sensor.setMeasurementTimingBudget(50000);

  // Start continuous readings at a rate of one measurement every 50 ms (the
  // inter-measurement period). This period should be at least as long as the
  // timing budget.
  sensor.startContinuous(100);
  Serial.println("mVL53L1X API Configured"); 
}

void loop() {
  frameCount++;
  frameCount = frameCount % 36000;  // loop every hour @ 10fps
  
  int distanceMM = sensor.read();
  if (sensor.timeoutOccurred()) { 
    Serial.println(F("mVL53L0X error: Timeout"));
    // restart program if timeout happens, for self-recovery
    asm volatile ("  jmp 0");
  } else {
    // send value - add delay before writing serial
    delay(20);
    Serial.println('a'+String(distanceMM));
    
    // print uptime - add another delay
    if(frameCount % 10 == 0) {
      delay(20);
      Serial.println("mUptime:"+String(frameCount/10/60.)+"m");
    }
  }

  // 10 readings per second
  delay(80);
}
*/