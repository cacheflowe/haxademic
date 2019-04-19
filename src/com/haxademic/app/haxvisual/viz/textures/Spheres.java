//package com.haxademic.app.haxvisual.viz.textures;
//
//import com.haxademic.app.haxvisual.viz.IVizModule;
//import com.haxademic.app.haxvisual.viz.ModuleBase;
//import com.haxademic.core.draw.camera.CameraBasic;
//import com.haxademic.core.draw.camera.CameraOscillate;
//
//import processing.core.PConstants;
//
//public class Spheres 
//{
////	protected ToxiclibsSupport gfx;
//	// class props
//	int _numAverages = 512;
//	int _curMode;
//	int _numModes = 6;
//	final int MODE_DEFAULT = 0;
//	protected Boolean _is_wireframe_mode = false;
//	float _r, _g, _b;
//
//	// 2D Array of objects
//	Cell[] grid;
//
//
//	// Number of columns and rows in the grid
//	int numPoints = _numAverages;
//	int numPointRows;
//	float startPhi = 0;
//	float startR = p.random(0,2* p.PI);
//	float startIncR = p.random(.0001f,.05f);
//	float incR = p.random(.0001f,.005f);
//	float startG = p.random(0,2* p.PI);
//	float startIncG = p.random(.0001f,.05f);
//	float incG = p.random(.001f,.005f);
//	float startB = p.random(0,2* p.PI);
//	float startIncB = p.random(.0001f,.05f);
//	float incB = p.random(.001f,.005f);
//	int _followIndex;
//	float _radMultiplier = 1.5f;
//	float _masterAngle = 0;
//	
//	float _linesOuterAlpha = 0;
//	float _linesAlpha = 0;
//	
//	public Spheres()
//	{
//		super();
//		// store and init audio engine
//		initAudio();
//		
//		// init viz
//		init();
//	}
//
//	public void init() {
////		gfx = new ToxiclibsSupport(p);	
//		
//		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
//		p.noStroke();
//		newCamera();
//		
//		numPointRows = p.round( _numAverages / 50 );
//		// create cells
//		float boxW = p.width / numPoints;
//		float boxH = p.height / numPoints;
//		grid = new Cell[numPoints];
//		for (int i = 0; i < numPoints; i++) {
//			grid[i] = new Cell( boxW, boxH, i );
//		}
//
//	}
//
//	public void initAudio()
//	{
////		audioData.setNumAverages( _numAverages );
////		audioData.setDampening( .13f );
//	}
//
//	public void focus() {
//		p.colorMode( PConstants.RGB, 1, 1, 1, 1 );
//		initAudio();
//		p.noStroke();
//		pickMode();
//		newCamera();
//		newFollowObject();
//		
//	}
//
//	public void update() {
//		p.resetMatrix();
//		p.rectMode(PConstants.CENTER);
//		_curCamera.update();
//
//		p.background(0);
//		p.shininess(500); 
//		p.lights();
//		p.ambientLight(0.2f,0.2f,0.2f, 0, 0, 6000);
//		p.ambientLight(0.2f,0.2f,0.2f, 0, 0, -6000);
//		
//		// put it all in a huge cube
//	  	p.fill( 0.1f, 1 );
//	  	p.stroke( 1, _linesOuterAlpha );
//		p.strokeWeight(2);
//		p.sphere(10);
//		p.noStroke();
//		
//		// rotate the sphere - no push/pop since we resetMatrix() every frame... kinda weak
//		_masterAngle += 0.01f;
//		p.rotateX( _masterAngle * .1f );
//		
//		// fade lines back down
//		_linesAlpha = ( _linesAlpha > 0 ) ? _linesAlpha - 0.05f : 0;
//		_linesOuterAlpha = ( _linesOuterAlpha > 0.1f ) ? _linesOuterAlpha - 0.05f : 0.1f;
//		
//
//
//		// increment color starting points
//		startR += startIncR;
//		float curR = startR;
//		startG += startIncG;
//		float curG = startG;
//		startB += startIncB;
//		float curB = startB;
//		
//		// sphere coordinate vars
//		int u = 1;
//		float theta = 0;
//		float phi = 0;
//		float thetaIncrement = 1f;
//		float phiIncrement = startPhi;
//		float pointX = 0;
//		float pointY = 0;
//		float pointZ = 0;
//
//		// for #5
//		int pointInc = 1;
//		float deltaPhi =  p.PI/(numPoints-1);
//		float dia = p.sqrt(2 - 2*p.cos(deltaPhi));
//		float horz_stagger = 0;
//
//		if( _curMode == 5 ) numPoints = _numAverages / 80;
//		else if( _curMode == 0 || _curMode == 2 ) numPoints = _numAverages / 3;
//		else numPoints = _numAverages;
//		
//		Cell minus1Cell, minus2Cell;
//		
//		for( int i = 0; i < numPoints - 1; i++ ) {
//
//			// grab previous particles for line drawing
//			minus1Cell = ( i > 0 ) ? grid[i-1] : null;
//			minus2Cell = ( i > 1 ) ? grid[i-2] : null;
//
//			// decide whether to follow this point
//			Boolean follow = false;
//			if( i == _followIndex ) follow = true;
//
//			// move camera to follow
//			/*if( i == p.round(numPoints/2) )
//		      {
//		        _curCamera.setTarget( (int)modelX(0,0,0), (int)modelY(0,0,0), (int)modelZ(0,0,0) );
//		      }*/
//
//			// draw different per mode
//			switch( _curMode )
//			{
//				case MODE_DEFAULT :
//
//					phi = p.acos( -1f + ( 2f * i - 1f ) / numPoints );
//		     		theta = p.sqrt( numPoints * p.PI ) * phi;
//		     		pointX = _radMultiplier * p.cos(theta)* p.sin(phi);
//		     		pointY = _radMultiplier * p.sin(theta)* p.sin(phi);
//		     		pointZ = _radMultiplier * p.cos(phi);
//	
//					break;
//				case 1 :
//					//phi = ( i*1. / numPoints ) * (  p.PI * (startB * .1) );
//					phi = ( i*1f / numPoints ) * (  p.PI * 2f );
//					theta = p.sqrt(numPoints* p.PI)*phi;
//	
//					pointX = _radMultiplier * p.cos(theta)*p.sin(phi);
//					pointY = _radMultiplier * p.sin(theta)*p.cos(phi);
//					pointZ = _radMultiplier * p.sin(phi);
//	
//					break;
//				case 2 :
//					phi = ( i*1f / numPoints ) * (  p.PI * (startB * .05f) );
//					theta = p.sqrt(numPoints* p.PI)*phi;
//	
//					pointX = _radMultiplier * p.cos(theta)*p.sin(phi);
//					pointY = _radMultiplier * p.sin(theta)*p.cos(phi);
//					pointZ = _radMultiplier * p.sin(phi);
//	
//					break;
//				case 3 :
//					//phi = 0;//sin( startPhi ) * 1;
//					phi += phiIncrement;
//					theta += thetaIncrement;
//	
//					pointX = _radMultiplier * p.cos( p.sqrt( phi ) ) * p.cos( theta );
//					pointY = _radMultiplier * p.cos( p.sqrt( phi ) ) * p.sin( theta );
//					pointZ = _radMultiplier * p.sin( p.sqrt( phi ) );
//	
//					break;
//				case 4 :
//					//phi = 0;//sin( startPhi ) * 1;
//					phi += phiIncrement;
//					theta += thetaIncrement;
//	
//					pointX = _radMultiplier * p.cos( p.sqrt( phi ) ) * p.cos( theta );
//					pointY = _radMultiplier * p.cos( p.sqrt( phi ) ) * p.sin( theta );
//					pointZ = _radMultiplier * p.sin( p.sqrt( phi ) );
//	
//					break;
//				case 5 :
//					float myPhi = i * deltaPhi;
//					float rad = p.sin(myPhi);
//					float num_balls;
//	
//					if( 2f * p.pow(rad,2f) == 0) {
//						num_balls = 1;
//					} else if( p.acos((2f*p.pow(rad,2f)-p.pow(dia,2f))/(2f*p.pow(rad,2f))) == 0 ) {
//						num_balls = 1;
//					} else {
//						num_balls = p.floor(2f* p.PI / p.acos((2f*p.pow(rad,2f)-p.pow(dia,2f))/(2*p.pow(rad,2f))));
//					}
//	
//					float theta_inc = 2f *  p.PI / num_balls;
//	
//					for( int m = 0; m < num_balls; m++ )
//					{	
//						float myTheta = m * theta_inc + horz_stagger;
//	
//						pointX = rad*_radMultiplier * p.sin(myTheta);
//						pointY = p.cos(myPhi)*_radMultiplier;
//						pointZ = rad*_radMultiplier * p.cos(myTheta);
//	
//						pointInc = pointInc + 1;
//	
//						curR += incR;
//						curG += incG;
//						curB += incB;
//	
//						if( i == (int)( _followIndex / 50 )-1 && m == 0 ) follow = true;
//						else follow = false;
//						
//						if( pointInc < _numAverages )
//						{
//							grid[pointInc].oscillate( curR, curG, curB );
//							grid[pointInc].setPosition( pointX, pointY, pointZ, 400  + p.audioFreq(pointInc)*1300, follow, minus1Cell, minus2Cell );
//						}
//					}
//	
//					break;
//			}
//
//			// post-sphere placement incrementing
//			if( _curMode == 3 ) startPhi += .0000001;
//			if( _curMode == 4 ) startPhi += .0000001;
//
//			if( _curMode != 5 ) {
//				// cycle colors
//				grid[i].oscillate( curR, curG, curB );
//				grid[i].setPosition( pointX, pointY, pointZ, 400  + p.audioFreq(i)*250, follow, minus1Cell, minus2Cell );
//								
//				curR += incR;
//				curG += incG;
//				curB += incB;
//			}
//		}
//	}
//
//	public void handleKeyboardInput()
//	{
//		if ( p.key == 'm' || p.key == 'M') {
//			pickMode();
//		}
//		if ( p.key == 'v' || p.key == 'V') {
//			newCamera();
//		}
//		if ( p.key == 'c' || p.key == 'C') {
//			pickNewColors();
//		}
//		if ( p.key == 'l' || p.key == 'L') {
//			newLineMode();
//		}
//		if ( p.key == 'f' || p.key == 'F') {
//			newFollowObject();
//		}
//	}
//
//	//  p.PIck new p.random colors
//	void pickNewColors()
//	{
//		_r = p.random( 0, .2f );
//		_g = p.random( 0, .2f );
//		_b = p.random( 0, .2f );
//		startR = p.random( 0, 100 );
//		startG = p.random( 0, 100 );
//		startB = p.random( 0, 100 );
//		startIncR = p.random(.0001f,.05f);
//		startIncG = p.random(.0001f,.05f);
//		startIncB = p.random(.0001f,.05f);
//		
//		_is_wireframe_mode = ( p.random(0,1) > 0.7 ) ? true : false;
//	}
//
//	//  p.PIck a p.random mode
//	void pickMode()
//	{
//		_radMultiplier = p.random(6f, 8.5f);
//		_curMode = p.round( p.random( 0, 5 ) );
//		//_curMode = 0;
////		p.println("new mode = "+_curMode);
//		newCamera();
//	}
//
//	void newFollowObject()
//	{
//		_followIndex = p.round( p.random( 0, numPoints - 1 ) );
//	}
//	
//	void newLineMode() {
//		_linesOuterAlpha = 0.6f;
//		_linesAlpha = 0.6f;
//	}
//
//	void newCamera()
//	{
//		int randCamera = p.round( p.random( 0, 1 ) );
//		int newZ = p.round( p.random( 0, 200 ) );
//		if( randCamera == 0 ) _curCamera = new CameraBasic( p, 0, 0, newZ );
//		else if( randCamera == 1 ) _curCamera = new CameraOscillate( p, 0, 0, newZ, 1000 );
//		_curCamera.reset();
//	}
//
//	public void beatDetect( int isKickCount, int isSnareCount, int isHatCount, int isOnsetCount )
//	{
//
//	}
//
//	// A Cell object
//	class Cell {
//		// A cell object knows about its location in the grid as well as its size with the variables x,y,w,h.
//		float x,y,z;   // 3d location
//		float w,h;   // p.width and p.height
//		float angle; // angle for oscillating brightness
//		float r,g,b;
//
//		// Cell Constructor
//		Cell(float tempW, float tempH, float tempAngle) {
//			w = tempW;
//			h = tempH;
//			angle = 0;
//
//		} 
//
//		// Oscillation means increase angle
//		void oscillate( float redColor, float greenColor, float blueColor ) {
//			r = redColor;
//			g = greenColor;
//			b = blueColor;
//		}
//
//		void setPosition( float newX, float newY, float newZ, float amp, Boolean follow, Cell minus1cell, Cell minus2cell ) {
//			
//			// get color
//			int cellColor = p.color( 1+.3f*p.sin(r), 1+.3f*p.sin(g), 1+.3f*p.cos(b) );
//			p.fill(cellColor);
//			
//			// use brightness to push radius out
//			float brightAdjust = 1 + p.brightness( cellColor ) * .75f;
//			brightAdjust = 1.5f;
//			x = newX * amp * brightAdjust;
//			y = newY * amp * brightAdjust;
//			z = newZ * amp * brightAdjust;
//			
//			// set camera to follow this particle 
//			if( follow == true ) _curCamera.setTarget( (int)x, (int)y, (int)z );
//
//			// use EQ amplitude to 
//			float ampSizeMultiplier = ( amp - 400 ) * .9f;
//			
//			// draw line to previous sphere point
//			p.stroke(cellColor, _linesAlpha);
//			p.strokeWeight(1*ampSizeMultiplier*.1f);
//			if( minus1cell != null && minus2cell != null && _linesAlpha > 0 ) {
//				p.line(x, y, z, minus1cell.x, minus1cell.y, minus1cell.z);				
//				p.line(x, y, z, minus2cell.x, minus2cell.y, minus2cell.z);			
//			}
//
//			p.pushMatrix();
//			//p.rect(x,y,w*ampSizeMultiplier,h*ampSizeMultiplier); 
//			
//			// draw shape at sphere point
//			p.translate( x, y, z );
//			if( _is_wireframe_mode == true ) {
//				p.stroke(cellColor);
//				p.noFill();
//			} else {
//				p.fill(cellColor);
//				p.noStroke();
//			}
//			float size = 50 * ampSizeMultiplier * .1f;
//			p.box(size);
////			gfx.sphere(new Sphere(new Vec3D(0,0,0),size), 10);
//
//			
//			
//			p.popMatrix();
//		}
//	}
//
//}