package com.haxademic.demo.hardware.serial;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.hardware.serial.SerialDevice;
import com.haxademic.core.hardware.serial.SerialDevice.ISerialInputLineReaderDelegate;

import processing.serial.Serial;

public class Demo_SerialDevice_Altimeter
extends PAppletHax
implements ISerialInputLineReaderDelegate {
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
		DrawUtil.setDrawCenter(p);
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

