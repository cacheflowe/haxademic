package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;

public class Demo_Shapes_originAxis
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		// set context
		background(0);
		PG.setBetterLights(p);
		PG.setCenterScreen(p.g);
		PG.basicCameraFromMouse(p.g);
		
		// draw origin axis
		// PG.drawOriginAxis(p.g, 100, 20);
		PG.drawOriginAxis(p.g);
	}

}
