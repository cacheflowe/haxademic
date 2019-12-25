package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_createExtrudedShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PShape> shapes;
	protected ArrayList<PShape> shapesExtruded;
	protected int curShapeIndex = 0;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
	}
	
	public void firstFrame() {

		
		shapes = new ArrayList<PShape>();
		shapes.add( p.loadShape( FileUtil.getPath("svg/cacheflowe-logotype-new.svg")).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getPath("svg/microphone.svg")).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getPath("svg/money.svg")).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getPath("svg/weed.svg")).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getPath("svg/diamond.svg")).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getPath("svg/coin.svg")).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getPath("svg/cacheflowe-logo.svg")).getTessellation() );
		
		shapesExtruded = new ArrayList<PShape>();
		for (PShape shape : shapes) {
			// center and scale
			PShapeUtil.centerShape(shape);
			PShapeUtil.scaleShapeToExtent(shape, p.height * 0.4f);
			
			// getTesselation seems to leave off the last triangle, so we can put one back :-D 
			// https://github.com/processing/processing/blob/f434d2c2303c8d03e265e14972b652c595e6cdf7/core/src/processing/opengl/PShapeOpenGL.java#L2652
			// 
			PShapeUtil.repairMissingSVGVertex(shape);
			
			// create extrusion
			shapesExtruded.add(PShapeUtil.createExtrudedShape( shape, 100 ));
		}
		
		// normalize shapes
		for (PShape shape : shapesExtruded) {
			// add UV coordinates to OBJ based on model extents
			PShapeUtil.addTextureUVToShape(shape, null);
		}
	}
	
	public void drawApp() {
		p.background(0);
		PG.setBetterLights(p);

		// draw
		p.translate(p.width/2, p.height/2);
//		p.rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
		p.rotateY(0.4f * P.sin(p.frameCount / 20f));
		p.noStroke();
		PShape curShape = shapesExtruded.get( curShapeIndex );
		PShapeUtil.drawTriangles(p.g, curShape, DemoAssets.textureNebula(), 1);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			curShapeIndex++;
			if( curShapeIndex >= shapes.size() ) curShapeIndex = 0; 
		}
	}
	
}