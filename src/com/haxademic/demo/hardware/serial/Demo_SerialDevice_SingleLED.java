package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialDeviceDelegate;

import processing.serial.Serial;

public class Demo_SerialDevice_SingleLED
extends PAppletHax
implements ISerialDeviceDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SerialDevice serialDevice;
	protected int numLights = 60;

	protected void config() {
	}

	protected void firstFrame() {
		SerialDevice.printDevices();
		serialDevice = new SerialDevice(this, 0, 115200); 
	}

	protected void drawApp() {
		background(0);
		updateLedLights();
	}
	
	protected void updateLedLights() {
		int numLights = 120;
		serialDevice.device().write(new byte[] {
			'c', 
			P.parseByte((p.frameCount+1) % numLights), 
			ConvertUtil.intToByte(127),
			ConvertUtil.intToByte(127),
			ConvertUtil.intToByte(0),
			'c', 
			P.parseByte(p.frameCount % numLights), 
			ConvertUtil.intToByte(127),
			ConvertUtil.intToByte(127),
			ConvertUtil.intToByte(0),
			'c', 
			P.parseByte((p.frameCount-1) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			'c', 
			P.parseByte((p.frameCount-2) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			'c', 
			P.parseByte((p.frameCount-3) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),

			'c', 
			P.parseByte((p.frameCount+1 + numLights/2) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(127),
			ConvertUtil.intToByte(127),
			'c', 
			P.parseByte((p.frameCount + numLights/2) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(127),
			ConvertUtil.intToByte(127),
			'c', 
			P.parseByte((p.frameCount-1 + numLights/2) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			'c', 
			P.parseByte((p.frameCount-2 + numLights/2) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			'c', 
			P.parseByte((p.frameCount-3 + numLights/2) % numLights), 
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0),
			ConvertUtil.intToByte(0)
		});
	}

	@Override
	public void newDataAvailable(Serial serialDevice) {
		// log incoming messages
		String serialIn = serialDevice.readString();
		DebugView.setValue("[Serial in]", serialIn);
		P.out(serialIn);
	}

}

