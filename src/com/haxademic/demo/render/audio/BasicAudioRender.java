package com.haxademic.demo.render.audio;

import com.haxademic.core.app.P;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.camera.CameraOscillate;
import com.haxademic.core.draw.camera.common.ICamera;
import com.haxademic.core.render.Renderer;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;

public class BasicAudioRender
	extends PApplet
{
	// global vars
	protected int _fps = 30;
	protected Cube[] _blocks;
	protected int _cols = 10;
	protected int _rows = 10;
	protected ICamera camera;
	protected int NUM_BLOCKS = 20;  
	protected Renderer _render;
	protected AudioInputWrapper _audioInput;
	protected float rotInc = 0;
	protected Boolean _isSetup = false;
	
	public void setup ()
	{
		// set up stage
		if( !_isSetup ){
			size(1280,720,P.P3D);

			//size( 640, 480, P3D );				//size(screen.width,screen.height,P3D);
			_isSetup = true;
			//hint(DISABLE_OPENGL_2X_SMOOTH); 
			//hint(ENABLE_OPENGL_4X_SMOOTH); 
			frameRate( _fps );
			colorMode( PConstants.RGB, 1, 1, 1 );
			background( 0, 0, 0 );
			//noSmooth();
			//smooth();
			shininess(500); 
			lights();
			noStroke();
			
			// create background cell objects
			_blocks = new Cube[ NUM_BLOCKS ];
			float cellW = width/_cols;
			float cellH = height/_rows;
			int tmpIndex = 0;
			
			// Initialize each object
			for ( int i = 0; i < NUM_BLOCKS; i++ ) 
			{
				_blocks[i] = new Cube( cellW, cellH, tmpIndex );
				tmpIndex++;
			}
			
			// set up audio input
			_audioInput = new AudioInputWrapper( this, false );
			_audioInput.setNumAverages( NUM_BLOCKS );
			
			// set up camera
			camera = new CameraOscillate( this, 0, 0, -500, 400 );
			
			// set up renderer
			_render = new Renderer( this, _fps, Renderer.OUTPUT_TYPE_MOVIE, "bin/output/" );
	//		_render.startRenderer();
			_render.startRendererForAudio( "cock_holster.wav" );
		}
	}

	/**
	 * Auto-initialization of the root class.
	 * @param args
	 */
//	public static void main(String args[]) {
//		PApplet.main(new String[] { "--present", "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.test2.TestDrawing2" });
//	}

	public void draw() 
	{
		_render.analyzeAudio();
		
		background( 0, 0, 0 );
		
		rotInc += .01;
		rotateX( rotInc );
		
		// Oscillate and display each object
		for (int i = 0; i < NUM_BLOCKS; i++) {
			_blocks[i].update( i, NUM_BLOCKS, _audioInput.getFFT().averages[i] );
		}
		
		camera.update();
		
		_render.renderFrame();
	}
	
	/**
	 * Key handling for rendering functions - stopping and saving an image
	 */
	public void keyPressed()
	{
		if( key == ' ' ) _render.stop();
		if( key == 'p' ) _render.renderFrame();
	}
	
	// A Cell object
	class Cube 
	{
		// A cell object knows about its location in the _blocks as well as its size with the variables x,y,w,h.
		float w,h;   // p.width and p.height
		float rotX, rotY, rotZ; // current rotation 
		int index; // angle for oscillating brightness
		float baseRadius = 400f;
		
		// Cell Constructor
		Cube( float tempW, float tempH, int tempIndex ) {
			w = tempW;
			h = tempH;
			index = tempIndex;
			rotX = rotY = rotZ = 0;
		} 

		/**
		 * Place and draw each block
		 * @param index
		 * @param total
		 */
		void update( int index, int total, float audioAmp ) 
		{
			
			pushMatrix();
			
			float radiusAmp = baseRadius + audioAmp * 300;
			audioAmp = audioAmp * 300;
			
			float angle = ( 2.0f * (float) Math.PI ) * ( (float) index / (float) total );
			float x = (width/2) + radiusAmp * sin( angle );
			float y = (height/2) + radiusAmp * cos( angle );
			
			translate( x, y );
			
			rotX += .1 * index * .01;
			rotY += .1 * index * .01;
			rotZ += .1 * index * .01;
			
			// amount of alpha depends on EQ value
			// draw square
			fill(  .3f + sin( rotX ) * .5f, .7f + cos( rotY ) * .5f, .8f + sin( rotZ ) * .5f, 255 );
			//rect( x, y, w, h ); 
			box( 60, 120, 30 * audioAmp );
			box( 120, 60, 30 * audioAmp );
			
			popMatrix();
		}
	}
	

	// PApp-level listener for audio input data ------------------------ 
	public void audioInputData( AudioInput theInput ) {
		_audioInput.getFFT().getSpectrum(theInput);
		_audioInput.detector.detect(theInput);
	}
	
}
