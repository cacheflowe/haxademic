package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;

import processing.core.PShape;

public class Demo_Shapes_createCan
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	protected void setupFirstFrame() {
		shape = 	Shapes.createCan(p.width * 2, p.height * 2, 140).getTessellation();
		PShapeUtil.repairMissingSVGVertex(shape);
	}

	public void drawApp() {
		DrawUtil.setBetterLights(p);
		background(0);
		translate(width/2,height/2);
		DrawUtil.basicCameraFromMouse(p.g);
		
		// draw can
		shape.disableStyle();
		p.noStroke();
		p.fill(255);
		PShapeUtil.drawTriangles(p.g, shape, DemoAssets.squareTexture(), 1f);			
	}

}
