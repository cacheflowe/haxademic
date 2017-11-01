package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.shapes.PShapeSolid;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_PShapeUtil_SVGNormalizationAndTexturing 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PShape> shapes;
	protected int curShapeIndex = 0;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
	}
	
	public void setup() {
		super.setup();
		
		shapes = new ArrayList<PShape>();
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/microphone.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/weed.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/gun-uzi.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/speaker.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/money-bag.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/diamond.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/car.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/coin.svg" ).getTessellation() );
		shapes.add( p.loadShape( FileUtil.getHaxademicDataPath() + "svg/cacheflowe-logo.svg" ).getTessellation() );
		
		// normalize shapes
		for (PShape shape : shapes) {
			
			// getTesselation seems to leave off the last triangle, so we can put one back :-D 
			// https://github.com/processing/processing/blob/f434d2c2303c8d03e265e14972b652c595e6cdf7/core/src/processing/opengl/PShapeOpenGL.java#L2652
			// 
			PShapeUtil.repairMissingSVGVertex(shape);

			// center and scale
			PShapeUtil.centerShape(shape);
			PShapeUtil.scaleShapeToHeight(shape, p.height * 0.4f);
			
			// add UV coordinates to OBJ based on model extents
			float modelExtent = PShapeUtil.getShapeMaxExtent(shape);
			PShapeUtil.addTextureUVToShape(shape, DemoAssets.justin(), modelExtent, true);
		}
	}
	
	public void drawApp() {
		p.background(0);

		// get current svg
		PShape curShape = shapes.get( curShapeIndex );
		curShape.disableStyle();

		// draw
		p.translate(p.width/2, p.height/2);
		p.noStroke();
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