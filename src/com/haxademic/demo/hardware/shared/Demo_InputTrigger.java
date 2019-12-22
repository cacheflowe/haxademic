package com.haxademic.demo.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.gamepad.GamepadState;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.hardware.keyboard.KeyCodes;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.osc.OscState;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_InputTrigger
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int triggerKey = KeyCodes.keyCodeFromChar('c');
	protected InputTrigger trigger = (new InputTrigger()).addKeyCodes(new char[]{'b', 'v'})
														 .addOscMessages(new String[]{"/video-start", "/3/fader1"})
														 .addMidiNotes(new Integer[]{LaunchControl.PAD_01, LaunchControl.PAD_03})
														 .addMidiCCNotes(new Integer[]{LaunchControl.KNOB_01})
														 .addHttpRequests(new String[]{"button1", "slider1", "slider2"})
														 .addGamepadControls(new String[]{"Button 9"})
														 .setBroadcastKey("TRIGGER");

	
	public void firstFrame() {
		// KeyboardState is auto-initialized in `P`
		KeyboardState.instance();	// ...but we can put it here for fun & uniformity
		MidiDevice.init(0, 3);
		HttpInputState.instance();
		GamepadState.instance();
		OscState.instance();
		
		// keep debug open
		DebugView.autoHide(false);
		DebugView.active(true);
		
		// listen for broadcast
		P.store.addListener(this);
	}
	
	public void drawApp() {
		// show triggering - TODO: add CC changes to trigger
		if(KeyboardState.instance().isKeyOn(triggerKey) || MidiState.instance().isMidiNoteOn(LaunchControl.PAD_01) || OscState.instance().isValueOn("/toggleC_2")) P.println("trigger 1"); 
		if(trigger.triggered()) P.println("trigger 2");
		if(trigger.on()) {
			p.background(0, 255, 255f * OscState.instance().getValue("/1/faderC"));
		} else {
			p.background(0);
		}
		DebugView.setValue("trigger.value()", trigger.value());
	}

	/////////////////////////////
	// AppStore callbacks
	/////////////////////////////
	
	public void updatedNumber(String key, Number val) {
		if(key.equals("TRIGGER")) {
			DebugView.setValue("AppStore TRIGGER", val.floatValue());
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
}
