package com.haxademic.core.camera;

import processing.core.PGraphics;

import com.haxademic.core.app.P;

public class CameraUtil {
	
	public static void resetCamera(PGraphics pg) {
		pg.camera(pg.width/2.0f, pg.height/2.0f, (pg.height/2.0f) / P.tan(P.PI*30.0f / 180.0f), pg.width/2.0f, pg.height/2.0f, 0, 0, 1, 0);
	}
	
	public static void setCameraDistance(PGraphics pg, float near, float far) {
		// extend camera view distance
		float fov = P.PI/3.0f;
		float cameraZ = (pg.height/2.0f) / P.tan(fov/2.0f);
		pg.perspective(fov, (float)pg.width/(float)pg.height, near, far);
	}

}
