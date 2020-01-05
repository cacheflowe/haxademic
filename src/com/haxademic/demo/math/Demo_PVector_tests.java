package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PVector;

public class Demo_PVector_tests
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PVector center;
	protected PVector mouse = new PVector();
	protected PVector perp = new PVector();
	
	protected PVector[] lineSegments = new PVector[] { 
			new PVector(150, 100), 
			new PVector(100, 200), 
			new PVector(180, 240)
	};
	protected PVector perp1 = new PVector();
	protected PVector perp2 = new PVector();

	
	protected void firstFrame() {
		background(0);
		center = new PVector(p.width/2, p.height/2);
	}
	
	protected void getPerp(PVector p1, PVector p2, PVector pDest) {
		pDest.set(p2).sub(p1).normalize();
		pDest.set(-pDest.y, pDest.x);
	}
	
	protected void drawApp() {
		p.background(0);
		p.stroke(255);
		PG.setDrawCenter(p);
		mouse.set(Mouse.xEased * p.width, Mouse.yEased * p.height);
		
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
		
		// average perpendiculars between line segments
		for (int i = 0; i < lineSegments.length; i++) {
			PVector point = lineSegments[i];
			point.add(1.5f * P.sin(i + p.frameCount * 0.05f), 1.5f * P.cos(i + p.frameCount * 0.04f));
			p.noStroke();
			p.ellipse(point.x, point.y, 10, 10);
			if(i > 0) {
				PVector pointLast = lineSegments[i-1];
				p.stroke(255);
				p.line(point.x, point.y, pointLast.x, pointLast.y);
			}
		}
		getPerp(lineSegments[0], lineSegments[1], perp1);
		getPerp(lineSegments[1], lineSegments[2], perp2);
		perp1.lerp(perp2, 0.5f);
		
		// draw averaged perpendicular
		perp1.mult(50).add(lineSegments[1]);
		p.stroke(0, 255, 0);
		p.line(lineSegments[1].x, lineSegments[1].y, perp1.x, perp1.y);
		p.ellipse(perp1.x, perp1.y, 10, 10);
	}
}
