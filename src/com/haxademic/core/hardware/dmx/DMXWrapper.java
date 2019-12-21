package com.haxademic.core.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

import dmxP512.DmxP512;
import processing.serial.Serial;

public class DMXWrapper {

	// This specifically works with the ENNTEC DMX USB Pro
	// Make sure the Processing Serial library is pointing to the correct native library
	
	// On Windows, port should be an actual serial port, and probably needs to be upper case - something like "COM1"
	// - Open Device Manager and go to Ports (COM & LPT)
	// - If plugged in, should be able to find something like "USB Serial Port (COM3)" 
	// - Right-click for properties, and you can look up the baud rate 
	
	// On OS X, port will likely be a virtual serial port via USB, looking like "/dev/tty.usbserial-EN158815"
	// - To make this work, you need to install something like the Plugable driver: 
	// - https://plugable.com/2011/07/12/installing-a-usb-serial-adapter-on-mac-os-x/
	// - And on my current MacBook Pro setup, I seem to have to keep installing it over again...
	
	// General DMX values from a 12v power supply that definitely differ depending on the fixture attached 
	// 1 = 2.14v
	// 9 = 2.5v
	// 18 = 3v
	// 53 = 5v
	// 119 = 9v
	// 240 = 12v

	
	public static final String DMXPRO_PORT = "DMXPRO_PORT";
	public static final String DMXPRO_BAUDRATE = "DMXPRO_BAUDRATE";
	public static final String DMXPRO_UNIVERSE_SIZE = "DMXPRO_UNIVERSE_SIZE";
	
	protected DmxP512 dmx;
	protected int universeSize = 512;

	public DMXWrapper(String port, int baudRate, int universeSize) {
		init(port, baudRate, universeSize);		
	}
	
	public DMXWrapper(String port, int baudRate) {
		init(port, baudRate, 512);
	}
	
	public DMXWrapper() {
		// if no port/baudrate, try pulling from appConfig 
		if (P.platform == P.MACOSX) {
			// mac
			init( 
				Config.getString(DMXPRO_PORT, "/dev/tty.usbserial-EN158815"), 
				Config.getInt(DMXPRO_BAUDRATE, 115000), 
				Config.getInt(DMXPRO_UNIVERSE_SIZE, 512)
			); 
		} else {
			// win
			init( 
				Config.getString(DMXPRO_PORT, "COM3"), 
				Config.getInt(DMXPRO_BAUDRATE, 9600), 
				Config.getInt(DMXPRO_UNIVERSE_SIZE, 512)
			);
		}
		
	}
	
	protected void init(String port, int baudRate, int universeSize) {
		this.universeSize = universeSize;
		P.out("Initializing DMXWrapper ---------------------");
		
		// debug serial devices
		String[] devicePorts = Serial.list();
		
		// check to see if requested device is found
		boolean foundDevice = false;
		P.out("Serial devices:");
		if (devicePorts != null && devicePorts.length > 0) {
			for (int i = 0; i < devicePorts.length; i++) {
				if(devicePorts[i].equals(port)) foundDevice = true;
				P.out("-", devicePorts[i]);
			}
		}

		if (foundDevice) {
			// init dmx object
			dmx = new DmxP512(P.p, universeSize, true);
			dmx.setupDmxPro(port, baudRate);
			dmx.setPriority(DmxP512.MAX_PRIORITY);
			P.out("DMX device found! - " + port + " / " + baudRate);
		} else {
			P.error("[ERROR] DMX device not found: ", port, "/", baudRate);
		}
	}
	
	public int universeSize() {
		return universeSize;
	}
	
	public void setValue(int channel, int value) {
		if (dmx != null) dmx.set(P.constrain(channel, 1, 512), P.constrain(value, 0, 255));
		if (channel < 1 || channel > 512) DebugView.setValue("DMX Error", "DMX channel out of range (1-"+ universeSize +"): " + value);
		if (value < 0 || value > 255) DebugView.setValue("DMX Error", "DMX value out of range (0-255): " + value);
	}
}
