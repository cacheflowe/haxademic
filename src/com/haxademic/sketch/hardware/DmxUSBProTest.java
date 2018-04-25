package com.haxademic.sketch.hardware;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import dmxP512.DmxP512;

public class DmxUSBProTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	DmxP512 dmx;
	
	// On Windows, port should be an actual serial port, and probably needs to be uppercase
	// On OS X, port will likely be a virtual serial port via USB, looking like "/dev/tty.usbserial-EN158815"
	// - To make this work, you need to install something like the Plugable driver: 
	// - https://plugable.com/2011/07/12/installing-a-usb-serial-adapter-on-mac-os-x/
	
	String DMXPRO_PORT = "DMXPRO_PORT";
	String DMXPRO_BAUDRATE = "DMXPRO_BAUDRATE";
	String DMXPRO_UNIVERSE_SIZE = "DMXPRO_UNIVERSE_SIZE";

	protected void overridePropsFile() {
		p.appConfig.setProperty(DMXPRO_PORT, "/dev/tty.usbserial-EN158815");
		p.appConfig.setProperty(DMXPRO_BAUDRATE, 115000);
		p.appConfig.setProperty(DMXPRO_UNIVERSE_SIZE, 128);
	}

	public void setupFirstFrame() {
		  dmx = new DmxP512(P.p, p.appConfig.getInt(DMXPRO_UNIVERSE_SIZE, 115000), false);
		  dmx.setupDmxPro(p.appConfig.getString(DMXPRO_PORT, "COM4"), p.appConfig.getInt(DMXPRO_BAUDRATE, 115000));
	}

	public void drawApp() {
		background(0);
		// color cycle
//		dmxOutput.set(1, round(127 + 127 * P.sin(p.frameCount * 0.2f)));
//		dmxOutput.set(2, round(127 + 127 * P.sin(p.frameCount * 0.08f)));
//		dmxOutput.set(3, round(127 + 127 * P.sin(p.frameCount * 0.02f)));
		// audio eq
		dmx.set(1, P.round(255 * p._audioInput.getFFT().spectrum[10]));
		dmx.set(2, P.round(255 * p._audioInput.getFFT().spectrum[20]));
		dmx.set(3, P.round(255 * p._audioInput.getFFT().spectrum[40]));
	}
}





