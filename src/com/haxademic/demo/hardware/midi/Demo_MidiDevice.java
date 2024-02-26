package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.midi.devices.LaunchControlXL;
import com.haxademic.core.media.audio.analysis.AudioIn;

import themidibus.SimpleMidiListener;

public class Demo_MidiDevice
extends PAppletHax
implements SimpleMidiListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected StringBufferLog logOut = new StringBufferLog(40);
	protected MidiDevice device1;
	protected MidiDevice device2;
	protected MidiDevice device3;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// prep audio input
		AudioIn.instance();
		
		// init 2 devices
		device1 = MidiDevice.init(LaunchControlXL.deviceName2, LaunchControlXL.deviceName2, this);	// basic singleton initialization in case there's only one device
		device2 = new MidiDevice(LaunchControlXL.deviceName3, LaunchControlXL.deviceName3, this);	// a 2nd device, with normal constructor
//		device3 = new MidiDevice(12, 15, this);	// a 2nd device, with normal constructor
	}
	
	protected void drawApp() {
		p.background(0);
		
		// debug log incoming messages
		logOut.printToScreen(p.g, p.width - 300, 20);

		// test MidiState storage
		DebugView.setValue("PAD 1 on", MidiState.instance().isMidiNoteOn(LaunchControl.PAD_01));
		
		// test midi out to launchpad
		// updateLaunchpadLEDs();
	}
	
	protected void updateLaunchpadLEDs() {
		// outgoing midi changes the LED color on the launch control
		int launchControlChannel = 0;
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_01, P.round(AudioIn.audioFreq(2) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_02, P.round(AudioIn.audioFreq(3) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_03, P.round(AudioIn.audioFreq(4) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_04, P.round(AudioIn.audioFreq(5) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_05, P.round(AudioIn.audioFreq(6) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_06, P.round(AudioIn.audioFreq(7) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_07, P.round(AudioIn.audioFreq(8) * 150f));
		MidiDevice.instance().sendMidiOut(true, launchControlChannel, LaunchControl.PAD_08, P.round(AudioIn.audioFreq(9) * 150f));
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