package com.haxademic.sketch.hardware;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;

import beads.AudioContext;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import dmxP512.DmxP512;

public class DmxUSBProMIDIFeet
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	DmxP512 dmx;
	
	// On Windows, port should be an actual serial port, and probably needs to be uppercase - something like "COM1"
	// On OS X, port will likely be a virtual serial port via USB, looking like "/dev/tty.usbserial-EN158815"
	// - To make this work, you need to install something like the Plugable driver: 
	// - https://plugable.com/2011/07/12/installing-a-usb-serial-adapter-on-mac-os-x/
	
	String DMXPRO_PORT = "DMXPRO_PORT";
	String DMXPRO_BAUDRATE = "DMXPRO_BAUDRATE";
	String DMXPRO_UNIVERSE_SIZE = "DMXPRO_UNIVERSE_SIZE";
	
	protected boolean audioActive = false;
	
	AudioContext ac;
	Sample sample01;
	int sampleTime01 = 0;
	Sample sample02;
	int sampleTime02 = 0;
	
	protected InputTrigger trigger1 = new InputTrigger(
			new char[]{'1'},
			null,
			new Integer[]{43},
			null,
			null
	);
	protected EasingColor color1 = new EasingColor(0xff00ff00, 8);
	protected InputTrigger trigger2 = new InputTrigger(
			new char[]{'2'},
			null,
			new Integer[]{49},
			null,
			null
			);
	protected EasingColor color2 = new EasingColor(0xffff0000, 8);


	protected void overridePropsFile() {
		p.appConfig.setProperty(DMXPRO_PORT, "/dev/tty.usbserial-EN158815");
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}

	public void setupFirstFrame() {
		dmx = new DmxP512(P.p, p.appConfig.getInt(DMXPRO_UNIVERSE_SIZE, 128), false);
		dmx.setupDmxPro(p.appConfig.getString(DMXPRO_PORT, "COM1"), p.appConfig.getInt(DMXPRO_BAUDRATE, 115000));

		ac = new AudioContext();
		sample01 = SampleManager.sample(FileUtil.getFile("audio/kit808/kick.wav"));
		sample02 = SampleManager.sample(FileUtil.getFile("audio/kit808/snare.wav"));
		ac.start();
	}

	public void drawApp() {
		background(0);
		
		if(trigger1.triggered() && p.millis() > sampleTime01 + 200) {
			sampleTime01 = p.millis();
			color1.setCurrentInt(0xffffffff);
			color1.setTargetInt(0xff005500);
			SamplePlayer samplePlayer01 = new SamplePlayer(ac, sample01);
			samplePlayer01.setKillOnEnd(true);
			ac.out.addInput(samplePlayer01);
			samplePlayer01.start();
		}
		if(trigger2.triggered() && p.millis() > sampleTime02 + 200) {
			sampleTime02 = p.millis();
			color2.setCurrentInt(0xffffffff);
			color2.setTargetInt(0xff000055);
			SamplePlayer samplePlayer02 = new SamplePlayer(ac, sample02);
			samplePlayer02.setKillOnEnd(true);
			ac.out.addInput(samplePlayer02);
			samplePlayer02.start();
		}
		
		color1.update();
		color2.update();
		
		if(!audioActive) {
			dmx.set(1, (int)color1.r());
			dmx.set(2, (int)color1.g());
			dmx.set(3, (int)color1.b());
			dmx.set(4, (int)color2.r());
			dmx.set(5, (int)color2.g());
			dmx.set(6, (int)color2.b());
		} else {
			dmx.set(1, P.round(255 * p.audioFreq(10)));
			dmx.set(2, P.round(255 * p.audioFreq(20)));
			dmx.set(3, P.round(255 * p.audioFreq(40)));
			dmx.set(4, P.round(255 * p.audioFreq(60)));
			dmx.set(5, P.round(255 * p.audioFreq(80)));
			dmx.set(6, P.round(255 * p.audioFreq(100)));
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') audioActive = !audioActive;
	}
}





