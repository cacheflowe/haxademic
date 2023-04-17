package com.haxademic.core.render;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class Renderer
implements IAppStoreListener {

	public VideoRenderer videoRenderer;
	public ImageSequenceRenderer imageSequenceRenderer;
	public MIDISequenceRenderer midiRenderer;
	public GifRenderer gifRenderer;
	public Boolean isRendering = true;
	protected Boolean renderingAudio = false;
	protected Boolean renderingMidi = true;
	public JoonsWrapper joons;
	
	// Singleton instance
	
	public static Renderer instance;
	
	public static Renderer instance() {
		if(instance != null) return instance;
		instance = new Renderer();
		return instance;
	}
	
	// Constructor

	public Renderer() {
		P.store.addListener(this);
		setRenderingProps();
		initRendering();
		P.p.registerMethod(PRegisterableMethods.dispose, this);	// when app is manually killed, make sure the render finishes saving the video
	}
	
	protected void setRenderingProps() {
		isRendering = Config.getBoolean(AppSettings.RENDERING_MOVIE, false);
		if( isRendering == true ) DebugUtil.printErr("When rendering, make sure to call super.keyPressed(); for esc key shutdown");
		renderingAudio = Config.getString(AppSettings.RENDER_AUDIO_FILE, "").length() > 0;
		renderingMidi = Config.getString(AppSettings.RENDER_MIDI_FILE, "").length() > 0;
	}

	protected void initRendering() {
		videoRenderer = new VideoRenderer( Config.getInt(AppSettings.FPS, 60), VideoRenderer.OUTPUT_TYPE_MOVIE, Config.getString( "render_output_dir", FileUtil.haxademicOutputPath() ) );
		if(Config.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			gifRenderer = new GifRenderer(Config.getInt(AppSettings.RENDERING_GIF_FRAMERATE, 45), Config.getInt(AppSettings.RENDERING_GIF_QUALITY, 15));
		}
		if(Config.getBoolean(AppSettings.RENDERING_IMAGE_SEQUENCE, false) == true) {
			imageSequenceRenderer = new ImageSequenceRenderer(P.p.g);
		}
		joons = ( Config.getBoolean(AppSettings.SUNFLOW, false ) == true ) ?
				new JoonsWrapper( P.p, P.p.width, P.p.height, ( Config.getString(AppSettings.SUNFLOW_QUALITY, "low" ) == AppSettings.SUNFLOW_QUALITY_HIGH ) ? JoonsWrapper.QUALITY_HIGH : JoonsWrapper.QUALITY_LOW, ( Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, true ) == true ) ? true : false )
				: null;
	}
	
	public void setPG(PGraphics pg) {
		if(videoRenderer != null) videoRenderer.setPG(pg);
		if(imageSequenceRenderer != null) imageSequenceRenderer.setPG(pg);
	}
	
	protected void handleRenderingStepthrough() {
		// step through midi file if set
		if( renderingMidi == true ) {
			if( FrameLoop.count() == 1 ) {
				try {
					midiRenderer = new MIDISequenceRenderer(P.p);
					midiRenderer.loadMIDIFile( Config.getString(AppSettings.RENDER_MIDI_FILE, ""), Config.getFloat(AppSettings.RENDER_MIDI_BPM, 150f), Config.getInt(AppSettings.FPS, 60), Config.getFloat(AppSettings.RENDER_MIDI_OFFSET, -8f) );
				} catch (InvalidMidiDataException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
		// analyze & init audio if stepping through a render
		if( isRendering == true ) {
			if( FrameLoop.count() == 1 ) {
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
			if(Config.getInt(AppSettings.RENDERING_GIF_START_FRAME, 1) == FrameLoop.count()) {
				gifRenderer.startGifRender(P.p);
			}
		}
		if(imageSequenceRenderer != null && Config.getBoolean(AppSettings.RENDERING_IMAGE_SEQUENCE, false) == true) {
			if(Config.getInt(AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1) == FrameLoop.count()) {
				imageSequenceRenderer.startImageSequenceRender();
			}
		}
	}
	
	protected void renderFrame() {
		// gives the app 1 frame to shutdown after the movie rendering stops
		if( isRendering == true ) {
			if(FrameLoop.count() >= Config.getInt(AppSettings.RENDERING_MOVIE_START_FRAME, 1)) {
				videoRenderer.renderFrame();
			}
			// check for movie rendering stop frame
			if(FrameLoop.count() == Config.getInt(AppSettings.RENDERING_MOVIE_STOP_FRAME, 5000)) {
				videoRenderer.stop();
				P.println("shutting down renderer");
			}
		}
		// check for gif rendering stop frame
		if(gifRenderer != null && Config.getBoolean(AppSettings.RENDERING_GIF, false) == true) {
			if(Config.getInt(AppSettings.RENDERING_GIF_START_FRAME, 1) == FrameLoop.count()) {
				gifRenderer.startGifRender(P.p);
			}
			PG.setColorForPImage(P.p);
			gifRenderer.renderGifFrame(P.p.g);
			if(Config.getInt(AppSettings.RENDERING_GIF_STOP_FRAME, 100) == FrameLoop.count()) {
				gifRenderer.finish();
			}
		}
		// check for image sequence stop frame
		if(imageSequenceRenderer != null && Config.getBoolean(AppSettings.RENDERING_IMAGE_SEQUENCE, false) == true) {
			if(FrameLoop.count() >= Config.getInt(AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1)) {
				imageSequenceRenderer.renderImageFrame();
			}
			if(FrameLoop.count() == Config.getInt(AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 500)) {
				imageSequenceRenderer.finish();
			}
		}
	}
	
	public void dispose() {
		if(isRendering) videoRenderer.stop();
	}
	
	///////////////////////////
	// save in-app buffers
	///////////////////////////

	public static String saveBufferToDisk( PGraphics pg ) {
		return saveBufferToDisk(pg, FileUtil.screenshotsPath());
	}
	
	public static String saveBufferToDisk( PGraphics pg, String outputDir ) {
		if( FileUtil.fileOrPathExists(outputDir) == false ) FileUtil.createDir(outputDir);
		String filename = outputDir + SystemUtil.getTimestampFine() + ".png";
		pg.save(filename);
		return filename;
	}
	
	public static String saveDemoScreenshot( PGraphics pg, String className ) {
		String outputDir = FileUtil.demoScreenshotsPath();
		if( FileUtil.fileOrPathExists(outputDir) == false ) FileUtil.createDir(outputDir);

		String outputFileName = className.concat(".png");
		pg.save(outputDir.concat(outputFileName));
		
		return outputFileName;
	}
	

	////////////////////////////
	// IAppStore listeners
	////////////////////////////
	
	public void updatedNumber(String key, Number val) {
		if(key.equals(PEvents.DRAW_PRE)) {
			handleRenderingStepthrough();
			if( joons != null ) joons.startFrame();
		} else if(key.equals(PEvents.DRAW_POST)) {
			if( joons != null ) {
				joons.endFrame( Config.getBoolean(AppSettings.SUNFLOW_SAVE_IMAGES, false) == true );
				// TODO: unset Joons context  
//				P.p.camera();
//				P.p.scale(0.72f);
//				P.p.translate(P.p.width/4, P.p.height/4);
			}
			renderFrame();
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
}
