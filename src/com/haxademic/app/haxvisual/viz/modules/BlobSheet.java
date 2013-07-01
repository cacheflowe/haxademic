package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.cameras.CameraBasic;
import com.haxademic.core.cameras.CameraSpotter;
import com.haxademic.core.hardware.midi.MidiWrapper;

public class BlobSheet
extends ModuleBase 
implements IVizModule
{
	// class props
	int _numAverages = 3;

	int _curMode;
	final int MODE_BOXES = 0;
	final int MODE_MESH = 1;
	final int MODE_MESH_COLOR = 2;
	final int MODE_TRIANGLES = 3;
	final int MODE_BOXES_OUTLINE = 4;
	final int MODE_BOXEN = 5;
	final int MODE_CIRCLES = 6;
	final int NUM_MODES = 7;
	
	
	float _r, _g, _b;

	// 2D Array of objects
	Cell[][] grid;


	// Number of columns and rows in the grid
	int cols = 40;
	int rows = 30;
	float startR = p.random(0,2*p.PI);
	float startIncR = p.random(.001f,.05f);
	float incR = p.random(.0001f,.001f);
	float startG = p.random(0,2* p.PI);
	float startIncG = p.random(.001f,.05f);
	float incG = p.random(.0001f,.001f);
	float startB = p.random(0,2* p.PI);
	float startIncB = p.random(.001f,.05f);
	float incB = p.random(.0001f,.001f);
	float sizeMult;
	int followRow;
	int followCol;
	float gridW, gridH;

	ControlPoint[] cntrls;
	int numCtrls = _numAverages;
	
	public BlobSheet()
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.noStroke();
		newCamera();
		newBoxSize();

		// create cells
		gridW = p.width * 10;
		gridH = p.height * 10;
		float boxW = gridW / cols;
		float boxH = gridH / rows;
		grid = new Cell[cols][rows];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// pass neighbor cells in for line drawing
				Cell tlCell = ( i > 0 && j > 0 ) ? grid[i-1][j-1] : null;
				Cell tCell = ( i > 0 && j > 0 ) ? grid[i-1][j] : null;
				Cell lCell = ( i > 0 && j > 0 ) ? grid[i][j-1] : null;
				// Initialize each object
				grid[i][j] = new Cell(-gridW/2 + i*boxW, -gridH/2 + j*boxH, boxW, boxH, tlCell, tCell, lCell );
			}
		}

		// create control points
		cntrls = new ControlPoint[numCtrls];
		for (int i = 0; i < numCtrls; i++) {
			// Initialize each object
			cntrls[i] = new ControlPoint();
		}
		
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.noStroke();
		newBoxSize();
		pickNewColors();
		newCamera();
	}

	public void update() {
		p.shininess(1000f); 
		p.lights();
		p.background(0);

		_curCamera.update();
		p.rectMode(PConstants.CORNER);
		
		
		// increment the starting points
		startR += startIncR;
		float curR = startR;
		startG += startIncG;
		float curG = startG;
		startB += startIncB;
		float curB = startB;

		for (int i = 0; i < numCtrls; i++) {
			cntrls[i].update( _audioData.getFFT().averages[i], _audioData.getFFT().averages[i] );
		}
		
		p.beginShape(p.TRIANGLES);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// Oscillate and display each object
				grid[i][j].oscillate( curR, curG, curB );

				Boolean follow = false;
				if( i == followCol && j == followRow ) 
				{
					_curCamera.setTarget( (int)grid[i][j].x, (int)grid[i][j].y, 0 );
				}

				grid[i][j].display();

				curR += incR;
				curG += incG;
				curB += incB;
			}
		}
		p.endShape();
	}

	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_01 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_01 ) == 1 ) {
			 pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_02 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_02 ) == 1 ) {
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_03 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_03 ) == 1 ) {
			newBoxSize();
		}
		if ( p.key == 'f' || p.key == 'F' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_05 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_05 ) == 1 ) {
			newFollowObject();
		}
		if ( p.key == 'm' || p.key == 'M' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_04 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_04 ) == 1 ) {
			newFollowObject();
			pickNewColors();
			newCamera();
			newBoxSize();
		}
		if ( p.key == 'l' || p.key == 'L' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_08 ) == 1 || p.getMidi().midiPadIsOn( MidiWrapper.NOTE_08 ) == 1 ) {
			newMode();
		}
	}

	//  p.PIck new p.random colors
	void pickNewColors()
	{
		_r = p.random( 0.1f, 1.2f );
		_g = p.random( 0.6f, 1.2f );
		if( _r > 1 && _g > 1 ) {
			_b = p.random( 0.1f, 0.5f );
		} else {
			_b = p.random( 0.1f, 1.2f );
		}
	}

	void newBoxSize()
	{
		sizeMult = p.random(.3f,.8f);
	}

	void newFollowObject()
	{
		// pick indexes to focus on from the center region of the grid
		followRow = (int) ( rows/2 + 1 +  p.round( p.random( -rows/9, rows/9 ) ) );
		followCol = (int) ( cols/2 + 1 +  p.round( p.random( -cols/9, cols/9 ) ) );
	}

	void newCamera()
	{
		int randCamera = p.round( p.random( 0, 1 ) );
		int newZ = p.round( p.random( 6000, 6000 ) );
		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 0, 0, newZ );
		else if( randCamera == 1 ) _curCamera = new CameraSpotter( p, 0, 0, newZ );
		//else if( randCamera == 2 ) _curCamera = new CameraOscillate( 0, 0, newZ/2, 100 );
		_curCamera.reset();
	}

	void newMode()
	{
		_curMode = p.round( p.random( 0, NUM_MODES - 1 ) );
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}

	// A Cell object
	public class Cell {
		// A cell object knows about its location in the grid as well as its size with the variables x,y,w,h.
		public float x,y,z;   // x,y location
		float w,h;   // p.width and p.height
		float angle; // angle for oscillating brightness
		float r,g,b;
		Cell tlCell, tCell, lCell;	// for mesh drawing

		// Cell Constructor
		Cell(float tempX, float tempY, float tempW, float tempH, Cell tlCell, Cell tCell, Cell lCell ){
			x = tempX;
			y = tempY;
			w = tempW;
			h = tempH;
			this.tlCell = tlCell;
			this.tCell = tCell;
			this.lCell = lCell;
		} 

		// Oscillation means increase angle
		void oscillate( float redColor, float greenColor, float blueColor ) {
			r = redColor;
			g = greenColor;
			b = blueColor;
		}


		void display() {
			
			// get color
			float centerDist = p.dist(x, y, gridW/2, gridH/2);
			float ctrlFactor = 0.002f;
			float ctrlPt1 = p.dist(x, y, cntrls[0].x, cntrls[0].y) * ctrlFactor;
			float ctrlPt2 = p.dist(x, y, cntrls[1].x, cntrls[1].y) * ctrlFactor;
			float ctrlPt3 = p.dist(x, y, cntrls[2].x, cntrls[2].y) * ctrlFactor;
			int cellColor = p.color(_r+.5f*p.sin(r) * p.cos(ctrlPt1) * p.cos(ctrlPt2) * p.sin(ctrlPt3), _g+.5f*p.sin(g) * p.sin(ctrlPt1) * p.sin(ctrlPt2) * p.sin(ctrlPt3), _b+.5f*p.cos(b) * p.sin(ctrlPt1) * p.cos(ctrlPt2) * p.sin(ctrlPt3) );
			// Color calculated using sine wave

			// adjust cell z per brightness
			z = 10*p.brightness( cellColor ) * ( 1000 * ( _audioData.getFFT().averages[1] + _audioData.getFFT().averages[2] )  );
			p.pushMatrix();
			p.translate( 0, 0, 0 + z );
									//p.rotateZ( _audioData.getFFT().averages[1] * .01f );
			
			
			switch( _curMode ){
				case MODE_BOXES :
					p.fill(cellColor);
					p.noStroke();
					p.rect(x,y,w*sizeMult,h*sizeMult); 
					break;
				case MODE_CIRCLES :
					p.stroke(cellColor);
					p.strokeWeight(4);
					p.noFill();
					p.ellipse(x,y,w*sizeMult,h*sizeMult); 
					break;
				case MODE_BOXEN :
					p.fill(cellColor);
					p.noStroke();
					p.translate( x, y, 0 );
					p.box( w*sizeMult, h*sizeMult, 10 ); 
					break;
				case MODE_BOXES_OUTLINE :
					p.stroke(cellColor);
					p.strokeWeight(4);
					p.noFill();
					p.rect(x,y,w*sizeMult,h*sizeMult); 
					break;
				case MODE_MESH :
					p.noFill();
					p.stroke(1,p.brightness( cellColor ));
					p.strokeWeight(3);
					drawTrianglesToNeighbor();
					break;
				case MODE_MESH_COLOR :
					p.noFill();
					p.stroke(cellColor,p.brightness( cellColor ));
					p.strokeWeight(3);
					drawTrianglesToNeighbor();
					break;
				case MODE_TRIANGLES :
					p.fill(cellColor);
					p.stroke(0);
					p.strokeWeight(25);
					drawTrianglesToNeighbor();
					break;
				default : 
					break;
			}
			
			p.popMatrix();

		}
		
		protected void drawTrianglesToNeighbor(){
			if( tCell != null ) {
				p.vertex( x, y, z );
				p.vertex( tCell.x, tCell.y, tCell.z );
				p.vertex( tlCell.x, tlCell.y, tlCell.z );
				
				p.vertex( x, y, z );
				p.vertex( lCell.x, lCell.y, lCell.z );
				p.vertex( tlCell.x, tlCell.y, tlCell.z );
			}
		}
	}


	// A Cell object
	class ControlPoint {

		float x, y, incX, incY, incXSpeed, incYSpeed;

		// Cell Constructor
		ControlPoint() {
			incX = p.random(0,2* p.PI);
			incY = p.random(0,2* p.PI);
			incXSpeed = p.random(.01f,.1f);
			incYSpeed = p.random(.01f,.1f);
			update(0,0);
		} 

		// Oscillation means increase angle
		void update(float xspeed, float yspeed) {
			incX += xspeed * .1;
			incY += yspeed * .2;
			x = gridW/2 + ( gridW/2 * 1.2f * p.sin(incX) );
			y = gridH/2 + ( gridH/2 * 1.2f * p.sin(incY) );
		}


	}

}
