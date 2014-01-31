package com.haxademic.app.haxvisual;

import java.util.ArrayList;

import processing.core.PApplet;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.modules.AmbientViz;
import com.haxademic.app.haxvisual.viz.modules.AudioTubes;
import com.haxademic.app.haxvisual.viz.modules.BlobSheet;
import com.haxademic.app.haxvisual.viz.modules.Boxen3D;
import com.haxademic.app.haxvisual.viz.modules.BrimLiski;
import com.haxademic.app.haxvisual.viz.modules.CacheRings;
import com.haxademic.app.haxvisual.viz.modules.GridAndLinesEQ;
import com.haxademic.app.haxvisual.viz.modules.HorizLines;
import com.haxademic.app.haxvisual.viz.modules.KaraokeViz;
import com.haxademic.app.haxvisual.viz.modules.MasterHax;
import com.haxademic.app.haxvisual.viz.modules.MaxCache;
import com.haxademic.app.haxvisual.viz.modules.PlusRing;
import com.haxademic.app.haxvisual.viz.modules.Spheres;
import com.haxademic.app.haxvisual.viz.modules.Toxi;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugText;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.hardware.midi.MidiWrapper;
import com.haxademic.core.image.ScreenUtil;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class HaxVisual
extends PAppletHax 
{
	/**
	 * HaxVisual allows the developer to create any number of "modules",
	 * with any number of "elements" that are controlled by each module.
	 * One module can run at a time, and a programs may be switched with MIDI or keyboard strokes.
	 */

	/**
	 * Auto-initialization of the main class.
	 * @param args
	 */
	static public void main(String args[]) {
		_isFullScreen = true;
		PApplet.main( "com.haxademic.app.haxvisual.HaxVisual", new String[] { "--hide-stop", "--bgcolor=000000" } );
	}

	/**
	 * The current IVizModule object that receives commands in the main Haxademic draw() loop.
	 */
	protected ArrayList<IVizModule> _modules;
	
	/**
	 * The current index for _modules, which is changed to switch programs.
	 */
	protected int _curModule = 0;
	
	/**
	 * Lets us use a sequence of pad presses to change programs via the Akai MPD.
	 * @TODO: refactor this.
	 */
	protected boolean _readyForProgramChange = false;
	
	/**
	 * Stores the next index for _modules, along with _readyForProgramChange.
	 * @TODO: refactor this.
	 */
	protected int _readyForProgramChangeInt = 0;

	/**
	 * Helps us draw text to the screen when needed.
	 */
	protected DebugText _debugText;
	
	/**
	 * Initializes all the IVizModules that we've created.
	 * @TODO: externalize this for different implementations - extend it like a VixCollection subclass?
	 */
	protected void initVizModules() {
		_modules = new ArrayList<IVizModule>();
		_modules.add( new MasterHax() );
		_modules.add( new AmbientViz() );
		_modules.add( new BrimLiski() );
		_modules.add( new KaraokeViz() );
		_modules.add( new Boxen3D() );
		_modules.add( new Toxi() );
		_modules.add( new Spheres() );
		_modules.add( new BlobSheet() );
		_modules.add( new GridAndLinesEQ() );
		_modules.add( new CacheRings() );
		_modules.add( new PlusRing() );
		_modules.add( new HorizLines() );
		_modules.add( new AudioTubes() );
		_modules.add( new MaxCache() );

		_modules.trimToSize();
		_modules.get( _curModule ).focus();
	}
	
	public void setup () {
		_customPropsFile = FileUtil.getHaxademicDataPath() + "properties/haxvisual.properties";
		super.setup();
		// p.smooth(OpenGLUtil.SMOOTH_LOW);
		p.noSmooth();
		initVizModules();
	}

	protected void handleInput( boolean isMidi ) {
		super.handleInput( isMidi );
		// int prevModule = _curModule;
		
		// change programs with midi pads
		if( _readyForProgramChange ) {
			if( _midi.midiPadIsOn( MidiWrapper.PAD_01 ) == 1 ) _readyForProgramChangeInt = 0;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_02 ) == 1 ) _readyForProgramChangeInt = 1;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_03 ) == 1 ) _readyForProgramChangeInt = 2;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_04 ) == 1 ) _readyForProgramChangeInt = 3;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_05 ) == 1 ) _readyForProgramChangeInt = 4;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_06 ) == 1 ) _readyForProgramChangeInt = 5;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_07 ) == 1 ) _readyForProgramChangeInt = 6;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_08 ) == 1 ) _readyForProgramChangeInt = 7;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_09 ) == 1 ) _readyForProgramChangeInt = 8;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_10 ) == 1 ) _readyForProgramChangeInt = 9;
			else if( _midi.midiPadIsOn( MidiWrapper.PAD_11 ) == 1 ) _readyForProgramChangeInt = 10;
//			else if( _midi.midiPadIsOn( MidiWrapper.PAD_12 ) == 1 ) _readyForProgramChangeInt = 11;
		} else if( _midi != null && _midi.midiPadIsOn( MidiWrapper.PAD_16 ) == 1 ) {
			_readyForProgramChange = true;
		}
		
		// handle midi loop program changes
		if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_01 ) == 1 ) _readyForProgramChangeInt = 0;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_02 ) == 1 ) _readyForProgramChangeInt = 1;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_03 ) == 1 ) _readyForProgramChangeInt = 2;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_04 ) == 1 ) _readyForProgramChangeInt = 3;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_05 ) == 1 ) _readyForProgramChangeInt = 4;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_06 ) == 1 ) _readyForProgramChangeInt = 5;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_07 ) == 1 ) _readyForProgramChangeInt = 6;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_08 ) == 1 ) _readyForProgramChangeInt = 7;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_09 ) == 1 ) _readyForProgramChangeInt = 8;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_10 ) == 1 ) _readyForProgramChangeInt = 9;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_11 ) == 1 ) _readyForProgramChangeInt = 10;
		else if( _midi.midiPadIsOn( MidiWrapper.PROGRAM_12 ) == 1 ) _readyForProgramChangeInt = 11;
		
		if( !isMidi ) {
			// change programs with keyboard
			if ( key == '!' ) _readyForProgramChangeInt = 0;
			if ( key == '@' ) _readyForProgramChangeInt = 1;
			if ( key == '#' ) _readyForProgramChangeInt = 2; 
			if ( key == '$' ) _readyForProgramChangeInt = 3;
			if ( key == '%' ) _readyForProgramChangeInt = 4;
			if ( key == '^' ) _readyForProgramChangeInt = 5;
			if ( key == '&' ) _readyForProgramChangeInt = 6;
			if ( key == '*' ) _readyForProgramChangeInt = 7; 
			if ( key == '(' ) _readyForProgramChangeInt = 8; 
			if ( key == ')' ) _readyForProgramChangeInt = 9; 
			if ( key == '_' ) _readyForProgramChangeInt = 10; 
			//if ( key == '+' ) _readyForProgramChangeInt = 11; 
			
			// big screenshot
			if ( key == '\\' ) { 
				ScreenUtil.screenshotHiRes( p, 3, P.P3D, FileUtil.getHaxademicOutputPath() + "saved_img/" );
			}
		}
		
		// pass input on to module
		_modules.get( _curModule ).handleKeyboardInput();
	}
	
	/**
	 * Called by PApplet as the main draw loop.
	 */
	public void drawApp() {
		
		// switch the program if we actually changed it
		if( _readyForProgramChangeInt != _curModule )
		{
			_readyForProgramChange = false;
			_curModule = _readyForProgramChangeInt;
			p.camera();
			p.background(0);
			_modules.get( _curModule ).focus(); 
		}
		
//		// detect beats and pass through to current visual module
		int[] beatDetectArr = _audioInput.getBeatDetection();
		_modules.get( _curModule ).beatDetect( beatDetectArr[0], beatDetectArr[1], beatDetectArr[2], beatDetectArr[3] );
		
		// update current visual module
		try{ _modules.get( _curModule ).update(); }
		catch( ArrayIndexOutOfBoundsException e ){println("draw() broke: ArrayIndexOutOfBoundsException");}
		
		// update launchpad hardware if it's around
//		if( _launchpadViz != null ) _launchpadViz.update();

	}

}
