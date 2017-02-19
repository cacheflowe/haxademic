package com.haxademic.sketch.three_d;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.camera.CameraOscillate;
import com.haxademic.core.camera.common.ICamera;
import com.haxademic.core.render.Renderer;

import processing.core.PConstants;

/**
 * 
 * @author justin
 *
 */
public class PlusGridWave
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// global vars
	protected int _fps = 30;
	protected Cube[][] _grid;
	protected int _cols = 20;
	protected int _rows = 20;
	protected ICamera _camera;
	protected Renderer _render;

	public void setup () {
		super.setup();
		
		colorMode( PConstants.RGB, 1, 1, 1 );
		background( 1 );
		noSmooth();
		shininess(1000); 
		lights();
		noStroke();
		
		// create background cell objects
		_grid = new Cube[_cols][_rows];
		float cellW = (width/_cols) * 4;
		float cellH = (height/_rows) * 4;
		int tmpIndex = 0;
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				// Initialize each object
				_grid[i][j] = new Cube( i*cellW - width/2, j*cellH - height/2, cellW, cellH, tmpIndex );
				tmpIndex++;
			}
		}
		
		// set up _camera
		_camera = new CameraOscillate( this, 0, 0, 800, 200 );
		//_camera.setTarget( width/2, height/2, 0 );
		
		// set up renderer
		_render = new Renderer( this, _fps, Renderer.OUTPUT_TYPE_IMAGE, "bin/output/" );
		_render.startRenderer();
	}

	public void drawApp() 
	{
		// redraw bg and set lighting/drawing props
		background( 1 );
		
		// draw grid  
		for (int i = 0; i < _cols; i++) {
			for (int j = 0; j < _rows; j++) {
				// Oscillate and display each object
				_grid[i][j].update();
			}
		}
		
		// oscillate camera
		_camera.update();
		
		// _render.renderFrame();
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
	class Cube {
		// A cell object knows about its location in the _grid as well as its size with the variables x,y,w,h.
		float x,y;   // x,y location
		float w,h;   // p.width and p.height
		float rotX, rotY, rotZ; // current rotation 
		int index; // angle for oscillating brightness

		// Cell Constructor
		Cube( float tempX, float tempY, float tempW, float tempH, int tempIndex ) {
			x = tempX;
			y = tempY;
			w = tempW;
			h = tempH;
			index = tempIndex;
			rotX = rotY = rotZ = 0;
		} 

		void update() {
			
			pushMatrix();
			
			// take half height of what it will be, to anchor on a flat surface
			float curZ = ( 600 + sin( rotX ) * 450 ) / 2;
			
			translate( x, y, curZ );
//			rotateX( sin( rotX * 0.003f ) * 20 );
//			rotateY( rotY );
//			rotateZ( rotZ );
			
			rotX += .1 * index * .01;
			rotY += .1 * index * .01;
			rotZ += .1 * index * .01;
			
			// amount of alpha depends on EQ value
			// draw square
			fill(  .2f + sin( rotX ) * .5f, .3f + cos( rotY ) * .5f, .4f + cos( rotZ ) * .5f, 255 );
			//rect( x, y, w, h ); 
			box( 40, 80, 600 + sin( rotX ) * 450 );
			box( 80, 40, 600 + sin( rotX ) * 450 );
			
			popMatrix();
		}
	}
	
}
