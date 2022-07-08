package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;

public class Demo_PShapeUtil_svgTesselationRepair 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PShape shapeTess;
	protected PShape shapeRepaired;
	protected int curShapeIndex = 0;
	
	protected void firstFrame() {
		shape = p.loadShape(FileUtil.getPath("svg/nine.svg"));
		shapeTess = shape.getTessellation();
		shapeRepaired = shape.getTessellation();

		PShapeUtil.scaleShapeToHeight(shape, p.height * 0.3f);
		PShapeUtil.scaleShapeToHeight(shapeTess, p.height * 0.3f);
		PShapeUtil.scaleShapeToHeight(shapeRepaired, p.height * 0.3f);
		PShapeUtil.centerShape(shape);
		PShapeUtil.centerShape(shapeTess);
		PShapeUtil.centerShape(shapeRepaired);
		
		P.out(PShapeUtil.vertexCount(shape));
		P.out(PShapeUtil.vertexCount(shapeTess));
		P.out(PShapeUtil.vertexCount(shapeRepaired));
		
		// getTesselation *can* leave off the last triangle 
//		PShapeUtil.repairMissingSVGVertex(shapeRepaired);

		// center and scale
	}
	
	protected void drawApp() {
		// set context
		p.background(127);
		PG.setDrawCorner(p);
		PG.setCenterScreen(p);

		// draw shapes
		PG.basicCameraFromMouse(p.g, 0.2f);
		p.shape(shape, -200, 0);
		p.shape(shapeTess);
		p.shape(shapeRepaired, 200, 0);
		
		// draw triangles
		// PShapeUtil.drawTriangles(p.g, shape, null, 1);
	}
	
}