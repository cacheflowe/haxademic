package com.haxademic.demo.math;

import com.haxademic.core.app.PAppletHax;

public class Demo_SphericalCoordinates
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float lat = 0;
	float lon = 0;
	float radius = 250;
	
	public void drawApp() {
		background( 0 );
		shininess(1000); 
		lights();
		noStroke();
		
		translate( width/2, height/2, 50 );
		rotateX(2f*PI/6f);
		rotateZ(frameCount/100f);
		rotateY(frameCount/100f);
		
		sphereDetail( 60 );
		stroke(100);
		noFill();
		sphere(radius*.9f);

		noStroke();
		fill(255, 50);
		// sphereDetail( 5 );
		for(float i=0; i < 2*PI; i+=(2*PI)/20) {
			pushMatrix();
			
			// spherical coordinates
			float x = cos(i) * cos(lat) * radius;
			float y = sin(i) * cos(lat) * radius;
			float z = sin(lat) * radius;
			
			translate( x, y, z );
			
			rotateX(cos(lon) * cos(lat));
			rotateY(sin(lon) * cos(lat));
			rotateZ(sin(lat));
			
			sphere(10);

			popMatrix();
		}
		
		lat += 0.01;
		lon += 0.05;
		
	}

}
