package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.draw.shapes.PShapeUtil.PShapeCopy;
import com.haxademic.core.ui.UI;

import processing.core.PShape;

public class Demo_PShape_perfTest 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<PShape> shapes = new ArrayList<PShape>();
	protected ArrayList<PShape> shared = new ArrayList<PShape>();
	protected PShape group;
	protected String MODE = "MODE";
	
	protected void config() {
		Config.setAppSize(1280, 720);
	}
	
	protected void firstFrame() {
		// UI
		UI.addTitle("Perf config");
		UI.addSlider(MODE, 1, 0, 1, 1, false);
		
		// config build shapes
		float shapeSize = 6;
		float shapeSpacing = 8;
		p.sphereDetail(18);
		int vertCount = PShapeUtil.vertexCount(PShapeUtil.createSphere(1, 0));

		// create individual PShapes
		int startBuildTime = p.millis();
		int cols = 240;
		int rows = 100;
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float gridX = -(shapeSpacing * cols/2) + (x * shapeSpacing);
				float gridY = -(shapeSpacing * rows/2) + (y * shapeSpacing);
				shapes.add(PShapeUtil.createSphere(shapeSize, gridX, gridY, 0, 127 + 127 * p.color(P.sin(x/10f), 127 + 127 * P.sin(y/10f), 127 + 127 * P.sin(x+y/100f)), 0, 0));
			}			
		}
		DebugView.setValue("Individual PShape time", p.millis() - startBuildTime + "ms");
		
		// create PShapes inside a group
		startBuildTime = p.millis();
		group = p.createShape(P.GROUP);
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				float gridX = -(shapeSpacing * cols/2) + (x * shapeSpacing);
				float gridY = -(shapeSpacing * rows/2) + (y * shapeSpacing);
				group.addChild(PShapeUtil.createSphere(shapeSize, gridX, gridY, 0, 127 + 127 * p.color(P.sin(x/10f), 127 + 127 * P.sin(y/10f), 127 + 127 * P.sin(x+y/100f)), 0, 0));
			}			
		}
		DebugView.setValue("Group PShape time", p.millis() - startBuildTime + "ms");
		DebugView.setValue("Num shapes", cols * rows);
		DebugView.setValue("Num verts", cols * rows * vertCount);
		
		// outer sphere
		shared.add(PShapeUtil.createSphere(3000, p.color(180, 180, 0)));
		PShape innerSphere = PShapeCopy.copyShape(shared.get(shared.size() - 1));
		PShapeUtil.setBasicShapeStyles(innerSphere, 0, p.color(255), 10);
		innerSphere.scale(0.6f);
		shared.add(innerSphere);
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
		
		// draw meshes in different mode
		if(UI.valueInt(MODE) == 0) {
			for (int i = 0; i < shapes.size(); i++) p.shape(shapes.get(i));
		} else {
			p.shape(group);
		}
		// shared elements
		for (int i = 0; i < shared.size(); i++) p.shape(shared.get(i));
	}
		
}