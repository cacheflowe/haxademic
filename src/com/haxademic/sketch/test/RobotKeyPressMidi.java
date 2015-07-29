package com.haxademic.sketch.test;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class RobotKeyPressMidi
extends PAppletHax {
	
	public static void main(String args[]) {
		PAppletHax.main(P.concat(args, new String[] { "--hide-stop", "--bgcolor=000000", RobotKeyPressMidi.class.getName() }));
	}

	protected Robot _robot;
	
	// player 1
	// A: A
	// B: S
	
	// SELECT: RIGHT SHIFT
	// START: RETURN

	// player 2
	// A: K
	// B: L
	
	protected MidiKeyTrigger[] midiKeyTriggers;

	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "480" );
	}

	public void setup() {
		super.setup();	
		try { _robot = new Robot(); } catch( Exception error ) { println("couldn't init Robot"); }
		
		midiKeyTriggers = new MidiKeyTrigger[] {
				new MidiKeyTrigger(39, KeyEvent.VK_V),
				new MidiKeyTrigger(40, KeyEvent.VK_A),
				new MidiKeyTrigger(54, KeyEvent.VK_B),
				new MidiKeyTrigger(65, KeyEvent.VK_L)
		};
	}

	public void drawApp() {
		background(0);

		for (MidiKeyTrigger trigger : midiKeyTriggers) {
			trigger.update();
		}
	}
	
	/**
	 * PApplet-level listener for MIDIBUS noteOn call
	 */
	public void noteOn(int channel, int  pitch, int velocity) {
		P.println(channel, pitch, velocity);
		for (MidiKeyTrigger trigger : midiKeyTriggers) {
			trigger.checkNote(pitch);
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

	
	public class MidiKeyTrigger {
		
		public int midiNote;
		public int keyEvent;
		protected boolean _keyOn = false;

		public MidiKeyTrigger(int midiNote, int keyEvent) {
			this.midiNote = midiNote;
			this.keyEvent = keyEvent;
		}
		
		public void checkNote(int newNote) {
			if(newNote == midiNote) {
				_robot.keyPress(keyEvent);
				_keyOn = true;
			}
		}
		
		public void update() {
			if(_keyOn == true) _robot.keyRelease(keyEvent);
			_keyOn = false;
		}
	}
	
}
