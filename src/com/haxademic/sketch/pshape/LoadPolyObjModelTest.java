package com.haxademic.sketch.pshape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.camera.CameraUtil;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class LoadPolyObjModelTest 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage objTexture;
	protected float _frames = 180;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, P.round(1 + _frames) );
	}

	public void setup() {
		super.setup();	
	}
	
	protected void firstFrameSetup() {
		// build obj PShape and scale to window
		obj = p.loadShape( FileUtil.getFile("models/poly/chicken/Chicken_01.obj"));
//		obj = p.loadShape( FileUtil.getFile("models/_0rx_H4qbwBp_obj/model.obj"));
//		obj = p.loadShape( FileUtil.getFile("models/_fFVqukPnc62_7DMFpXfx_ta_obj/TocoToucan.obj"));
//		objTexture = p.loadImage(FileUtil.getFile("models/_fFVqukPnc62_7DMFpXfx_ta_obj/TocoToucan_Albedo.png"));
//		obj.setTexture();
		
		// normalize model
		PShapeUtil.meshRotateOnAxis(obj, P.PI, P.Z);
		PShapeUtil.meshRotateOnAxis(obj, -P.HALF_PI, P.Y);
		PShapeUtil.scaleShapeToExtent(obj, p.height * 0.1f);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
	}

	public void drawApp() {
		if(p.frameCount == 1) firstFrameSetup();
		
		p.pushMatrix();
		background(255);
		CameraUtil.setCameraDistance(p.g, 100, 20000);
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float radsComplete = percentComplete * P.TWO_PI;
		
//		p.ortho();
		
		// setup lights
		 DrawUtil.setBetterLights(p);
//		p.lights();
		 p.noStroke();

		// rotate
		p.translate(p.width/2f, p.height * 0.5f);
//		p.rotateY(0.9f * P.sin(radsComplete));		
		
		// twist it
//		p.rotateY(2f * P.sin(-radsComplete));
//		PShapeUtil.verticalTwistShape(obj,  0.006f, P.cos(radsComplete) * 20);
		
		// texture mapped with decent performance:
		boolean wire = (percentComplete > .5f);
		OpenGLUtil.setWireframe(p.g, wire);
		p.shape(obj);
//		PShapeUtil.drawTriangles(p.g, objSolid.shape(), objTexture, 1f); // img
		
		p.popMatrix();
	}
		
}