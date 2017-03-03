package com.haxademic.core.app;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.audio.AudioInputWrapperMinim;
import com.haxademic.core.audio.WaveformData;
import com.haxademic.core.debug.DebugText;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.debug.Stats;
import com.haxademic.core.draw.mesh.MeshPool;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.kinect.IKinectWrapper;
import com.haxademic.core.hardware.kinect.KinectWrapperV1;
import com.haxademic.core.hardware.kinect.KinectWrapperV2;
import com.haxademic.core.hardware.kinect.KinectWrapperV2Mac;
import com.haxademic.core.hardware.midi.MidiWrapper;
import com.haxademic.core.hardware.osc.OscWrapper;
import com.haxademic.core.hardware.webcam.WebCamWrapper;
import com.haxademic.core.render.GifRenderer;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.MIDISequenceRenderer;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.P5Properties;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.StringFormatter;

import de.voidplus.leapmotion.LeapMotion;
import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.video.Movie;
import themidibus.MidiBus;

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
//	Simplest launch:
//	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

//	Fancier launch:
//	public static void main(String args[]) {
//		PAppletHax.main(P.concat(args, new String[] { "--hide-stop", "--bgcolor=000000", Thread.currentThread().getStackTrace()[1].getClassName() }));
//		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "--location=1920,0", "--display=1", ElloMotion.class.getName() });
//	}

	/**
	 * Global/static ref to PApplet - any audio-reactive object should be passed this reference, or grabbed from this static ref.
	 */
	protected static PAppletHax p;

	/**
	 * Loads the project .properties file to configure several app properties externally.
	 */
	public P5Properties appConfig;

	/**
	 * Loads an app-specific project .properties file.
	 */
	protected String customPropsFile = null;

	/**
	 * Single instance and wrapper for the ESS audio object.
	 */
	public AudioInputWrapper _audioInput;
	public AudioInputWrapperMinim audioIn;

	/**
	 * Single instance of the data needed to draw a realtime waveform / oscilloscpe.
	 */
	public WaveformData _waveformData;
	public WaveformData _waveformDataMinim;

	/**
	 * Renderer object for saving frames and rendering movies & gifs.
	 */
	public Renderer _renderer;
	public MIDISequenceRenderer _midiRenderer;
	public GifRenderer _gifRenderer;

	/**
	 * Wraps up MIDI functionality with theMIDIbus library.
	 */
	public MidiWrapper midi = null;
	protected MidiBus midiBus;
	protected boolean _debugMidi = false;

	/**
	 * Loads and stores a pool of WETriangleMesh objects.
	 */
	public MeshPool meshPool = null;

	/**
	 * Wraps up Kinect functionality.
	 */
	public IKinectWrapper kinectWrapper = null;

	/**
	 * Wraps up Leap Motion functionality.
	 */
	public LeapMotion leapMotion = null;

	/**
	 * Wraps up incoming OSC commands with the oscP5 library.
	 */
	public OscWrapper _oscWrapper = null;

	/**
	 * Native Java object that simulates occasional keystrokes to prevent the system's screensaver from starting.
	 */
	protected Robot _robot;

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
	public String rendererMode;

	/**
	 * Joons renderer wrapper
	 */
	protected JoonsWrapper joons;

	/**
	 * Helps the Renderer object work with minimal reconfiguration. Maybe this should be moved at some point...
	 */
	protected Boolean _isRendering = true;
//	protected int _renderShutdown = -1;

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
		if(customPropsFile != null) DebugUtil.printErr("Make sure to load custom .properties files in settings()");
		p.rendererMode = p.g.getClass().getName();
		setAppletProps();
	}
	
	public void settings() {
		P.p = p = this;
		AppUtil.setFrameBackground(p,0,0,0);
		loadAppConfig();
		overridePropsFile();
		setRenderer();
		setSmoothing();
		setRetinaScreen();
	}
	
	protected void loadAppConfig() {
		p.appConfig = new P5Properties(p);
		appConfig = p.appConfig;
		if( customPropsFile != null ) p.appConfig.loadPropertiesFile( customPropsFile );
		customPropsFile = null;
	}
	
	protected void setRetinaScreen() {
		if(p.appConfig.getBoolean(AppSettings.RETINA, false) == true) {
			if(p.displayDensity() == 2) {
				pixelDensity(2);
			} else {
				DebugUtil.printErr("Error: Attempting to set retina drawing on a non-retina screen");
			}
		}	
	}
	
	protected void setSmoothing() {
		if(p.appConfig.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH) == 0) {
			p.noSmooth();
		} else {
			p.smooth(p.appConfig.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH));	
		}
	}
	
	protected void setRenderer() {
		String renderer = p.appConfig.getString(AppSettings.RENDERER, P.P3D);
		if(p.appConfig.getBoolean(AppSettings.FULLSCREEN, false) == true) {
			p.fullScreen(renderer, p.appConfig.getInt(AppSettings.DISPLAY, 1));
		} else if(p.appConfig.getBoolean(AppSettings.FILLS_SCREEN, false) == true) {
			p.size(displayWidth,displayHeight,renderer);
		} else {
			p.size(p.appConfig.getInt(AppSettings.WIDTH, 800),p.appConfig.getInt(AppSettings.HEIGHT, 600),renderer);
		}
	}

	protected void overridePropsFile() {
		if( customPropsFile == null ) P.println("YOU SHOULD OVERRIDE overridePropsFile()");
	}

	protected void drawApp() {
		P.println("YOU MUST OVERRIDE drawApp()");
	}

	public void handleInput( boolean isMidi ) {
//		p.println("YOU MUST OVERRIDE KEYPRESSED");
		if( isMidi == true ) {

		} else {
//			P.println("p.key = "+p.key);
			// audio gain
//			if ( p.key == '.' || _midi.midiPadIsOn( MidiWrapper.PAD_14 ) == 1 ) _audioInput.gainUp();
//			if ( p.key == ',' || _midi.midiPadIsOn( MidiWrapper.PAD_13 ) == 1 ) _audioInput.gainDown();
			if ( p.key == '.' && _audioInput != null ) _audioInput.gainUp();
			if ( p.key == ',' && _audioInput != null ) _audioInput.gainDown();
			if ( p.key == '.' && audioIn != null ) audioIn.gainUp();
			if ( p.key == ',' && audioIn != null ) audioIn.gainDown();
		}

	}

	/**
	 * Sets some initial Applet properties for OpenGL quality, FPS, and nocursor().
	 */
	protected void setAppletProps() {
		_isRendering = p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false);
		if( _isRendering == true ) DebugUtil.printErr("When rendering, make sure to call super.keyPressed(); for esc key shutdown");
		_isRenderingAudio = p.appConfig.getBoolean(AppSettings.RENDER_AUDIO, false);
		_isRenderingMidi = p.appConfig.getBoolean(AppSettings.RENDER_MIDI, false);
		_showStats = p.appConfig.getBoolean(AppSettings.SHOW_STATS, false);
		_fps = p.appConfig.getInt(AppSettings.FPS, 60);
		frameRate(_fps);
		if( p.appConfig.getBoolean(AppSettings.HIDE_CURSOR, false) == true ) p.noCursor();
	}

	/**
	 * Initializes app-wide support objects for hardware interaction and rendering purposes.
	 */
	protected void initHaxademicObjects() {
		// save single reference for other objects
		if( appConfig.getBoolean("init_ess_audio", true) == true ) {
			_audioInput = new AudioInputWrapper( p, _isRenderingAudio );
			_waveformData = new WaveformData( p, _audioInput.bufferSize() );
			if(appConfig.getBoolean(AppSettings.AUDIO_DEBUG, false) == true) _audioInput.debugInfo();
		}
		if( appConfig.getBoolean("init_minim_audio", true) == true ) {
			audioIn = new AudioInputWrapperMinim( p, _isRenderingAudio );
			_waveformDataMinim = new WaveformData( p, audioIn.bufferSize() );
		}
		_renderer = new Renderer( p, _fps, Renderer.OUTPUT_TYPE_MOVIE, p.appConfig.getString( "render_output_dir", FileUtil.getHaxademicOutputPath() ) );
		if(appConfig.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			_gifRenderer = new GifRenderer(appConfig.getInt(AppSettings.RENDERING_GIF_FRAMERATE, 45), appConfig.getInt(AppSettings.RENDERING_GIF_QUALITY, 15));
		}
		
		if( p.appConfig.getBoolean( AppSettings.KINECT_V2_WIN_ACTIVE, false ) == true ) {
			kinectWrapper = new KinectWrapperV2( p, p.appConfig.getBoolean( "kinect_depth", true ), p.appConfig.getBoolean( "kinect_rgb", true ), p.appConfig.getBoolean( "kinect_depth_image", true ) );
		} else if( p.appConfig.getBoolean( AppSettings.KINECT_V2_MAC_ACTIVE, false ) == true ) {
			kinectWrapper = new KinectWrapperV2Mac( p, p.appConfig.getBoolean( "kinect_depth", true ), p.appConfig.getBoolean( "kinect_rgb", true ), p.appConfig.getBoolean( "kinect_depth_image", true ) );
		} else if( p.appConfig.getBoolean( AppSettings.KINECT_ACTIVE, false ) == true ) {
			kinectWrapper = new KinectWrapperV1( p, p.appConfig.getBoolean( "kinect_depth", true ), p.appConfig.getBoolean( "kinect_rgb", true ), p.appConfig.getBoolean( "kinect_depth_image", true ) );
		}
		if(kinectWrapper != null) {
			kinectWrapper.setMirror( p.appConfig.getBoolean( "kinect_mirrored", true ) );
			kinectWrapper.setFlipped( p.appConfig.getBoolean( "kinect_flipped", false ) );
		}
		if( p.appConfig.getInt(AppSettings.MIDI_DEVICE_IN_INDEX, -1) >= 0 ) {
			MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.
			midiBus = new MidiBus(
					this, 
					p.appConfig.getInt(AppSettings.MIDI_DEVICE_IN_INDEX, 0), 
					p.appConfig.getInt(AppSettings.MIDI_DEVICE_OUT_INDEX, 0)
					);
			_debugMidi = p.appConfig.getBoolean(AppSettings.MIDI_DEBUG, false);
		}
		if( p.appConfig.getBoolean( "leap_active", false ) == true ) leapMotion = new LeapMotion(this);
		if( p.appConfig.getBoolean( "osc_active", false ) == true ) _oscWrapper = new OscWrapper( p );
		meshPool = new MeshPool( p );
		joons = ( p.appConfig.getBoolean(AppSettings.SUNFLOW, false ) == true ) ?
				new JoonsWrapper( p, width, height, ( p.appConfig.getString(AppSettings.SUNFLOW_QUALITY, "low" ) == AppSettings.SUNFLOW_QUALITY_HIGH ) ? JoonsWrapper.QUALITY_HIGH : JoonsWrapper.QUALITY_LOW, ( p.appConfig.getBoolean(AppSettings.SUNFLOW_ACTIVE, true ) == true ) ? true : false )
				: null;
		_debugText = new DebugText( p );
		if( _showStats == true ) _stats = new Stats( p );
		try { _robot = new Robot(); } catch( Exception error ) { println("couldn't init Robot for screensaver disabling"); }
	}

	protected void initializeOn1stFrame() {
		if( p.frameCount == 1 ) {
			P.println("Using Java version: "+SystemUtil.getJavaVersion());
			initHaxademicObjects();
			if( p.appConfig.getString("midi_device_in", "") != "" ) {
				midi = new MidiWrapper( p, p.appConfig.getString("midi_device_in", ""), p.appConfig.getString("midi_device_out", "") );
			}
		}
	}

	public void draw() {
		//if( keyPressed ) handleInput( false ); // handles overall keyboard commands
		killScreensaver();
		forceForeground();
		initializeOn1stFrame();	// wait until draw() happens, to avoid weird launch crash if midi signals were coming in as haxademic starts
		handleRenderingStepthrough();
		updateAudioData();
		if( kinectWrapper != null ) kinectWrapper.update();
		if( joons != null ) joons.startFrame();
		drawApp();
		if( joons != null ) joons.endFrame( p.appConfig.getBoolean(AppSettings.SUNFLOW_SAVE_IMAGES, false) == true );
		renderFrame();
		showStats();
		setAppDockIconAndTitle();
	}
	
	protected void updateAudioData() {
		if( _audioInput != null ) _audioInput.getBeatDetection(); // detect beats and pass through to current visual module	// 		int[] beatDetectArr =
		if( audioIn != null ) {
			audioIn.update(); // detect beats and pass through to current visual module	// 		int[] beatDetectArr =
			_waveformDataMinim.updateWaveformDataMinim( audioIn.getAudioInput() );
		}
	}

	protected void showStats() {
		if( _showStats == false ) return; 
		_stats.update();
		_debugText.draw(new String[]{
			"FPS: " + _stats.getFps() + " / " + p.frameRate,
			"Memory Allocated: " + StringFormatter.formattedInteger(DebugUtil.memoryAllocated()),
			"Memory Free: " + StringFormatter.formattedInteger(DebugUtil.memoryFree()),
			"Memory Max: " + StringFormatter.formattedInteger(DebugUtil.memoryMax()),
			"Frame: " + p.frameCount,
			"Time: " + StringFormatter.timeFromSeconds(p.millis() / 1000, true),
		});
	}

	protected void setAppDockIconAndTitle() {
		if(p.frameCount == 1) {
			AppUtil.setTitle(p, "Haxademic");
			AppUtil.setAppToDockIcon(p);
		}	
	}
	
	protected void handleRenderingStepthrough() {
		// step through midi file if set
		if( _isRenderingMidi == true ) {
			if( p.frameCount == 1 ) {
				try {
					_midiRenderer = new MIDISequenceRenderer(p);
					_midiRenderer.loadMIDIFile( p.appConfig.getString(AppSettings.RENDER_MIDI_FILE, ""), p.appConfig.getFloat(AppSettings.RENDER_MIDI_BPM, 150f), _fps, p.appConfig.getFloat(AppSettings.RENDER_MIDI_OFFSET, -8f) );
				} catch (InvalidMidiDataException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
		// analyze & init audio if stepping through a render
		if( _isRendering == true ) {
			if( p.frameCount == 1 ) {
				if( _isRenderingAudio == true ) {
					_renderer.startRendererForAudio( p.appConfig.getString(AppSettings.RENDER_AUDIO_FILE, ""), _audioInput );
					_audioInput.gainDown();
					_audioInput.gainDown();
					_audioInput.gainDown();
				} else {
					_renderer.startRenderer();
				}
			}

//			if( p.frameCount > 1 ) {
				// have renderer step through audio, then special call to update the single WaveformData storage object
				if( _isRenderingAudio == true ) {
					_renderer.analyzeAudio();
					_waveformData.updateWaveformDataForRender( _renderer, _audioInput.getAudioInput(), _audioInput.bufferSize() );
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
				if( triggered == false && midi != null ) midi.allOff();
			}
		}
		if(_gifRenderer != null && appConfig.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			if(appConfig.getInt(AppSettings.RENDERING_GIF_START_FRAME, 1) == p.frameCount) {
				_gifRenderer.startGifRender(this);
			}
		}
	}
	
	protected void renderFrame() {
		// gives the app 1 frame to shutdown after the movie rendering stops
		if( _isRendering == true ) {
			if(p.frameCount >= appConfig.getInt(AppSettings.RENDERING_MOVIE_START_FRAME, 1)) {
				_renderer.renderFrame();
			}
			// check for movie rendering stop frame
			if(p.frameCount == appConfig.getInt(AppSettings.RENDERING_MOVIE_STOP_FRAME, 5000)) {
				_renderer.stop();
				P.println("shutting down renderer");
			}
		}
		// check for gifrendering stop frame
		if(_gifRenderer != null && appConfig.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			if(appConfig.getInt(AppSettings.RENDERING_GIF_START_FRAME, 1) == p.frameCount) {
				_gifRenderer.startGifRender(this);
			}
			DrawUtil.setColorForPImage(p);
			_gifRenderer.renderGifFrame(p.g);
			if(appConfig.getInt(AppSettings.RENDERING_GIF_STOP_FRAME, 100) == p.frameCount) {
				_gifRenderer.finish();
			}
		}
	}
	
	protected void forceForeground(){
		if(p.focused == false) {
			if(p.appConfig.getBoolean(AppSettings.FORCE_FOREGROUND, false) == true) {
				AppUtil.requestForeground(p);
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
		if( p.key == P.ESC && ( p.appConfig.getBoolean(AppSettings.DISABLE_ESC_KEY, false) == true ) ) {   //  || p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false) == true )
			key = 0;
//			renderShutdownBeforeExit();
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
		if( kinectWrapper != null ) {
			kinectWrapper.stop();
			kinectWrapper = null;
		}
		if( leapMotion != null ) leapMotion.dispose();
		super.stop();
	}

	// PApplet-level listeners ------------------------------------------------
	/**
	 * PApplet-level listener for Movie frame update events
	 */
	public void movieEvent(Movie m) {
//		if(p.frameCount > 2) { // solves Processing 2.x video problem: http://forum.processing.org/two/discussion/5926/video-library-problem-in-processing-2-2-1
			m.read();
//		}
	}

	////////////////////////////////////////////////////
	// MIDIBUS LISTENERS
	////////////////////////////////////////////////////
	public void noteOn(int channel, int  pitch, int velocity) {
		if( midi != null ) { 
			if( midi.midiNoteIsOn( pitch ) == 0 ) {
				midi.noteOn( channel, pitch, velocity );
				try{ 
					handleInput( true );
				}
				catch( ArrayIndexOutOfBoundsException e ){println("noteOn BROKE!");}
			}
		}
		if(_debugMidi == true) P.println(channel, pitch, velocity);
	}
	
	public void noteOff(int channel, int  pitch, int velocity) {
		if( midi != null ) midi.noteOff( channel, pitch, velocity );
		if(_debugMidi == true) P.println(channel, pitch, velocity);
	}
	
	public void controllerChange(int channel, int number, int value) {
		if( midi != null ) midi.controllerChange( channel, number, value );
		if(_debugMidi == true) P.println(channel, number, value);
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
	 * PApplet-level listeners for LeapMotion events
	 */
	void leapOnInit(){
	    // println("Leap Motion Init");
	}
	void leapOnConnect(){
	    // println("Leap Motion Connect");
	}
	void leapOnFrame(){
	    // println("Leap Motion Frame");
	}
	void leapOnDisconnect(){
	    // println("Leap Motion Disconnect");
	}
	void leapOnExit(){
	    // println("Leap Motion Exit");
	}

	/**
	 * Getters / Setters
	 */
	// instance of audio wrapper -------------------------------------------------
	public AudioInputWrapper getAudio() { return _audioInput; }
	// get fps of app -------------------------------------------------
	public int getFps() { return _fps; }
	// get fps factor of app -------------------------------------------------
	public float getFpsFactor() { return 30f / _fps; }

}
