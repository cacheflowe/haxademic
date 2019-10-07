package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PShape;

public class Demo_PShapeUtil_setMaterialColor 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape obj;
	protected float modelHeight;
	
	protected void setupFirstFrame() {
		// build obj PShape and scale to window
		obj = DemoAssets.objSkullRealistic();
		float objHeight = p.height * 0.7f;
		PShapeUtil.centerShape(obj);
		PShapeUtil.scaleShapeToHeight(obj, objHeight);
	}

	public void drawApp() {
		background(0);
		p.translate(p.width/2f, p.height/2f, 0);
		p.rotateX(P.map(p.mouseY, 0, p.height, P.PI, -P.PI));
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		PG.setBetterLights(p);
//		p.lights();
		
		PShapeUtil.setWireframeColor(obj, 
				p.color(50 + 50 * P.sin(p.frameCount * 0.03f), 50 + 50 * P.sin(p.frameCount * 0.02f), 50 + 50 * P.sin(p.frameCount * 0.015f)), 
				p.color(127 + 127 * P.sin(p.frameCount * 0.02f), 127 + 127 * P.sin(p.frameCount * 0.01f), 127 + 127 * P.sin(p.frameCount * 0.005f))
				);

		// draw mesh 
		p.shape(obj);
	}
		
}