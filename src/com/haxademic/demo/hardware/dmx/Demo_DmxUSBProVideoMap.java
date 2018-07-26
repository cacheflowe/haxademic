package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;

import dmxP512.DmxP512;
import processing.core.PGraphics;
import processing.serial.Serial;
import processing.video.Movie;

public class Demo_DmxUSBProVideoMap
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
	
	protected Movie video;
	protected PGraphics videoBuffer;

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
		// init DMX
		Serial.list();
		dmx = new DmxP512(P.p, p.appConfig.getInt(DMXPRO_UNIVERSE_SIZE, 256), false);
		dmx.setupDmxPro(p.appConfig.getString(DMXPRO_PORT, "COM1"), p.appConfig.getInt(DMXPRO_BAUDRATE, 115000));
		
		// init video
		video = DemoAssets.movieFractalCube();
		video.loop();
	}

	public void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		
		// lazy-init video copy buffer
		if(videoBuffer == null && video.width > 50) {
			videoBuffer = p.createGraphics(video.width, video.height, P.P2D);
		}
		
		if(videoBuffer != null) {
			// copy video to buffer
			ImageUtil.copyImage(video, videoBuffer);
			
			// access bitmap pixel data
			videoBuffer.loadPixels();
			int pixelX = 250;
			int pixelY = 250;
			int colorFromVideo = ImageUtil.getPixelColor(videoBuffer, pixelX, pixelY);
			
			// draw video 
			p.fill(255);
			p.image(videoBuffer, 0, 0);
			
			// debug pixel grabbing position
			p.fill(0, 255, 0);
			p.rect(pixelX, pixelY, 3, 3);
			
			// color cycle
			dmx.set(1, P.round(p.red(colorFromVideo)));
			dmx.set(2, P.round(p.green(colorFromVideo)));
			dmx.set(3, P.round(p.blue(colorFromVideo)));
		}
	}

}
