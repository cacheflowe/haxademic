package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OrientationUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.DemoAssets;

import processing.core.PVector;

public class Demo_OrientationUtil
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PVector center = new PVector();
	PVector[] points = new PVector[300];
	
	public void setup() {
		super.setup();	
		center.set(p.width / 2, p.height / 2);
		for (int i = 0; i < points.length; i++) {
			points[i] = new PVector(p.random(0, p.width), p.random(0, p.height), p.random(0, -p.width));
		}
	}

	public void drawApp() {
		background(0);
		
		DrawUtil.setBetterLights(p);
		p.noStroke();
		
		// move center
		center.set(
				p.width/2 + 250 * P.sin(p.frameCount * 0.02f), 
				p.height/2 + 150 * P.sin(p.frameCount * 0.04f),
				100 * P.sin(p.frameCount * 0.02f)
				);
		p.pushMatrix();
		p.fill(0,255,0);
		p.translate(center.x, center.y, center.z);
		p.sphere(30);
		p.popMatrix();

		
		// draw points
		p.fill(255);
		p.stroke(0);
		p.strokeWeight(0.75f);
		for (int i = 0; i < points.length; i++) {
			// move points
			points[i].set(
					points[i].x + 2 * P.sin(i + p.frameCount * 0.02f), 
					points[i].y + 2 * P.cos(i + p.frameCount * 0.04f),
					points[i].z + 2 * P.sin(i + p.frameCount * 0.02f)
					);

			
			// draw pointed at center
			p.pushMatrix();
			p.translate(points[i].x, points[i].y, points[i].z);
			OrientationUtil.setRotationTowards2(p.g, points[i], center);
			
			// draw box
			p.box(6, 50, 12);
			
			// draw polygon
//			p.rotateY(P.PI);
//			p.rotateX(-P.HALF_PI);
//			Shapes.drawPyramid(p, 100, 20, true);
			
			// draw human
//			p.rotateX(P.PI);
//			p.rotateY(P.HALF_PI);
//			p.pushMatrix();
//			p.scale(0.4f);
//			p.shape(DemoAssets.objHumanoid());
//			p.popMatrix();
			
			p.popMatrix();
		}
	}

}
