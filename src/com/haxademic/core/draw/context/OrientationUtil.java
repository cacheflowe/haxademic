package com.haxademic.core.draw.context;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.core.PVector;

public class OrientationUtil {
	
	public static PVector lookAt = new PVector();
	
	public static void setRotationTowards( PGraphics pg, PVector point1, PVector point2 ) {
		lookAt.set(point1);
		lookAt.sub(point2);
		float r = P.sqrt(lookAt.x * lookAt.x + lookAt.y * lookAt.y + lookAt.z * lookAt.z);
		float theta = P.atan2(lookAt.y, lookAt.x);
		float phi = P.acos(lookAt.z / r);
		pg.rotateZ(theta);
		pg.rotateY(phi);
		pg.rotateX(P.HALF_PI);
	}

}
