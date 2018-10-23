package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;

public class Demo_Shapes_drawDashedLine_Pyramid 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void drawApp() {
		p.background(0);
		p.stroke(255);
		p.strokeWeight(1f + 4f * p.mousePercentX());
		DrawUtil.setCenterScreen(p.g);
		// p.ortho();
		DrawUtil.basicCameraFromMouse(p.g);
		
		float dashLength = 25f + 10f * P.sin(p.frameCount * 0.01f);
		
		// base
		float baseSize = 300f;
		float baseSizeHalf = baseSize / 2f;
		Shapes.drawDashedLine(p.g, -baseSizeHalf, 0, -baseSizeHalf, baseSizeHalf, 0, -baseSizeHalf, dashLength, false);
		Shapes.drawDashedLine(p.g, baseSizeHalf, 0, -baseSizeHalf, baseSizeHalf, 0, baseSizeHalf, dashLength, false);
		Shapes.drawDashedLine(p.g, baseSizeHalf, 0, baseSizeHalf, -baseSizeHalf, 0, baseSizeHalf, dashLength, false);
		Shapes.drawDashedLine(p.g, -baseSizeHalf, 0, baseSizeHalf, -baseSizeHalf, 0, -baseSizeHalf, dashLength, false);
		
		// cone
		float coneHeight = 190;
		Shapes.drawDashedLine(p.g, -baseSizeHalf, 0, -baseSizeHalf, 0, -coneHeight, 0, dashLength, false);
		Shapes.drawDashedLine(p.g, baseSizeHalf, 0, -baseSizeHalf, 0, -coneHeight, 0, dashLength, false);
		Shapes.drawDashedLine(p.g, baseSizeHalf, 0, baseSizeHalf, 0, -coneHeight, 0, dashLength, false);
		Shapes.drawDashedLine(p.g, -baseSizeHalf, 0, baseSizeHalf, 0, -coneHeight, 0, dashLength, false);
	}
	
}