package com.haxademic.demo.render.joons;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PShape;

public class JoonsPShapeRender
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float objHeight;
	protected float frames = 200;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH );

		p.appConfig.setProperty( AppSettings.WIDTH, 960 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 3 );
		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 3 + 20 );
	}

	public void setup() {
		super.setup();
		
		// load & normalize shape
		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
		obj = p.loadShape( FileUtil.getFile("models/poly-hole-penta.obj"));	
		PShapeUtil.centerSvg(obj);
		PShapeUtil.scaleObjToExtentVerticesAdjust(obj, p.height * 0.4f);
		objHeight = PShapeUtil.getObjHeight(obj);
	}


	public void drawApp() {
		if(p.appConfig.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == false) {
			p.background(0);
			p.lights();
			p.noStroke();
		}
//		joons.jr.background(JoonsWrapper.BACKGROUND_GI);
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		p.translate(0, 0, -width);
		
		// progress
		float progress = (p.frameCount % frames) / frames;

		// draw environment
		p.pushMatrix();
		setUpRoom();
		p.popMatrix();
		
		// draw shape
		p.pushMatrix();
		p.rotateZ(P.PI);
		p.rotateY(progress * P.TWO_PI);
		joons.jr.fill(JoonsWrapper.MATERIAL_PHONG, 205, 150, 205);		p.fill( 205, 150, 205 );
		PShapeUtil.drawTrianglesJoons(p, obj, 1);
		p.popMatrix();

		// draw sphere
		p.pushMatrix();
//		joons.jr.fill(JoonsWrapper.MATERIAL_LIGHT, 5, 5, 5);		p.fill( 255, 255, 255 );
		joons.jr.fill(JoonsWrapper.MATERIAL_AMBIENT_OCCLUSION, 10, 10, 30 + 20f * P.sin(progress * P.TWO_PI), 0, 0, 0,   50, 16);
		p.rotateY(1);
		p.rotateZ(1);
		p.sphere(210);
		p.popMatrix();
		
		// draw floor
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		p.translate(0, objHeight/2);
		joons.jr.fill(JoonsWrapper.MATERIAL_PHONG, 10, 10, 10);		p.fill( 10, 10, 10 );
		p.box(p.height * 3, 2, p.height * 3);
		p.popMatrix();
	}

	protected void setUpRoom() {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 10;
		int samples = 16;
		joons.jr.background(JoonsWrapper.CORNELL_BOX, 
				4000, 3000, 5000,						// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				40, 40, 40, 							// left rgb
				40, 40, 40, 							// right rgb
				0, 0, 0,	 							// back rgb
				60, 60, 60, 							// top rgb
				60, 60, 60  							// bottom rgb
		); 
		popMatrix();		
	}
	
}