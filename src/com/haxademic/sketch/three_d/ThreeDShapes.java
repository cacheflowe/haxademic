package com.haxademic.sketch.three_d;

import processing.core.PApplet;
import processing.core.PConstants;
import toxi.geom.AABB;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.core.app.P;
/*
-d32
-Xmx1024M
-Xms1024M
*/

public class ThreeDShapes
	extends PApplet
{
	// global vars
	protected int _fps = 30;
	
	// positioning vars
	float _rotX = 0;
	float _rotY = 0;
	float _rotZ = 0;
	float _posX = 0;
	float _posY = 0;
	float _posZ = 0;
	
	ToxiclibsSupport gfx;

	public void setup ()
	{
		// set up stage and drawing properties
		size( displayWidth, displayHeight, P.OPENGL );				//size(screen.width,screen.height,P3D);
		//size( 800, 600, PConstants.OPENGL );				//size(screen.width,screen.height,P3D);
		frameRate( _fps );
		colorMode( PConstants.RGB, 255, 255, 255, 255 );
		background( 0 );
		shininess(1000); 
		lights();
		noStroke();
		//noLoop();
		gfx = new ToxiclibsSupport(this);
	}

	public void draw() 
	{
		//drawStars();
		drawDiscs();
		
	}
	
	protected void drawDiscs()
	{
		// clear screen
		background(0);
		
		// strt at center
		translate( width / 2, height / 2, 0 );

		// set initial world position based on keyboard-controlled vars
		rotateX(radians(_rotX));
		rotateY(radians(_rotY));
		rotateZ(radians(_rotZ));
		translate( _posX, _posY, _posZ );
		
		
		//rotateX(radians(40*sin( frameCount * 0.02f )));
		
		
		/*
		 Toxic test
		 */
//		background(0);
		pushMatrix();
//		rotateY(45);
		translate( 1000, 0, -6000 );
		
		Sphere ball;
		AABB cube;
		
		lights();
		noStroke();
		
		float curX = 0;
		float curY = 0;
		float curZ = 0;
		float curR = 240;
		float curG = 240;
		float curB = 240;
		float curSize = 300;
		int rot = 0;
		for(int i=0; i < 20; i++) {
			float oldSize = curSize;
			// pick new size
			curSize -= 10;// = random(20,600);
			
			fill(curR,curG,curB);
			
			// translate to one side
			int rand = round( random(0,5) );
			if(rand == 0 || rand == 2) { curX += curSize + oldSize; curR -= random(3,12); curR = constrain(curR,0,255); }
			if(rand == 1 || rand == 4) { curX -= curSize + oldSize; curR -= random(3,12); curR = constrain(curR,0,255); }
			if(rand == 2 || rand == 1) { curY += curSize + oldSize; curG -= random(3,12); curG = constrain(curG,0,255); }
			if(rand == 3 || rand == 5) { curY -= curSize + oldSize; curG -= random(3,12); curG = constrain(curG,0,255); }
			if(rand == 4 || rand == 2) { curZ += curSize + oldSize; curB -= random(3,12); curB = constrain(curB,0,255); }
			if(rand == 5 || rand == 0) { curZ += curSize + oldSize; curB -= random(3,12); curB = constrain(curB,0,255); }
			
			pushMatrix();
			//rotateZ(rot);
			//cube = new AABB(new Vec3D(curX,curY,curZ),new Vec3D(curSize,curSize,curSize));
			//gfx.box(cube);
			ball = new Sphere(new Vec3D(curX,curY,curZ),curSize);
			gfx.sphere(ball, (int)curSize);
			
			
			for(int j=0; j < 8; j++) {
				//cube = new AABB(new Vec3D(curX+random(-curSize*2,curSize*2),curY+random(-curSize*2,curSize*2),curZ+random(-curSize*2,curSize*2)),new Vec3D((int)(curSize/random(2,7)),(int)(curSize/random(2,7)),(int)(curSize/random(2,7))));
				//gfx.box(cube);
				ball = new Sphere(new Vec3D(curX+random(-curSize*2,curSize*2),curY+random(-curSize*2,curSize*2),curZ+random(-curSize*2,curSize*2)),curSize/random(2,7));
				gfx.sphere(ball, (int)(curSize/random(2,7)));
			}
			popMatrix();
			
			rot += 1;
		}
		
		popMatrix();
		
		
		/*
		// CACHEFLOWE		
		float scale = 1.1f + sin( frameCount * 0.01f );
		float thickness = 100 + 50 * cos( frameCount * 0.03f );
		int discPrecision = 100;
		
		if( frameCount < 100 ) scale *= frameCount / 100f;
		
		int c1 = color(255, 249, 0, 255);
		int c2 = color(235, 229, 0, 255);
		//stroke(255);
		
		// "c" radii : 25, 55   outer ring radii: 89, 118
		drawCacheFloweLogo( scale, thickness, c1, c2 );		
		
		int outerDiscRadius = 29;
		int outerDiscStartRadius = 89;

		int discSpacing = 190;
		for( int i = 1; i < 10; i++ )
		{
			int innerRadius = ( outerDiscStartRadius + discSpacing * i );
			drawDisc3D( innerRadius * scale, ( innerRadius + outerDiscRadius ) * scale, thickness, discPrecision, c1, c2 );
		}
		*/
		
		//drawDisc3D( (outerDiscStartRadius+outerDiscRadius+discSpacing*2)*scale, outerDiscStartRadius*scale+discSpacing*2, 50, 50, c1, c2 );
		
		
		
		
		/*
		// CONE SHAPE
		fill(255, 150, 150);
		
		int numPoints = 6;
		float segmentCircumference = (2f*PI) / numPoints;
		int radius = 100;
		beginShape(TRIANGLES);
		for( int i = 0; i < numPoints; i++ )
		{
			vertex( 0, 0, 0 );
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, 200 );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, 200 );
		}
		endShape();
		*/
		
		// DISC TUNNEL
		
		// set un angle to render
//		rotateX(radians(-5));
//		rotateY(radians(90));
//		rotateZ(radians(5));
//		translate( 50, 0, 370 );
		
		/*
		// tunnel
		for( int i = 0; i < 30; i++ )
		{
			
			fill(140 + 5*i,255,0);
			pushMatrix();
			translate(0,0,400 - i*50);
			//rotateY(radians(90));
			float radius = 600 - i * 20;
			drawDisc( radius, radius / 2, 75 );
			popMatrix();

		}
		*/
		
		// CHOPPED CONCENTRIC TUBES
		/*
		background(100,50,50);

		// chopped concentric tubes
		rotateX(radians(-10));
		rotateY(radians(10));
		rotateZ(radians(-90));
		translate( -140, 40, -250 );

		fill( 200, 100, 100 );
		pushMatrix();
		rotateY(radians(90));
		rect( -2000, -2000, 4000, 4000 );
		popMatrix();
		
		for( int i = 0; i < 10; i++ )
		{
			
			fill( 200 );
			pushMatrix();
			drawDisc3D( (i+1)*50, (i+1)*50 - 10, 1000 - i*100 , 75 );
			popMatrix();

		}
		*/
		
		/*
		// PYRAMID CLUSTER
		
		background(50, 15, 25);
		
		rotateX(radians(50.0f));
		rotateY(radians(0.0f));
		rotateZ(radians(-45.0f));
		translate( 0.0f, 0.0f, 0.0f );
		
		for( int i = 0; i < 50; i++ )
		{
			fill( random(200,240), random(40,80), random(100,140) );
	
			pushMatrix();
			translate( random(-300,300), random(-300,300), random(-100,100) );
			drawPyramid( random(10f,250f), (int)random(20,130) );
			popMatrix();
		}
		*/
		
		
		
		// CUBE CLUSTER - THROW YA BOXES UP
		/*
		//noLoop();
		background(40, 70, 50);
		
		rotateX(radians(0.0f));
		rotateY(radians(-50.0f));
		rotateZ(radians(5.0f));
		translate( 445.0f, 0.0f, -175.0f );
		
		for( int i = 0; i < 100; i++ )
		{
			fill( random(70,140), random(170,250), random(70,210) );
	
			pushMatrix();
			
			float randX = random(-2000,-100);
			float randY = random(-100,100);
			float randZ = random(-50,50);
			translate( randX, randY, randZ );
			
			box( random(5f,700f), random(50f,110f), random(50f,210f) );
			
			popMatrix();
		}
		*/
		
		
		/*
		// SMALL PYRAMID CLUSTER 
		 
		background(0,40,0);
		
		if( _rotX == 0 && _rotY == 0 & _rotZ == 0 ) {
			_rotX = 70.0f;
			_rotY = 0.0f;
			_rotZ = -55.0f;
			_posX = 10.0f;
			_posY = 25.0f;
			_posZ = 65.0f;
		}

		fill( 100, 200, 100 );
		pushMatrix();
		rect( -1000, -1000, 2000, 2000 );
		popMatrix();
		
		int pyraSize = 100;
		int numPyras = 6;
		
		for( int i = 0; i < numPyras; i++ )
		{
			for( int j = 0; j < numPyras; j++ )
			{
				fill( random(40,80), random(200,240), random(100,140) );
		
				pushMatrix();
//				translate( -(pyraSize*numPyras)/2 + (i*pyraSize), -(pyraSize*numPyras)/2 + (j*pyraSize), 0 );
//				drawPyramid( random(50f,250f), 20 );
				
				rotateZ(radians( random(0,360) ));
				translate( random( -(pyraSize*numPyras)/2, (pyraSize*numPyras)/2 ), random( -(pyraSize*numPyras)/2, (pyraSize*numPyras)/2 ), 0 );
				drawPyramid( random(50f,250f), (int)random(20,250) );
				
				
				popMatrix();
			}
		}
		*/
		
	}
	
	protected void drawPyramid( float shapeHeight, int baseWidth )
	{
		baseWidth *= HALF_PI;
		
		pushMatrix();
		rotateZ(radians(-45.0f));
		beginShape(TRIANGLES);
		
		int numSides = 4;
		float segmentCircumference = (2f*PI) / numSides;
		float halfBaseW = baseWidth / 2;

		for( int i = 0; i < numSides; i++ )
		{
			vertex( 0, 0, shapeHeight );
			vertex( sin( i * segmentCircumference ) * halfBaseW, cos( i * segmentCircumference ) * halfBaseW, 0 );
			vertex( sin( (i + 1) * segmentCircumference ) * halfBaseW, cos( (i + 1) * segmentCircumference ) * halfBaseW, 0 );
		}
		
		// base
		vertex( sin( 0 * segmentCircumference ) * halfBaseW, cos( 0 * segmentCircumference ) * halfBaseW, 0 );
		vertex( sin( 1 * segmentCircumference ) * halfBaseW, cos( 1 * segmentCircumference ) * halfBaseW, 0 );
		vertex( sin( 2 * segmentCircumference ) * halfBaseW, cos( 2 * segmentCircumference ) * halfBaseW, 0 );

		vertex( sin( 2 * segmentCircumference ) * halfBaseW, cos( 2 * segmentCircumference ) * halfBaseW, 0 );
		vertex( sin( 3 * segmentCircumference ) * halfBaseW, cos( 3 * segmentCircumference ) * halfBaseW, 0 );
		vertex( sin( 0 * segmentCircumference ) * halfBaseW, cos( 0 * segmentCircumference ) * halfBaseW, 0 );

		endShape();
		popMatrix();
	}
	
	protected void drawCacheFloweLogo( float scale, float thickness, int c1, int c2 )
	{
		// outer disc
		int outerDiscRadius = 29;
		int outerDiscStartRadius = 89;
		int centPrecision = 25;
		int discPrecision = 50;

		drawCent3D( scale, thickness, centPrecision, c1, c2 );
		drawDisc3D( ( outerDiscStartRadius + outerDiscRadius ) * scale, outerDiscStartRadius * scale, thickness, discPrecision, c1, c2 );

	}
	
	protected void drawCent3D( float scale, float cylinderHeight, int numSegments, int color, int wallcolor )
	{
		// draw triangles
		beginShape(TRIANGLES);
		
		// 87.5% around for "C"
		float segmentCircumference = ( 0.875f * (2f*PI) ) / numSegments;
		float halfHeight = cylinderHeight / 2;
		
		float knobCenterY = 65 * scale;
		float knobWidth = 28 * scale;
		float halfKnobWidth = 14 * scale;
		float outerRadius = 55 * scale;
		float innerRadius = 25 * scale;
		
		for( int j = 0; j < numSegments; j++ )
		{
			// start from magic rotation number
			float i = j + 8.911f;

			// set fill color
			fill( color );
			
			// top disc
			vertex( sin( i * segmentCircumference ) * innerRadius, 	cos( i * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( i * segmentCircumference ) * outerRadius, 		cos( i * segmentCircumference ) * outerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			
			vertex( sin( i * segmentCircumference ) * innerRadius, 			cos( i * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, 	cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, 		cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			
			// bottom disc
			vertex( sin( i * segmentCircumference ) * innerRadius, 	cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( i * segmentCircumference ) * outerRadius, 		cos( i * segmentCircumference ) * outerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
			
			vertex( sin( i * segmentCircumference ) * innerRadius, 			cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, 	cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, 		cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
			
			
			// set fill color
			fill( wallcolor );
			
			// outer wall
			vertex( sin( i * segmentCircumference ) * outerRadius, cos( i * segmentCircumference ) * outerRadius, halfHeight );
			vertex( sin( i * segmentCircumference ) * outerRadius, cos( i * segmentCircumference ) * outerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			
			vertex( sin( i * segmentCircumference ) * outerRadius, cos( i * segmentCircumference ) * outerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
			
			// inner wall
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			
			
			// handle disc ends' extra segment, from same inner radius point
			if( j == 0 || j == numSegments - 1 )
			{
				if( j == 0 )
				{
					// extra triangle at end
					fill( color );
					
					// top disc
					vertex( sin( i * segmentCircumference ) * innerRadius, 		 cos( i * segmentCircumference ) * innerRadius, halfHeight );
					vertex( sin( i * segmentCircumference ) * outerRadius, 		 cos( i * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					// bottom disc
					vertex( sin( i * segmentCircumference ) * innerRadius, 		 cos( i * segmentCircumference ) * innerRadius, -halfHeight );
					vertex( sin( i * segmentCircumference ) * outerRadius, 		 cos( i * segmentCircumference ) * outerRadius, -halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );
					
					fill( wallcolor );
					
					// draw outer wall for extra triangle
					vertex( sin( i * segmentCircumference ) * outerRadius, 		 cos( i * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( i * segmentCircumference ) * outerRadius, 		 cos( i * segmentCircumference ) * outerRadius, -halfHeight );
					
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( i * segmentCircumference ) * outerRadius, 		 cos( i * segmentCircumference ) * outerRadius, -halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );

					// draw "C" end cap
					vertex( sin( i * segmentCircumference ) * innerRadius, 		 cos( i * segmentCircumference ) * innerRadius, halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );
					
					vertex( sin( i * segmentCircumference ) * innerRadius, 		 cos( i * segmentCircumference ) * innerRadius, halfHeight );
					vertex( sin( i * segmentCircumference ) * innerRadius, 		 cos( i * segmentCircumference ) * innerRadius, -halfHeight );
					vertex( sin( (i - 1) * segmentCircumference ) * outerRadius, cos( (i - 1) * segmentCircumference ) * outerRadius, -halfHeight );
				}
				if( j == numSegments - 1 )
				{
					// extra triangle at end
					fill( color );
					
					// top disc
					vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
					vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, halfHeight );
					// bottom disc
					vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
					vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );

					fill( wallcolor );
					
					// draw outer wall for extra triangle
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					
					vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					vertex( sin( (i + 1) * segmentCircumference ) * outerRadius, cos( (i + 1) * segmentCircumference ) * outerRadius, -halfHeight );
					
					// draw "C" end cap
					vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					
					vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
					vertex( sin( (i + 2) * segmentCircumference ) * outerRadius, cos( (i + 2) * segmentCircumference ) * outerRadius, -halfHeight );
					vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				}
			}			
		}
		
		endShape();
		

		// "C" top - move to center of block
		pushMatrix();
		translate( 0, -knobCenterY, 0 );
		
		// front and back panel
		fill( color );
		pushMatrix();
		translate( 0, 0, halfHeight );
		rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		translate( 0, 0, -cylinderHeight );
		rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		popMatrix();
		
		// side panels
		fill( wallcolor );

		pushMatrix();
		rotateY( radians(90.0f) );
		translate( 0, 0, -halfKnobWidth );
		rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		translate( 0, 0, knobWidth );
		rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		popMatrix();
		
		// top cap
		pushMatrix();
		rotateX( radians(90.0f) );
		translate( 0, 0, halfKnobWidth );
		rect( -halfKnobWidth, -halfHeight, knobWidth, cylinderHeight );
		popMatrix();

		popMatrix();
		
		
		// "C" bottom - move to center of block
		pushMatrix();
		translate( 0, knobCenterY, 0 );
		
		// front and back panel
		fill( color );
		pushMatrix();
		translate( 0, 0, halfHeight );
		rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		translate( 0, 0, -cylinderHeight );
		rect( -halfKnobWidth, -halfKnobWidth, knobWidth, knobWidth );
		popMatrix();
		
		// side panels
		fill( wallcolor );

		pushMatrix();
		rotateY( radians(90.0f) );
		translate( 0, 0, -halfKnobWidth );
		rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		translate( 0, 0, knobWidth );
		rect( -halfHeight, -halfKnobWidth, cylinderHeight, knobWidth );
		popMatrix();
		
		// top cap
		pushMatrix();
		rotateX( radians(-90.0f) );
		translate( 0, 0, halfKnobWidth );
		rect( -halfKnobWidth, -halfHeight, knobWidth, cylinderHeight );
		popMatrix();

		popMatrix();
	}
	
	protected void drawDisc3D( float radius, float innerRadius, float cylinderHeight, int numSegments, int color, int wallcolor )
	{
		// draw triangles
		beginShape(TRIANGLES);
		
		float segmentCircumference = (2f*PI) / numSegments;
		float halfHeight = cylinderHeight / 2;

		for( int i = 0; i < numSegments; i++ )
		{
			fill( color );

			// top disc
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			// bottom disc
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			fill( wallcolor );
			// outer wall
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, halfHeight );
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius, -halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, halfHeight );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius, -halfHeight );
			
			// only draw inner radius if needed
			if( innerRadius > 0 )
			{
				fill(wallcolor);
				// inner wall
				vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, halfHeight );
				vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
				vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				
				vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius, -halfHeight );
				vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, halfHeight );
				vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius, -halfHeight );
			}
		}
		
		endShape();
	}
	
	protected void drawDisc( float radius, float innerRadius, int numSegments )
	{
		// draw triangles
		beginShape(TRIANGLES);
		
		for( int i = 0; i < numSegments; i++ )
		{
			float segmentCircumference = (2f*PI) / numSegments;
			
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius );
			vertex( sin( i * segmentCircumference ) * radius, cos( i * segmentCircumference ) * radius );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius );
			
			vertex( sin( i * segmentCircumference ) * innerRadius, cos( i * segmentCircumference ) * innerRadius );
			vertex( sin( (i + 1) * segmentCircumference ) * innerRadius, cos( (i + 1) * segmentCircumference ) * innerRadius );
			vertex( sin( (i + 1) * segmentCircumference ) * radius, cos( (i + 1) * segmentCircumference ) * radius );
		}
		
		endShape();
	}
	
	protected void drawStars()
	{
		
		//background(0.8f);
		
		translate( 80, -20, 250 );
		rotateX(radians(15));
		rotateZ(radians(20));
//		rotateY(radians(10));
		
		pushMatrix();
		translate( 0, 0, 5 );
		fill(200);
		rect( -100,-100,width+200,height+200);
		popMatrix();

		int numStars = 10;
		for( int i = 0; i < numStars; i++ )
		{
			pushMatrix();
			translate( ( width / numStars ) * i, ( height / numStars ) * i, 2 );
			//rotateX(1);
			rotateZ(100);
			float b = random( 20, 105 );
			float r = random( 100, 255 );
			float g = random( 100, 205 );
			fill(r, g, b);
			star( 5, 150, .5f, 30, 20);
			popMatrix();
		}

	}
	
	/**
	 * Key handling for rendering functions - stopping and saving an image
	 */
	public void keyPressed()
	{
		if( key == '1' ) _rotX -= 5;
		if( key == '9' ) _rotX += 5;
		if( key == '4' ) _rotY -= 5;
		if( key == '6' ) _rotY += 5;
		if( key == '2' ) _rotZ -= 5;
		if( key == '8' ) _rotZ += 5;
		if( key == 'a' ) _posX -= 5;
		if( key == 'd' ) _posX += 5;
		if( key == 'q' ) _posY -= 5;
		if( key == 'e' ) _posY += 5;
		if( key == 'w' ) _posZ -= 5;
		if( key == 's' ) _posZ += 5;
		
		
		println("rotateX(radians("+_rotX+"));");
		println("rotateY(radians("+_rotY+"));");
		println("rotateZ(radians("+_rotZ+"));");
		println("translate( "+_posX+", "+_posY+", "+_posZ+" );");

	}  
	
	public void mouseClicked()
	{
		
	}
	
	
	void star(float spikes, float outerrad, float innerradpercent, float h, float rot)
	{
		int pi, pj;
		float futil;
		beginShape(TRIANGLE_STRIP);
		for(pi=0;pi<spikes+1;pi++)
		{
		    vertex(0,0,h/2);
		    futil=(pi/spikes)  * TWO_PI;    //current angle 
		    vertex(  cos(futil+rot)*outerrad, sin(futil+rot)*outerrad, 0);
		    futil=futil+ (  (1/spikes)/2 *TWO_PI  );
		    vertex(  cos(futil+rot)*outerrad*innerradpercent, sin(futil+rot)*outerrad*innerradpercent, 0);
		}
		endShape();
		beginShape(TRIANGLE_STRIP);
		for(pi=0;pi<spikes+1;pi++)
		{
			vertex(0,0,-h/2);
			futil=(pi/spikes)  * TWO_PI;    //current angle 
			vertex(  cos(futil+rot)*outerrad, sin(futil+rot)*outerrad, 0);
			futil=futil+ (  (1/spikes)/2 *TWO_PI  );
			vertex(  cos(futil+rot)*outerrad*innerradpercent, sin(futil+rot)*outerrad*innerradpercent, 0);
		}
		endShape();	  
	}
	
	
	/*
	void cache(float outrad, float rot, float sides, float zshift){
		 float h=.15f*outrad;
		 float inrad=.9f*outrad;
		 float sweep=.88f;

		 float i,j,x1,y1,x2,y2,x3,y3,x4,y4,w;
		 float currentangle, nextangle;
		 float ycomp1, xcomp1, ycomp2, xcomp2;
		float hh=h/2.f;
		 float cap1x=cost(rot+HALF_PI);
		 float cap1y=sint(rot+HALF_PI);

		 float cap2x=cost(HALF_PI+rot+sweep*TWO_PI);
		 float cap2y=sint(HALF_PI+rot+sweep*TWO_PI);

		   pushMatrix();
		   translate(0,0,zshift);

		 for( i=0;i<(sides);i++){

		   currentangle=i/sides*TWO_PI*sweep;
		   nextangle=(i+1.f)/sides*TWO_PI*sweep;

		   xcomp1=cos(currentangle+rot);
		   ycomp1=sin(currentangle+rot);
		   xcomp2=cos(nextangle+rot);
		   ycomp2=sin(nextangle+rot);

		   x1=xcomp1*outrad;
		   y1=ycomp1*outrad;

		   x2=xcomp2*outrad;
		   y2=ycomp2*outrad;

		   x3=xcomp2*inrad;
		   y3=ycomp2*inrad;

		   x4=xcomp1*inrad;
		   y4=ycomp1*inrad;


		   beginShape(QUADS);
		   //texture(t[tex]);
		   normal(xcomp1,ycomp1,0);
		   vertex(x1,y1,-h/2);        //outter edge tall part
		   normal(xcomp2,ycomp2,0);
		   vertex(x2,y2,-h/2);
		   normal(xcomp2,ycomp2,0);
		   vertex(x2,y2,h/2);
		   normal(xcomp1,ycomp1,0);
		   vertex(x1,y1,h/2);
		   endShape();

		   beginShape(QUADS);
//		    texture(t[tex]);
		   normal(-xcomp2,-ycomp2,0);
		   vertex(x3,y3,-hh);      //inner edge tall part
		   normal(-xcomp1,-ycomp1,0);
		   vertex(x4,y4,-hh);
		   normal(-xcomp1,-ycomp1,0);
		   vertex(x4,y4,hh);
		   normal(-xcomp2,-ycomp2,0);
		   vertex(x3,y3,hh);
		   endShape();

		   beginShape(QUADS);
		 //  texture(t[tex]);
		   normal(0,0,1);
		   vertex(x1,y1,hh );      // top "c"
		   vertex(x2,y2,hh);
		   vertex(x3,y3,hh );
		   vertex(x4,y4,hh);

		   normal(0,0,-1);
		   vertex(x1,y1,-hh );      // bottom "c"
		   vertex(x2,y2,-hh);
		   vertex(x3,y3,-hh );
		   vertex(x4,y4,-hh);

		   if(int(i)>=sides-1){       //draw ONLY the cap on the last segment.
		     normal(cap2x,cap2y,0);
		     vertex(x2,y2,-hh);
		     vertex(x3,y3,-hh);
		     vertex(x3,y3,hh);
		     vertex(x2,y2,hh);
		   }


		     if(i==0){                    //back cap, only on first segment

		        normal(cap1x,cap1y,0);
		        vertex(x1,y1,-hh);
		       vertex(x4,y4,-hh);
		       vertex(x4,y4,hh);
		       vertex(x1,y1,hh);
		     }
		           endShape();
		   }

		//draw little boxxy knobs for cacheflowe logo
		for(int v=0;v<2;v++){
		 w=PI/32.;    //width of knob
		 currentangle=sweep*PI-HALF_PI-w+rot;
		 nextangle=sweep*PI-HALF_PI+w+rot;
		 x1=cos(currentangle)*outrad;
		 y1=sin(currentangle)*outrad;
		 x2=cos(nextangle)*outrad;
		 y2=sin(nextangle)*outrad;

		 x3=x1*1.1;
		 y3=y1*1.1;
		 x4=x2*1.1;
		 y4=y2*1.1;

		 beginShape(QUAD_STRIP);
		 vertex(x1,y1,hh);
		 vertex(x3,y3,hh);
		 vertex(x2,y2,hh);
		 vertex(x4,y4,hh);

		 vertex(x2,y2,-hh);
		 vertex(x4,y4,-hh);

		 vertex(x1,y1,-hh);
		 vertex(x3,y3,-hh);

		 vertex(x1,y1,hh);
		 vertex(x3,y3,hh);
		 endShape();
		 beginShape(QUADS);

		 vertex(x4,y4,hh);
		 vertex(x3,y3,hh);
		 vertex(x3,y3,-hh);
		 vertex(x4,y4,-hh);


		 endShape();
		 rot+=PI;
		}


		 popMatrix();
		}
	*/
	
	
	
	

////////Sin tables
//	float sint(float in){
//		in+=HALF_PI;
//		return cost(in);  
//	}
//
//	float cost(float in)
//	{
//	  if(in>=0) return(costable[int(in*indexfactor)%3600]);
//	  else return(costable[int(-in*indexfactor)%3600]);
//	  
//	}
//
//	void makecos(){
//	 float i;
//	 indexfactor=180./PI*10.;
//	 for(i=0;i<3600;i++){
//	   costable[int(i)]=cos(i/3600.f*TWO_PI);
//	 } 
//	  
//	  
//	  
//	}
	
	
}





