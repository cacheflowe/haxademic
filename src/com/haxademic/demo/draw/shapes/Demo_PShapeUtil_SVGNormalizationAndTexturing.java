package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_SVGNormalizationAndTexturing 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PShape> shapes;
	protected int curShapeIndex = 0;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
	}
	
	protected void firstFrame() {

		
		shapes = new ArrayList<PShape>();
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/money.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/weed.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/gun.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/gun-uzi.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/speaker.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/money-bag.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/diamond.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/car.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/coin.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.haxademicDataPath() + "svg/cacheflowe-logo.svg" ).getTessellation() );
		
		// normalize shapes
		for (PShape shape : shapes) {
			
			// getTesselation seems to leave off the last triangle, so we can put one back :-D 
			// https://github.com/processing/processing/blob/f434d2c2303c8d03e265e14972b652c595e6cdf7/core/src/processing/opengl/PShapeOpenGL.java#L2652
			// 
			PShapeUtil.repairMissingSVGVertex(shape);

			// center and scale
			PShapeUtil.centerShape(shape);
			PShapeUtil.scaleShapeToMaxAbsY(shape, p.height * 0.4f);
			
			// add UV coordinates to OBJ based on model extents
			PShapeUtil.addTextureUVToShape(shape, DemoAssets.justin());
		}
	}
	
	protected void drawApp() {
		p.background(0);

		// draw
		p.translate(p.width/2, p.height/2);
		p.rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
		p.noStroke();
		PShape curShape = shapes.get( curShapeIndex );
		PShapeUtil.drawTriangles(p.g, curShape, DemoAssets.justin(), 1);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			curShapeIndex++;
			if( curShapeIndex >= shapes.size() ) curShapeIndex = 0; 
		}
	}
	
}