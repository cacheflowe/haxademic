package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_PShapeUtil_OBJLoaderRegistrationMeasurement 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage sprite;
	protected boolean overrideColor = false;

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 768);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
	}
	
	protected void setupFirstFrame() {
		// build shape and assign texture
		shape = DemoAssets.objSkullRealistic();
		
		// normalize shape (scaling centers)
//		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.5f);
//		PShapeUtil.scaleShapeToWidth(shape, p.width * 0.4f);
		PShapeUtil.scaleShapeToDepth(shape, p.width * 0.4f);
//		PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
		
		// check size
		p.debugView.setValue("shape.width", PShapeUtil.getWidth(shape));
		p.debugView.setValue("shape.height", PShapeUtil.getHeight(shape));
		p.debugView.setValue("shape.depth", PShapeUtil.getDepth(shape));
	}
		
	public void drawApp() {
		// clear the screen
		background(10);
		p.noStroke();
		DrawUtil.setBetterLights(p.g);

		// rotate camera
		p.translate(p.width/2, p.height/2);
		p.rotateX(P.map(p.mouseY, 0, p.height, 0.5f, -0.5f));
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		
		// draw shape
		p.shape(shape);
	}
	
}