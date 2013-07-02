package com.haxademic.sketch.three_d;

import processing.core.PVector;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.TColorInit;
import com.haxademic.core.draw.util.DrawUtil;

@SuppressWarnings("serial")
public class BoxConnect
extends PAppletHax  
{

	public void setup() {
		super.setup();

		// Sunflow needs to use colors between 0-1. Default processing color mode is 0-255. 
		// TColor likes the former, so we normalize with TColorInit, depending on the rendering mode.
		// This way we can always use normal 0-255 RGB color blending in either case.
		if( _graphicsMode == P.OPENGL ) {
			p.colorMode( P.RGB, 1f, 1f, 1f, 1f );
		} else {
			p.colorMode( P.RGB, 255f, 255f, 255f, 255f );
		}

		// for sunflow, we need to set these before the first draw()
		p.background( 0 );
		p.smooth();
		p.noStroke();
	}

	protected void overridePropsFile() {
		_appConfig.setProperty( "sunflow", "false" );
		_appConfig.setProperty( "rendering", "false" );
	}

	public void drawApp() {

		DrawUtil.setBasicLights( p );
		if( _graphicsMode == P.OPENGL ) p.background(0,0,0,255);

		p.translate(p.width/2, p.height/2, 500);
		// rotateY(frameCount/40f);

		p.noStroke();
		p.fill( TColorInit.newRGBA( 100f, 200f, 134f, 255f ).toARGB() );
		
		
		// spiral
		float segments = 20;
		float radius = 300 * (float) Math.sin(frameCount/90f);
		float inc = P.TWO_PI / segments;
		float zInc = 0;
		float zIncStep = 30;
		for( float i=0; i < P.TWO_PI * 100; i+= inc ) {
			float x = P.sin(i) * radius;
			float y = P.cos(i) * radius;
			float xNext = P.sin(i+inc) * radius;
			float yNext = P.cos(i+inc) * radius;
			
			drawBoxBetween( new PVector(x, y, zInc ), new PVector( xNext, yNext, zInc - zIncStep ) );
			drawBoxBetween( new PVector(x, y, zInc ), new PVector( P.sin(i) * radius*2, P.cos(i) * radius*2, zInc ) );
			
			zInc -= zIncStep;
		}
		
	}

	public void drawBoxBetween( PVector point1, PVector point2 ) {
		PVector pointMid = point1.get();
		pointMid.lerp(point2, 0.5f);

		// Rotation vectors
		// use to perform orientation to velocity vector
		PVector new_dir = PVector.sub(point1,point2);
		float r = sqrt(new_dir.x * new_dir.x + new_dir.y * new_dir.y + new_dir.z * new_dir.z);
		float theta = atan2(new_dir.y, new_dir.x);
		float phi = acos(new_dir.z / r);

		pushMatrix();
		// update location
		p.translate(pointMid.x, pointMid.y, pointMid.z);
		// orientation to velocity
		rotateZ(theta);
		rotateY(phi);
		rotateX(HALF_PI);

		// draw your stuff here
		p.box(5, point1.dist(point2), 5);

		popMatrix(); 

	}
}
