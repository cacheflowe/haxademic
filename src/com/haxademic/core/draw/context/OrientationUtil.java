package com.haxademic.core.draw.context;

import processing.core.PApplet;
import processing.core.PVector;

import com.haxademic.core.app.P;

public class OrientationUtil {
	
	public static PVector lookAt = new PVector();
	
	public static void setRotationTowards( PApplet p, PVector point1, PVector point2 ) {
		lookAt.set(point1);
		lookAt.sub(point2);
		float r = P.sqrt(lookAt.x * lookAt.x + lookAt.y * lookAt.y + lookAt.z * lookAt.z);
		float theta = P.atan2(lookAt.y, lookAt.x);
		float phi = P.acos(lookAt.z / r);
		p.rotateZ(theta);
		p.rotateY(phi);
		p.rotateX(P.HALF_PI);
	}

}
