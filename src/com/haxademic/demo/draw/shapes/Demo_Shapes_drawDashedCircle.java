package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.render.FrameLoop;

public class Demo_Shapes_drawDashedCircle 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void drawApp() {
		p.background(0);
		p.stroke(255);
		p.strokeWeight(1f + 4f * Mouse.xNorm);
		
		PG.setCenterScreen(p.g);
		p.rotateX(0.75f * Mouse.yNorm);
		
		float spacing = 10;
		float numLines = p.height / spacing;
		boolean roundsDashLength = p.frameCount % 200 > 100;
		roundsDashLength = true;
		for (int i = 0; i < numLines; i++) {
			float radius = 20 + i * 20;
			float dashLength = 20;
			float offset = FrameLoop.count(0.03f);
			p.push();
			p.translate(0, 0, P.sin(FrameLoop.count(0.04f) + i*0.1f) * 100f * Mouse.yNorm);
			Shapes.drawDashedCircle(p.g, 0, 0, radius, dashLength, offset, roundsDashLength);
			p.pop();
		}
		
	}
	
}