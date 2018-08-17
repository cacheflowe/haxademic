package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.color.EasingColor;

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
	
	int numLights = 7;
	int numColors = 3;
	int numChannels = numLights * numColors;

	protected boolean audioActive = false;
	
	protected EasingColor[] colors;
	protected EasingColor targetColor;

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
		// init dmx hardware connection
		Serial.list();
		dmx = new DmxP512(P.p, p.appConfig.getInt(DMXPRO_UNIVERSE_SIZE, 256), true);
		dmx.setupDmxPro(p.appConfig.getString(DMXPRO_PORT, "COM1"), p.appConfig.getInt(DMXPRO_BAUDRATE, 115000));
		
		// init easing colors
		colors = new EasingColor[numLights];
		for (int i = 0; i < numLights; i++) {
			colors[i] = new EasingColor(0x000000, 0.15f);
		}
		targetColor = new EasingColor(0x00ff00, 0.5f);
	}

	public void drawApp() {
		p.debugView.setValue("audioActive", audioActive);
		background(0);
		if(audioActive) {
			// audio eq
			for (int i = 0; i < numChannels; i++) {
				dmx.set(i+1, P.constrain(P.round(255 * p.audioFreq(5 + 5 * i)), 0, 255));
			}
		} else {
			// easing color zone
			for (int i = 0; i < numLights; i++) {
				colors[i].update();
			}
			targetColor.update();
			
			// step through lights every x frames
			int frameInterval = P.round(p.mousePercentX() * 10 + 1);
			if(p.frameCount % frameInterval == 0) {
				int frameDivided = P.floor(p.frameCount / frameInterval);
				int curLightIndex = frameDivided % numLights;
//				if(curLightIndex == 0) targetColor.setCurrentHex(ColorUtil.randomHex());
				colors[curLightIndex].setCurrentInt(targetColor.colorInt());
				colors[curLightIndex].setTargetInt(0x000000);
			}
			
			// send light rgb colors
			for (int i = 0; i < numChannels; i+=3) {
				int curLightIndex = P.floor(i / 3);
				int channelR = curLightIndex * numColors + 1;
				int channelG = curLightIndex * numColors + 2;
				int channelB = curLightIndex * numColors + 3;
				dmx.set(channelR, round(colors[curLightIndex].r()));
				dmx.set(channelG, round(colors[curLightIndex].g()));
				dmx.set(channelB, round(colors[curLightIndex].b()));
			}

//			// color cycle
//			for (int i = 0; i < numChannels; i++) {
//				float osc = (0.5f + 0.4f * P.sin(i)) * 0.15f;
//				dmx.set(i+1, round(127 + 127 * P.sin(p.frameCount * osc)));
//			}
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') audioActive = !audioActive;
	}
}





