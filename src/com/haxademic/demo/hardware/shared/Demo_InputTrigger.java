package com.haxademic.demo.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.hardware.keyboard.Keyboard;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.shared.InputTrigger;

public class Demo_InputTrigger
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected InputTrigger trigger = new InputTrigger(
			new char[]{'c', 'v'},
			new Integer[]{LaunchControl.PAD_01, LaunchControl.PAD_03}
	);
	
	protected int triggerKey = Keyboard.keyCodeFromChar('c');
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}
	
	public void drawApp() {
		// if(hardwareButtons.isKeyOn(triggerKey) || hardwareButtons.isMidiButtonOn(26)) {
		if(trigger.triggered()) P.println("trigger");
		if(trigger.on()) {
			p.background(0, 255, 0);
		} else {
			p.background(0);
		}
		keyboardState.printKeys();
		midi.printButtons();
		midi.printCC();
	}
	
}
