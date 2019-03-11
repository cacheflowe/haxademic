package com.haxademic.core.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class OrientationUtil {
	
	protected static PVector pointMid = new PVector();

	public static void setMidPoint( PGraphics pg, PVector point1, PVector point2 ) {
		pointMid.set( point1.x, point1.y, point1.z );
		pointMid.lerp( point2, 0.5f );
		pg.translate( pointMid.x, pointMid.y, pointMid.z );	
	}
	
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

	public static void setRotationTowards2(PGraphics pg, PVector point1, PVector point2) {
		// spin on y axis
		float yRads = MathUtil.getRadiansToTarget(point1.x, point1.z, point2.x, point2.z);
		// calculate z-tilt
		float c = point1.dist(point2);	 		// we have the diagonal distance
		float b = point1.y - point2.y;	 		// and y-difference
		float a = P.sqrt(P.sq(c) - P.sq(b));		// so we solve for a (c^2 - b^2 = a^2)
		float zRads = MathUtil.getRadiansToTarget(0, 0, a, b);	// get radians based on a/b (x/y) offset
		pg.rotateY(yRads);
		pg.rotateZ(zRads);
		pg.rotateZ(-P.HALF_PI);
	}
	
}
