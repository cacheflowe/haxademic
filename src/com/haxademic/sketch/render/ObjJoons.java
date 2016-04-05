
package com.haxademic.sketch.render;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.util.PShapeUtil;
import com.haxademic.core.system.FileUtil;

import processing.core.PShape;

public class ObjJoons 
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	protected PShape obj;
	protected PShapeSolid objSolid;
	protected float _frames = 40;
	protected float percentComplete;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );


		p.appConfig.setProperty( AppSettings.WIDTH, "800" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "800" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void setup() {
		super.setup();
	}
	
	public void drawApp() {
		percentComplete = ((float)(p.frameCount%_frames)/_frames);
		P.println("frameCount: ",p.frameCount);
		
		joons.jr.background(0,0,0); //background(gray), or (r, g, b), like Processing.
		joons.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.
		joons.jr.background("gi_instant"); //Global illumination, normal mode.

		
		// drawGlassRoom();
		// drawBlankStare();
		// drawInterlockingBlocksRows();
		drawMan();
				
		// render movie -------------------------------
		if( _isRendering == true && _renderer != null ) {
			if(p.frameCount > _frames+1) {
				_renderer.stop();
				exit();
			}
		}
	}
	
	public void drawInterlockingBlocksRows() {
		_frames = 120;
		
		float spacing = p.width / 15f;
		float boxSize = p.width / 70f;
		float boxDepth = 150;
		
		// draw room
		float radiance = 20;
		int samples = 16;
		float r = 200;
		float g = 200;
		float b = 200;
		joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				r,g,b, // left rgb
				r,g,b, // right rgb
				r,g,b, // back rgb
				r,g,b, // top rgb
				r,g,b  // bottom rgb
		); 
		
		translate(0, 0, -120);
		p.rotateX(0.1f);
		
		// background box
		pushMatrix();
		translate(0, 0, -boxDepth);
//		_jw.jr.fill("shiny", 127, 127, 127);
		joons.jr.fill("mirror", 100, 120, 150);
		box(p.width * 2, p.height * 2, 5);
		popMatrix();
		
		// rotate scene
		rotateZ(percentComplete * P.TWO_PI);

		// draw middle row of boxes
		for(float x = -p.width + spacing * percentComplete; x < p.width; x += spacing) {
			pushMatrix();
			translate(x, 0, -boxDepth);
			joons.jr.fill("shiny", 215, 255, 235);
			box(boxSize, boxSize, boxDepth);
			popMatrix();
		}

		// draw 2nd row of boxes
		for(float y = -p.width + spacing * (percentComplete + 0.5f); y < p.width; y += spacing) {
			pushMatrix();
			translate(0, y, -boxDepth);
//			_jw.jr.fill("light", 10, 10, 10);
			joons.jr.fill("shiny", 205, 230, 185);
			box(boxSize, boxSize, boxDepth);
			popMatrix();
		}
	}

	public void drawGlassRoom() {
		// Glass Room ---------------------
		if(obj == null) {
			obj = p.loadShape( FileUtil.getFile("models/poly-hole-square.obj"));	
			objSolid = new PShapeSolid(obj);
		}
		
		// draw room
		float radiance = 20;
		int samples = 16;
		joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				0,0,0, // left rgb
				0,0,0, // right rgb
				0,0,0, // back rgb
				0,0,0, // top rgb
				0,0,0  // bottom rgb
		); 

		
		// position scene center
		translate(0, 0, -120);
		
		p.rotateY(percentComplete * P.PI/2f);

		//_jw.jr.fill("light"); or
		//_jw.jr.fill("light", r, g, b); or
		//_jw.jr.fill("light", r, g, b, int samples);
//		pushMatrix();
//		float radius = 10;
//		translate(0,0,0);
//		_jw.jr.fill("shiny", 255, 235, 235);
//		sphere(radius);
//		popMatrix();

//		// glass inner
		pushMatrix();
//		translate(0,0,0);
		joons.jr.fill("shiny", 255, 235, 235, 0.3f);
		sphere(12f + 2f * P.sin(-P.PI/2f + P.TWO_PI * percentComplete));
		popMatrix();
		
		
		// draw obj
		pushMatrix();
//		translate(0, 0, 20);
		joons.jr.fill("glass", 255, 255, 255);
		p.rotateZ(P.PI);
//		p.rotateY(P.PI);
		p.scale(11);
		obj.disableStyle();
		p.noStroke();
//		objSolid.updateWithTrig(true, percentComplete, 0.04f, 17.4f);
		PShapeUtil.drawTriangles(p, obj.getTessellation());
		popMatrix();
	}
	
	public void drawBlankStare() {
		// Blank Stare ---------------------
		if(obj == null) {
			obj = p.loadShape( FileUtil.getFile("models/skull.obj"));	
			objSolid = new PShapeSolid(obj);
		}
		
		// draw room
		joons.jr.background("cornell_box", p.width, p.height, p.height); //cornellBox(width, height, depth);

		// position scene center
		translate(0, 0, -120);

		//_jw.jr.fill("light"); or
		//_jw.jr.fill("light", r, g, b); or
		//_jw.jr.fill("light", r, g, b, int samples);
		pushMatrix();
		float radius = 10;
		translate(0,0,0);
		joons.jr.fill("shiny", 255, 235, 235);
		sphere(radius);
		popMatrix();

//		// glass outer
//		pushMatrix();
////		float radius = 50;
//		translate(0,0,0);
//		_jw.jr.fill("glass", 255, 255, 255);
//		sphere(20);
//		popMatrix();
		
		
		// draw obj
		pushMatrix();
		translate(0, 0, 20);
//		_jw.jr.fill("glass", 255, 255, 255);
		joons.jr.fill("shiny", 255, 235, 235, 0.3f);
		p.rotateZ(P.PI);
//		p.rotateY(P.PI);
		p.scale(11);
		obj.disableStyle();
		p.noStroke();
		objSolid.updateWithTrig(true, percentComplete, 0.04f, 17.4f);
		PShapeUtil.drawTriangles(p, objSolid.shape().getTessellation());
		popMatrix();
	}

	public void drawMan() {
		// Man ---------------------
		if(obj == null) {
			obj = p.loadShape( FileUtil.getFile("models/man_free_lowpoly.obj"));	
			objSolid = new PShapeSolid(obj);
		}
		
		PShape diamond = p.loadShape( FileUtil.getFile("models/diamond.obj"));
		
		
		// draw room
		float radiance = 20;
		int samples = 16;
		float r = 30;
		float g = 30;
		float b = 30;
		joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				r,g,b, // left rgb
				r,g,b, // right rgb
				r,g,b, // back rgb
				r,g,b, // top rgb
				r,g,b  // bottom rgb
		); 
		
		// position scene center
		translate(0, 0, -120);
		
		// background boxes
		float bgBoxDist = 50f;
		
		pushMatrix();
		p.rotateY(P.PI*0.5f);
		translate(0, 0, -bgBoxDist);
		joons.jr.fill("mirror", 150, 120, 150);
		box(p.width * 3, p.height * 3, 5);
		popMatrix();
		
		pushMatrix();
		p.rotateY(P.PI*0.5f * 3f);
		translate(0, 0, -bgBoxDist);
		joons.jr.fill("mirror", 150, 120, 150);
		box(p.width * 2, p.height * 2, 5);
		popMatrix();
		
		// lights behind camera
		pushMatrix();
		p.rotateY(P.PI*0.5f);
		translate(0, 0, 150);
		joons.jr.fill("light", 120, 180, 150);
		box(p.width * 2, p.height * 3, 5);
		popMatrix();

//		pushMatrix();
//		translate(0, 0, 150);
//		_jw.jr.fill("light", 100, 130, 100);
//		box(p.width * 2, p.height * 3, 5);
//		popMatrix();

		pushMatrix();
		p.rotateY(P.PI*0.5f * 3f);
		translate(0, 0, 150);
		joons.jr.fill("light", 150, 120, 180);
		box(p.width * 2, p.height * 3, 5);
		popMatrix();

		
		// extra objs
		pushMatrix();
		translate(0, -18, 20);
		p.rotateZ(P.PI);
		p.scale(0.08f);
//		_jw.jr.fill("light", 120, 180, 150);
//		_jw.jr.fill("mirror", 150, 120, 150);
		joons.jr.fill("shiny", 255, 255, 255, 0.3f);
		PShapeUtil.drawTriangles(p, diamond.getTessellation());
		popMatrix();

		
		// draw obj
		pushMatrix();
		translate(0, 40, 20);
//		_jw.jr.fill("glass", 255, 255, 255);
		joons.jr.fill("shiny", 255, 255, 255, 0.3f);
		p.rotateZ(P.PI);
		p.rotateY(P.TWO_PI*0.75f);
//		p.rotateX(0.5f);
		p.scale(0.26f);
		obj.disableStyle();
		p.noStroke();
		objSolid.updateWithTrig(true, percentComplete, 0.023f, 7.4f);
		PShapeUtil.drawTriangles(p, objSolid.shape().getTessellation());
		popMatrix();
	}
	
	public void keyPressed() {
		if (key == 'r' || key == 'R') joons.jr.render(); //Press 'r' key to start rendering.
	}

}
