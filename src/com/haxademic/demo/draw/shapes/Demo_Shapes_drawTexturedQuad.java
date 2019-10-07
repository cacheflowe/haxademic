package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.media.DemoAssets;

public class Demo_Shapes_drawTexturedQuad 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		background(0);
		lights();
		translate(width/2, height/2, 0);

		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		p.rotateX(P.map(p.mouseY, 0, p.height, P.TWO_PI * 2, 0));
		
		Shapes.drawTexturedRect(p.g, DemoAssets.justin());
	}
}
