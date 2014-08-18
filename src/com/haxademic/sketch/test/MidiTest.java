package com.haxademic.sketch.test;

import netP5.NetAddress;
import oscP5.OscP5;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class MidiTest
extends PAppletHax {

	OscP5 oscP5;
	NetAddress myRemoteLocation;

	protected void overridePropsFile() {
//		 _appConfig.setProperty( "osc_active", "true" );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		background(0);
	}

	/**
	 * PApplet-level listener for MIDIBUS noteOn call
	 */
	public void noteOn(int channel, int  pitch, int velocity) {
		P.println(channel, pitch, velocity);
		if( _midi != null ) { 
			if( _midi.midiNoteIsOn( pitch ) == 0 ) {
				_midi.noteOn( channel, pitch, velocity );
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
		if( _midi != null ) _midi.noteOff( channel, pitch, velocity );
	}
	
	/**
	 * PApplet-level listener for MIDIBUS CC signal
	 */
	public void controllerChange(int channel, int number, int value) {
		if( _midi != null ) _midi.controllerChange( channel, number, value );
		P.println(channel, number, value);
	}
}