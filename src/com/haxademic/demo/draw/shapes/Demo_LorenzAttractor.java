package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;

import processing.core.PVector;

public class Demo_LorenzAttractor
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float a=10;
	protected float b=28;
	protected float c=8.0f/3.0f;

	protected float x=0.01f;
	protected float y=0;
	protected float z=0;
	
	protected ArrayList <PVector> points;

	public void firstFrame()	{
		points = new ArrayList<PVector>();
		p.colorMode(P.HSB);
	}

	protected void drawApp() {
		PG.setBetterLights(p);
		background(0);
		PG.setCenterScreen(p);
		PG.basicCameraFromMouse(p.g);

		// draw word
		float h = 0;
		float dt = 0.01f;
		
		for (int i = 0; i < 10; i++) {
			float dx = (a*(y-x))*dt;
			float dy = (x*(b-z)-y)*dt;
			float dz = (x*y-c*z)*dt;
			x = x + dx;
			y = y + dy;
			z = z + dz;
			points.add(new PVector(x,y,z));
		}

		noFill();
		stroke(255);
		float shapeScale = 20f;
		beginShape();
		for(PVector v : points) {
			stroke(h,200,255);
			vertex(v.x * shapeScale, v.y * shapeScale, v.z * shapeScale);
			h += 0.5;
			if(h>255) {
				h=0;
			}
		}
		endShape();
	}

}
