package com.haxademic.core.draw.shapes;

import processing.core.PApplet;
import processing.core.PVector;

import com.haxademic.core.app.P;

public class BoxBetween {
	
	protected static PVector pointMid = new PVector();
	protected static PVector rotationDir = new PVector();
	
	/**
	 * Draw a box between (2) 3d points. Original code from Dave Bollinger.
	 * @param p
	 * @param point1
	 * @param point2
	 * @param thickness
	 */
	public static void draw( PApplet p, PVector point1, PVector point2, float thickness ) {
		if( point1 == null || point2 == null ) return; 
			
		// reuse halfway vector and find the midpoint
		pointMid.set( point1.x, point1.y, point1.z );
		pointMid.lerp( point2, 0.5f );

		// use to perform orientation to velocity vector
		PVector.sub( point1, point2, rotationDir );
		float r = P.sqrt( rotationDir.x * rotationDir.x + rotationDir.y * rotationDir.y + rotationDir.z * rotationDir.z );
		float theta = P.atan2(rotationDir.y, rotationDir.x);
		float phi = P.acos(rotationDir.z / r);

		// update location
		p.pushMatrix();
		p.translate( pointMid.x, pointMid.y, pointMid.z );
		
		// set draw context orientation 
		p.rotateZ(theta);
		p.rotateY(phi);
		p.rotateX(P.HALF_PI);

		// draw box
		p.box( thickness, point1.dist(point2), thickness );

		p.popMatrix(); 
	}
}
