package com.haxademic.app.haxvisual.viz.modules;

import processing.core.PConstants;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.app.haxvisual.viz.IVizModule;
import com.haxademic.app.haxvisual.viz.ModuleBase;
import com.haxademic.app.haxvisual.viz.modules.BlobSheet.Cell;
import com.haxademic.app.haxvisual.viz.modules.BlobSheet.ControlPoint;
import com.haxademic.core.cameras.CameraBasic;
import com.haxademic.core.cameras.CameraSpotter;
import com.haxademic.core.hardware.midi.MidiWrapper;

public class CubeStrand extends ModuleBase implements IVizModule {
	
	protected ToxiclibsSupport gfx;


	// class props
	int _numAverages = 3;

	int _curMode;
	final int MODE_DEFAULT = 0;
	float _r, _g, _b;

	// 2D Array of objects
	Cell[][] grid;


	// Number of columns and rows in the grid
	int cols = 50;
	int rows = 50;
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

	ControlPoint[] cntrls;
	int numCtrls = _numAverages;
	
	public CubeStrand()
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
		newSize();		
	}

	public void initAudio()
	{
		_audioData.setNumAverages( _numAverages );
		_audioData.setDampening( .13f );
	}

	public void focus() {
		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
		p.noStroke();
		newCamera();
		newSize();
	}

	public void update() {
		// clear screen
		p.background(0);
		
		// strt at center
		p.translate( p.width / 2, p.height / 2, 0 );

		// set initial world position based on keyboard-controlled vars
//		rotateX(radians(_rotX));
//		rotateY(radians(_rotY));
//		rotateZ(radians(_rotZ));
//		translate( _posX, _posY, _posZ );
		
		
		//rotateX(radians(40*sin( frameCount * 0.02f )));
		
		
		/*
		 Toxic test
		 */
		p.pushMatrix();
		p.translate( 0, 0, -5000 );
		
		Sphere ball;
		AABB cube;
		
		p.background(255);
		p.lights();
		p.noStroke();
		
		float curX = 0;
		float curY = 0;
		float curZ = 0;
		float curR = 240;
		float curG = 240;
		float curB = 240;
		float curSize = 200;
		for(int i=0; i < 20; i++) {
			float oldSize = curSize;
			// pick new size
			curSize -= 10;// = random(20,600);
			
			p.fill(curR,curG,curB);
			
			// translate to one side
			int rand = p.round( p.random(0,5) );
			if(rand == 0 || rand == 2) { curX += curSize + oldSize; curR -= p.random(10,30); curR = p.constrain(curR,0,255); }
			if(rand == 1 || rand == 4) { curX -= curSize + oldSize; curR -= p.random(10,30); curR = p.constrain(curR,0,255); }
			if(rand == 2 || rand == 1) { curY += curSize + oldSize; curG -= p.random(10,30); curG = p.constrain(curG,0,255); }
			if(rand == 3 || rand == 5) { curY -= curSize + oldSize; curG -= p.random(10,30); curG = p.constrain(curG,0,255); }
			if(rand == 4 || rand == 2) { curZ += curSize + oldSize; curB -= p.random(10,30); curB = p.constrain(curB,0,255); }
			if(rand == 5 || rand == 0) { curZ += curSize + oldSize; curB -= p.random(10,30); curB = p.constrain(curB,0,255); }
			
			cube = new AABB(new Vec3D(curX,curY,curZ),new Vec3D(curSize,curSize,curSize));
			gfx.box(cube);
			
			//ball = new Sphere(new Vec3D(curX,curY,curZ),curSize);
			//gfx.sphere(ball, 30);
			
		}
		
		p.popMatrix();


		_curCamera.update();
		p.rectMode(PConstants.CORNER);
		
		// draw different per mode
		switch( _curMode ){
		case MODE_DEFAULT :

			break;
		default :
			break;
		}
		
		
		// inrement the starting points
		startR += startIncR;
		curR = startR;
		startG += startIncG;
		curG = startG;
		startB += startIncB;
		curB = startB;

		for (int i = 0; i < numCtrls; i++) {
			cntrls[i].update( _audioData.getFFT().averages[i], _audioData.getFFT().averages[i] );
		}

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// Oscillate and display each object
				grid[i][j].oscillate( curR, curG, curB );

				Boolean follow = false;
				if( i == followRow && j == followCol ) follow = true;

				//grid[i][j].display( follow );

				curR += incR;
				curG += incG;
				curB += incB;
			}
		}
	}

	public void handleKeyboardInput()
	{
		if ( p.key == 'c' || p.key == 'C' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_01 ) == 1 ) {
			 pickNewColors();
		}
		if ( p.key == 'v' || p.key == 'V' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_02 ) == 1 ) {
			newCamera();
		}
		if ( p.key == 'b' || p.key == 'B' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_03 ) == 1 ) {
			newSize();
		}
		if ( p.key == 'f' || p.key == 'F' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_05 ) == 1 ) {
			newFollowObject();
		}
		if ( p.key == 'm' || p.key == 'M' || p.getMidi().midiPadIsOn( MidiWrapper.PAD_04 ) == 1 ) {
			pickNewColors();
			newCamera();
			newSize();
		}
	}

	//  p.PIck new p.random colors
	void  pickNewColors()
	{
		_r = p.random( 0, 1 );
		_g = p.random( 0, 1 );
		_b = p.random( 0, 1 );
	}

	void newSize()
	{
		sizeMult = p.random(.3f,.9f);
	}

	void newFollowObject()
	{
		followRow = p.round( p.random( 0, rows ) );
		followCol = p.round( p.random( 0, cols ) );
	}

	void newCamera()
	{
		int randCamera = p.round( p.random( 0, 1 ) );
		int newZ = p.round( p.random( -800, -600 ) );
		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 0, 0, newZ );
		else if( randCamera == 1 ) _curCamera = new CameraSpotter( p, 0, 0, newZ );
		//else if( randCamera == 2 ) _curCamera = new CameraOscillate( 0, 0, newZ/2, 100 );
		_curCamera.reset();
	}

	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
	{

	}
	
	
	
	
	

}
