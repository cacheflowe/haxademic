package com.haxademic.core.hardware.serial;

import com.haxademic.core.app.P;

import processing.serial.Serial;

public class SerialDevice {
	
	// callback interface
	public interface ISerialInputLineReaderDelegate {
		public void newDataAvailable(Serial serialDevice);
	}
	
	// Serial connection note:
	// - If no usb serial ports are listed on OS X, try downloading the FTDI driver: http://www.ftdichip.com/Drivers/VCP.htm
	
	// hardware connection
	protected ISerialInputLineReaderDelegate delegate = null;
	protected int serialPortIndex = 0;
	protected int baudRate = 9600;

	protected Serial device;

	protected String serialInputString = null;
	
	public static final int lf = 10;    			// Linefeed in ASCII - used by Arduino interface
	public static final int cr = 13;    			// Carriage return in ASCII
	
	// thread the hardware communication
	protected SerialUpdater _loader;
	protected Thread _updateThread;
	protected Boolean threadWorking = false;
	

	public SerialDevice(ISerialInputLineReaderDelegate delegate, int serialPortIndex, int baudRate) {
		this.delegate = delegate;
		this.serialPortIndex = serialPortIndex;
		this.baudRate = baudRate;
		
		initSerialDevice();
		P.p.registerMethod("post", this);
	}
	
	public static void printDevices() {
		P.println("Serial devices:", Serial.list().length);
		for (int i = 0; i < Serial.list().length; i++) {
			P.println(Serial.list()[i]);
		}
	}

	protected void initSerialDevice() {
		if(serialPortIndex < Serial.list().length) {
			device = new Serial(P.p, Serial.list()[serialPortIndex], baudRate); 
			device.clear();
			// Throw out the first reading, in case we started reading in the middle of a string from the sender.
			device.readStringUntil(cr);
		}
	}
	
	// getters
	
	public Serial device() {
		return device;
	}

	public boolean isError() {
		return (device == null);
	}
	
	// events

	public void post() {
		if(device == null) return;
		if(threadWorking == false) {
			threadWorking = true;
			if(_loader == null) _loader = new SerialUpdater();
			_updateThread = new Thread( _loader );
			_updateThread.start();
		}
	}
	
	// threaded serial port reading
	
	class SerialUpdater implements Runnable {
		public SerialUpdater() {}    

		public void run() {
			updateSerialInput();
			threadWorking = false;
		} 
	}
	
	protected void updateSerialInput() {
		while (device.available() > 0) {
			if(delegate != null) {
				delegate.newDataAvailable(device);
			}
		} 
	}
	
}
