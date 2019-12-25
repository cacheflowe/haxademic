package com.haxademic.sketch.pshape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.camera.CameraUtil;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class ChickenDataViz 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage objTexture;
	protected float _frames = 180;
	protected PVector modelSize; 

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + _frames) );
	}

	protected void firstFrame() {
		// build obj PShape and scale to window
		obj = p.loadShape( FileUtil.getPath("models/poly/chicken/Chicken_01.obj"));
		
		// normalize model
		PShapeUtil.meshRotateOnAxis(obj, P.PI, P.Z);
		PShapeUtil.meshRotateOnAxis(obj, -P.HALF_PI, P.Y);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.05f);
		modelSize = new PVector(PShapeUtil.getMaxAbsX(obj), PShapeUtil.getMaxAbsY(obj), PShapeUtil.getMaxAbsZ(obj));
	}

	public void drawApp() {
		p.pushMatrix();
		background(255);
		CameraUtil.setCameraDistance(p.g, 100, 20000);
		
//		p.ortho();
		
		// setup lights
//		 PG.setBetterLights(p);
		p.lights();
		 p.noStroke();

		// rotate
		p.translate(p.width/2f, p.height * 0.7f);
		p.rotateX(-0.6f);
		
		// texture mapped with decent performance:
		p.translate(-300, 0, 0);
		int numChicks = 0;
		for (int z = 0; z < 10000; z+=modelSize.z) {
			for (int x = 0; x < 600; x+=modelSize.x) {
				if(numChicks < 1000) {
					p.pushMatrix();
					p.translate(x, 0, -z);
					p.rotateY(p.noise(x * 10f, numChicks, z));
					p.shape(obj);				
					p.popMatrix();
					numChicks++;
				}
			}
		}
		DebugView.setValue("numChicks", numChicks);
		
		p.popMatrix();
	}
		
}