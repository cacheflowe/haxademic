package com.haxademic.sketch.system;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class RobotKeyPressMidi
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
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
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}

	public void setupFirstFrame() {
	
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
		if( midiState != null ) { 
//			if( midi.isMidiButtonOn( pitch ) == 0 ) {
//				midi.noteOn( channel, pitch, velocity );
//				try{ 
//					handleInput( true );
//				}
//				catch( ArrayIndexOutOfBoundsException e ){println("noteOn BROKE!");}
//			}
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
