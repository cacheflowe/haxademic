package com.haxademic.sketch.hardware;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class MidiTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
//		 p.appConfig.setProperty( "osc_active", "true" );
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
		if( midi != null ) { 
//			if( midi.isMidiButtonOn( pitch ) == 0 ) {
//				midi.noteOn( channel, pitch, velocity );
//				try{ 
//					handleInput( true );
//				}
//				catch( ArrayIndexOutOfBoundsException e ){println("noteOn BROKE!");}
//			}
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
//			p.midi.sendMidiOut(true, 1, 58, 127);
		}
	}

	
}