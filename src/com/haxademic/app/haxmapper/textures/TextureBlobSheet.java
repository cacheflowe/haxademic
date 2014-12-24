package com.haxademic.app.haxmapper.textures;

import processing.core.PConstants;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.util.DrawUtil;

public class TextureBlobSheet 
extends BaseTexture {

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
	float startR = P.p.random(0,2*P.PI);
	float startIncR = P.p.random(.001f,.05f);
	float incR = P.p.random(.0001f,.001f);
	float startG = P.p.random(0,2* P.PI);
	float startIncG = P.p.random(.001f,.05f);
	float incG = P.p.random(.0001f,.001f);
	float startB = P.p.random(0,2* P.PI);
	float startIncB = P.p.random(.001f,.05f);
	float incB = P.p.random(.0001f,.001f);
	float sizeMult;
	int followRow;
	int followCol;
	float gridW, gridH;

	ControlPoint[] cntrls;
	int numCtrls = _numAverages;

	public TextureBlobSheet( int width, int height ) {
		super();

		buildGraphics( width, height );
		
		initBlobSheet();
	}
	
	protected void initBlobSheet() {
		newLineMode();

		// create cells
		gridW = _texture.width * 2;
		gridH = _texture.height * 2;
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
	
	public void newMode() {
		_curMode = P.round( P.p.random( 0, NUM_MODES - 1 ) );
	}

	public void updateDraw() {
		_texture.clear();
		
		DrawUtil.resetGlobalProps( _texture );
		DrawUtil.setCenterScreen( _texture );
		
		_texture.rectMode(PConstants.CORNER);
		
		
		
		// increment the starting points
		startR += startIncR;
		float curR = startR;
		startG += startIncG;
		float curG = startG;
		startB += startIncB;
		float curB = startB;

		for (int i = 0; i < numCtrls; i++) {
			cntrls[i].update( P.p._audioInput.getFFT().averages[i], P.p._audioInput.getFFT().averages[i] );
		}
		
		if( _curMode ==  MODE_MESH || _curMode == MODE_TRIANGLES || _curMode == MODE_MESH_COLOR ) {
			_texture.beginShape(P.TRIANGLES);
		}
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// Oscillate and display each object
				grid[i][j].oscillate( curR, curG, curB );

				// Boolean follow = false;
				if( i == followCol && j == followRow ) 
				{
//					_curCamera.setTarget( (int)grid[i][j].x, (int)grid[i][j].y, 0 );
				}

				grid[i][j].display();

				curR += incR;
				curG += incG;
				curB += incB;
			}
		}
		if( _curMode ==  MODE_MESH || _curMode == MODE_TRIANGLES || _curMode == MODE_MESH_COLOR ) {
			_texture.endShape();
		}
	}
	
	public void setColor( int color ) {
//		super.setColor(color);
		_r = P.p.random( 0.1f, 1.2f );
		_g = P.p.random( 0.6f, 1.2f );
		if( _r > 1 && _g > 1 ) {
			_b = P.p.random( 0.1f, 0.5f );
		} else {
			_b = P.p.random( 0.1f, 1.2f );
		}
	}

	public void newLineMode() {
		sizeMult = P.p.random(.3f,.8f);
	}

	void newFollowObject()
	{
		// pick indexes to focus on from the center region of the grid
		followRow = (int) ( rows/2 + 1 +  P.round( P.p.random( -rows/9, rows/9 ) ) );
		followCol = (int) ( cols/2 + 1 +  P.round( P.p.random( -cols/9, cols/9 ) ) );
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
			// float centerDist = P.dist(x, y, gridW/2, gridH/2);
			float ctrlFactor = 0.002f;
			float ctrlPt1 = P.dist(x, y, cntrls[0].x, cntrls[0].y) * ctrlFactor;
			float ctrlPt2 = P.dist(x, y, cntrls[1].x, cntrls[1].y) * ctrlFactor;
			float ctrlPt3 = P.dist(x, y, cntrls[2].x, cntrls[2].y) * ctrlFactor;
			int cellColor = _texture.color(
					255f * ( _r+.5f*P.sin(r) * P.cos(ctrlPt1) * P.cos(ctrlPt2) * P.sin(ctrlPt3) ), 
					255f * ( _g+.5f*P.sin(g) * P.sin(ctrlPt1) * P.sin(ctrlPt2) * P.sin(ctrlPt3) ), 
					255f * ( _b+.5f*P.cos(b) * P.sin(ctrlPt1) * P.cos(ctrlPt2) * P.sin(ctrlPt3) ) 
			);
			// Color calculated using sine wave

			// adjust cell z per brightness
			z = -100 + 1 * _texture.brightness( cellColor )/2f * ( P.p._audioInput.getFFT().averages[2] + P.p._audioInput.getFFT().averages[3] );
			_texture.pushMatrix();
			_texture.translate( 0, 0, 0 + z );
			
			
			switch( _curMode ){
				case MODE_BOXES :
				case MODE_CIRCLES :
					_texture.fill(cellColor, 167f);
					_texture.noStroke();
					_texture.rect(x,y,w*sizeMult,h*sizeMult); 
					break;
//				case MODE_CIRCLES :
//					p.stroke(cellColor);
//					p.strokeWeight(1);
//					p.noFill();
//					p.ellipse(x,y,w*sizeMult,h*sizeMult); 
//					break;
				case MODE_BOXEN :
					_texture.fill(cellColor, 167f);
					_texture.noStroke();
					_texture.translate( x, y, 0 );
					_texture.box( w*sizeMult, h*sizeMult, 10 ); 
					break;
				case MODE_BOXES_OUTLINE :
					_texture.stroke(cellColor);
					_texture.strokeWeight(1);
					_texture.noFill();
					_texture.rect(x,y,w*sizeMult,h*sizeMult); 
					break;
				case MODE_MESH :
					_texture.noFill();
					_texture.stroke( _color ); // ,p.brightness( cellColor )
					_texture.strokeWeight(1);
					drawTrianglesToNeighbor();
					break;
				case MODE_MESH_COLOR :
					_texture.noFill();
					_texture.stroke(cellColor);	// ,p.brightness( cellColor )
					_texture.strokeWeight(2);
					drawTrianglesToNeighbor();
					break;
				case MODE_TRIANGLES :
					_texture.fill(cellColor, 167f);
					_texture.stroke(0);
					_texture.strokeWeight(3);
					drawTrianglesToNeighbor();
					break;
				default : 
//					p.fill(127);
//					p.stroke(255,255);
//					p.strokeWeight(3);
//					drawTrianglesToNeighbor();
//					break;
					break;
			}
			
			_texture.popMatrix();

		}
		
		protected void drawTrianglesToNeighbor(){
			if( tCell != null ) {
				_texture.vertex( x, y, z );
				_texture.vertex( tCell.x, tCell.y, tCell.z );
				_texture.vertex( tlCell.x, tlCell.y, tlCell.z );
				
				_texture.vertex( x, y, z );
				_texture.vertex( lCell.x, lCell.y, lCell.z );
				_texture.vertex( tlCell.x, tlCell.y, tlCell.z );
			}
		}
	}


	// A Cell object
	class ControlPoint {

		float x, y, incX, incY, incXSpeed, incYSpeed;

		// Cell Constructor
		ControlPoint() {
			incX = P.p.random(0,2* P.PI);
			incY = P.p.random(0,2* P.PI);
			incXSpeed = P.p.random(.01f,.1f);
			incYSpeed = P.p.random(.01f,.1f);
			update(0,0);
		} 

		// Oscillation means increase angle
		void update(float xspeed, float yspeed) {
			incX += xspeed * .1;
			incY += yspeed * .2;
			x = gridW/2 + ( gridW/2 * 1.2f * P.sin(incX) );
			y = gridH/2 + ( gridH/2 * 1.2f * P.sin(incY) );
		}


	}
}
