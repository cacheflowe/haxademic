package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.PShapeUtil.PShapeCopy;

import processing.core.PShape;

public class Demo_PShapeUtil_createBasicPShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PShape> shapes = new ArrayList<PShape>();
	
	protected void config() {
		Config.setAppSize(1280, 720);
	}
	
	protected void firstFrame() {
		// config build shapes
		float shapeSize = 100;
		float spacing = 120;
		float strokeWeight = 3;
		p.sphereDetail(8);
		
		// grid of shapes
		// boxes
		shapes.add(PShapeUtil.createBox(shapeSize, shapeSize, shapeSize, p.color(0, 255, 255)));
		lastShape().scale(1, 1, 0.5f);
		lastShape().translate(spacing*-2.5f, spacing*-1, 0);
		shapes.add(PShapeUtil.createBox(shapeSize, shapeSize, shapeSize, spacing*-1.5f, spacing*-1, 0, 0, p.color(255, 0, 255), strokeWeight));
		shapes.add(PShapeUtil.createBox(shapeSize, shapeSize, shapeSize, spacing*-0.5f, spacing*-1, 0, p.color(255), p.color(1), strokeWeight));
		// spheres
		shapes.add(PShapeUtil.createSphere(shapeSize, p.color(0, 255, 255)));
		lastShape().translate(spacing*0.5f, spacing*-1, 0);
		shapes.add(PShapeUtil.createSphere(shapeSize, spacing*1.5f, spacing*-1, 0, 0, p.color(255, 0, 255), strokeWeight));
		shapes.add(PShapeUtil.createSphere(shapeSize, spacing*2.5f, spacing*-1, 0, p.color(255), p.color(1), strokeWeight));
		// rect
		shapes.add(PShapeUtil.createRect(shapeSize, shapeSize, p.color(0, 255, 255)));
		lastShape().translate(spacing*-2.5f, spacing*1, 0);
		shapes.add(PShapeUtil.createRect(shapeSize, shapeSize, spacing*-1.5f, spacing*1, 0, 0, p.color(255, 0, 255), strokeWeight));
		shapes.add(PShapeUtil.createRect(shapeSize, shapeSize, spacing*-0.5f, spacing*1, 0, p.color(255), p.color(1), strokeWeight));
		// ellipse
		shapes.add(PShapeUtil.createEllipse(shapeSize, shapeSize, p.color(0, 255, 255)));
		lastShape().translate(spacing*0.5f, spacing*1, 0);
		shapes.add(PShapeUtil.createEllipse(shapeSize, shapeSize, spacing*1.5f, spacing*1, 0, 0, p.color(255, 0, 255), strokeWeight));
		shapes.add(PShapeUtil.createEllipse(shapeSize, shapeSize, spacing*2.5f, spacing*1, 0, p.color(255), p.color(1), strokeWeight));

		// outer sphere
		shapes.add(PShapeUtil.createSphere(3000, p.color(180, 180, 0)));
		PShape innerSphere = PShapeCopy.copyShape(lastShape());
		PShapeUtil.setBasicShapeStyles(innerSphere, 0, p.color(0), 10);
		innerSphere.scale(0.6f);
		shapes.add(innerSphere);
	}
	
	
	protected PShape lastShape() {
		return shapes.get(shapes.size() - 1);
	}

	protected void drawApp() {
		// setup context
		background(50);
		PG.setCenterScreen(p);
		PG.setBetterLights(p);
		PG.basicCameraFromMouse(p.g, 0.3f);
		
		// draw meshes
		for (int i = 0; i < shapes.size(); i++) {
			p.shape(shapes.get(i));
		}
	}
		
}