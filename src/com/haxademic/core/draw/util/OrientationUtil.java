package com.haxademic.core.draw.util;

import processing.core.PApplet;
import processing.core.PVector;

import com.haxademic.core.app.P;

public class OrientationUtil {
	
	public static void setRotationTowards( PApplet p, PVector point1, PVector point2 ) {
		// Rotation vectors
		// use to perform orientation to velocity vector
		PVector new_dir = PVector.sub(point1,point2);
		float r = P.sqrt(new_dir.x * new_dir.x + new_dir.y * new_dir.y + new_dir.z * new_dir.z);
		float theta = P.atan2(new_dir.y, new_dir.x);
		float phi = P.acos(new_dir.z / r);
		
		p.rotateZ(theta);
		p.rotateY(phi);
		p.rotateX(P.HALF_PI);
	}

}
