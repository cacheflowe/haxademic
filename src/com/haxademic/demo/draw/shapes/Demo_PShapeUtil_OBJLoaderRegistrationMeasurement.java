package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_OBJLoaderRegistrationMeasurement 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PImage sprite;
	protected PVector modelSize;
	protected boolean overrideColor = false;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 768);
		Config.setProperty(AppSettings.HEIGHT, 768);
	}
	
	protected void firstFrame() {
		// build shape and assign texture
		shape = DemoAssets.objHumanoid();
		
		// normalize shape (scaling centers)
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.5f);
		PShapeUtil.setOnGround(shape);
//		PShapeUtil.setRegistrationOffset(shape, 0, -0.5f, 0);
//		PShapeUtil.scaleShapeToWidth(shape, p.width * 0.4f);
//		PShapeUtil.scaleShapeToDepth(shape, p.width * 0.4f);
//		PShapeUtil.meshRotateOnAxis(shape, P.PI, P.Z);
		
		// check size
		modelSize = new PVector(PShapeUtil.getWidth(shape), PShapeUtil.getHeight(shape), PShapeUtil.getDepth(shape));
		DebugView.setValue("shape.width", modelSize.x);
		DebugView.setValue("shape.height", modelSize.y);
		DebugView.setValue("shape.depth", modelSize.z);
	}
		
	public void drawApp() {
		// clear the screen
		background(10);
		p.noStroke();
		PG.setBetterLights(p.g);

		// rotate camera
		p.translate(p.width/2, p.height * 0.65f);
		p.rotateX(P.map(p.mouseY, 0, p.height, 0.5f, -0.5f));
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		
		// draw floor
		p.pushMatrix();
		p.rotateX(P.HALF_PI);
		PG.setDrawCenter(p.g);
		p.fill(0, 200, 0);
		p.rect(0, 0, p.width/2, p.width/2);
		p.popMatrix();

		// draw shape
		PG.setDrawCorner(p.g);
		p.shape(shape);
		
		// draw extents
		p.noFill();
		p.stroke(255);
		p.pushMatrix();
		p.translate(0, -modelSize.y / 2f, 0);
		p.box(modelSize.x, modelSize.y, modelSize.z);
		p.popMatrix();
	}
	
}