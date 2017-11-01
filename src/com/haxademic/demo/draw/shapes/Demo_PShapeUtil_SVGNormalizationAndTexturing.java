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
	protected int _curShapeIndex = 0;
	protected PImage img;

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
	}
	
	public void setup() {
		super.setup();
		
		img = DemoAssets.justin();

		shapes = new ArrayList<PShape>();
		
		// getTesselation seems to leave off the last triangle...
		// https://github.com/processing/processing/blob/f434d2c2303c8d03e265e14972b652c595e6cdf7/core/src/processing/opengl/PShapeOpenGL.java#L2652
		// 
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
			
			PShapeUtil.repairMissingSVGVertex(shape);
			PShapeUtil.centerShape(shape);
//			 PShapeUtil.scaleObjToExtentVerticesAdjust(shape, p.height * 0.45f);
			PShapeUtil.scaleShapeToHeight(shape, p.height * 0.45f);
			
			// add UV coordinates to OBJ based on model extents
			float modelExtent = PShapeUtil.getShapeMaxExtent(shape);
			
			PShapeUtil.addTextureUVToShape(shape, img, modelExtent, true);
		}
	}
	
	public void drawApp() {
		p.background(0);
		
//		DrawUtil.resetGlobalProps(p);
		p.pushMatrix();

		PShape curShape = shapes.get( _curShapeIndex );
		curShape.disableStyle();

		p.noStroke();
//		p.stroke(255);
//		p.noFill();

		p.translate(p.width/2, p.height/2);
		PShapeUtil.drawTriangles(p.g, curShape, img, 1);
//		p.shape(curShape, 0, 0);
		
		p.popMatrix();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			_curShapeIndex++;
			if( _curShapeIndex >= shapes.size() ) _curShapeIndex = 0; 
		}
	}}