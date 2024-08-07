package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PShape;

public class Demo_PShapeTransparentTexture 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PShape shape;
	protected PGraphics spritePG;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, 768);
		Config.setProperty(AppSettings.HEIGHT, 768);
	}
	
	protected void firstFrame() {
		// build shape and assign texture
		shape = Shapes.createSheet(1, 400, 400);
		shape.setTexture(DemoAssets.particle());
	}
	
	protected void drawApp() {
		// clear the screen
		background(50 + 50 * P.sin(p.frameCount * 0.01f), 50 + 50 * P.sin(p.frameCount * 0.02f), 50 + 50 * P.sin(p.frameCount * 0.03f));

		// rotate camera
		p.translate(p.width/2, p.height/2);
		p.rotateY(P.map(p.mouseX, 0, p.width, 0, P.TWO_PI * 2));
		p.rotateX(P.map(p.mouseY, 0, p.height, P.TWO_PI * 2, 0));
		
		// draw another shape
		PG.setBetterLights(p);
		p.noStroke();
		p.fill(127 + 127 * P.cos(p.frameCount * 0.01f), 127 + 127 * P.cos(p.frameCount * 0.02f), 127 + 127 * P.cos(p.frameCount * 0.006f));
		Shapes.drawDisc3D(p, 100, 80, 80, 100, 0, 0);
		p.noLights();
		
		// textured transparent shape must be drawn AFTER other intersecting shapes
		p.shape(shape);
	}
	
}