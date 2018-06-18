package com.haxademic.core.camera;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

import processing.core.PGraphics;

public class CameraUtil {
	
	public static void resetCamera(PGraphics pg) {
		pg.camera(pg.width/2.0f, pg.height/2.0f, (pg.height/2.0f) / P.tan(P.PI*30.0f / 180.0f), pg.width/2.0f, pg.height/2.0f, 0, 0, 1, 0);
	}
	
	public static void setCameraDistance(PGraphics pg, float near, float far) {
		if(near < 0 || far < 0) DebugUtil.printErr("[ERROR]: CameraUtil.setCameraDistance() can only use positive numbers");
		float fov = P.PI/3.0f;
		pg.perspective(fov, (float)pg.width/(float)pg.height, near, far);
	}

	public static void setCameraDistanceGood(PGraphics pg, float near, float far) {
		float fov = P.PI/3.0f;
		pg.perspective(fov, pg.width / pg.height, near, far);
	}

}
