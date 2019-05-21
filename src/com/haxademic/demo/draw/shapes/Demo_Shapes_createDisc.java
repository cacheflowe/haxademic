package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;

import processing.core.PShape;

public class Demo_Shapes_createDisc
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	protected void setupFirstFrame() {
		shape = Shapes.createDisc(p.height / 2, 36, 20);
		shape.setTexture(DemoAssets.textureNebula());
	}

	public void drawApp() {
		PG.setBetterLights(p);
		background(0);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);
		
		p.debugView.setTexture(DemoAssets.textureNebula());
		// draw can
//		shape.disableStyle();
//		p.fill(255);
//		p.noFill();
//		p.stroke(255);
//		p.strokeWeight(2);
		if(p.frameCount % 100 < 50) {
			p.shape(shape);
		} else {
			PShapeUtil.drawTriangles(p.g, shape, DemoAssets.squareTexture(), 1f);
		}
	}

}
