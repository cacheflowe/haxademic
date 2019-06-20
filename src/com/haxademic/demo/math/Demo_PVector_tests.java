package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;

import processing.core.PVector;

public class Demo_PVector_tests
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PVector center;
	protected PVector mouse = new PVector();
	protected PVector perp = new PVector();
	
	public void setupFirstFrame() {
		background(0);
		center = new PVector(p.width/2, p.height/2);
	}
	
	public void drawApp() {
		p.background(0);
		p.stroke(255);
		PG.setDrawCenter(p);
		mouse.set(p.mouseXEase.value() * p.width, p.mouseYEase.value() * p.height);
		
		// line to mouse
		p.line(center.x, center.y, mouse.x, mouse.y);
		
		// get perpendicular location from mouse
		// from: https://twitter.com/mattdesl/status/1140218255069646848
		p.stroke(0, 255, 0);
		float perpLength = 70f + 20f * P.sin(p.frameCount * 0.03f);
		perp.set(mouse).sub(center).normalize();
		perp.set(-perp.y, perp.x);
		perp.mult(perpLength).add(mouse);
		p.line(mouse.x, mouse.y, perp.x, perp.y);
		p.ellipse(perp.x, perp.y, 10, 10);
	}
}
