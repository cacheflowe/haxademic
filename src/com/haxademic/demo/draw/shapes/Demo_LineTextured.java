package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.LineTextured;

import processing.core.PImage;
import processing.core.PVector;

public class Demo_LineTextured 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PVector p1 = new PVector();
	protected PVector p2 = new PVector();
	protected PVector p3 = new PVector();
	protected PImage lineTexture;

	public void drawApp() {
		p.background(0);
		if(lineTexture == null) lineTexture = P.getImage("haxademic/images/hand-drawn-line.png");
		
		// update points
		p1.set(p.width * 0.25f, p.height/2 + p.height * 0.2f * P.sin(p.frameCount * 0.01f));
		p3.set(p.width * 0.75f, p.height/2 + p.height * 0.2f * P.sin(P.HALF_PI + p.frameCount * 0.01f));
		p2.set(p.mouseX, p.mouseY);
		
		// update line properties
		float thickness = 10f;// + 4f * P.sin(p.frameCount/30f);
		float texOffset = p.frameCount * 1f; // P.sin(p.frameCount/30f) * 400f;
		
		// draw background
		p.fill(0xff800080);
		p.noStroke();
		p.beginShape(PShapeTypes.TRIANGLES);
		p.vertex(p1.x, p1.y);
		p.vertex(p2.x, p2.y);
		p.vertex(p3.x, p3.y);
		p.endShape();

		// draw lines
		PG.setDrawFlat2d(p.g, true);
		LineTextured.draw(p.g, lineTexture, p1.x, p1.y, p2.x, p2.y, 0xffffffff, thickness, texOffset);
		LineTextured.draw(p.g, lineTexture, p2.x, p2.y, p3.x, p3.y, 0xffffffff, thickness, texOffset);
		LineTextured.draw(p.g, lineTexture, p3.x, p3.y, p1.x, p1.y, 0xffffffff, thickness, texOffset);
	}
	
}
