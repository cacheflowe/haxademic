package com.haxademic.demo.draw.shapes.polygons;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.polygons.Polygon;

public class Demo_PolygonFill 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Polygon poly;

	public void setupFirstFrame() {
		poly = Polygon.buildShape(p.width/2, p.height/2, 3, 200);
	}

	public void drawApp() {
		p.background(0);

		// draw toxiclibs mesh
		p.fill(255);
		poly.bgColor(0xff00ff00);
		poly.draw(p.g, false);
	}

}
