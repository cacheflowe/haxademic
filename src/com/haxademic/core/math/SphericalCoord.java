package com.haxademic.core.math;

import com.haxademic.core.app.P;

import processing.core.PVector;

public class SphericalCoord {
	
	// Info from: http://blog.blprnt.com/blog/blprnt/processing-tutorial-spherical-coordinates
	// and: https://en.wikipedia.org/wiki/Vector_fields_in_cylindrical_and_spherical_coordinates#Spherical_coordinate_system
	// and: https://neutrium.net/mathematics/converting-between-spherical-and-cartesian-co-ordinate-systems/
	// and: https://math.stackexchange.com/questions/2466728/cartesian-to-spherical-coordinates-translation-how-to-differentiate-x-y-signs
	// and: http://www.java-gaming.org/index.php?topic=36791.0

	public float theta = P.p.random(P.TWO_PI);	// longitude (vert)
	public float phi = P.p.random(P.TWO_PI);		// latitude	(horiz)
	public float radius = 1f;
	public PVector cartesian = new PVector();

	public SphericalCoord() {
		
	}
	
	public void setSpherical(float t, float p, float r) {
		// store spherical
		theta = t;
		phi = p;
		radius = r;
		
		// convert to cartesian
		cartesian.set(
			radius * P.sin(theta) * P.cos(phi),
			radius * P.sin(theta) * P.sin(phi),
			radius * P.cos(theta)
		);
	}
	
	public void addSpherical(float t, float p) {
		setSpherical(theta += t, phi += p, radius);
	}
	
	public void setCartesian(float x, float y, float z) {
		// store cartesian
		cartesian.set(x, y, z);
		cartesian.normalize();
		
		// convert cartesian to spherical 
		radius = P.sqrt(x * x + y * y + z * z);
        theta = P.acos(z / radius);
        phi   = P.atan2(y, x);
        // phi   = P.atan(pos.y / pos.x); // this technique requires a quadrant check to get right, so we use atan2 :) 
	}

	// https://stackoverflow.com/questions/9600801/evenly-distributing-n-points-on-a-sphere
	// Fibonacci distribution
	public static PVector[] buildFibonacciSpherePoints(int numPoints) {
		PVector[] points = new PVector[numPoints];
		float offset = 2f/points.length;
		float inc = P.PI * ( 3.0f - P.sqrt(5.0f));

		for(int i = 0; i < points.length; i++) {
			float z   = i*offset - 1f + ( offset/2f );
			float r   = P.sqrt(1.0f - z*z);
			float phi = i*inc;

			points[i] = new PVector( P.cos(phi)*r, P.sin(phi)*r, -z);
		}
		return points;
	}

}
