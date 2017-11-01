package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_PShapeSolid_deformMethods 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected PShapeSolid objSolid;
	protected PImage img;
	protected float _frames = 60;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.FILLS_SCREEN, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "45" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}

	public void setup() {
		super.setup();	

		// load texture
		img = DemoAssets.squareTexture();
		
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		
		// scale obj
		float objHeight = p.height * 0.3f;
		PShapeUtil.scaleShapeToExtent(obj, objHeight);
		
		// add UV coordinates to OBJ
//		float modelExtent = PShapeUtil.getShapeMaxExtent(obj);
//		PShapeUtil.addTextureUVToObj(obj, img, modelExtent);
		// obj.setTexture(img);
		
		// build solid, deformable PShape object
		objSolid = new PShapeSolid(obj);
		DebugUtil.printErr("Fix PShapeSolid objects created by other methods");
//		objSolid = newSolidIcos(200, img);
//		objSolid = newSolidSphere(200, null);
		
	}
	
	protected PShape newSphere(float size, PImage texture) {
		PShape shape = p.createShape(P.SPHERE, size);
		shape.setTexture(texture);
//		float extent = PShapeUtil.getSvgMaxExtent(shape);
//		PShapeUtil.addUVsToPShape(shape, extent);
		return shape;
	}
	
	protected PShapeSolid newSolidSphere(float size, PImage texture) {
		PShape group = createShape(GROUP);
		group.addChild(newSphere(size, texture));
		return new PShapeSolid(group);
	}
	
	protected PShapeSolid newSolidIcos(float size, PImage texture) {
		PShape group = createShape(GROUP);
		PShape icos = Icosahedron.createIcosahedron(p.g, 4, texture);
		PShapeUtil.scaleShapeToExtent(icos, size);
		group.addChild(icos);
		return new PShapeSolid(icos);
	}


	public void drawApp() {
		background(0);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);

		// blending test 
//		if(P.round(p.frameCount/20) % 2 == 0) {
//			OpenGLUtil.setBlending(p.g, true);
//			OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.ALPHA_REVEAL);
//		} else {
//			OpenGLUtil.setBlending(p.g, false);
//			OpenGLUtil.setBlendMode(p.g, OpenGLUtil.Blend.DEFAULT);
//		}
		
		// wireframe hotness!
//		if(P.round(p.frameCount/40) % 2 == 0) {
//			OpenGLUtil.setWireframe(p.g, true);
//		} else {
//			OpenGLUtil.setWireframe(p.g, false);
//		}
		
		// setup lights
		p.lightSpecular(230, 230, 230); 
		p.directionalLight(200, 200, 200, -0.0f, -0.0f, 1); 
		p.directionalLight(200, 200, 200, 0.0f, 0.0f, -1); 
		p.specular(color(255)); 
		p.shininess(5.0f); 

		// rotate
		p.translate(p.width/2f, p.height/2.1f);
//		p.translate(0, 0, -1000);
		p.rotateX(P.sin(-percentComplete * P.TWO_PI) * 0.3f);
//		p.rotateY(-percentComplete * P.TWO_PI);
		p.rotateZ(P.PI);

		// swap deform modes
		int deformMode = P.round(p.frameCount / 100) % 3;
		if(deformMode == 0) 		objSolid.updateWithTrig(true, percentComplete * 2f, 0.05f, 17.4f);
		else if(deformMode == 1) objSolid.deformWithAudio();
		else if(deformMode == 2) objSolid.deformWithAudioByNormals();

		// draw!
		p.noStroke();
		p.fill(255);
		p.shape(objSolid.shape());
		
	}
		
}