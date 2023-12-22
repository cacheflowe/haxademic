package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;

import processing.core.PShape;

public class Demo_PShape_cachedCustomShapeDashedCircle 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;

	protected void config() {
		// Config.setAppSize(960, 960);
	}

	protected void firstFrame() {
		float weight = 3;
		int color = p.color(255, 200, 0);
		shape = Shapes.drawDashedCircle(0, 0, 400, 20, 0, true, color, weight);
	}

	protected void drawApp() {
		background(0);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g, 1.3f);
		// PG.setDrawFlat2d(p, true);
		PG.setBetterLights(p);
		// p.lights();
		p.shape(shape);
	}

}