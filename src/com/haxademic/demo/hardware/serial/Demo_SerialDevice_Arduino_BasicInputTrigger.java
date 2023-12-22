package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;
import com.haxademic.core.math.easing.EasingFloat;

import processing.serial.Serial;

public class Demo_SerialDevice_Arduino_BasicInputTrigger
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String serialInputString = null;
	protected SerialDevice serialDevice;
	protected EasingFloat easing = new EasingFloat(0, 0.1f);

	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		SerialDevice.printDevices();
		serialDevice = new SerialDevice(this, "COM7", 115200); 
	}

	protected void drawApp() {
		easing.update();
		background(easing.value() * 255);
	}
	

	@Override
	public void newDataAvailable(Serial serialDevice) {
		String serialInputString = P.trim(serialDevice.readStringUntil(SerialDevice.cr));
		if(serialInputString != null) {
			DebugView.setValue("[Serial in]", p.frameCount + " | " + serialInputString);
			serialDevice.clear();
			easing.setCurrent(1).setTarget(0);
		}
	}

}
