package com.haxademic.sketch.test;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class RobotKeyPressMidiTest
extends PAppletHax {
	
	protected Robot _robot;
	protected boolean _keyOn = false;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}

	public void setup() {
		super.setup();	
		try { _robot = new Robot(); } catch( Exception error ) { println("couldn't init Robot"); }
	}

	public void drawApp() {
		background(0);

		if(_keyOn == true) {
			_robot.keyRelease(KeyEvent.VK_A);
			_keyOn = false;
		}
	}
	
	/**
	 * PApplet-level listener for MIDIBUS noteOn call
	 */
	public void noteOn(int channel, int  pitch, int velocity) {
		P.println(channel, pitch, velocity);
		if(pitch == 39) {
			_keyOn = true;
			_robot.keyPress(KeyEvent.VK_A);
		}
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

}
