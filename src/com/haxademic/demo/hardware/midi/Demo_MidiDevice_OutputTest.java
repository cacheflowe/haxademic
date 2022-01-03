package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import themidibus.SimpleMidiListener;

public class Demo_MidiDevice_OutputTest
extends PAppletHax
implements SimpleMidiListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected StringBufferLog logOut = new StringBufferLog(40);
	
	protected String MIDI_NOTE = "MIDI_NOTE";
	protected String MIDI_VELOCITY = "MIDI_VELOCITY";
	protected String MIDI_CHANNEL = "MIDI_CHANNEL";

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
		Config.setProperty(AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		// prep audio input
		AudioIn.instance();
		// init MIDI device
		MidiDevice.init(2, 5, this);			// basic singleton initialization in case there's only one device
		
		// UI
		UI.addTitle("MIDI OUTPUT");
		UI.addSlider(MIDI_NOTE, 0, 0, 127, 1, false);
		UI.addSlider(MIDI_VELOCITY, 127, 0, 127, 1, false);
		UI.addSlider(MIDI_CHANNEL, 0, 0, 127, 1, false);
	}
	
	protected void drawApp() {
		p.background(0);
		
		// debug views
		DebugView.active(true);
		logOut.printToScreen(p.g, 340, 20);
		
		// outgoing midi changes the LED color on the launch control
		if(FrameLoop.frameModLooped(10)) {
			MidiDevice.instance().sendMidiOut(true, UI.valueInt(MIDI_CHANNEL), UI.valueInt(MIDI_NOTE), UI.valueInt(MIDI_VELOCITY));
		}
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