package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.UC33;

import themidibus.SimpleMidiListener;

public class Demo_MidiDevice_LaunchKeyMini
extends PAppletHax
implements SimpleMidiListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected StringBufferLog logOut = new StringBufferLog(20);

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// init 1 device w/basic singleton initialization
		MidiDevice.init(UC33.deviceName, this);
	}
	
	protected void drawApp() {
		p.background(MidiState.instance().midiCCNormalized(UC33.KNOB_ROW1_01) * 255);
		
		// debug views
		logOut.printToScreen(p.g, 340, 20);
	}
	
	////////////////////////////////////
	// SimpleMidiListener callbacks
	////////////////////////////////////

	public void controllerChange(int channel, int  pitch, int velocity) {
		logOut.update("CC: " + channel + ", " + pitch + ", " + velocity);
	}

	public void noteOff(int channel, int  pitch, int velocity) {
		logOut.update("OFF: " + channel + ", " + pitch + ", " + velocity);
	}

	public void noteOn(int channel, int pitch, int velocity) {
		logOut.update("ON:  " + channel + ", " + pitch + ", " + velocity);
	}
	
}