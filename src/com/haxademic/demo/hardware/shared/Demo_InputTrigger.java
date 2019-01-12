package com.haxademic.demo.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.keyboard.Keyboard;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandlerUIControls;

public class Demo_InputTrigger
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	
	protected InputTrigger trigger = (new InputTrigger()).addKeyCodes(new char[]{'c', 'v'})
														 .addOscMessages(new String[]{"/toggleC_2", "/1/faderC"})
														 .addMidiNotes(new Integer[]{LaunchControl.PAD_01, LaunchControl.PAD_03})
														 .addWebControls(new String[]{"slider1", "slider2"})
														 .addGamepadControls(new String[]{"Button 2"});

	protected int triggerKey = Keyboard.keyCodeFromChar('c');
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty(AppSettings.OSC_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.GAMEPADS_ACTIVE, true );
	}
	
	
	public void setup() {
		super.setup();	
		server = new WebServer(new WebServerRequestHandlerUIControls(), true);
	}
	
	public void drawApp() {
		// show triggering - TODO: add CC changes to trigger
		if(p.keyboardState.isKeyOn(triggerKey) || p.midiState.isMidiButtonOn(LaunchControl.PAD_01) || p.oscState.isValueOn("/toggleC_2")) P.println("trigger 1"); 
		if(trigger.triggered()) P.println("trigger 2");
		if(trigger.on()) {
			p.background(0, 255, 255f * p.oscState.getValue("/1/faderC"));
		} else {
			p.background(0);
		}
		
		// debug print maps
		p.keyboardState.printKeys();
		p.midiState.printButtons();
		p.midiState.printCC();
		p.oscState.printButtons();
		p.browserInputState.printButtons();
		p.gamepadState.printControls();
	}
	
}
