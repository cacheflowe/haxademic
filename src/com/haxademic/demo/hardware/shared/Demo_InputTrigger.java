package com.haxademic.demo.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.keyboard.KeyCodes;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;

public class Demo_InputTrigger
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	
	protected InputTrigger trigger = (new InputTrigger()).addKeyCodes(new char[]{'b', 'v'})
														 .addOscMessages(new String[]{"/video-start", "/1/faderC"})
														 .addMidiNotes(new Integer[]{LaunchControl.PAD_01, LaunchControl.PAD_03})
														 .addWebControls(new String[]{"button1", "slider1", "slider2"})
														 .addGamepadControls(new String[]{"Button 2"});

	protected int triggerKey = KeyCodes.keyCodeFromChar('c');
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.OSC_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.GAMEPADS_ACTIVE, true );
	}
	
	
	public void setupFirstFrame() {
		MidiDevice.init(0, 0);
		server = new WebServer(new UIControlsHandler(), true);
	}
	
	public void drawApp() {
		// show triggering - TODO: add CC changes to trigger
		if(KeyboardState.instance().isKeyOn(triggerKey) || MidiState.instance().isMidiButtonOn(LaunchControl.PAD_01) || p.oscState.isValueOn("/toggleC_2")) P.println("trigger 1"); 
		if(trigger.triggered()) P.println("trigger 2");
		if(trigger.on()) {
			p.background(0, 255, 255f * p.oscState.getValue("/1/faderC"));
		} else {
			p.background(0);
		}
		
		// debug print maps
		KeyboardState.instance().printKeys();
		MidiState.instance().printButtons();
		MidiState.instance().printCC();
		p.oscState.printButtons();
		p.browserInputState.printButtons();
		p.gamepadState.printControls();
	}
	
}
