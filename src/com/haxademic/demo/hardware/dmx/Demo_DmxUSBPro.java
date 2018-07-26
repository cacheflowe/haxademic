package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import dmxP512.DmxP512;
import processing.serial.Serial;

public class Demo_DmxUSBPro
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	DmxP512 dmx;
	
	// Make sure the Processing Serial library is pointing to the correct native library
	
	// On Windows, port should be an actual serial port, and probably needs to be upper case - something like "COM1"
	// - Open Device Manager and go to Ports (COM & LPT)
	// - If plugged in, should be able to find something like "USB Serial Port (COM3)" 
	// - Right-click for properties, and you can look up the baud rate 
	
	// On OS X, port will likely be a virtual serial port via USB, looking like "/dev/tty.usbserial-EN158815"
	// - To make this work, you need to install something like the Plugable driver: 
	// - https://plugable.com/2011/07/12/installing-a-usb-serial-adapter-on-mac-os-x/
	// - And on my current MacBook Pro setup, I seem to have to keep installing it over again...
	
	String DMXPRO_PORT = "DMXPRO_PORT";
	String DMXPRO_BAUDRATE = "DMXPRO_BAUDRATE";
	String DMXPRO_UNIVERSE_SIZE = "DMXPRO_UNIVERSE_SIZE";
	
	protected boolean audioActive = false;

	protected void overridePropsFile() {
		if(P.platform == P.MACOSX) {
			// mac
			p.appConfig.setProperty(DMXPRO_PORT, "/dev/tty.usbserial-EN158815");
			p.appConfig.setProperty(DMXPRO_BAUDRATE, 115000);
		} else {
			// win
			p.appConfig.setProperty(DMXPRO_PORT, "COM3");
			p.appConfig.setProperty(DMXPRO_BAUDRATE, 9600);
		}
	}

	public void setupFirstFrame() {
		Serial.list();
		dmx = new DmxP512(P.p, p.appConfig.getInt(DMXPRO_UNIVERSE_SIZE, 256), false);
		dmx.setupDmxPro(p.appConfig.getString(DMXPRO_PORT, "COM1"), p.appConfig.getInt(DMXPRO_BAUDRATE, 115000));
	}

	public void drawApp() {
		background(0);
		if(audioActive) {
			// audio eq
			dmx.set(1, P.constrain(P.round(255 * p.audioFreq(10)), 0, 255));
			dmx.set(2, P.constrain(P.round(255 * p.audioFreq(20)), 0, 255));
			dmx.set(3, P.constrain(P.round(255 * p.audioFreq(40)), 0, 255));
			dmx.set(4, P.constrain(P.round(255 * p.audioFreq(60)), 0, 255));
			dmx.set(5, P.constrain(P.round(255 * p.audioFreq(80)), 0, 255));
			dmx.set(6, P.constrain(P.round(255 * p.audioFreq(100)), 0, 255));
		} else {
			// color cycle
			dmx.set(1, round(127 + 127 * P.sin(p.frameCount * 0.2f)));
			dmx.set(2, round(127 + 127 * P.sin(p.frameCount * 0.08f)));
			dmx.set(3, round(127 + 127 * P.sin(p.frameCount * 0.02f)));
			dmx.set(4, round(127 + 127 * P.sin(p.frameCount * 0.1f)));
			dmx.set(5, round(127 + 127 * P.sin(p.frameCount * 0.07f)));
			dmx.set(6, round(127 + 127 * P.sin(p.frameCount * 0.04f)));
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') audioActive = !audioActive;
	}
}





