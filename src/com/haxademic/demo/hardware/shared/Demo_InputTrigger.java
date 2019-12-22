package com.haxademic.demo.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.gamepad.GamepadState;
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
	
	protected void config() {
		Config.setProperty(AppSettings.OSC_ACTIVE, true );
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}
	
	
	public void firstFrame() {
		// KeyboardState is auto-initialized in `P`
		MidiDevice.init(0, 3);
		GamepadState.instance();
		server = new WebServer(new UIControlsHandler(), true);
	}
	
	public void drawApp() {
		// show triggering - TODO: add CC changes to trigger
		if(KeyboardState.instance().isKeyOn(triggerKey) || MidiState.instance().isMidiNoteOn(LaunchControl.PAD_01) || p.oscState.isValueOn("/toggleC_2")) P.println("trigger 1"); 
		if(trigger.triggered()) P.println("trigger 2");
		if(trigger.on()) {
			p.background(0, 255, 255f * p.oscState.getValue("/1/faderC"));
		} else {
			p.background(0);
		}
		
		// debug print maps
		p.oscState.printButtons();
		p.browserInputState.printButtons();
	}
	
}
