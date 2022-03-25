package com.haxademic.demo.hardware.dmx;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
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
	protected StringBufferLog logOut = new StringBufferLog(40);

	protected InputTrigger trigger1 = new InputTrigger().addKeyCodes(new char[]{'1'}).addMidiNotes(new Integer[]{RolandSPDSX.PAD_06});
	protected InputTrigger trigger2 = new InputTrigger().addKeyCodes(new char[]{'2'}).addMidiNotes(new Integer[]{RolandSPDSX.PAD_07});
	protected InputTrigger trigger3 = new InputTrigger().addKeyCodes(new char[]{'3'}).addMidiNotes(new Integer[]{RolandSPDSX.PAD_08});
	protected InputTrigger trigger4 = new InputTrigger().addKeyCodes(new char[]{'4'}).addMidiNotes(new Integer[]{RolandSPDSX.PAD_09});
	
	protected void firstFrame() {
		// Build DMXFixtures
		DMXUniverse.instanceInit("COM7", 115200);
		fixtures = new DMXFixture[] {
				(new DMXFixture(1)).setEaseFactor(0.4f),
				(new DMXFixture(4)).setEaseFactor(0.4f),
				(new DMXFixture(7)).setEaseFactor(0.4f),
				(new DMXFixture(10)).setEaseFactor(0.4f),
		};
		
		// prep audio input
		AudioIn.instance();
		
		// init MIDI device
		device = MidiDevice.init(1, 4, this);	// basic singleton initialization in case there's only one device
		MidiState.instance();
	}

	protected void drawApp() {
		p.background(0);
		
		// debug
		if(MidiState.instance().isMidiNoteOn(RolandSPDSX.PAD_07)) P.println("trigger 1"); 
		DebugView.setValue("trigger1.triggered()", trigger1.triggered());
		DebugView.setValue("trigger2.triggered()", trigger2.triggered());
		DebugView.setValue("trigger3.triggered()", trigger3.triggered());
		
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
		int rand = p.color(p.random(50, 255), p.random(50, 255), p.random(50, 255));
		if(pitch == RolandSPDSX.PAD_06) fixtures[0].color().setCurrentInt(rand).setTargetInt(0xff000000);
		if(pitch == RolandSPDSX.PAD_07) fixtures[1].color().setCurrentInt(rand).setTargetInt(0xff000000);
		if(pitch == RolandSPDSX.PAD_08) fixtures[2].color().setCurrentInt(rand).setTargetInt(0xff000000);
		if(pitch == RolandSPDSX.PAD_09) fixtures[3].color().setCurrentInt(rand).setTargetInt(0xff000000);
	}

}
