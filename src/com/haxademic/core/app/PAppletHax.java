package com.haxademic.core.app;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import krister.Ess.AudioInput;
import oscP5.OscMessage;
import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

import SimpleOpenNI.SimpleOpenNI;

import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.debug.DebugText;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.debug.Stats;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.hardware.kinect.KinectWrapper;
import com.haxademic.core.hardware.midi.MidiWrapper;
import com.haxademic.core.hardware.osc.OscWrapper;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.MIDISequenceRenderer;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.P5Properties;
import com.haxademic.core.system.SystemUtil;

import ddf.minim.Minim;

/**
 * PAppletHax is a starting point for interactive visuals, giving you a unified
 * environment for both realtime and rendering modes. It loads several Java
 * libraries and wraps them up to play nicely with each other, all within the
 * context of Haxademic.
 * 
 * @TODO: Add better Processing lights() situation
 * @TODO: Refactor MIDI input for easier switching between ableton & akai pad control
 * @TODO: Add MIDI debug flag in .properties
 * @TODO: Mesh traversal drawing
 * @TODO: Handle MIDI CC / Allow more than just note_on messages from PAppletHax base. should be able to respond to any midi data
 * @TODO: Implement new viz ideas from sketchbook
 * @TODO: Add SVG animation class
 * @TODO: Create PGraphics & PImage audio-reactive textures to apply to meshes across sketches/apps. See SphereTextureMap and abstracts some of the goodness. !!! - add current texture and iVizTextureDraw classes to VizCollection Module
 * 
 * @TODO: Use a static Haxademic.support( PApplet ) type static instance to let us gain access to the applet without passing it everywhere. Look at Geomerative & Toxiclibs to see how they did it.
 * @TODO: ^^^ General cleanup of PAppletHax references throughout codebase
 * @TODO: Make sure it's cool to post all the 3rd-party code within. potentially rewrite these bits
 * @TODO: Address garbage collection - a larger project would be to have dispose() methods in every class, and implement disposal across the project.
 * @TODO: Come up with a single solution to be an IVizModule or an extension of PAppletHax. 
 * @TODO: optimize the kinectMesh element - shit is slow
 * @TODO: MIDI signals from rendering and live should be abstracted as MIDI message objects?
 * @TODO: Mesh traversal drawing: more configurable. generative options - implement mesh drawing strategy pattern
 * @TODO: Finish converting old modules into new Elements: AudioTubes, Blobsheet, cacheRings outer rings, GridEQ w/lines, MaxCache outer rings, PlusRing, more spheres
 * @TODO: Create more abstract user/hardware input system that routes different inputs into certain types of commands.
 * @TODO: Fix stepping through audio for WaveformData - this was hacked for BNC video rendering but shouldn't have to play & cue() the audio to capture the data
 * @TODO: Don't initialize MIDI object if not defined in run.properties. Will need to prevent attempting to detect MIDI input on handleKeyboardInput() methods
 * @TODO: Figure out camera perspective stretching issues in MasterHax
 * @TODO: Improve launchpad visuals
 * @TODO: Add launchpad back in without a secondary AudioInputWrapper
 * @TODO: Improve color selecting - use test sketch to figure out how to deal with color-traversing
 * @TODO: New elements: trails, supershapes, GEARS, particles
 * @TODO: add foreground/background modes to elements that could use them. 
 * @TODO: Create good input system for building up MasterHax module over time & manage flow of Elements.
 * @TODO: create more complex uses of new Elements
 * @TODO: Refine existing elements
 * 
 * @author cacheflowe
 *
 */

public class PAppletHax
extends PApplet 
{
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;

	/**
	 * Global/static ref to PApplet - any audio-reactive object should be passed this reference, or grabbed from this static ref.
	 */
	protected static PAppletHax p;
	
	/**
	 * Loads the project .properties file to configure several app properties externally.
	 */
	protected P5Properties _appConfig;
	
	/**
	 * Loads an app-specific project .properties file.
	 */
	protected String _customPropsFile = null;
	
	/**
	 * Single instance of Toxiclibs object
	 */
	public ToxiclibsSupport toxi;
	
	/**
	 * Single instance and wrapper for the ESS audio object.
	 */
	public AudioInputWrapper _audioInput;
	
	/**
	 * Single instance of the data needed to draw a realtime waveform / oscilloscpe.
	 */
	public WaveformData _waveformData;
	
	/**
	 * Renderer object for saving frames and rendering movies.
	 */
	public Renderer _renderer;
	public MIDISequenceRenderer _midiRenderer;
	
	/**
	 * Wraps up MIDI functionality with theMIDIbus library.
	 */
	public MidiWrapper _midi = null;
	
	/**
	 * Loads and stores a pool of WETriangleMesh objects.
	 */
	public MeshPool meshPool = null;
	
	/**
	 * Wraps up Kinect functionality.
	 */
	public KinectWrapper kinectWrapper = null;
	
	/**
	 * A secondary system of running the visuals on the Launchpad. This should probably be integrated into Modules?
	 */
//	public LaunchpadViz _launchpadViz = null;
	
	/**
	 * Wraps up incoming OSC commands with the oscP5 library.
	 */
	public OscWrapper _oscWrapper = null;
	
	/**
	 * Native Java object that simulates occasional keystrokes to prevent the system's screensaver from starting.
	 */
	protected Robot _robot;
	
	/**
	 * Single instance for minim audio library.
	 */
	public Minim _minim;
	
	/**
	 * Prevents crashing from possible attempts to re-initialize. 
	 * Similar error described here: http://code.google.com/p/processing/issues/detail?id=356
	 */
	protected Boolean _is_setup = false;
	
	/**
	 * Override this in a subclass of PAppletHax in main() if you want to remove the window chrome. Must be published as an Application, not an Applet.
	 */
	protected static Boolean _hasChrome = true;
	
	/**
	 * Override this in a subclass of PAppletHax in main() if you want to go full screen. Must be published as an Application, not an Applet.
	 */
	protected static Boolean _isFullScreen = false;
	
	/**
	 * Executable's target frames per second. 
	 * This value is set in .properties file.
	 */
	public int _fps;
	
	/**
	 * Stats debug class
	 */
	public Stats _stats;
	
	/**
	 * Flag for showing stats
	 */
	public boolean _showStats;
	
	/**
	 * Text for showing stats
	 */
	public DebugText _debugText;
	
	
	/**
	 * Graphical render mode
	 */
	public String _graphicsMode;

	/**
	 * Joons renderer wrapper
	 */
	protected JoonsWrapper _jw;
	
	/**
	 * Helps the Renderer object work with minimal reconfiguration. Maybe this should be moved at some point... 
	 */
	protected Boolean _isRendering = true;
	protected int _renderShutdown = -1;
	
	/**
	 * Helps the Renderer object work without trying to read an audio file
	 */
	protected Boolean _isRenderingAudio = true;
	
	/**
	 * Helps the Renderer object work without trying to read a MIDI file
	 */
	protected Boolean _isRenderingMidi = true;
	
	// OVERRIDE THE FOLLOWING METHODS 
	/**
	 * Called by PApplet to run before the first draw() command.
	 */
	public void setup () {
		P.p = p = this;
		if ( !_is_setup ) { 
			// load external properties and set flag
			_appConfig = new P5Properties(p);
			if( _customPropsFile != null ) 
				_appConfig.loadPropertiesFile( _customPropsFile );
			overridePropsFile();
			_is_setup = true;
			// set screen size and renderer
			String renderer = P.OPENGL;
			if( _appConfig.getBoolean("fills_screen", false) == true || _appConfig.getBoolean("fullscreen", false) == true ) {
				p.size(displayWidth,displayHeight,renderer);
			} else {
				p.size(_appConfig.getInt("width", 800),_appConfig.getInt("height", 600),renderer);
			}
		}
		_graphicsMode = p.g.getClass().getName();
		if( frame != null ) frame.setBackground(new java.awt.Color(0,0,0));
		setAppletProps();
		initHaxademicObjects();
	}
	
	protected void overridePropsFile() {
		if( _customPropsFile == null ) P.println("YOU SHOULD OVERRIDE overridePropsFile()");
	}
	
	protected void drawApp() {
		P.println("YOU MUST OVERRIDE drawApp()");
	}
	
	public boolean sketchFullScreen() {
		return _isFullScreen;
	}
	
	protected void handleInput( boolean isMidi ) {
//		p.println("YOU MUST OVERRIDE KEYPRESSED");
		if( isMidi == true ) {
			
		} else {
//			P.println("p.key = "+p.key);
			// audio gain
//			if ( p.key == '.' || _midi.midiPadIsOn( MidiWrapper.PAD_14 ) == 1 ) _audioInput.gainUp(); 
//			if ( p.key == ',' || _midi.midiPadIsOn( MidiWrapper.PAD_13 ) == 1 ) _audioInput.gainDown(); 
			if ( p.key == '.' ) _audioInput.gainUp(); 
			if ( p.key == ',' ) _audioInput.gainDown(); 
		}

	}
	
	/**
	 * Sets some initial Applet properties for OpenGL quality, FPS, and nocursor().
	 */
	protected void setAppletProps() {
		_isRendering = _appConfig.getBoolean("rendering", false);
		if( _isRendering == true ) DebugUtil.printErr("When rendering, make sure to call super.keyPressed(); for esc key shutdown");
		_isRenderingAudio = _appConfig.getBoolean("render_audio", false);
		_isRenderingMidi = _appConfig.getBoolean("render_midi", false);
		_showStats = _appConfig.getBoolean("show_stats", false);
		if(_graphicsMode == P.OPENGL) {
			if( _isRendering == true ) {
				// prevents an error
				// hint(DISABLE_OPENGL_2X_SMOOTH);
			} else {
				// OpenGLUtil.setQuality(p, OpenGLUtil.HIGH);
			}
		}

		_fps = _appConfig.getInt("fps", 30);
		frameRate(_fps);
		if( _appConfig.getBoolean("hide_cursor", false) == true ) p.noCursor();
	}
	
	public void init() {
		// frame only exists on Java Applications, not Applets
		if( frame != null && _hasChrome == false ) {
			frame.removeNotify(); 
			frame.setUndecorated(true); 
			frame.addNotify(); 
		}
		super.init();
	}
	
	/**
	 * Initializes app-wide support objects for hardware interaction and rendering purposes.
	 */
	protected void initHaxademicObjects() {
		// save single reference for other objects
		toxi = new ToxiclibsSupport(p);
		_audioInput = new AudioInputWrapper( p, _isRenderingAudio );
		_waveformData = new WaveformData( p, _audioInput._bufferSize );
		_renderer = new Renderer( p, _fps, Renderer.OUTPUT_TYPE_MOVIE, _appConfig.getString( "render_output_dir", FileUtil.getHaxademicOutputPath() ) );
		if( _appConfig.getBoolean( "kinect_active", false ) == true ) {
			kinectWrapper = new KinectWrapper( p, _appConfig.getBoolean( "kinect_depth", true ), _appConfig.getBoolean( "kinect_rgb", true ), _appConfig.getBoolean( "kinect_depth_image", true ) );
			kinectWrapper.setMirror( _appConfig.getBoolean( "kinect_mirrored", true ) );
		}
//		_launchpadViz = new LaunchpadViz( p5 );
		_oscWrapper = new OscWrapper( p );
		_minim = new Minim( p );
		meshPool = new MeshPool( p );
		_jw = ( _appConfig.getBoolean("sunflow", true ) == true ) ? 
				new JoonsWrapper( p, width, height, ( _appConfig.getString("sunflow_quality", "high" ) == "high" ) ? JoonsWrapper.QUALITY_HIGH : JoonsWrapper.QUALITY_LOW, ( _appConfig.getBoolean("sunflow_active", true ) == true ) ? true : false ) 
				: null;
		_debugText = new DebugText( p );
		if( _showStats == true ) _stats = new Stats( p );
		try { _robot = new Robot(); } catch( Exception error ) { println("couldn't init Robot for screensaver disabling"); }
	}
	
	protected void initializeExtraObjectsOn1stFrame() {
		if( p.frameCount == 1 ){
			P.println("Using Java version: "+SystemUtil.getJavaVersion());
			if( _appConfig.getString("midi_device_in", "") != "" ) {
				_midi = new MidiWrapper( p, _appConfig.getString("midi_device_in", ""), _appConfig.getString("midi_device_out", "") );
			}
		}
	}
	
	public void draw() {
		//if( keyPressed ) handleInput( false ); // handles overall keyboard commands
		killScreensaver();
		initializeExtraObjectsOn1stFrame();	// wait until draw() happens, to avoid weird launch crash if midi signals were coming in as haxademic starts
		handleRenderingStepthrough();
		_audioInput.getBeatDetection(); // detect beats and pass through to current visual module	// 		int[] beatDetectArr = 
		if( kinectWrapper != null ) kinectWrapper.update();
		if( _jw != null ) _jw.startFrame();
		drawApp();
		if( _jw != null ) _jw.endFrame( _appConfig.getBoolean("sunflow_save_images", false) == true );
		if( _isRendering == true || _renderShutdown > 1 ) {
			if( _renderShutdown == -1 ) {
				_renderer.renderFrame();
			} else if( p.frameCount >= _renderShutdown + 1 ) {
				P.println("should exit!");
				p.exit();
			}
		}
		if( _showStats == true ) showStats();
	}
	
	protected void showStats() {
		_stats.update();
		_debugText.draw( "FPS: " + _fps + " :: ACTUAL FPS: " + _stats.getFps() );	// display some info
		if( p.frameCount % 60 == 0 ) {
			_stats.printStats();
			DebugUtil.showMemoryUsage();
		}
	}
	
	protected void handleRenderingStepthrough() {
		// analyze & init audio if stepping through a render
		if( _isRendering == true ) {
			if( p.frameCount == 1 ) {
				if( _isRenderingAudio == true ) {
					_renderer.startRendererForAudio( _appConfig.getString("render_audio_file", ""), _audioInput );
					_audioInput.gainDown();
					_audioInput.gainDown();
					_audioInput.gainDown();
				} else {
					_renderer.startRenderer();
				}
				if( _isRenderingMidi == true ) {
					try {
						_midiRenderer = new MIDISequenceRenderer(p);
						_midiRenderer.loadMIDIFile( _appConfig.getString("render_midi_file", ""), _appConfig.getFloat("render_midi_bpm", 150f), _fps, _appConfig.getFloat("render_midi_offset", -8f) ); 
					} catch (InvalidMidiDataException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
				}
			}
			
//			if( p.frameCount > 1 ) {
				// have renderer step through audio, then special call to update the single WaveformData storage object				
				if( _isRenderingAudio == true ) {
					_renderer.analyzeAudio();				
					_waveformData.updateWaveformDataForRender( _renderer, _audioInput.getAudioInput(), _audioInput._bufferSize );
				}
//			}
			
			if( _midiRenderer != null ) {
				boolean doneCheckingForMidi = false;
				boolean triggered = false;
				while( doneCheckingForMidi == false ) {
					int rendererNote = _midiRenderer.checkForCurrentFrameNoteEvents();
					if( rendererNote != -1 ) {
						noteOn( 0, rendererNote, 100 );
						triggered = true;
					} else {
						doneCheckingForMidi = true;
					}
				}
				if( triggered == false && _midi != null ) _midi.allOff();
			}
		}

	}
	
	protected void killScreensaver(){
		// keep screensaver off - hit shift every 1000 frames
		if( p.frameCount % 1000 == 0 ) _robot.keyRelease(KeyEvent.VK_SHIFT);
	}
	
	/**
	 * Called by PApplet as the keyboard input listener.
	 */
	public void keyPressed() {
		// disable esc key - subclass must call super.keyPressed()
		if( p.key == P.ESC && ( _appConfig.getBoolean("disable_esc", false) == true || _appConfig.getBoolean("rendering", false) == true ) ) {
			key = 0;
			if( _isRendering ) {
				_renderShutdown = p.frameCount;
				_renderer.stop();
				P.println("shutting down");
			}
		}

		handleInput( false );
	}

	/**
	 * Called by PApplet to shut down the Applet.
	 * We stop rendering if applicable, and clean up hardware connections that might barf if left open.
	 */
	public void stop() {
		WebCamWrapper.dispose();
//		if( _launchpadViz != null ) _launchpadViz.dispose();
		if( kinectWrapper != null ) kinectWrapper.stop();
		super.stop();
	}

	// PApplet-level listeners ------------------------------------------------
	/**
	 * PApplet-level listener for Movie frame update events
	 */
	public void movieEvent(Movie m) {
		m.read();
	}

	/**
	 * PApplet-level listener for MIDIBUS noteOn call
	 */
	public void noteOn(int channel, int  pitch, int velocity) {
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
		if( _midi != null ) _midi.noteOff( channel, pitch, velocity );
	}
	
	/**
	 * PApplet-level listener for MIDIBUS CC signal
	 */
	public void controllerChange(int channel, int number, int value) {
		if( _midi != null ) _midi.controllerChange( channel, number, value );
	}

	/**
	 * PApplet-level listener for AudioInput data from the ESS library
	 */
	public void audioInputData(AudioInput theInput) {
		_audioInput.getFFT().getSpectrum(theInput);
//		if( _launchpadViz != null ) _launchpadViz.getAudio().getFFT().getSpectrum(theInput);
		_audioInput.detector.detect(theInput);
		_waveformData.updateWaveformData( theInput, _audioInput._bufferSize );
	}
	
	/**
	 * PApplet-level listener for OSC data from the oscP5 library
	 */
	public void oscEvent(OscMessage theOscMessage) {
		float oscValue = theOscMessage.get(0).floatValue();
		String oscMsg = theOscMessage.addrPattern();
		PAppletHax.println(oscMsg+": "+oscValue);
		_oscWrapper.setOscMapItem(oscMsg, oscValue);

		try { 
			if( oscValue > 0 ) {
				handleInput( true );
			}
		}
		catch( ArrayIndexOutOfBoundsException e ){println("noteOn BROKE!");}
	}

	/**
	 * PApplet-level listeners for SimpleOpenNI user events
	 */
	public void onNewUser(SimpleOpenNI curContext,int userId)
	{
	  println("onNewUser - userId: " + userId);
	  println("\tstart tracking skeleton");
	  p.kinectWrapper.openni().startTrackingSkeleton(userId);
	}

	public void onLostUser( SimpleOpenNI curContext, int userId ) {
	  println("onLostUser - userId: " + userId);
	}

	public void onVisibleUser( SimpleOpenNI curContext, int userId ) {
	  // println("onVisibleUser - userId: " + userId);
	}

	/**
	 * Getters / Setters
	 */
	// instance of this -------------------------------------------------
	public static PAppletHax getInstance(){ return p; }
	// instance of toxiclibs -------------------------------------------------
	public ToxiclibsSupport getToxi(){ return toxi; }
	// instance of audio wrapper -------------------------------------------------
	public AudioInputWrapper getAudio() { return _audioInput; }
	// instance of midi wrapper -------------------------------------------------
	public MidiWrapper getMidi() { return _midi; }
	// instance of osc wrapper -------------------------------------------------
	public OscWrapper getOsc() { return _oscWrapper; }
	// instance of osc wrapper -------------------------------------------------
	public Minim getMinim() { return _minim; }
	// get fps of app -------------------------------------------------
	public int getFps() { return _fps; }
	// get fps factor of app -------------------------------------------------
	public float getFpsFactor() { return 30f / _fps; }
	// get autopilot boolean -------------------------------------------------
//	public Boolean getIsAutopilot() { return _isAutoPilot; }

}
