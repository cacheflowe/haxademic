package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.OrientationUtil;

import processing.core.PVector;

public class Demo_OrientationUtil
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PVector center = new PVector();
	PVector[] points = new PVector[10];
	
	protected void overridePropsFile() {
	}

	public void setup() {
		super.setup();	
		
		for (int i = 0; i < points.length; i++) {
			points[i] = new PVector(p.random(0, p.width), p.random(0, p.height), p.random(0, -p.width));
		}
	}

	public void drawApp() {
		background(0);
		
		p.lights();
		
		// move center
		center.set(
				p.width/2 + 30 * P.sin(p.frameCount * 0.02f), 
				p.height/2 + 30 * P.sin(p.frameCount * 0.04f), 
				p.width/2 + 90 * P.sin(p.frameCount * 0.02f)
				);
		p.pushMatrix();
		p.fill(0,255,0);
		p.translate(center.x, center.y, center.z);
		p.sphere(3);
		p.popMatrix();

		
		// draw points
		p.fill(255);
		for (int i = 0; i < points.length; i++) {
			p.pushMatrix();
			p.translate(points[i].x, points[i].y, points[i].z);
			OrientationUtil.setRotationTowards(p, points[i], center);
			p.box(100, 10, 10);
			p.popMatrix();
		}
	}

}
