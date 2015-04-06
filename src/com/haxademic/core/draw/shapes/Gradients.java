package com.haxademic.core.draw.shapes;

import processing.core.PApplet;
import processing.core.PGraphics;

import com.haxademic.core.app.P;

public class Gradients {
	
	public static void linear( PApplet p, float width, float height, int colorStart, int colorStop ) {
		linear(p.g, width, height, colorStart, colorStop);
	}
	
	public static void linear( PGraphics p, float width, float height, int colorStart, int colorStop )
	{
		p.pushMatrix();
		
		float halfW = width/2;
		float halfH = height/2;
		
		p.beginShape();
		p.fill(colorStart);
		p.vertex(-halfW, -halfH);
		p.fill(colorStop);
		p.vertex(halfW, -halfH);
		p.fill(colorStop);
		p.vertex(halfW, halfH);
		p.fill(colorStart);
		p.vertex(-halfW, halfH);
		p.endShape(P.CLOSE);
		
		p.popMatrix();
	}
	
	public static void radial( PApplet p, float width, float height, int colorInner, int colorOuter, int numSegments ) {
		radial(p.g, width, height, colorInner, colorOuter, numSegments);
	}
	
	public static void radial( PGraphics p, float width, float height, int colorInner, int colorOuter, int numSegments )
	{
		p.pushMatrix();

		float halfW = width/2;
		float halfH = height/2;
		
		float segmentRadians = P.TWO_PI / numSegments;
		p.noStroke();
		for(float r=0; r < P.TWO_PI; r += segmentRadians) {
			float r2 = r + segmentRadians;
			p.beginShape();
			p.fill(colorInner);
			p.vertex(0,0);
			p.fill(colorOuter);
			p.vertex(P.sin(r) * halfW, P.cos(r) * halfH);
			p.vertex(P.sin(r2) * halfW, P.cos(r2) * halfH);
			p.endShape(P.CLOSE);
		}
		
		p.popMatrix();
	}
	
}
