package com.haxademic.sketch.three_d;

import processing.core.PApplet;
import processing.core.PConstants;

public class SphericalCoordinates
	extends PApplet
{
	float lat = 0;
	float lon = 0;
	float radius = 250;
	
	public void setup () {
		// set up stage and drawing properties
//		size( 800, 800, "hipstersinc.P5Sunflow" );
		size( 800, 800, PConstants.OPENGL );				//size(screen.width,screen.height,P3D);
		frameRate( 30 );
		colorMode( PConstants.RGB, 255, 255, 255, 255 );
		background( 0 );
		shininess(1000); 
		lights();
		noStroke();
	}

	public void draw() {
		if(frameCount < 2) return;
		background( 0 );
		
		translate( width/2, height/2, 50 );
		rotateX(2f*PI/6f);
		rotateZ(frameCount/100f);
		rotateY(frameCount/100f);
		
		sphereDetail( 60 );
		stroke(100);
		noFill();
		sphere(radius*.9f);

		noStroke();
		fill(255);
		sphereDetail( 5 );
		for(float i=0; i < 2*PI; i+=(2*PI)/100) {
			pushMatrix();
			
			// spherical coordinates
			float x = cos(i) * cos(lat) * radius;
			float y = sin(i) * cos(lat) * radius;
			float z = sin(lat) * radius;
			
			translate( x, y, z );
			
			rotateX(cos(lon) * cos(lat));
			rotateY(sin(lon) * cos(lat));
			rotateZ(sin(lat));
			
			box(30);

			popMatrix();
		}
		
		lat += 0.01;
		lon += 0.05;
		
	}

}
