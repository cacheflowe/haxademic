package com.haxademic.demo.render.joons;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PShape;

public class Demo_Joons_PShapeRenderOBJ
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float objHeight;
	protected int FRAMES = 120;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		
		p.appConfig.setProperty( AppSettings.SUNFLOW, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH);

		p.appConfig.setProperty( AppSettings.WIDTH, 960 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 3 );
		p.appConfig.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 3 + FRAMES );
	}

	public void setup() {
		super.setup();
		
		// load & normalize shape
		obj = p.loadShape( FileUtil.getFile("models/skull-realistic.obj"));	
		obj = p.loadShape( FileUtil.getFile("models/poly-hole-penta.obj"));
		obj = DemoAssets.objSkullRealistic();
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.8f);
		objHeight = PShapeUtil.getHeight(obj);
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
		
		// draw environment
		p.pushMatrix();
		setUpRoom(255, 255, 255);
		p.popMatrix();
		
		// draw shape
		p.pushMatrix();
//		p.rotateZ(P.PI);
		p.rotateY(-loop.progressRads()); // divide by 5 for pentagon
		joons.jr.fill(JoonsWrapper.MATERIAL_MIRROR, 230, 230, 230);		p.fill( 230, 230, 230 );
		PShapeUtil.drawTrianglesJoons(p, obj, 1);
		p.popMatrix();

		// draw sphere
//		p.pushMatrix();
////		joons.jr.fill(JoonsWrapper.MATERIAL_LIGHT, 5, 5, 5);		p.fill( 255, 255, 255 );
//		joons.jr.fill(JoonsWrapper.MATERIAL_GLASS, 5, 5, 5);		p.fill( 255, 255, 255 );
////		joons.jr.fill(JoonsWrapper.MATERIAL_AMBIENT_OCCLUSION, 10, 10, 30 + 20f * P.sin(loop.progressRads()), 0, 0, 0,   50, 16);
//		p.rotateY(1);
//		p.rotateZ(1);
//		p.sphere(70);
//		p.popMatrix();
		
		// draw ring of spheres
		float numCircles = 23;
		float segmentRads = P.TWO_PI / numCircles;
		float radius = p.width * 0.32f;
		for (int i = 0; i < numCircles; i++) {
			float progress = i / numCircles;
			float rads = i * segmentRads;
			float x = P.cos(rads) * radius;
			float z = P.sin(rads) * radius;
			float y = P.map(progress, 0, 1, objHeight/3, -objHeight/3);
			p.pushMatrix();
			p.rotateY(P.HALF_PI - P.QUARTER_PI + p.loop.progressRads());
			p.translate(x, y, z);
//			if(i%2 == 0) {
				joons.jr.fill(JoonsWrapper.MATERIAL_GLASS, 200 + 55f * P.sin(p.loop.progressRads() * 1f), 100 + 55f * P.sin(i/5f + p.loop.progressRads() * 1f), 200 + 55f * P.sin(i/3f + p.loop.progressRads() * 1f));		
												   p.fill( 200 + 55f * P.sin(p.loop.progressRads() * 1f), 100 + 55f * P.sin(i/5f + p.loop.progressRads() * 1f), 200 + 55f * P.sin(i/3f + p.loop.progressRads() * 1f) );
//			} else {
//				joons.jr.fill(JoonsWrapper.MATERIAL_LIGHT, 255, 255, 255);		p.fill( 255, 255, 255 );
//			}
			float size = 10f + 10f * P.sin(-p.loop.progressRads() + rads + P.QUARTER_PI);
			p.sphere(size);
			p.popMatrix();
		}
		
		// draw floor
		p.pushMatrix();
		DrawUtil.setDrawCenter(p);
		p.translate(0, objHeight/2);
		joons.jr.fill(JoonsWrapper.MATERIAL_PHONG, 255, 255, 255);		p.fill( 255, 255, 255 );
		p.box(p.height * 4, 2, p.height * 4);
		p.popMatrix();
	}

	protected void setUpRoom(int r, int g, int b) {
		pushMatrix();
		translate(0, 0, 0);
		float radiance = 10;
		int samples = 16;
		joons.jr.background(JoonsWrapper.CORNELL_BOX, 
				4000, 3000, 5000,						// width, height, depth
				radiance, radiance, radiance, samples,  // radiance rgb & samples
				r, g, b, 								// left rgb
				r, g, b, 								// right rgb
				r, g, b, 								// back rgb
				r, g, b, 								// top rgb
				r, g, b 								// bottom rgb
		); 
		popMatrix();		
	}
	
}