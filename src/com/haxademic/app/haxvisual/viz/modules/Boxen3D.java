package com.haxademic.app.haxvisual.viz.modules;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.core.app.P;
import com.haxademic.core.camera.CameraBasic;
import com.haxademic.core.camera.CameraOscillate;

import processing.core.PConstants;

public class Boxen3D 
extends ModuleBase
implements IVizModule
{
	// class props
	int _numAverages = 12;

	int _curMode;
	final int MODE_DEFAULT = 0;
	float _r, _g, _b;
	float _rMult, _gMult, _bMult;
	// box geom
	float spacingInc = 0;    
	float box_space = p.random(100,400); 
	float box_size = box_space / 10; 
	int next_view = 0;
	float rotZ = 0.02500000f;
	float _rotZinc = 0;
	float rotY = 0.00000000f;
	float rotX = 0.50000000f;
	float rotXMin = rotX;
	float rotXMax = 10;
	int rotXDir = 0;
	int gridSize = 16;
	int dir = 1;
	float origGridSize = gridSize * (box_space + box_size); 
	int currMouseX = 0;
	int switch_view_count = 0;
	float currXInc = 5;
	float currYInc = 5;
	int _hasStroke = 0;
	int _boxStretchMode = 0;
	int _followRow;
	int _followCol;

	public Boxen3D()
	{
		super();
		// store and init audio engine
		initAudio();

		// init viz
		init();
	}

	public void init() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		//lights();
		p.camera();
		newCamera();
		newMode();
	}

	public void initAudio()
	{
//		audioData.setNumAverages( _numAverages );
//		audioData.setDampening( .17f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		//lights();
		p.camera();
		_curCamera.reset();
		newMode();



	}

	public void update() {
		// clear screen
		p.background(0,100); 
		p.shininess(500); 
		p.lights();
		// get spacing between boxes. used to make them all spread and contract
		float currBoxSpacing = box_space + p.sin(spacingInc/1000)*10;
		// get p.width of entire grid
		float currGridSize = gridSize * (currBoxSpacing + box_size); 
		// set stage centerpoint vars and set starting point so the grid pops out from middle. +100 is a hack
		float startX = p.width/2;
		float startY = p.height/2;
		p.translate(startX-(currGridSize/2)+300, startY-(currGridSize/2)+100,-1000);
		// set color incrementer
		float color_inc = .001f; 
		// loop through grid
		int curIndex = 0;
		
		p.translate(0,0,0);
		p.noStroke();
		
		for(int i = 0; i < gridSize; i++) {   // row
			for(int j = 0; j < gridSize; j++) { // col
				// move camera to follow
				if( i == _followRow && j == _followCol ) _curCamera.setTarget( (int)p.modelX(0,0,0), (int)p.modelY(0,0,0), (int)p.modelZ(0,0,0) );


				// space boxen different per mode
				switch( _curMode )
				{
					case MODE_DEFAULT :
						p.translate(0,currBoxSpacing, 10);
						break;
					case 1 :
						p.translate(0,currBoxSpacing * P.p.audioFreq(curIndex) * 2, 10);
						break;
					case 2 :
						p.translate(currBoxSpacing * P.p.audioFreq(curIndex) * 2,P.p.audioFreq(curIndex), 20);
						break;
					case 3 :
						p.translate(0,0, 20);
						break;
					default :
						break;
				}


				// rotate as we place blocks on stage
				switch( _curMode ){
					case MODE_DEFAULT :
						p.rotateZ( p.PI/rotZ );
						p.rotateX( p.PI/currXInc );
						p.rotateY( p.PI/currYInc );
						break;
					case 1 :
						p.rotateZ( p.PI/rotZ * 3);
						p.rotateX( p.PI/currXInc * .3f);
						p.rotateY( p.PI/currYInc);
						break;
					case 2 :
						p.rotateZ( p.PI/rotZ * .002f);
						p.rotateX( p.PI/currXInc * .002f);
						p.rotateY( p.PI/currYInc*.5f);
						break;
					case 3 :
						p.rotateZ( p.PI/p.sin(rotZ) * .002f);
						p.rotateX( p.PI/p.cos(currXInc) * .002f);
						p.rotateY( p.PI/(currYInc * 2));
						break;
					default :
						break;
				}
				// increment color
				float curR = 0.75f + P.p.audioFreq(curIndex) * ( _r + p.sin( color_inc * _rMult ) );
				float curG = 0.75f + P.p.audioFreq(curIndex) * ( _g + p.cos( color_inc * _gMult ) );
				float curB = 0.75f + P.p.audioFreq(curIndex) * ( _b + p.sin( color_inc * _bMult ) );
				p.fill( curR, curG, curB );
				//p.rect(0, 0, box_size, box_size);

				// stretch boxes based on different modes
				float tmpW, tmpH, tmpD;
				
				switch( _boxStretchMode )
				{
					case 0 : 
						tmpW = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 220 );
						tmpH = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 20 );
						tmpD = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 4 );
						break;
					case 1 : 
						tmpW = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 40 );
						tmpH = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 320 );
						tmpD = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 5 );
						p.box( tmpW, tmpH, tmpD );
						break;
					default : 
						tmpW = tmpH = box_size + P.p.audioFreq(curIndex) * box_size * 200;
						tmpD = P.round( box_size + P.p.audioFreq(curIndex) * box_size * 5 );
						break; 
				}
				p.box( tmpW, tmpH, tmpD );

				curIndex++;
				// increment color for a gradient across the grid
				color_inc = color_inc + 0.001f; 

			}
			// alter direction of changing the spacing between blocks
			spacingInc = 10+p.sin(next_view*.01f)*500;
			// move to beginning of next row 
			switch( _curMode ){
			case MODE_DEFAULT :
				p.translate(currBoxSpacing,-(currBoxSpacing*gridSize));  
				break;
			case 1 :
				p.translate(currBoxSpacing,-(currBoxSpacing*gridSize));  
				break;
			case 2 :
				p.translate(currBoxSpacing*.2f,-(currBoxSpacing*gridSize*2),p.sin(rotZ)*currBoxSpacing/2); 
				break;
			case 3 :
				p.translate(currBoxSpacing,currBoxSpacing*gridSize * p.sin(rotZ)*7, p.sin(rotZ)*30); 
				break;
			default :
				break;
			}

		}
		// always increment z
		rotZ += _rotZinc;
		// increment timer for bouncing blocks
		next_view++;
		//switch_view_count++;
		if(switch_view_count == 500){
			//bounceBlocks();
		}
		// update camera
		_curCamera.update();
	}

	void fnRotX(){
		// very slowly rotate in 2 directions per frame
		if(rotXDir == 1){
			rotX += 0.0100000;
			if(rotX > rotXMax){ 
				rotXDir = 0;
			}
		} 
		else {
			rotX -= 0.0100000;
			if(rotX < rotXMin){
				rotXDir = 1;
			}
		}
	}

	void bounceBlocks(){
		// switch views if we've had enough

		currXInc = p.random(0.00000001f,70);
		currYInc = p.random(100, 5000f);
		switch_view_count = 0; 

		box_space = p.random(100,600); 
		currXInc = p.random(1,10);
		currYInc = p.random(1,10);

		newCamera();

	}

	void newCamera()
	{
		int randCamera = P.round( p.random( 0, 1 ) );
		int newZ = 40000;//round( p.random( -80000, -50000 ) );
		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 1000, 1000, newZ );
		else if( randCamera == 1 ) _curCamera = new CameraOscillate( p, 1000, 1000, newZ, 100 );
		_curCamera.reset();
	}


	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C') {
			pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V') {
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B') {
			newBoxSize();
		}
		if ( p.key == 'l' || p.key == 'L') {
			toggleStroke();
		}
		if ( p.key == 'f' || p.key == 'F') {
			newFollowObject();
		}
		if ( p.key == 'm' || p.key == 'M') {
			newMode();
		}
		if ( p.key == ' ') {
			bounceBlocks();
			_rotZinc = p.random( 0.00000002f, 0.00000003f );
		}
	}

	void newBoxSize() 
	{
		box_size = p.random( box_space / 3, box_space );
		_boxStretchMode = P.round( p.random( 0, 3 ) );
	}

	void toggleStroke()
	{
		if( _hasStroke == 0 )
		{
			_hasStroke = 1;
			p.stroke(0,0,0);
		}
		else
		{
			_hasStroke = 0;
			p.noStroke();
		}
		
		p.noStroke();
	}

	void newFollowObject()
	{
		_followRow = P.round( p.random( 0, gridSize - 1 ) );
		_followCol = P.round( p.random( 0, gridSize - 1 ) );
	}

	//  p.PIck new p.random colors
	void  pickNewColors()
	{
		_r = p.random( 0, 1 );
		_g = p.random( 0, 1 );
		_b = p.random( 0, 1 );
		_rMult = p.random( 1, 50 );
		_gMult = p.random( 1, 50 );
		_gMult = p.random( 1, 50 );
	}

	void newMode()
	{
		_hasStroke = P.round( p.random( 0, 1 ) );
		_curMode = P.round( p.random( 0, 3 ) );
		// p.println(_curMode);
		newBoxSize();
		_rotZinc = p.random( 0.00000002f, 0.000002f );
		newFollowObject();
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{
		/*
    if( isKickCount != 0 && isKickCount % 25 == 0 ) { bounceBlocks(); newBoxSize(); }
    if( isKickCount != 0 && isKickCount % 75 == 0 ) { newMode(); }
    if( isSnareCount != 0 && isSnareCount % 5 == 0 ) { newFollowObject(); }
    if( isHatCount != 0 && isHatCount % 10 == 0 ) {  pickNewColors(); }
		 */
	}

}



