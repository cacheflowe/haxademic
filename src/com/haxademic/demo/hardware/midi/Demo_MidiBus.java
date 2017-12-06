package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class Demo_MidiBus
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
//		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
	
	public void drawApp() {
		p.background(0);
		// p.midi.isMidiButtonOn(LaunchControl.PAD_01)
		p.midi.printButtons();
		p.midi.printCC();
	}
	
	/**
	 * Test sending a signal out
	 */
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			P.println("sending midi");
//			midiHandler.sendMidiOut(true, 1, 58, 127);
//			p.midi.sendMidiOut(true, 1, 58, 127);
		}
	}

	
}