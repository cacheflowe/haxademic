package com.haxademic.demo.render.joons;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.JoonsWrapper;
import com.haxademic.core.render.Renderer;

import processing.core.PShape;

public class Demo_Joons_PShapeRenderSVG
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float objHeight;
	protected float frames = 60;
	
	protected void config() {
		Config.setProperty( AppSettings.SUNFLOW, true );
		Config.setProperty( AppSettings.SUNFLOW_ACTIVE, true );
		Config.setProperty( AppSettings.SUNFLOW_QUALITY, AppSettings.SUNFLOW_QUALITY_LOW );

		Config.setProperty( AppSettings.WIDTH, 960 );
		Config.setProperty( AppSettings.HEIGHT, 720 );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE, false );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_START_FRAME, 1 );
//		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 3 + (int) frames - 1 );
		Config.setProperty( AppSettings.RENDERING_IMAGE_SEQUENCE_STOP_FRAME, 2 );
		Config.setProperty(AppSettings.RENDER_DEMO_SCREENSHOT_FRAME, 2);
	}

	protected void firstFrame() {
		// load & repair tesselated shape
		obj = DemoAssets.shapeX().getTessellation(); // p.loadShape( FileUtil.getFile("svg/fractal-1.svg")).getTessellation();
		PShapeUtil.repairMissingSVGVertex(obj);
			
		// create extrusion
		obj = PShapeUtil.createExtrudedShape( obj, 175 );
		PShapeUtil.centerShape(obj);
		objHeight = p.height * 0.35f;
		PShapeUtil.scaleShapeToMaxAbsY(obj, objHeight);
	}


	protected void drawApp() {
		JoonsWrapper joons = Renderer.instance().joons;
		if(Config.getBoolean(AppSettings.SUNFLOW_ACTIVE, false) == false) {
			p.background(0);
			p.lights();
			p.noStroke();
		}
		joons.jr.background(JoonsWrapper.BACKGROUND_AO);
		p.translate(0, 0, -width);
		
		// progress
		// float progress = (p.frameCount % frames) / frames;

		// draw environment
		p.pushMatrix();
		setUpRoom();
		p.popMatrix();

		// draw shape
		p.pushMatrix();
//		joons.jr.fill(JoonsWrapper.MATERIAL_AMBIENT_OCCLUSION, 200, 200, 200);		p.fill( 20, 20, 20 );
		PShapeUtil.drawTrianglesJoons(p, obj, 1, JoonsWrapper.MATERIAL_AMBIENT_OCCLUSION);
		p.popMatrix();

		// draw floor
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(0, objHeight);
		joons.jr.fill(JoonsWrapper.MATERIAL_SHINY, 255, 255, 255, 0.6f);		p.fill( 255, 255, 255 );
		p.box(p.height * 4, 2, p.height * 2f);
		p.popMatrix();
		
		// draw back wall
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(0, 0, -5);
		joons.jr.fill(JoonsWrapper.MATERIAL_PHONG, 255, 255, 255);		p.fill( 255, 255, 255 );
		p.box(p.height * 4, p.height * 4, 2);
		p.popMatrix();
	}

	protected void setUpRoom() {
		JoonsWrapper joons = Renderer.instance().joons;
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