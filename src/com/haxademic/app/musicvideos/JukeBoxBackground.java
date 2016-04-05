package com.haxademic.app.musicvideos;

import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.render.Renderer;

import krister.Ess.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class JukeBoxBackground
	extends PApplet
{
	public static void main(String args[]) {
//		PApplet.main(new String[] { "--present", "--hide-stop", "--bgcolor=000000", "com.haxademic.Haxademic" });
		PApplet.main(new String[] {              "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.test2.JukeBoxBackground" });
	}
	
	Cell[][] _grid;
	int _numGridQuares = 512;
	int _numAverages = 15;
	float _cols = Math.round( _numAverages * 1.77777f ) ;
	float _rows = Math.round( _numAverages );
//	float _r, _g, _b;

	int _curMode;
	final int MODE_SQUARE = 0;
	final int MODE_PYRAMID = 1;
	final int MODE_IMG = 2;
	int _hasStroke;
	PApplet p = this;
	
	int _fps = 30;

	protected int SUNFLOW = 0;
	protected boolean RENDERING = false;
	protected Renderer _render;
	protected AudioInputWrapper _audioInput;
	protected Boolean _isSetup = false;
	
	PImage _img;       // The source image

	
	public void setup ()
	{
		// set up stage
		if( !_isSetup ){
			//size(1280,720,P3D);
			//size( 640, 480, P3D );				//size(screen.width,screen.height,P3D);
			SUNFLOW = 0;
			RENDERING = true;
			_curMode = MODE_PYRAMID;
			//noLoop();
			// set up stage
			if( SUNFLOW == 1 ) {
				size( 1200,1200, "hipstersinc.P5Sunflow" );
			} else {
				size( 720,720, OPENGL );
//				if( RENDERING == true ) hint(DISABLE_OPENGL_2X_SMOOTH); 
				//smooth();
				shininess(500); 
				lights();
//				perspective(1.0f,1.5f,1f,200000f);
			}
			
			if( _curMode == MODE_IMG ) {
				_img = loadImage("middle-finger-black.png");
				_cols = _img.width;
				_rows = _img.height;
				noLoop();
			}
			
			//hint(DISABLE_OPENGL_2X_SMOOTH); 
			//hint(ENABLE_OPENGL_4X_SMOOTH); 
			
			frameRate( _fps );
			colorMode( PConstants.RGB, 255, 255, 255, 255 );
			background( 255, 255, 255 );
			noStroke();
			
			// create background cell objects
			_grid = new Cell[round(_cols)][round(_rows)];
			float cellW = width/_cols * 0.8f;
			float cellH = height/_rows * 0.8f;
			int tmpIndex = 0;
			for (int i = 0; i < _cols; i++) {
				for (int j = 0; j < _rows; j++) {
					// Initialize each object
					_grid[i][j] = new Cell(i*cellW,j*cellH,cellW,cellH,tmpIndex);
					tmpIndex++;
				}
			}
			
			// set up audio input
			_audioInput = new AudioInputWrapper( this, false );
			_audioInput.setNumAverages( 20 );
			
			// set up renderer
			if( RENDERING ) {
				_render = new Renderer( this, _fps, Renderer.OUTPUT_TYPE_MOVIE, "bin/output/" );
				//		_render.startRenderer();
				_render.startRendererForAudio( "wav/cache-money.wav", _audioInput );
			}
			
			// prevent re-initialization
			_isSetup = true;
		}
	}
	
	/**
	 * Auto-initialization of the root class.
	 * @param args
	 */
//	public static void main(String args[]) {
//		PApplet.main(new String[] { "--present", "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.test2.TestDrawing2" });
//	}

	public void draw() {
		if( RENDERING && p.frameCount > 2 ) { _render.analyzeAudio(); }
		
		if( SUNFLOW == 0 ) { background( 255, 255, 255, 255 ); }
		
//		if( frameCount > 2 ) {
		
			rectMode( PConstants.CORNER);
			
			background( 0, 0, 0 );
	
			// draw grid  
			for (int i = 0; i < _cols; i++) {
				for (int j = 0; j < _rows; j++) {
					// get color from image
					if( _curMode == MODE_IMG ) {
						int x = i;
						int y = j;
						int loc = x + y * _img.width;  //  p.Pixel array location
						int c = _img.pixels[loc];  // Grab the color
						_grid[i][j].updateBrightness( brightness(_img.pixels[loc] ) );
						_grid[i][j].updateDistance( dist(x, y, _cols/2, _rows/2) );
						_grid[i][j].updateImgColor( c );
					}
					
					// draw each object
					_grid[i][j].update();
				}
			}
//		}
		
		if( RENDERING && p.frameCount > 2 ) { _render.renderFrame(); }
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
	class Cell {
		// A cell object knows about its location in the _grid as well as its size with the variables x,y,w,h.
		float x,y;   // x,y location
		float w,h;   // width and height
		int index; // angle for oscillating brightness
		int imgColor = 0;
		float imgBrightness = 0;
		float distFromCenter = 0;
		
		// Cell Constructor
		Cell(float tempX, float tempY, float tempW, float tempH, int tempIndex) {
			x = tempX;
			y = tempY;
			w = tempW;
			h = tempH;
			index = tempIndex;
			if(index >= _numGridQuares) {
				index = _numGridQuares - 1; 
			}
		} 

		void update() {
			// amount of alpha depends on EQ value
			float alphaVal = _audioInput.getFFT().spectrum[index];
//			alphaVal *= 0.6;
//p.println("alphaVal = "+alphaVal);
			// calculate color
			fill( ( 1f - alphaVal ) * 255, ( 1f - alphaVal ) * 255, ( 1f - alphaVal ) * 255, alphaVal * 512 );

			// draw per mode
			if( _curMode == MODE_SQUARE ) {
				// draw square
				rect( x, y, w, h );
			} else if ( _curMode == MODE_PYRAMID ) {
				// translate and draw pyramid
				pushMatrix();
				translate(x + 24,y + 24,-50);
				Shapes.drawPyramid( p, 500f * alphaVal, (int)w, false );
				popMatrix();
			} else if ( _curMode == MODE_IMG ) {
				fill( imgColor );
				// translate and draw pyramid
				pushMatrix();
				translate(x,y - 40,-50);
				// blocks
				float boxZ = constrain( imgBrightness*200f, 2f, 2000f );
				//float boxZ = constrain( 2000f - imgBrightness*2.9f, 2f, 2000f );
				box( (int)w, (int)w, boxZ );
				// brightness-based height
//				Shapes.drawPyramid( p, 21f * imgBrightness/10, (int)w, false );
				// center-based heights
//				Shapes.drawPyramid( p, 40 * imgBrightness/280 * distFromCenter, (int)w, false );
				popMatrix();
			}
		}
		
		void updateImgColor( int c ) {
			imgColor = c;
		}
		
		void updateBrightness( float b ) {
			imgBrightness = b;
		}
		
		void updateDistance( float d ) {
			distFromCenter = d;
		}
	}
	
	// Image loading functions ------------------------ 
//	void loadImg( String fileName ) {
//		loadImg( fileName ); 
//		_img = p.loadImage( fileName );
//		_cols = _img.width;
//		_rows = _img.height;
//	}
	
	
	// PApp-level listener for audio input data ------------------------ 
	public void audioInputData( AudioInput theInput ) {
		_audioInput.getFFT().getSpectrum(theInput);
		_audioInput.detector.detect(theInput);
	}
	
}
