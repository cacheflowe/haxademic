package com.haxademic.core.render;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.analysis.input.AudioInputESS;
import com.haxademic.core.audio.analysis.input.AudioStreamData;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.text.StringFormatter;

import krister.Ess.AudioChannel;
import processing.core.PApplet;
import unlekker.moviemaker.UMovieMaker;

public class Renderer 
{
	/**
	 * Reference to the applet that's currently rendering
	 */
	protected PApplet p;
	
	/**
	 * Create Quicktime movies
	 */
	protected UMovieMaker _mm;
	
	/**
	 * Analyzes audio data via the AudioChannel
	 */
	protected AudioStreamData _audioData;
	protected AudioInputESS audioInput;
	protected float[] frequencies;
	protected float[] waveform;
	
	/**
	 * AudioChannel object for stepping through .wav data
	 */
	protected AudioChannel audioPlayer;
	
	/**
	 * Keeps track of how many frames have been rendered
	 */
	protected int _frameNumber = 0;
	
	/**
	 * String prefix for each file saved
	 */
	protected String _timestamp;
	
	protected String _outputDir;
	
	/**
	 * Flag for whether we're currently rendering
	 */
	protected Boolean _isRendering = false;
	
	/**
	 * FPS passed in from the PApplet
	 */
	protected float fps;
	
	/**
	 * Current type of renderer being used
	 */
	public int _outputType;
	
	/**
	 * Outputs jpg images
	 */
	public static final int OUTPUT_TYPE_IMAGE = 0;
	
	/**
	 * Outputs .mov movies
	 */
	public static final int OUTPUT_TYPE_MOVIE = 1;

	/**
	 * Time render was started
	 */
	public int _timeStarted;

	/**
	 * Creates a renderer to save images or movies of your work
	 * @param p5
	 * @param framesPerSecond
	 * @param outputType
	 */
	public Renderer( PApplet p5, int framesPerSecond, int outputType, String outputDir )
	{
		// store ref to PApplet
		p = p5;
		
		// store frames per second, to match the PApplet
		fps = framesPerSecond;
		
		// store output type - image or movie
		_outputType = outputType;
		
		// store render directory
		_outputDir = outputDir;
	}
	
	/**
	 * Starts the movie cap
	 */
	public void startRenderer() 
	{
		// create timestamp for file output
		_timestamp = SystemUtil.getTimestamp( p );

		// initialize movie renderer
		if( _outputType == OUTPUT_TYPE_MOVIE ) {
			if( FileUtil.fileOrPathExists(_outputDir) == false ) FileUtil.createDir(_outputDir);
			_mm = new UMovieMaker( p, _outputDir+"render-"+_timestamp+".mov", p.width, p.height, (int)fps );
			P.println("new UMovieMaker created :: "+_timestamp);
		}
		
		// set rendering flag
		_isRendering = true;
		_timeStarted = p.millis();
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
		startRenderer();
		
		// load & play audio
		audioPlayer = new AudioChannel( p.dataPath( audioFile ) );
		audioPlayer.play();
		audioPlayer.pause();
//		audioPlayer.volume( 0 );
	}

	/**
	 * Called by the PApplet to render the current frame.
	 * If using audio, make sure to call analyzeAudio() at the beginning of draw(), and renderFrame() at the end draw();
	 */
	public void renderFrame() {
		// don't do anything if renderer hasn't been inited
		if( _isRendering == true ) {
			// print a message every 100 frames
			if ((_frameNumber % 10) == 0) {
				P.println( "=============================" );
				P.println( "= Working on frame number " + _frameNumber );
				P.println( "=============================" );
			}
			
			// if movie, add frame to MovieMaker file
			if ( _outputType == OUTPUT_TYPE_MOVIE ) {
				p.loadPixels();
				_mm.addFrame();
				
			// otherwise, save an image
			} else {
				p.saveFrame( "output/"+_timestamp+"/img_" + P.nf( _frameNumber, 8 ) + ".jpg" );
			}
			
			// keep track of rendered frame count
			_frameNumber++;
		}
	}
		
	/**
	 * Called at the beginning of PApplet.draw() to prepare the audio data 
	 */
	public void analyzeAudio() {
		try{
			// get position in wav file
			if( audioPlayer != null ) {
				int pos = (int)( _frameNumber * audioPlayer.sampleRate / fps );
				// make sure we're still in bounds - kept getting data run-out errors
				if (pos < audioPlayer.samples.length - (audioPlayer.sampleRate * 0.01f)) { 
					float seconds = (float) _frameNumber / fps;
					audioPlayer.cue(pos);
					if ((_frameNumber % 15) == 0) {
						P.println( "=============================" );
						P.println( "= Audio position: " + pos + " fps: " + fps + " seconds: " + seconds + " _chn.sampleRate = " + audioPlayer.sampleRate + "  position in file: " + pos + " / " + audioPlayer.samples.length );
						P.println( "= Audio @ seconds: " + seconds + "  Progress: " + Math.round(100f*((float)pos/(float)audioPlayer.samples.length)) + "%" );
						P.println( "= Rendering time: " + StringFormatter.timeFromMilliseconds( p.millis() - _timeStarted, true ) );
						P.println( "= Frame Number: " + _frameNumber );
						P.println( "=============================" );
					}
			
					// if still running & in-bounds, grab next data within the ESS wrapper
					audioInput.updateForRender(audioPlayer, pos);

					// OLD METHOD:
					// _audioData.getFFT().getSpectrum( _channel.samples, pos );
				} else {
					if(_outputType == OUTPUT_TYPE_MOVIE) _mm.finish();
					stop();  
					p.exit();
					return;
				}
			}
		} catch (NullPointerException e) {
			DebugUtil.printErr( "## krister.Ess.AudioChannel read error caught" );
		}
		
	}
	
//	public AudioChannel getChannel () {
//		return audioPlayer;
//	}

	/**
	 * Stops rendering and shuts down MovieMaker and AudioInputWrapper objects
	 */
	public void stop() 
	{
		// shut down movie making pieces
		if( _outputType == OUTPUT_TYPE_MOVIE ) _mm.finish();
		_isRendering = false;
	}
	
}
