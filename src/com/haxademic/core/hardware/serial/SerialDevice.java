package com.haxademic.core.hardware.serial;

import com.haxademic.core.app.P;

import processing.serial.Serial;

public class SerialDevice {
	
	// callback interface
	public interface ISerialDeviceDelegate {
		public void newDataAvailable(Serial serialDevice);
	}
	
	// Serial connection note:
	// - If no usb serial ports are listed on OS X, try downloading the FTDI driver: http://www.ftdichip.com/Drivers/VCP.htm
	
	// hardware connection
	protected ISerialDeviceDelegate delegate = null;
	protected int serialPortIndex = 0;
	protected int baudRate = 9600;

	protected Serial device;

	protected String serialInputString = null;
	
	public static final int lf = 10;    			// Linefeed in ASCII - used by Arduino interface
	public static final int cr = 13;    			// Carriage return in ASCII
	
	// thread the hardware communication
	protected SerialReader reader;
	protected Thread serailReadThread;
	protected Boolean readThreadBusy = false;
	
	protected Boolean writeThreadBusy = false;
	

	public SerialDevice(ISerialDeviceDelegate delegate, int serialPortIndex, int baudRate) {
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

	public boolean isWriteBusy() {
		return writeThreadBusy;
	}
	
	public boolean isError() {
		return (device == null);
	}
	
	// threaded serial port output
	
	public void write(byte[] byteArray) {
		if(writeThreadBusy == true) return;
		writeThreadBusy = true;
		new Thread(new Runnable() { public void run() {
			device.write(byteArray);
			writeThreadBusy = false;
		}}).start();
	}
	
	public void write(int intVal) {
		if(writeThreadBusy == true) return;
		writeThreadBusy = true;
		new Thread(new Runnable() { public void run() {
			device.write(intVal);
			writeThreadBusy = false;
		}}).start();
	}
	
	public void write(String strVal) {
		if(writeThreadBusy == true) return;
		writeThreadBusy = true;
		new Thread(new Runnable() { public void run() {
			device.write(strVal);
			writeThreadBusy = false;
		}}).start();
	}
	
	// check read/write threads
	
	public void post() {
		if(device == null) return;
		checkReadThread();
	}
	
	// threaded serial port input
	
	protected void checkReadThread() {
		if(readThreadBusy == true) return;
		readThreadBusy = true;
		if(reader == null) reader = new SerialReader();
		serailReadThread = new Thread(reader);
		serailReadThread.start();
	}
	
	class SerialReader implements Runnable {
		public SerialReader() {}    
		public void run() {
			updateSerialInput();
			readThreadBusy = false;
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
