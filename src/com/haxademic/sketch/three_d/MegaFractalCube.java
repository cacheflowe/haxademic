package com.haxademic.sketch.three_d;

import hipstersinc.P5Sunflow;
import hipstersinc.sunflow.SunflowCamera;
import processing.core.PApplet;
import processing.core.PConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.cameras.CameraOscillate;
import com.haxademic.core.cameras.common.ICamera;
import com.haxademic.core.render.Renderer;

//import hipstersinc.P5Sunflow;

@SuppressWarnings({ "serial" })
public class MegaFractalCube
	extends PApplet
{
	// global vars
	protected int _fps = 30;
	protected FractCube _cube;
	protected int _cols = 10;
	protected int _rows = 10;
	protected ICamera camera;
	protected int NUM_BLOCKS = 20;  
	protected Renderer _render;
	protected float rotInc = 0;
	protected int BASE_CUBE_SIZE = 500;
	protected boolean RENDERING = false;
	
	public void setup ()
	{
		int SUNFLOW = 1;
		RENDERING = false;
		//noLoop();
		// set up stage
		if( SUNFLOW == 1 ) {
			size( 800, 600, P.SUNFLOW );
			BASE_CUBE_SIZE *= 1 + 1/3;
//			noLoop();
		} else {
			size( 800, 600, PConstants.P3D );
			lights();
			shininess(500); 
//			smooth();
			camera = new CameraOscillate( this, 200, 200, 0, 200 );
		}
		frameRate( _fps );
		colorMode( PConstants.RGB, 255, 255, 255, 255 );
		background( 0, 0, 0 );
		noStroke();
		
		
		_cube = new FractCube( BASE_CUBE_SIZE );
		
		// set up camera
		
		if( RENDERING )
		{
			// set up renderer
			_render = new Renderer( this, _fps, Renderer.OUTPUT_TYPE_MOVIE, "bin/output/" );
			_render.startRenderer();
		}
	}

	/**
	 * Auto-initialization of the root class.
	 * @param args
	 *//*
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "--hide-stop", "--bgcolor=000000", "com.haxademic.sketch.test2.TestDrawing2" });
	}*/

	public void draw() 
	{
		background( 0, 0, 40 );
		camera( 600, 700, -600, 400, 300, 0, 0, 1, 0);
		rotateY(45);
		
		// update, keep base cube in center
		if(camera != null) camera.update();
		_cube.update( width/2, height/2, 0 );

		if( frameCount > 1 && RENDERING ) 
		{
			_render.renderFrame();
			println("rendering frame "+frameCount);
		}
		
		if( frameCount >= 600 )
		{
			_render.stop();
			exit();
		}
	}
	
	void setupCamera() {
		P5Sunflow sunflow = (P5Sunflow) g;
		sunflow.camera.setType(SunflowCamera.THINLENS);
	}

	void setupScene() {
		// P5Sunflow sunflow = (P5Sunflow) g;
	}
	
	/**
	 * Key handling for rendering functions - stopping and saving an image
	 */
	public void keyPressed()
	{
		if( key == ' ' ) _render.stop();
		if( key == 'p' ) _render.renderFrame();
	}
	
	// A Cube object
	class FractCube 
	{
		// A cell object knows about its location in the _blocks as well as its size with the variables x,y,w,h.
		float _baseSize;
		float _curSize;
		float _x, _y, _z;
		protected FractCube[] _childrens;
		protected float CHILD_RATIO = 0.5f;
		
		// Cube Constructor
		FractCube( float size ) 
		{
			_baseSize = size;
			_curSize = _baseSize;	//0;// _baseSize * 1f 
			
			if( _baseSize > 8 )
			{
				_childrens = new FractCube[ 6 ];
					
				// Initialize each object with base size
				for ( int i = 0; i < _childrens.length; i++ ) 
					_childrens[i] = new FractCube( _baseSize * CHILD_RATIO );
				
			}
		} 
		
		float getCurrentSizeRatio()
		{
			if( _curSize == 0 )
				return .0000001f;
			else
				return _curSize / _baseSize;
		}

		/**
		 * Place and draw each cube
		 */
		void update( float x, float y, float z ) 
		{
			// store 3d coordinates
			_x = x;
			_y = y;
			_z = z;
			
			// ease up the size
			if( _curSize < _baseSize ) _curSize += ( _baseSize - _curSize ) / 75;
			
			pushMatrix();
			
			// draw self
			translate( _x, _y, _z );
			fill( 255 - (_curSize / BASE_CUBE_SIZE) * 235 );
			box(_curSize);
			
			if( _childrens != null )
				if( _curSize > _baseSize / 2 )
					updateChildrenBoxen();
			
			popMatrix();
		}
		
		void updateChildrenBoxen()
		{
			// half size of 
			float distance = ( _curSize / 2 ) + ( _curSize * CHILD_RATIO ) / 2;
			
			// update 6 sides
			_childrens[0].update( 0 + distance * _childrens[0].getCurrentSizeRatio(), 0, 0 );
			_childrens[1].update( 0 - distance * _childrens[1].getCurrentSizeRatio(), 0, 0 );
			_childrens[2].update( 0, 0 + distance * _childrens[2].getCurrentSizeRatio(), 0 );
			_childrens[3].update( 0, 0 - distance * _childrens[3].getCurrentSizeRatio(), 0 );
			_childrens[4].update( 0, 0, 0 + distance * _childrens[4].getCurrentSizeRatio() );
			_childrens[5].update( 0, 0, 0 - distance * _childrens[5].getCurrentSizeRatio() );
		
		}
	}
	
}
