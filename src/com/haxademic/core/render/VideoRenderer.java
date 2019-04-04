package com.haxademic.core.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.audio.analysis.input.AudioInputESS;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.StringFormatter;

import krister.Ess.AudioChannel;
import unlekker.moviemaker.UMovieMaker;

public class VideoRenderer {
	
	protected UMovieMaker _mm;
	protected AudioInputESS audioInput;
	protected AudioChannel audioPlayer;
	protected int curFrame = 0;
	protected int audioPos = 0;
	protected float audioCurSeconds;
	protected String _timestamp;
	protected String _outputDir;
	protected Boolean isRendering = false;
	protected Boolean audioSimulation = false;
	protected float fps;
	public int _outputType;
	public static final int OUTPUT_TYPE_IMAGE = 0;
	public static final int OUTPUT_TYPE_MOVIE = 1;
	public int timeStarted;

	public VideoRenderer(int framesPerSecond, int outputType, String outputDir ) {
		fps = framesPerSecond;
		_outputType = outputType;
		_outputDir = outputDir;
		audioSimulation = P.p.appConfig.getBoolean( AppSettings.RENDER_AUDIO_SIMULATION, false );
	}
	
	/**
	 * Starts the movie cap
	 */
	public void startVideoRenderer() {
		// bail if simulating
		if(audioSimulation) return;
		
		// create timestamp for file output
		_timestamp = SystemUtil.getTimestamp( P.p );

		// initialize movie renderer
		if( _outputType == OUTPUT_TYPE_MOVIE ) {
			if( FileUtil.fileOrPathExists(_outputDir) == false ) FileUtil.createDir(_outputDir);
			_mm = new UMovieMaker( P.p, _outputDir+"render-"+_timestamp+".mov", P.p.width, P.p.height, (int)fps );
			P.println("new UMovieMaker created :: "+_timestamp);
		}
		
		// set rendering flag
		isRendering = true;
		timeStarted = P.p.millis();
	}
	
	/**
	 * Stores an instance of the audio analyzer, and loads a .wav to render
	 * @param audioFile			reference to a .wav file
	 * @param audioWrapper		instance of the audio analyzer object
	 */
	public void startRendererForAudio( String audioFile ) {
		// grab the system ESS audio input object
		audioInput = (AudioInputESS) P.p.audioInput;
		
		// store audio analysis object
		P.println("Rendering with audio: "+audioFile);
		
		// fire it up
		startVideoRenderer();
		
		// load & play audio
		audioPlayer = new AudioChannel( P.p.dataPath( audioFile ) );
		audioPlayer.play();
		if(audioSimulation == false) {
			audioPlayer.pause();
		} else {
			audioPlayer.loop();
		}
	}

	/**
	 * Called by the PApplet to render the current frame.
	 * If using audio, make sure to call analyzeAudio() at the beginning of draw(), and renderFrame() at the end draw();
	 */
	public void renderFrame() {
		// don't do anything if renderer hasn't been inited, or has been stopped
		if( isRendering == true ) {			
			// if movie, add frame to MovieMaker file
			if ( _outputType == OUTPUT_TYPE_MOVIE ) {
				P.p.loadPixels();
				_mm.addFrame();
				
			// otherwise, save an image
			} else {
				P.p.saveFrame( "output/"+_timestamp+"/img_" + P.nf( curFrame, 8 ) + ".jpg" );
			}
			
			// keep track of rendered frame count
			curFrame++;
			
			// debug output
			debugRenderProgress();
		}
	}
		
	/**
	 * Called at the beginning of PApplet.draw() to prepare the audio data 
	 */
	public void analyzeAudio() {
		if(audioSimulation) {
			// restart realtime audio on loop, if there's a loop
			if(P.p.loop != null) {
				if(P.p.loop.loopCurFrame() == 0) {
					audioPlayer.cue(0);
					audioPlayer.loop();
				}
			}
			// pass through audio player directly to audio input data
			audioInput.updateForRender(audioPlayer, -1);
		} else {
			// get position in wav file
			audioPos = (int)( curFrame * audioPlayer.sampleRate / fps );
			// make sure we're still in bounds - kept getting data run-out errors
			if (audioPos < audioPlayer.size) {	//  - (audioPlayer.sampleRate * 0.01f) 
				audioCurSeconds = (float) curFrame / fps;
				audioPlayer.cue(audioPos);			
				// if still running & in-bounds, grab next data within the ESS wrapper
				audioInput.updateForRender(audioPlayer, audioPos);
			} else {
				if(_outputType == OUTPUT_TYPE_MOVIE) _mm.finish();
				stop();  
				P.p.exit();
				return;
			}
		}
	}
	
	protected void debugRenderProgress() {
		P.println( "= RENDERING ==================" );
		P.println( "= Exporting frame number " + curFrame );
		P.println( "= Elapsed time "  + StringFormatter.timeFromMilliseconds( P.p.millis() - timeStarted, true));
		if(audioPlayer != null) {
			if ((curFrame % 15) == 0) {
				P.println( "= Audio position: " + audioPos + " fps: " + fps + " seconds: " + audioCurSeconds + " _chn.sampleRate = " + audioPlayer.sampleRate + "  position in file: " + audioPos + " / " + audioPlayer.size );
				P.println( "= Audio @ seconds: " + audioCurSeconds + "  Progress: " + Math.round(100f*((float)audioPos/(float)audioPlayer.size)) + "%" );
				P.println( "= Frame Number: " + curFrame );
			}
		}
		P.println( "============================" );
	}

	public void stop() {
		if( _outputType == OUTPUT_TYPE_MOVIE && audioSimulation == false ) _mm.finish();
		isRendering = false;
	}
	
}
