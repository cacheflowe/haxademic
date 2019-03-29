package com.haxademic.demo.draw.shapes;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.PointTrail;

import processing.core.PVector;

public class Demo_PointTrail 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PointTrail trail;
	protected PVector mouseVec = new PVector();

	public void drawApp() {
		p.background(0);
		p.noFill();
		p.stroke(40, 255, 40);
		
		mouseVec.set(p.mouseX, p.mouseY);
		if(trail == null) trail = new PointTrail(10);
		trail.update(p.g, mouseVec);
	}
}
