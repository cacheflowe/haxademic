package com.haxademic.demo.render.joons;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PShape;

public class Demo_PShapeUtil_shapeFromImage_Joons 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	String[] materials = new String[] {
			JoonsWrapper.MATERIAL_PHONG,
			JoonsWrapper.MATERIAL_AMBIENT_OCCLUSION,
			JoonsWrapper.MATERIAL_GLASS,
			JoonsWrapper.MATERIAL_DIFFUSE,
			JoonsWrapper.MATERIAL_MIRROR,
			JoonsWrapper.MATERIAL_SHINY,
	};

	protected int FRAMES = 360;
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
		Config.setProperty( AppSettings.LOOP_FRAMES, FRAMES );
		Config.setProperty( AppSettings.SUNFLOW, true );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, true );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_HIGH );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE, true );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 3 );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 3 + FRAMES );
	}
	
	public void firstFrame() {
		shape = PShapeUtil.shapeFromImage(DemoAssets.textureCursor());
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleVertices(shape, 1, 1, 4);
		PShapeUtil.scaleShapeToExtent(shape, p.height * 0.2f);
	}
	
	public void drawApp() {
		if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == false) {
			p.background(200, 255, 200);
//			PG.setCenterScreen(p);
			PG.setBetterLights(p);
			p.noStroke();
//			PG.basicCameraFromMouse(p.g);
		} else {
			joons.jr.background(JoonsWrapper.BACKGROUND_AO);
			joons.setUpRoom(255, 255, 255);
		}
		p.translate(0, -p.height * 0.1f, -p.width);
		p.rotateX(-0.2f);

		// draw shape
		float segmentRads = P.TWO_PI / materials.length;
		for (int i = 0; i < materials.length; i++) {
			float curRads = segmentRads * i - FrameLoop.progressRads();
			float radius = 400;
			p.pushMatrix();
			p.translate(P.cos(curRads) * radius, p.height * 0.1f * P.sin(segmentRads * i * 2f + FrameLoop.progressRads() * 2), P.sin(curRads) * radius);
//			p.rotateY(-curRads);
			p.rotateZ(P.HALF_PI);
			p.rotateX(-curRads + P.HALF_PI);
//		p.rotateY(0.3f + p.frameCount * 0.1f);
//			PShapeUtil.drawTriangles(p.g, shape, null, 1);
			PShapeUtil.drawTrianglesJoons(p, shape, 1, materials[i]);
			p.popMatrix();
		}
		
		// draw floor
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(0, p.height * 0.3f);
		PShapeUtil.setColorForJoons(JoonsWrapper.MATERIAL_MIRROR, p.color(100, 0, 255));
		p.box(p.height * 4, 2, p.height * 4);
		p.popMatrix();

	}
}