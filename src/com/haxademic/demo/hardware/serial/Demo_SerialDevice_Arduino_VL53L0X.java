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

public class Demo_SerialDevice_Arduino_VL53L0X
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
		serialDevice = new SerialDevice(this, 0, 115200);
		
		// set up UI for min/max
		UI.addTitle("VL53L0X Controls");
		UI.addSlider(DIST_MIN, 0, 0, 1000, 1, false);
		UI.addSlider(DIST_MAX, 300, 0, 1000, 1, false);
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
 // hardware assembly: https://learn.adafruit.com/adafruit-vl53l0x-micro-lidar-distance-sensor-breakout/arduino-code
 // VIN -> VIN
 // GND -> GND
 // SCL -> SCL || A5
 // SDA -> SDA || A4

 #include "Adafruit_VL53L0X.h"

 Adafruit_VL53L0X lox = Adafruit_VL53L0X();

 void setup() {
		Serial.begin(115200);

		// wait until serial port opens for native USB devices
		while (! Serial) {
			delay(1);
		}

		Serial.println("Adafruit VL53L0X test");
		if (!lox.begin()) {
			Serial.println(F("Failed to boot VL53L0X"));
			while(1);
		}
		// power
		Serial.println(F("VL53L0X API Simple Ranging data\n\n"));
	}


	void loop() {
		VL53L0X_RangingMeasurementData_t measure;

		// Serial.print("Reading a measurement... ");
		lox.rangingTest(&measure, false); // pass in 'true' to get debug data printout!

		if (measure.RangeStatus != 4) {  // phase failures have incorrect data
			// Serial.print("Distance (mm): "); Serial.println(measure.RangeMilliMeter);
			Serial.println('a'+String(measure.RangeMilliMeter));
		} else {
			Serial.println('a'+String(0));
			// Serial.println(" out of range ");
	}

	 delay(20);
 }

*/