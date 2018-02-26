package com.haxademic.sketch.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class JOY_01 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage sprite;
	protected PVector modelSize;
	protected boolean overrideColor = false;

	protected void overridePropsFile() {
		int FRAMES = 120;
		p.appConfig.setProperty(AppSettings.WIDTH, 1024);
		p.appConfig.setProperty(AppSettings.HEIGHT, 768);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
	}
	
	protected void setupFirstFrame() {
		// build shape and assign texture
		shape = p.loadShape(FileUtil.getFile("models/joy-hoop/joy_hoop.obj"));
		
		// normalize shape (scaling centers)
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.85f);
//		PShapeUtil.setOnGround(shape);
//		PShapeUtil.setRegistrationOffset(shape, 0, -0.5f, 0);
//		PShapeUtil.scaleShapeToWidth(shape, p.width * 0.4f);
//		PShapeUtil.scaleShapeToDepth(shape, p.width * 0.4f);
//		PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
		
		// check size
		modelSize = new PVector(PShapeUtil.getWidth(shape), PShapeUtil.getHeight(shape), PShapeUtil.getDepth(shape));
		p.debugView.setValue("shape.width", modelSize.x);
		p.debugView.setValue("shape.height", modelSize.y);
		p.debugView.setValue("shape.depth", modelSize.z);
	}
		
	public void drawApp() {
		// clear the screen
		background(0);
		p.noStroke();
		DrawUtil.setBetterLights(p.g);

		// rotate camera
		p.translate(p.width/2, p.height/2);
//		p.rotateX(P.map(p.mouseY, 0, p.height, 0.5f, -0.5f));
//		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		p.rotateY(loop.progressRads());
		
		// draw shape
		DrawUtil.setDrawCorner(p.g);
		p.shape(shape);
	}
	
}