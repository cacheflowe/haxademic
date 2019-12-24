package com.haxademic.render.joons;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

import processing.core.PShape;

public class JoonsTestSvg
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape _shape;
	
	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, "true" );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, "true" );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, "low" );


		Config.setProperty( AppSettings.WIDTH, "800" );
		Config.setProperty( AppSettings.HEIGHT, "600" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.FPS, "30" );
	}

	public void firstFrame() {

		
		_shape = p.loadShape(FileUtil.getHaxademicDataPath() + "/svg/pink-eye-skeleton.svg");
		_shape.scale(3);
		
//		for( PShape child: _shape.getChildren() ) {
//			if(child != null) {
//				if(child.getFill(0) == p.color(255)) {
//					P.println(child.getFill(0));
//					child.translate(0, 0, 30);
//				}
//			}
//		}

	}
	
	public void drawApp() {
//		p.background(0);
		Renderer.instance().joons.jr.background(100, 100, 100); //background(gray), or (r, g, b), like Processing.
		Renderer.instance().joons.jr.background("gi_instant"); //Global illumination, normal mode.
		Renderer.instance().joons.jr.background("gi_ambient_occlusion"); //Global illumination, ambient occlusion mode.

		setUpRoom();
		
		PG.setDrawCenter(p);
		p.pushMatrix();
		p.translate(0, 0, -1000);
		_shape.disableStyle();
		Renderer.instance().joons.jr.fill( JoonsWrapper.MATERIAL_SHINY, 255, 255, 255);
		p.shape(_shape);
		p.popMatrix();
//			P.println("===================");
			//for( int i = 0; i < _shape.getChildCount(); i++ ) {
//			for( PShape child: _shape.getChildren() ) {
//				if(child != null) {
//					if(child.getFill(0) == p.color(255)) {
//						P.println(child.getFill(0));
//						p.shape(child);
//					}
//				}
//			}

		
//		_jw.jr.fill("light", 255, 255, 255);
//		sphere(13);
//		translate(27, 0, 0);
//
//		_jw.jr.fill("mirror", 255, 255, 255);
//		sphere(13);
//		translate(27, 0, 0);
//
//		_jw.jr.fill("ambient_occlusion", 150, 255, 255, 0, 0, 255, 50, 16);
//		sphere(13);
//		translate(27, 0, 0);
//
//		_jw.jr.fill("phong", 150, 255, 255);
//		sphere(13);
//		translate(27, 0, 0);
//
//		_jw.jr.fill("glass", 255, 255, 255);
//		sphere(13);
//		translate(27, 0, 0);
	}

	public void keyPressed() {
		if (key == 'r' || key == 'R') Renderer.instance().joons.jr.render(); //Press 'r' key to start rendering.
	}

	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, -1000);
		float radiance = 20;
		int samples = 16;
		Renderer.instance().joons.jr.background("cornell_box", 
				12000, 6000, 6000,	// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, // left rgb
				40, 40, 40, // right rgb
				60, 60, 60, // back rgb
				60, 60, 60, // top rgb
				60, 60, 60  // bottom rgb
		); 
//		_jw.jr.background("cornell_box", 
//				12000, 6000, 6000,	// width, height, depth
//				radiance, radiance, radiance, samples,  // radiance rgb & samples
//				0,0,0, // left rgb
//				0,0,0, // right rgb
//				0,0,0, // back rgb
//				0,0,0, // top rgb
//				0,0,0  // bottom rgb
//		); 
		popMatrix();		
	}

}
