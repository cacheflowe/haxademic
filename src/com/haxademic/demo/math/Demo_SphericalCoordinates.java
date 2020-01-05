package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;

public class Demo_SphericalCoordinates
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	float lat = 0;
	float lon = 0;
	float radius = 250;
	
	protected void drawApp() {
		background( 0 );
		shininess(1000); 
		lights();
		noStroke();
		
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);
		
		sphereDetail( 60 );
		stroke(100);
		noFill();
		sphere(radius*.9f);

		noStroke();
		fill(255);
		// sphereDetail( 5 );
		for(float i=0; i < P.TWO_PI; i+=(P.TWO_PI)/20) {
			pushMatrix();
			
			// spherical coordinates
			float x = cos(i) * cos(lat) * radius;
			float y = sin(i) * cos(lat) * radius;
			float z = sin(lat) * radius;
			
			translate( x, y, z );
			
			rotateX(cos(lon) * cos(lat));
			rotateY(sin(lon) * cos(lat));
			rotateZ(sin(lat));
			
			box(10);

			popMatrix();
		}
		
		lat += 0.01;
		lon += 0.05;
		
	}

}
