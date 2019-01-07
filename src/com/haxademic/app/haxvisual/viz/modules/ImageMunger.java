package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.app.P;
import com.haxademic.core.draw.camera.CameraBasic;
import com.haxademic.core.draw.camera.CameraOscillate;
import com.haxademic.core.draw.camera.CameraSpotter;

import processing.core.PConstants;
import processing.core.PImage;

public class ImageMunger 
extends ModuleBase
implements IVizModule
{
	// class props
	int _numAverages = 10;

	int _curMode;
	final int MODE_DEFAULT = 0;
	float _r, _g, _b;

	PImage img;       // The source image
	int cellsize = 2; // Dimensions of each cell in the grid
	int columns, rows;   // Number of columns and rows in our system
	String imgPath = "images/sexpanther/";
	int curImg = 0;
	int numImages = 8;
	int curFrameCount = 0;
	int recycleAfterNumFrames = 50;
	int numBrightnesLevels = 10;
	float cameraXSpeed;
	float cameraYSpeed;
	float cameraZSpeed;

	int _roundingMode;

	public ImageMunger()
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		nextImage();
		p.camera();
		newCamera();
	}

	public void initAudio()
	{
//		audioData.setNumAverages( _numAverages );
//		audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		//lights();
		p.camera();
		_curCamera.reset();
	}

	public void update() {
		// update camera
		_curCamera.update();

		p.background(0);
		p.noStroke();
		//rotateY(1.4);
		// Begin loop for columns
		for ( int i = 0; i < columns; i++) {
			// Begin loop for rows
			for ( int j = 0; j < rows; j++) {
				int x = i*cellsize + cellsize/2;  // x position
				int y = j*cellsize + cellsize/2;  // y position
				int loc = x + y*img.width;  //  p.PIxel array location
				int c = img.pixels[loc];  // Grab the color
				float z;
				switch( _roundingMode )
				{
					case 0 :
						int index = p.round( p.brightness(img.pixels[loc]) * 511 );
						z = P.p.audioFreq(index) * 800 + 250;
						break;
					case 1 :
						z = p.brightness(img.pixels[loc]) * P.p.audioFreq(5) * 800 + 250;
						break;
					case 2 :
						int roundedBrightnesss = p.round( p.brightness(img.pixels[loc]) * 20 );
						roundedBrightnesss = p.constrain( roundedBrightnesss, 0, numBrightnesLevels - 1 );
						z = P.p.audioFreq(roundedBrightnesss) * 800 + 250;
						break;
					default :
						int roundedBrightness = p.round( p.brightness(img.pixels[loc]) * 10 );
						roundedBrightness = p.constrain( roundedBrightness, 0, numBrightnesLevels - 1 );
						z = P.p.audioFreq(roundedBrightness) * 800 + 250;
						break;
				}

				// Translate to the location, set fill and stroke, and draw the rect
				p.pushMatrix();
				p.translate(x + p.width/2 - img.width/2, y + p.height/2 - img.height/2, z);
				p.fill(c, 1);

				p.rectMode(PConstants.CENTER);
				p.rect(0, 0, cellsize, cellsize);
				p.popMatrix();
			}
		}
	}

	void loadImg( String fileName ) {
		img = p.loadImage( fileName );  // Load the image
		columns = img.width / cellsize;  // Calculate # of columns
		rows = img.height / cellsize;  // Calculate # of rows
	}

	void nextImage() {
		// p.random range of time to show this image
		recycleAfterNumFrames = p.round( p.random( 50, 250 ) );
		// increment the image index
		curImg++;
		if( curImg > numImages ){ 
			curImg = 1; 
		}
		// load image
		loadImg(imgPath.concat( addLeadingZero(curImg) ).concat(".jpg")); 
	}
	
	void randImage() {
		// p.random range of time to show this image
		recycleAfterNumFrames = p.round( p.random( 50, 250 ) );
		// actually, let's make ti p.random
		int randImg = p.round(p.random(1,numImages));
		curImg = randImg;
		// load image
		loadImg(imgPath.concat( addLeadingZero( randImg ) ).concat(".jpg")); 
	}


	void newCamera()
	{
		int randCamera = p.round( p.random( 0, 2 ) );
		int newZ = p.round( p.random( 200, 200 ) );
		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 0, 0, newZ );
		else if( randCamera == 1 ) _curCamera = new CameraOscillate( p, 0, 0, newZ, 100 );
		else if( randCamera == 2 ) _curCamera = new CameraSpotter( p, 0, 0, newZ );
		_curCamera.reset();
	}

	void newMode()
	{
		_roundingMode = p.round( p.random( 0, 3 ) );
	}


	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C') {
			 pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V') {
			newCamera();
		}
		if ( p.key == 'm' || p.key == 'M') {
			newMode();
			//nextImage();
			randImage();
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B') {
			newMode();
			randImage();
			newCamera();
		}
		if ( p.key == ' ') {
			//bounceBlocks();
		}
		if ( p.key == '1') {
			_roundingMode = 0;
		} else if ( p.key == '2') {
			_roundingMode = 1;
		} else if ( p.key == '3') {
			_roundingMode = 2;
		} else if ( p.key == '4') {
			_roundingMode = 3;
		}

	}

	//  p.PIck new p.random colors
	void  pickNewColors()
	{
		_r = p.random( .25f, .5f );
		_g = p.random( .25f, .5f );
		_b = p.random( .25f, .5f );
	}
	
	// add leading zero if under 10
	public String addLeadingZero( int imgNum )
	{
		String parsedNum;
		if( imgNum < 10 ){
			parsedNum = "0" + imgNum;
		} 
		else {
			parsedNum = ""+imgNum;
		}
		return parsedNum;
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{
		
	}

}
