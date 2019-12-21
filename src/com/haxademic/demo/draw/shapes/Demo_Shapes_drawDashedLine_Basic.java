package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.mouse.Mouse;

public class Demo_Shapes_drawDashedLine_Basic 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		p.background(0);
		p.stroke(255);
		p.strokeWeight(1f + 4f * Mouse.xNorm);
		
		float spacing = 10;
		float numLines = p.height / spacing;
		boolean roundsDashLength = p.frameCount % 200 > 100;
		for (int i = 0; i < numLines; i++) {
			float y = spacing/ 2 + i * 10;
			Shapes.drawDashedLine(p.g, 0, y, 0, p.width, y, 0, 8 + i, roundsDashLength);
		}
		
	}
	
}