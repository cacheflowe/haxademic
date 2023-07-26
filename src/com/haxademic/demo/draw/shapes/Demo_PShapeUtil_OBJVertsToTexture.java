package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_OBJVertsToTexture
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PVector modelSize;

	protected void firstFrame() {
		// build shape and assign texture
		shape = DemoAssets.objSkullRealistic();
		PShapeUtil.centerShape(shape);
		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.5f);
		PShapeUtil.setOnGround(shape);
		
		// check size
		modelSize = new PVector(PShapeUtil.getWidth(shape), PShapeUtil.getHeight(shape), PShapeUtil.getDepth(shape));
		DebugView.setValue("shape.width", modelSize.x);
		DebugView.setValue("shape.height", modelSize.y);
		DebugView.setValue("shape.depth", modelSize.z);

		// obj to texture
		PGraphics texture = PShapeUtil.objVertsToTexture(shape, 64);
		DebugView.setTexture("texture", texture);
	}

	protected void drawApp() {
		// clear the screen
		background(0, 0, 0);
		p.noStroke();
		PG.setBetterLights(p.g);

		// rotate camera
		p.translate(p.width/2, p.height * 0.65f);
		PG.basicCameraFromMouse(p.g);
		
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