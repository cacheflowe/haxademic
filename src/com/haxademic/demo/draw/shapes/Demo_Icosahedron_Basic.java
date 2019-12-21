package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Icosahedron;
import com.haxademic.core.draw.shapes.PShapeUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;
import processing.core.PShape;

public class Demo_Icosahedron_Basic
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage texture;
	protected PShape icosa;

	public void firstFrame() {

		int detail = 4;
		icosa = Icosahedron.createIcosahedron(p.g, detail, DemoAssets.textureJupiter());
		PShapeUtil.scaleShapeToHeight(icosa, p.height * 0.75f);
	}
	
	public void drawApp() {
		background(0);
		PG.setDrawCorner(p);
		
		// test mesh detail
		OpenGLUtil.setWireframe(p.g, (p.frameCount % 200 < 100));
		
		// draw from center
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI));
		p.shape(icosa);
		p.popMatrix();
	}

}

