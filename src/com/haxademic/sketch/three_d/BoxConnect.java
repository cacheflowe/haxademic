package com.haxademic.sketch.three_d;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.PRenderers;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.BoxBetween;

import processing.core.PVector;

public class BoxConnect
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }


	public void setupFirstFrame() {
		p.background( 0 );
		p.smooth();
		p.noStroke();
	}

	public void drawApp() {

		DrawUtil.setBasicLights( p );
		if( PRenderers.currentRenderer() == P.P3D ) p.background(0,0,0,255);

		p.translate(p.width/2, p.height/2, 500);
		// rotateY(frameCount/40f);

		p.noStroke();
		p.fill( 100f, 200f, 134f, 255f );
		
		
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
