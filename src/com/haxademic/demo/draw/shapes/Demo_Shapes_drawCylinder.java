package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;

public class Demo_Shapes_drawCylinder
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		background(0);
		PG.setCenterScreen(p);
		PG.setBetterLights(p);
		PG.basicCameraFromMouse(p.g);
		
		// draw can
		p.stroke(255);
		p.noStroke();
		p.fill(0, 255, 0);
		Shapes.drawCylinder(p.g, 36, p.width * 0.1f, p.width * 0.2f, p.height * 0.5f, true);	
	}

}
