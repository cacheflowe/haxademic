package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class Demo_PShapeUtil_getSubShapeByColor 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	
	protected PShape bumper;
	protected PShape pinkCar;
	protected PShape yellow;
	protected PShape windows;

	// we need to create a separate version if we want to disableStyle() for dynamic drawing colors

	protected PShape objWire;
	
	protected PShape bumperWire;
	protected PShape pinkCarWire;
	protected PShape yellowWire;
	protected PShape windowsWire;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 768);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
	}
	
	protected void setupFirstFrame() {
		// build shape and assign texture
		obj = p.loadShape(FileUtil.getFile("haxademic/models/pink-car.obj"));
		objWire = p.loadShape(FileUtil.getFile("haxademic/models/pink-car.obj"));
//		shape.disableStyle();
		
		// normalize original shape
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, p.height* 0.5f);
		PShapeUtil.meshRotateOnAxis(obj, P.PI, P.Z);

		PShapeUtil.centerShape(objWire);
		PShapeUtil.scaleShapeToHeight(objWire, p.height* 0.5f);
		PShapeUtil.meshRotateOnAxis(objWire, P.PI, P.Z);
		
		// Bumper Purple - 			Kd 0.17254902422428 0.21568627655506 0.44705882668495
		// Bumper Purple Light - 	Kd 0.30588236451149 0.20000000298023 0.43137255311012
		// Paint Pink Darker - 		Kd 0.52999997138977 0.14839999377728 0.38372001051903
		// Paint Pink - 			Kd 0.81176471710205 0.30035293102264 0.61572355031967
		// Yellow                   Kd 0.92549020051956 0.84705883264542 0.40000000596046
		// Car Window				Kd 0.07999999821186 0.13028571009636 0.40000000596046

		float colorMatchThreshold = 0.02f;

		bumper = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(obj, 0.17254902422428f, 0.21568627655506f, 0.44705882668495f, bumper, colorMatchThreshold);
		PShapeUtil.addShapesByColor(obj, 0.30588236451149f, 0.20000000298023f, 0.43137255311012f, bumper, colorMatchThreshold);

		pinkCar = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(obj, 0.52999997138977f, 0.14839999377728f, 0.38372001051903f, pinkCar, colorMatchThreshold);
		PShapeUtil.addShapesByColor(obj, 0.81176471710205f, 0.30035293102264f, 0.61572355031967f, pinkCar, colorMatchThreshold);
		
		yellow = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(obj, 0.92549020051956f, 0.84705883264542f, 0.40000000596046f, yellow, colorMatchThreshold);
		
		windows = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(obj, 0.07999999821186f, 0.13028571009636f, 0.40000000596046f, windows, colorMatchThreshold);
		
		// wireframe version
		
		bumperWire = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(objWire, 0.17254902422428f, 0.21568627655506f, 0.44705882668495f, bumperWire, colorMatchThreshold);
		PShapeUtil.addShapesByColor(objWire, 0.30588236451149f, 0.20000000298023f, 0.43137255311012f, bumperWire, colorMatchThreshold);
		bumperWire.disableStyle();
		
		pinkCarWire = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(objWire, 0.52999997138977f, 0.14839999377728f, 0.38372001051903f, pinkCarWire, colorMatchThreshold);
		PShapeUtil.addShapesByColor(objWire, 0.81176471710205f, 0.30035293102264f, 0.61572355031967f, pinkCarWire, colorMatchThreshold);
		pinkCarWire.disableStyle();
		
		yellowWire = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(objWire, 0.92549020051956f, 0.84705883264542f, 0.40000000596046f, yellowWire, colorMatchThreshold);
		yellowWire.disableStyle();
		
		windowsWire = p.createShape(P.GROUP);
		PShapeUtil.addShapesByColor(objWire, 0.07999999821186f, 0.13028571009636f, 0.40000000596046f, windowsWire, colorMatchThreshold);
		windowsWire.disableStyle();
	}
			
	public void drawApp() {
		// clear the screen
		pg.beginDraw();
		pg.background(0);
		PG.setBasicLights(pg);

		// rotate camera
		pg.translate(p.width/2, p.height/2, -1000f);
		pg.rotateX(-0.4f);
		pg.rotateY(p.frameCount * 0.01f);
		
		// show different modes
		boolean origColor = (p.frameCount % 200 < 100);
		p.debugView.setValue("originalColor", origColor);
		if(origColor == true) {
			if(p.frameCount % 200 > 25) pg.shape(bumper);
			if(p.frameCount % 200 > 50) pg.shape(pinkCar);
			if(p.frameCount % 200 > 75) pg.shape(yellow);
			if(p.frameCount % 200 > 0) pg.shape(windows);
		} else {
			pg.fill(255);
			pg.stroke(0);
			pg.strokeWeight(2);
			pg.shape(bumperWire);
	
			pg.fill(255, 100, 200);
			pg.stroke(255);
			pg.strokeWeight(2);
			pg.shape(pinkCarWire);
			
			pg.fill(0, 255, 0);
			if(p.frameCount % 10 < 5) pg.fill(0, 0, 0);
			pg.stroke(255);
			pg.strokeWeight(2);
			pg.shape(yellowWire);
			
			pg.fill(255, 70);
			pg.stroke(0, 0);
			pg.strokeWeight(2);
			pg.shape(windowsWire);
		}
		 
		pg.endDraw();
		p.image(pg, 0, 0);
	}
	
}