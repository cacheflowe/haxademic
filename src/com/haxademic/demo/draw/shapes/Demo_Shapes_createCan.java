package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;

import processing.core.PShape;

public class Demo_Shapes_createCan
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	
	public void setup()	{
		super.setup();
		shape = 	Shapes.createCan(p.width * 2, p.height * 2, 140).getTessellation();
		PShapeUtil.repairMissingSVGVertex(shape);
	}

	public void draw() {
		DrawUtil.setBetterLights(p);
		background(0);
		translate(width/2,height/2);
		rotateX(P.map(p.mouseY, 0, p.height, -1f, 1f));
		rotateY(P.map(p.mouseX, 0, p.width, -1f, 1f));
		
		// draw can
		shape.disableStyle();
		p.noStroke();
		p.fill(255);
		PShapeUtil.drawTriangles(p.g, shape, DemoAssets.squareTexture(), 1f);			
	}

}
