package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.render.FrameLoop;

import processing.serial.Serial;

public class Demo_SerialDevice_Arduino_MultiInput_Reconnect
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String serialInputString = null;
	protected EasingFloat easing = new EasingFloat(0, 0.1f);

	protected SerialDevice serialDevice;
	protected int lastSuccessTime = 0;
	protected int lastHeartbeatTime = 0;
	protected int lastVibrationTime = 0;
	protected StringBufferLog eventLog = new StringBufferLog(20);

	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		initSerialDevice();
	}

	protected void initSerialDevice() {
		SerialDevice.printDevices();
		String comPort = Config.getString("arduino_port", "COM7");
		serialDevice = new SerialDevice(this, comPort, 115200);
	}
	
	protected void reconnectSerial() {
		serialDevice.dispose();
		initSerialDevice();
	}

	protected void drawApp() {
		if(KeyboardState.keyTriggered(' ')) reconnectSerial();
		easing.update();
		p.background(easing.value() * 255);
		drawFailIndicator();
		eventLog.printToScreen(p.g, 50, 50);
	}

	protected void drawFailIndicator() {
		float sensorFailSize = FrameLoop.osc(0.1f, 50, 80);
		p.push();
		PG.setDrawCenter(p);
		p.fill(0xffff0000);
		p.ellipse(100, 100, sensorFailSize, sensorFailSize);
		p.pop();
	}

	//////////////////////////////////////
	// Serial input callback
	//////////////////////////////////////

	public void newDataAvailable(Serial serialDevice) {
		// String serialInputString = P.trim(serialDevice.readString());
		String serialInputString = P.trim(serialDevice.readStringUntil(SerialDevice.cr));
		if (serialInputString != null) {
			// log incoming value
			DebugView.setValue("[Serial in]", p.frameCount + " | " + serialInputString);
			eventLog.update("[Serial in] " + serialInputString.replace("\n", "").replace("\r", ""));
			// serialDevice.clear();

			// look for specific input characters
			if (serialInputString.indexOf('p') > -1 && p.millis() > lastSuccessTime + 1000) {
				lastSuccessTime = p.millis();
				eventLog.update("SHOT_TRIGGERED");
			}
			if (serialInputString.indexOf('h') > -1) {
				lastHeartbeatTime = p.millis();
				P.store.setBoolean("SENSOR_ACTIVE", p.millis() < lastHeartbeatTime + 10000);
				eventLog.update("HEARTBEAT");
			}
			if (serialInputString.indexOf('v') > -1 && p.millis() > lastVibrationTime + 300) {
				lastVibrationTime = p.millis();
				eventLog.update("SHOT_BOUNCE");
			}
			if (serialInputString.indexOf('t') > -1) {
				P.out("ARDUINO TIMEOUT");
				eventLog.update("ARDUINO TIMEOUT");
			}
		}
	}
}
