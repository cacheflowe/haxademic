package com.haxademic.core.cameras;

import processing.core.PGraphics;

import com.haxademic.core.app.P;

public class CameraUtil {
	
	public static void setCameraDistance(PGraphics pg, float near, float far) {
		// extend camera view distance
		float fov = P.PI/3.0f;
		float cameraZ = (pg.height/2.0f) / P.tan(fov/2.0f);
		pg.perspective(fov, (float)pg.width/(float)pg.height, near, far);
	}

}
