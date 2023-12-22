package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;

import processing.core.PShape;

public class Demo_PShape_cachedCustomShape 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape s;

	protected void config() {
		Config.setAppSize(960, 960);
	}

	protected void firstFrame() {
		s = createShape(P.GROUP);
		
		float numRows = 12;
		float numCols = 48;
		float rowSpacing = p.height * 0.02f;
		float staticLightsH = numRows * rowSpacing;
		float startY = -staticLightsH / 2f;
		float segRads = P.TWO_PI / numCols;
		float staticRadius = p.width * 0.3f;
		float totemRadius = p.width * 0.15f;
		
		for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				float colRads = segRads * i;
				float lightX = P.cos(colRads) * staticRadius;
				float lightZ = P.sin(colRads) * staticRadius;
				float lightY = startY + j * rowSpacing;
				float lightFloorX = P.cos(colRads) * totemRadius;
				float lightFloorZ = P.sin(colRads) * totemRadius;
				
				PShape subShape = p.createShape();
				subShape.beginShape();
				subShape.strokeWeight(2);
				subShape.stroke(255, 127);
				subShape.noFill();
				subShape.vertex(lightX, lightY, lightZ);
				subShape.quadraticVertex(lightFloorX, lightY, lightFloorZ, lightFloorX, staticLightsH, lightFloorZ);
				subShape.endShape();
				
				s.addChild(subShape);
			}
		}
	}

	protected void drawApp() {
		background(0);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g, 1.3f);
		PG.setDrawFlat2d(p, true);
//		PG.setBetterLights(p);
		p.lights();

		p.shape(s);
	}

}