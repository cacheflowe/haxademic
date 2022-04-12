package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.hardware.dmx.DMXFixture;
import com.haxademic.core.hardware.dmx.DMXUniverse;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.RolandSPDSX;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.media.audio.analysis.AudioIn;

import themidibus.SimpleMidiListener;

public class Demo_DmxMidiToFixtures
extends PAppletHax 
implements SimpleMidiListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DMXFixture fixtures[];
	protected MidiDevice device;
	protected MidiDevice device2;
	protected StringBufferLog logOut = new StringBufferLog(40);
	protected Midi2DMX[] midi2Dmx;

	protected void firstFrame() {
		// Build DMXUniverse for fixtures
		DMXUniverse.instanceInit("COM8", 115200);
		midi2Dmx = new Midi2DMX[] {
				new Midi2DMX('1', RolandSPDSX.PAD_01, 1),
				new Midi2DMX('2', RolandSPDSX.PAD_02, 4),
				new Midi2DMX('3', RolandSPDSX.PAD_03, 7),
				new Midi2DMX('4', RolandSPDSX.PAD_04, 10),
				new Midi2DMX('5', RolandSPDSX.PAD_05, 13),
				new Midi2DMX('6', RolandSPDSX.PAD_06, 16),
				new Midi2DMX('7', RolandSPDSX.PAD_07, 19),
				new Midi2DMX('8', RolandSPDSX.PAD_08, 22),
				new Midi2DMX('9', RolandSPDSX.PAD_09, 25),
		};
		
		// prep audio input
		AudioIn.instance();
		
		// init MIDI device
		device = MidiDevice.init(1, 4, this);	// basic singleton initialization in case there's only one device
		device2 = new MidiDevice(2, 5, this);	// add 2nd input device
		MidiState.instance();
	}

	protected void drawApp() {
		p.background(0);
		
		// debug
		if(MidiState.instance().isMidiNoteOn(RolandSPDSX.PAD_07)) P.println("trigger 1"); 
		
		// check input triggers
		// THIS DOESN'T WORK with SPD-SX, because the on/off note events happen at the same time...
		// MidiState should handle this...
//		if(trigger1.triggered()) fixtures[0].color().setCurrentInt(0xffffffff).setTargetInt(0xff000000);
//		if(trigger2.triggered()) fixtures[1].color().setCurrentInt(0xffffffff).setTargetInt(0xff000000);
//		if(trigger3.triggered()) fixtures[2].color().setCurrentInt(0xffffffff).setTargetInt(0xff000000);
//		if(trigger4.triggered()) fixtures[3].color().setCurrentInt(0xffffffff).setTargetInt(0xff000000);

		// draw MIDI event log
		logOut.printToScreen(p.g, p.width - 300, 20);

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
		
		// add direct midi listeners
		for (int i = 0; i < midi2Dmx.length; i++) {
			midi2Dmx[i].checkIncomingPitch(pitch);
		}
	}

	
	public class Midi2DMX {
		
		protected InputTrigger trigger;
		protected DMXFixture fixture;
		protected int midiNote;
		
		public Midi2DMX(char keyTrigger, int midiTrigger, int dmxChannel) {
			midiNote = midiTrigger;
			trigger = new InputTrigger().addKeyCodes(new char[]{keyTrigger}).addMidiNotes(new Integer[]{midiTrigger});
			fixture = (new DMXFixture(dmxChannel)).setEaseFactor(0.2f);
			fixture.color().setCurrentInt(0xffffffff).setTargetInt(0xff000000);
		}
		
		public void checkIncomingPitch(int pitch) {
			// pick random color and fade back to black
			int rand = p.color(p.random(50, 255), p.random(50, 255), p.random(50, 255));
			if(pitch == midiNote) fixture.color().setCurrentInt(rand).setTargetInt(0xff000000);
		}
		
	}
}
