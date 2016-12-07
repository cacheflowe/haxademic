package com.haxademic.sketch.hardware;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import themidibus.MidiBus;

public class MidibusTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MidiBus midiBus;
	protected int MIDI_IN_INDEX = 0;
	protected int MIDI_OUT_INDEX = 4;

	protected void overridePropsFile() {
		 p.appConfig.setProperty( "midi_device_in", "Launch Control" );
	}

	public void setup() {
		super.setup();
		initMidi();
	}

	protected void initMidi() {
		MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.
//		midiBus = new MidiBus(this, MIDI_IN_INDEX, MIDI_OUT_INDEX);
		midiBus = new MidiBus(this);
	}

	public void drawApp() {
		background(0);
	}

	/**
	 * PApplet-level listener for MIDIBUS noteOn call
	 */
	public void noteOn(int channel, int  pitch, int velocity) {
		P.println(channel, pitch, velocity);
		if( midi != null ) { 
			if( midi.midiNoteIsOn( pitch ) == 0 ) {
				midi.noteOn( channel, pitch, velocity );
				try{ 
					handleInput( true );
				}
				catch( ArrayIndexOutOfBoundsException e ){println("noteOn BROKE!");}
			}
		}
	}
	
	/**
	 * PApplet-level listener for MIDIBUS noteOff call
	 */
	public void noteOff(int channel, int  pitch, int velocity) {
		P.println(channel, pitch, velocity);
		if( midi != null ) midi.noteOff( channel, pitch, velocity );
	}
	
	/**
	 * PApplet-level listener for MIDIBUS CC signal
	 */
	public void controllerChange(int channel, int number, int value) {
		if( midi != null ) midi.controllerChange( channel, number, value );
		P.println(channel, number, value);
	}
	
	/**
	 * Test sending a signal out
	 */
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			P.println("sending midi");
//			midiHandler.sendMidiOut(true, 1, 58, 127);
			p.midi.sendMidiOut(true, 1, 58, 127);
		}
	}

	
}