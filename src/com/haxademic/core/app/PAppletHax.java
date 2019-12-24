package com.haxademic.core.app;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputESS;
import com.haxademic.core.media.video.MovieBuffer;
import com.haxademic.core.render.GifRenderer;
import com.haxademic.core.render.ImageSequenceRenderer;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.MIDISequenceRenderer;
import com.haxademic.core.render.VideoRenderer;
import com.haxademic.core.system.AppUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UIButton;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PSurface;
import processing.opengl.PJOGL;
import processing.video.Movie;

public class PAppletHax
extends PApplet {
	//	Simplest launch:
	//	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	//	Fancier launch:
	//	public static void main(String args[]) {
	//		PAppletHax.main(P.concat(args, new String[] { "--hide-stop", "--bgcolor=000000", Thread.currentThread().getStackTrace()[1].getClassName() }));
	//		PApplet.main(new String[] { "--hide-stop", "--bgcolor=000000", "--location=1920,0", "--display=1", ElloMotion.class.getName() });
	//	}

	// app
	public static String arguments[] = null;	// Args passed in via main() launch command
	protected static PAppletHax p;				// Global/static ref to PApplet - any class can access reference from this static ref. Easier access via `P.p`
	public PGraphics pg;						// Offscreen buffer that matches the app size by default
	protected boolean alwaysOnTop = false;

	// rendering
	public VideoRenderer videoRenderer;
	public ImageSequenceRenderer imageSequenceRenderer;
	public MIDISequenceRenderer midiRenderer;
	public GifRenderer gifRenderer;
	protected Boolean isRendering = true;
	protected Boolean renderingAudio = false;
	protected Boolean renderingMidi = true;
	public JoonsWrapper joons;

	////////////////////////
	// INIT
	////////////////////////
	
	public void settings() {
		p = this;
		printArgs();
		P.init(this);
		config();
		buildAppWindow();
		setRenderingProps();
	}
	
	protected void printArgs() {
		if(arguments == null || arguments.length == 0) return;
		// print command line arguments
		P.out("=============");
		P.out("main() args:");
		for (String string : arguments) {
			P.out("# " + string);
		}
		P.out("=============");
	}
	
	public void setup() {
		P.appInitialized();
	}
	
	////////////////////////
	// INIT GRAPHICS
	////////////////////////
	
	protected void buildAppWindow() {
		// SELECT RENDERER AND WINDOW SIZE
		PJOGL.profile = 4;
		if(Config.getBoolean(AppSettings.SPAN_SCREENS, false) == true) {
			// run fullscreen across all screens
			p.fullScreen(P.renderer, P.SPAN);
		} else if(Config.getBoolean(AppSettings.FULLSCREEN, false) == true) {
			// run fullscreen - default to screen #1 unless another is specified
			if(Config.getInt(AppSettings.FULLSCREEN_SCREEN_NUMBER, 1) != 1) DebugUtil.printErr("AppSettings.FULLSCREEN_SCREEN_NUMBER is busted if not screen #1. Use AppSettings.SCREEN_X, etc.");
			p.fullScreen(P.renderer); // , Config.getInt(AppSettings.FULLSCREEN_SCREEN_NUMBER, 1)
		} else if(Config.getBoolean(AppSettings.FILLS_SCREEN, false) == true) {
			// fills the screen, but not fullscreen
			p.size(displayWidth,displayHeight,P.renderer);
		} else {
			if(P.renderer == PRenderers.PDF) {
				// set headless pdf output file
				p.size(Config.getInt(AppSettings.WIDTH, 800),Config.getInt(AppSettings.HEIGHT, 600), P.renderer, Config.getString(AppSettings.PDF_RENDERER_OUTPUT_FILE, "output/output.pdf"));
			} else {
				// run normal P3D renderer
				p.size(Config.getInt(AppSettings.WIDTH, 800),Config.getInt(AppSettings.HEIGHT, 600), P.renderer);
			}
		}
		
		// SMOOTHING
		if(Config.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH) == 0) {
			p.noSmooth();
		} else {
			p.smooth(Config.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH));	
		}

		// DO WE DARE TRY THE RETINA SETTING?
		if(Config.getBoolean(AppSettings.RETINA, false) == true) {
			if(p.displayDensity() == 2) {
				p.pixelDensity(2);
			} else {
				DebugUtil.printErr("Error: Attempting to set retina drawing on a non-retina screen");
			}
		}	
		
		// FRAMERATE
		int _fps = Config.getInt(AppSettings.FPS, 60);
		if(Config.getInt(AppSettings.FPS, 60) != 60) frameRate(_fps);
		
		// SET APP ICON
		String appIconFile = Config.getString(AppSettings.APP_ICON, "haxademic/images/haxademic-logo.png");
		String iconPath = FileUtil.getFile(appIconFile);
		if(FileUtil.fileExists(iconPath)) {
			PJOGL.setIcon(iconPath);
		}
	}
	
	protected void checkScreenManualPosition() {
		boolean isFullscreen = Config.getBoolean(AppSettings.FULLSCREEN, false);
		// check for additional screen_x params to manually place the window
		if(Config.getInt("screen_x", -1) != -1) {
			if(isFullscreen == false) {
				DebugUtil.printErr("Error: Manual screen positioning requires AppSettings.FULLSCREEN = true");
				return;
			}
			surface.setSize(Config.getInt(AppSettings.WIDTH, 800), Config.getInt(AppSettings.HEIGHT, 600));
			surface.setLocation(Config.getInt(AppSettings.SCREEN_X, 0), Config.getInt(AppSettings.SCREEN_Y, 0));  // location has to happen after size, to break it out of fullscreen
		}
	}

	////////////////////////
	// INIT OBJECTS
	////////////////////////
	
	protected void setRenderingProps() {
		isRendering = Config.getBoolean(AppSettings.RENDERING_MOVIE, false);
		if( isRendering == true ) DebugUtil.printErr("When rendering, make sure to call super.keyPressed(); for esc key shutdown");
		renderingAudio = Config.getString(AppSettings.RENDER_AUDIO_FILE, "").length() > 0;
		renderingMidi = Config.getString(AppSettings.RENDER_MIDI_FILE, "").length() > 0;
	}
	
	protected void initHaxademicObjects() {
		// create offscreen buffer
		if(P.isOpenGL()) pg = PG.newPG(Config.getInt(AppSettings.PG_WIDTH, p.width), Config.getInt(AppSettings.PG_HEIGHT, p.height));

		// rendering
		videoRenderer = new VideoRenderer( Config.getInt(AppSettings.FPS, 60), VideoRenderer.OUTPUT_TYPE_MOVIE, Config.getString( "render_output_dir", FileUtil.getHaxademicOutputPath() ) );
		if(Config.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			gifRenderer = new GifRenderer(Config.getInt(AppSettings.RENDERING_GIF_FRAMERATE, 45), Config.getInt(AppSettings.RENDERING_GIF_QUALITY, 15));
		}
		if(Config.getBoolean(AppSettings.RENDERING_IMAGE_SEQUENCE, false) == true) {
			imageSequenceRenderer = new ImageSequenceRenderer(p.g);
		}
		joons = ( Config.getBoolean(AppSettings.SUNFLOW, false ) == true ) ?
				new JoonsWrapper( p, width, height, ( Config.getString(AppSettings.SUNFLOW_QUALITY, "low" ) == AppSettings.SUNFLOW_QUALITY_HIGH ) ? JoonsWrapper.QUALITY_HIGH : JoonsWrapper.QUALITY_LOW, ( Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, true ) == true ) ? true : false )
				: null;
		
		// fullscreen
		boolean isFullscreen = Config.getBoolean(AppSettings.FULLSCREEN, false);
		if(isFullscreen == true) {
			alwaysOnTop = Config.getBoolean(AppSettings.ALWAYS_ON_TOP, false);
			if(alwaysOnTop) AppUtil.setAlwaysOnTop(p, true);
		}
	}
		
	protected void initializeOn1stFrame() {
		if( p.frameCount == 1 ) {
			if(P.isOpenGL()) P.println("Using Java version: " + SystemUtil.getJavaVersion() + " and GL version: " + OpenGLUtil.getGlVersion(p.g));
			initHaxademicObjects();
			firstFrame();
		}
		if(p.frameCount == 10) {
			// move screen after first frame is rendered. this prevents weird issues (i.e. the app not even starting)
			checkScreenManualPosition();
		}
	}
	
	////////////////////////
	// OVERRIDES
	////////////////////////

	protected void config() {
		// ovverride this to add hard-coded Config properties 
	}

	protected void firstFrame() {
		// Use setupFirstFrame() instead of setup() to avoid 5000ms Processing/Java timeout in setup()
	}

	protected void drawApp() {
		P.println("Haxademic: YOU MUST OVERRIDE drawApp()");
	}
	
	////////////////////////
	// GETTERS
	////////////////////////

	// app surface
	
	public PSurface getSurface() {
		return surface;
	}
	
	public boolean alwaysOnTop() {
		return alwaysOnTop;
	}
			
	////////////////////////
	// DRAW
	////////////////////////
	
	public void draw() {
		initializeOn1stFrame();
		handleRenderingStepthrough();
		p.pushMatrix();
		if( joons != null ) joons.startFrame();
		drawApp();
		if( joons != null ) joons.endFrame( Config.getBoolean(AppSettings.SUNFLOW_SAVE_IMAGES, false) == true );
		p.popMatrix();
		renderFrame();
		
		if(WebCam.instance != null && p.key == 'W') WebCam.instance().drawMenu(p.g);
		keepOnTop();
		setAppDockIconAndTitle();
		if(P.renderer == PRenderers.PDF) finishPdfRender();
	}
	
	////////////////////////
	// UPDATE OBJECTS
	////////////////////////	

	protected void keepOnTop() {
		if(alwaysOnTop == true) {
			if(p.frameCount % 600 == 0) AppUtil.requestForegroundSafe();
		}
	}
	
	protected void setAppDockIconAndTitle() {
		if(P.renderer != PRenderers.PDF) {
			if(p.frameCount == 1) {
				AppUtil.setTitle(p, Config.getString(AppSettings.APP_NAME, "Haxademic | " + this.getClass().getSimpleName()));
//				AppUtil.setAppToDockIcon(p);
			} else if(Config.getBoolean(AppSettings.SHOW_FPS_IN_TITLE, false)) {
				AppUtil.setTitle(p, Config.getString(AppSettings.APP_NAME, "Haxademic | " + this.getClass().getSimpleName()) + " | " + P.round(p.frameRate) + "fps");
			}
		}	
	}
	
	////////////////////////
	// RENDERING
	////////////////////////
	
	protected void finishPdfRender() {
		P.println("Finished PDF render.");
		p.exit();
	}
	
	protected void handleRenderingStepthrough() {
		// step through midi file if set
		if( renderingMidi == true ) {
			if( p.frameCount == 1 ) {
				try {
					midiRenderer = new MIDISequenceRenderer(p);
					midiRenderer.loadMIDIFile( Config.getString(AppSettings.RENDER_MIDI_FILE, ""), Config.getFloat(AppSettings.RENDER_MIDI_BPM, 150f), Config.getInt(AppSettings.FPS, 60), Config.getFloat(AppSettings.RENDER_MIDI_OFFSET, -8f) );
				} catch (InvalidMidiDataException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
		// analyze & init audio if stepping through a render
		if( isRendering == true ) {
			if( p.frameCount == 1 ) {
				if( renderingAudio == true ) {
					videoRenderer.startRendererForAudio( Config.getString(AppSettings.RENDER_AUDIO_FILE, "") );
				} else {
					videoRenderer.startVideoRenderer();
				}
			}

			// have renderer step through audio, then special call to update the single WaveformData storage object
			if( renderingAudio == true ) {
				videoRenderer.analyzeAudio();
			}

			if( midiRenderer != null ) {
				boolean doneCheckingForMidi = false;
				while( doneCheckingForMidi == false ) {
					int rendererNote = midiRenderer.checkForCurrentFrameNoteEvents();
					if( rendererNote != -1 ) {
						MidiState.instance().noteOn( 0, rendererNote, 100 );
					} else {
						doneCheckingForMidi = true;
					}
				}
			}
		}
		if(gifRenderer != null && Config.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			if(Config.getInt(AppSettings.RENDERING_GIF_START_FRAME, 1) == p.frameCount) {
				gifRenderer.startGifRender(this);
			}
		}
		if(imageSequenceRenderer != null && Config.getBoolean(AppSettings.RENDERING_IMAGE_SEQUENCE, false) == true) {
			if(Config.getInt(AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1) == p.frameCount) {
				imageSequenceRenderer.startImageSequenceRender();
			}
		}
	}
	
	protected void renderFrame() {
		// gives the app 1 frame to shutdown after the movie rendering stops
		if( isRendering == true ) {
			if(p.frameCount >= Config.getInt(AppSettings.RENDERING_MOVIE_START_FRAME, 1)) {
				videoRenderer.renderFrame();
			}
			// check for movie rendering stop frame
			if(p.frameCount == Config.getInt(AppSettings.RENDERING_MOVIE_STOP_FRAME, 5000)) {
				videoRenderer.stop();
				P.println("shutting down renderer");
			}
		}
		// check for gif rendering stop frame
		if(gifRenderer != null && Config.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			if(Config.getInt(AppSettings.RENDERING_GIF_START_FRAME, 1) == p.frameCount) {
				gifRenderer.startGifRender(this);
			}
			PG.setColorForPImage(p);
			gifRenderer.renderGifFrame(p.g);
			if(Config.getInt(AppSettings.RENDERING_GIF_STOP_FRAME, 100) == p.frameCount) {
				gifRenderer.finish();
			}
		}
		// check for image sequence stop frame
		if(imageSequenceRenderer != null && Config.getBoolean(AppSettings.RENDERING_IMAGE_SEQUENCE, false) == true) {
			if(p.frameCount >= Config.getInt(AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1)) {
				imageSequenceRenderer.renderImageFrame();
			}
			if(p.frameCount == Config.getInt(AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 500)) {
				imageSequenceRenderer.finish();
			}
		}
	}
	
	public void saveScreenshot(PGraphics savePG) {
		savePG.save(FileUtil.getHaxademicOutputPath() + "_screenshots/" + SystemUtil.getTimestamp() + ".png");
	}
	
	////////////////////////
	// INPUT
	////////////////////////
	
	public void keyPressed() {
		// disable esc key - subclass must call super.keyPressed()
		if( p.key == P.ESC && ( Config.getBoolean(AppSettings.DISABLE_ESC_KEY, false) == true ) ) {   //  || Config.getBoolean(AppSettings.RENDERING_MOVIE, false) == true )
			key = 0;
//			renderShutdownBeforeExit();
		}
		
		// special core app key commands
		if (p.key == 'F') {
			alwaysOnTop = !alwaysOnTop;
			AppUtil.setAlwaysOnTop(p, alwaysOnTop);
		}
		
		// show debug & prefs sliders
		if (p.key == '|') saveScreenshot(p.g);
		
		// let other objects know
		P.store.setString(PEvents.KEY_PRESSED, p.key+"");
	}
	
	////////////////////////
	// SHUTDOWN
	////////////////////////
	
	public void stop() {
		if(WebCam.instance != null) WebCam.instance().dispose();
		if(DepthCamera.instance != null) DepthCamera.instance().dispose();
		super.stop();
	}

	////////////////////////
	// PAPPLET LISTENERS
	////////////////////////
	
	// Movie playback
	public void movieEvent(Movie m) {
		m.read();
		MovieBuffer.moviesEventFrames.put(m, p.frameCount);
	}

	// ESS audio input
	public void audioInputData(AudioInput theInput) {
		if(AudioIn.audioInput instanceof AudioInputESS) {
			((AudioInputESS) AudioIn.audioInput).audioInputCallback(theInput);
		}
	}

	// UIControlPanel listeners

	public void uiButtonClicked(UIButton button) {
		P.out("uiButtonClicked: please override", button.id(), button.value());
	}

}
