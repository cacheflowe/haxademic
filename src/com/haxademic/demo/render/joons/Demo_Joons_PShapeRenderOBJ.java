package com.haxademic.demo.render.joons;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

import processing.core.PShape;

public class Demo_Joons_PShapeRenderOBJ
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float objHeight;
	protected int FRAMES = 120;
	
	protected void config() {
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		
		Config.setProperty( AppSettings.SUNFLOW, true );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, false );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH);

		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE, false );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 3 );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 3 + FRAMES );
	}

	protected void firstFrame() {

		
		// load & normalize shape
		obj = p.loadShape( FileUtil.getPath("models/skull-realistic.obj"));	
		obj = p.loadShape( FileUtil.getPath("models/poly-hole-penta.obj"));
		obj = DemoAssets.objSkullRealistic();
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height * 0.8f);
		objHeight = PShapeUtil.getHeight(obj);
	}


	protected void drawApp() {
		JoonsWrapper joons = Renderer.instance().joons;
		if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == false) {
			p.background(0);
			p.lights();
			p.noStroke();
		}
//		joons.jr.background(JoonsWrapper.BACKGROUND_GI);
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		p.translate(0, 0, -width);
		
		// draw environment
		p.pushMatrix();
		joons.setUpRoom(255, 255, 255);
		p.popMatrix();
		
		// draw shape
		p.pushMatrix();
//		p.rotateZ(P.PI);
		p.rotateY(-FrameLoop.progressRads()); // divide by 5 for pentagon
//		joons.jr.fill(JoonsWrapper.MATERIAL_MIRROR, 230, 230, 230);		p.fill( 230, 230, 230 );
		PShapeUtil.drawTrianglesJoons(p, obj, 1, JoonsWrapper.MATERIAL_MIRROR);
		p.popMatrix();

		// draw sphere
//		p.pushMatrix();
////		joons.jr.fill(JoonsWrapper.MATERIAL_LIGHT, 5, 5, 5);		p.fill( 255, 255, 255 );
//		joons.jr.fill(JoonsWrapper.MATERIAL_GLASS, 5, 5, 5);		p.fill( 255, 255, 255 );
////		joons.jr.fill(JoonsWrapper.MATERIAL_AMBIENT_OCCLUSION, 10, 10, 30 + 20f * P.sin(AnimationLoop.progressRads()), 0, 0, 0,   50, 16);
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
			p.rotateY(P.HALF_PI - P.QUARTER_PI + FrameLoop.progressRads());
			p.translate(x, y, z);
//			if(i%2 == 0) {
				joons.jr.fill(JoonsWrapper.MATERIAL_GLASS, 200 + 55f * P.sin(FrameLoop.progressRads() * 1f), 100 + 55f * P.sin(i/5f + FrameLoop.progressRads() * 1f), 200 + 55f * P.sin(i/3f + FrameLoop.progressRads() * 1f));		
												   p.fill( 200 + 55f * P.sin(FrameLoop.progressRads() * 1f), 100 + 55f * P.sin(i/5f + FrameLoop.progressRads() * 1f), 200 + 55f * P.sin(i/3f + FrameLoop.progressRads() * 1f) );
//			} else {
//				joons.jr.fill(JoonsWrapper.MATERIAL_LIGHT, 255, 255, 255);		p.fill( 255, 255, 255 );
//			}
			float size = 10f + 10f * P.sin(-FrameLoop.progressRads() + rads + P.QUARTER_PI);
			p.sphere(size);
			p.popMatrix();
		}
		
		// draw floor
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(0, objHeight/2);
		joons.jr.fill(JoonsWrapper.MATERIAL_PHONG, 255, 255, 255);		p.fill( 255, 255, 255 );
		p.box(p.height * 4, 2, p.height * 4);
		p.popMatrix();
	}

}