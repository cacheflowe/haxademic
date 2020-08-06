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

public class Demo_SerialDevice_Arduino_UltrasonicHCSR04_Two
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String serialInputString = null;
	protected SerialDevice serialDevice;
	protected String DIST_MIN = "DIST_MIN";
	protected String DIST_MAX = "DIST_MAX";
	protected FloatBuffer ultrasonicAvg1 = new FloatBuffer(3);
	protected EasingFloat ultrasonicVal1 = new EasingFloat(0, 0.3f);
	protected FloatBuffer ultrasonicAvg2 = new FloatBuffer(10);
	protected EasingFloat ultrasonicVal2 = new EasingFloat(0, 0.3f);

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
		UI.addTitle("HC-SR04 Controls");
		UI.addSlider(DIST_MIN, 0, 0, 1000, 1, false);
		UI.addSlider(DIST_MAX, 50, 0, 1000, 1, false);
	}

	protected void drawApp() {
		background(0);
		
		// draw eased reading
		ultrasonicVal1.update(true);
		ultrasonicVal2.update(true);
		p.fill(255);
		float barH = P.map(ultrasonicVal1.value(), UI.value(DIST_MIN), UI.value(DIST_MAX), 0, p.height);
		p.rect(p.width/2, p.height, p.width/2, -barH);
		float barH2 = P.map(ultrasonicVal2.value(), UI.value(DIST_MIN), UI.value(DIST_MAX), 0, p.height);
		p.rect(0, p.height, p.width/2, -barH2);
	}
	

	@Override
	public void newDataAvailable(Serial serialDevice) {
		String serialInputString = P.trim(serialDevice.readStringUntil(SerialDevice.cr));
		if(serialInputString != null) {
			// log incoming value
			DebugView.setValue("[Serial in]", p.frameCount + " | " + serialInputString);
			serialDevice.clear();
			
			// update easing value. bad readings are `-1`
			if(serialInputString.substring(0, 1).equals("a")) {
				float newVal = ConvertUtil.stringToFloat(serialInputString.substring(1));
				DebugView.setValue("[Serial in a]", newVal);
				if(newVal > UI.value(DIST_MIN) && newVal < UI.value(DIST_MAX)) {
					ultrasonicAvg1.update(newVal);
					ultrasonicVal1.setTarget(ultrasonicAvg1.average());
				}
			}
			if(serialInputString.substring(0, 1).equals("b")) {
				float newVal = ConvertUtil.stringToFloat(serialInputString.substring(1));
				DebugView.setValue("[Serial in b]", newVal);
				if(newVal > 0) {
					ultrasonicAvg2.update(newVal);
					ultrasonicVal2.setTarget(ultrasonicAvg2.average());
				}
			}
		}
	}

}


// ARDUINO CODE
/**
// Wiring:
    vcc to 5V
    trig to digital pin 13/11
    echo to digital pin 12/10
    gnd to gnd

// https://github.com/Martinsos/arduino-lib-hc-sr04
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

*/