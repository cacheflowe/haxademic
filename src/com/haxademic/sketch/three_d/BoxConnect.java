package com.haxademic.sketch.three_d;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.color.TColorInit;
import com.haxademic.core.draw.shapes.BoxBetween;
import com.haxademic.core.draw.util.DrawUtil;

import processing.core.PVector;

public class BoxConnect
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	public void setup() {
		super.setup();

		// Sunflow needs to use colors between 0-1. Default processing color mode is 0-255. 
		// TColor likes the former, so we normalize with TColorInit, depending on the rendering mode.
		// This way we can always use normal 0-255 RGB color blending in either case.
		if( rendererMode == P.P3D ) {
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
		p.appConfig.setProperty( AppSettings.SUNFLOW, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void drawApp() {

		DrawUtil.setBasicLights( p );
		if( rendererMode == P.P3D ) p.background(0,0,0,255);

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
			
			BoxBetween.draw( p, new PVector(x, y, zInc ), new PVector( xNext, yNext, zInc - zIncStep ), 5 );
			BoxBetween.draw( p, new PVector(x, y, zInc ), new PVector( P.sin(i) * radius*2, P.cos(i) * radius*2, zInc ), 5 );
			
			zInc -= zIncStep;
		}
		
	}

}
