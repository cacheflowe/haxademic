package com.haxademic.core.render;

import krister.Ess.AudioChannel;
import processing.core.PApplet;
import unlekker.moviemaker.UMovieMaker;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.system.SystemUtil;

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
	protected AudioInputWrapper _audioData;
	
	/**
	 * AudioChannel object for stepping through .wav data
	 */
	protected AudioChannel _chn;
	
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
	protected int _framesPerSecond;
	
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
		_framesPerSecond = framesPerSecond;
		
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
			_mm = new UMovieMaker( p, _outputDir+"render-"+_timestamp+".mov", p.width, p.height, _framesPerSecond );
			P.println("new MovieMaker success :: "+_timestamp);
		}
		
		// set rendering flag
		_isRendering = true;
	}
	
	/**
	 * Stores an instance of the audio analyzer, and loads a .wav to render
	 * @param audioFile			reference to a .wav file
	 * @param audioWrapper		instance of the audio analyzer object
	 */
	public void startRendererForAudio( String audioFile, AudioInputWrapper audioWrapper ) 
	{
		// store audio analysis object
		P.println("Rendering with audio: "+audioFile);
		_audioData = audioWrapper;
		
		// fire it up
		startRenderer();
		
		// load & play audio
		_chn = new AudioChannel( p.dataPath( audioFile ) );
		_chn.play();
		_chn.volume( 0 );
	}

	/**
	 * Called by the PApplet to render the current frame.
	 * If using audio, make sure to call analyzeAudio() at the beginning of draw(), and renderFrame() at the end draw();
	 */
	public void renderFrame() 
	{
		// don't do anything if renderer hasn't been inited
		if( _isRendering == true ) {
			// print a message every 100 frames
			if ((_frameNumber%100) == 0) {
				P.println( "Working on frame number " + _frameNumber );
			}
			
			// if movie, add frame to MovieMaker file
			if ( _outputType == OUTPUT_TYPE_MOVIE ) {
				p.loadPixels();
				_mm.addFrame();
				
			// otherwise, save an image
			} else {
				p.saveFrame( "output/img_" + _timestamp+ P.nf( _frameNumber, 8 ) + ".jpg" );
			}
			
			// keep track of rendered frame count
			_frameNumber++;
		}
	}
		
	/**
	 * Called at the beginning of PApplet.draw() to prepare the audio data 
	 */
	public void analyzeAudio() {
//		

		// get position in wav file
		int pos = (int)( _frameNumber * _chn.sampleRate / _framesPerSecond );
		float seconds = _frameNumber / _framesPerSecond;
		_chn.cue(pos);
		if ((_frameNumber%100) == 0) {
//			p.println( "Audio position: " + pos + " fps: " + _framesPerSecond + " seconds: " + seconds + " _chn.sampleRate = " + _chn.sampleRate + "  position in file: " + pos + " / " + _chn.samples.length );
			P.println( "Audio seconds: " + seconds + "  Progress: " + Math.round(100f*((float)pos/(float)_chn.samples.length)) + "%" );
		}
		
		// make sure we're still in bounds - kept getting data run-out errors
		if (pos >= _chn.size - 4000) {
			if (_outputType==OUTPUT_TYPE_MOVIE)
				_mm.finish();
			stop();  
			p.exit();
			return;
		} else {
			// if still running & in-bounds, grab next data
			_audioData.getFFT().getSpectrum( _chn.samples, pos );
		}
		
	}
	
	public AudioChannel getChannel () {
		return _chn;
	}

	/**
	 * Stops rendering and shuts down MovieMaker and AudioInputWrapper objects
	 */
	public void stop() 
	{
		// shut down movie making pieces
		if( _outputType == OUTPUT_TYPE_MOVIE ) _mm.finish();
		if( _audioData != null ) _audioData.stop();
		_isRendering = false;
	}
	
}
