package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.OrientationUtil;

import processing.core.PVector;

public class Demo_OrientationUtil
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PVector center = new PVector();
	PVector[] points = new PVector[600];
	
	public void setupFirstFrame() {
	
		center.set(0, 0);
		for (int i = 0; i < points.length; i++) {
			points[i] = new PVector(p.random(-p.width/2, p.width/2), p.random(-p.height/2, p.height/2), p.random(-p.width/2, p.width/2));
		}
	}

	public void drawApp() {
		background(0);
		
		PG.setCenterScreen(p.g);
		PG.setBetterLights(p.g);
		PG.basicCameraFromMouse(p.g);
		p.noStroke();
		
		// move center
		center.set(
				250 * P.sin(p.frameCount * 0.02f), 
				150 * P.sin(p.frameCount * 0.04f),
				300 * P.sin(p.frameCount * 0.02f)
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
