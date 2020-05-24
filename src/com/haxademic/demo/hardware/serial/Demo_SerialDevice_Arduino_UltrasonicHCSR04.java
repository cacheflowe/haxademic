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

public class Demo_SerialDevice_Arduino_UltrasonicHCSR04
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String serialInputString = null;
	protected SerialDevice serialDevice;
	protected String DIST_MIN = "DIST_MIN";
	protected String DIST_MAX = "DIST_MAX";
	protected FloatBuffer ultrasonicAvg = new FloatBuffer(10);
	protected EasingFloat ultrasonicVal = new EasingFloat(0, 0.3f);

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
		ultrasonicVal.update(true);
		p.fill(255);
		float barH = P.map(ultrasonicVal.value(), UI.value(DIST_MIN), UI.value(DIST_MAX), 0, p.height);
		p.rect(0, p.height, p.width, -barH);
	}
	

	@Override
	public void newDataAvailable(Serial serialDevice) {
		String serialInputString = P.trim(serialDevice.readStringUntil(SerialDevice.cr));
		if(serialInputString != null) {
			// log incoming value
			DebugView.setValue("[Serial in]", p.frameCount + " | " + serialInputString);
			serialDevice.clear();
			
			// update easing value. bad readings are `-1`
			float newVal = ConvertUtil.stringToFloat(serialInputString);
			if(newVal > 0) {
				ultrasonicAvg.update(newVal);
				ultrasonicVal.setTarget(ultrasonicAvg.average());
			}
		}
	}

}


// ARDUINO CODE
/**
// Wiring:
    vcc to 5V
    trig to digital pin 13
    echo to digital pin 12
    gnd to gnd

// https://github.com/Martinsos/arduino-lib-hc-sr04
#include <HCSR04.h>

// Initialize sensor that uses digital pins 13 and 12.
int triggerPin = 13;
int echoPin = 12;
UltraSonicDistanceSensor distanceSensor(triggerPin, echoPin);

void setup () {
    Serial.begin(115200);  // We initialize serial connection so that we could print values from sensor.
}

void loop () {
    double distance = distanceSensor.measureDistanceCm();
    Serial.println(distance);
    delay(20);
}

*/