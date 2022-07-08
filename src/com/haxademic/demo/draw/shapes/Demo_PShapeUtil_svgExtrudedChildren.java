package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PShape;
import processing.core.PVector;

public class Demo_PShapeUtil_svgExtrudedChildren 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected ArrayList<PShape> shapeChildren = new ArrayList<PShape>();
	protected PVector modelSize;
	protected boolean overrideColor = false;

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}
	
	protected void firstFrame() {
		// build shape and assign texture
		shape = p.loadShape(FileUtil.getPath("svg/nine.svg"));
//		PShapeUtil.centerShape(shape);
		for (int j = 0; j < shape.getChildCount(); j++) {
			PShape subShape = shape.getChild(j);
//			shapeColor = p.color(j * 50);
			PShape subShapeCopy = subShape.getTessellation();
			int shapeColor = subShapeCopy.getFill(0);
			P.out("shapeColor", shapeColor);
			ColorUtil.printColor(shapeColor);
			PShapeUtil.repairMissingSVGVertex(subShapeCopy);
			PShape childShape = PShapeUtil.createExtrudedShape(subShapeCopy, 2 + 2 * j);
			PShapeUtil.setMaterialColor(childShape, shapeColor);
			shapeChildren.add(childShape);
			PShapeUtil.scaleShapeToHeight(childShape, p.height * 0.4f);
		}
		
		// normalize shape (scaling centers)
//		P.out("vertexCount", PShapeUtil.vertexCount(shapeSolid));
	}
		
	protected void drawApp() {
		// clear the screen
		background(127);
		p.noStroke();
		PG.setCenterScreen(p);
		PG.setDrawCorner(p.g);
		PG.setBetterLights(p.g);
		PG.basicCameraFromMouse(p.g, 0.9f);

		// draw shape
//		for (int i = 0; i < 1; i++) {
		for (int i = 0; i < shapeChildren.size(); i++) {
			PShape childShape = shapeChildren.get(i);
			p.shape(childShape);
//			PShapeUtil.debugVertices(p.g, childShape);
//			PShapeUtil.drawTriangles(p.g, childShape, null, 1);
		}
	}
	
}