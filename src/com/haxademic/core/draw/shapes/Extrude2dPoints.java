package com.haxademic.core.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Extrude2dPoints {
	
	public static void drawExtruded2dPointList( PApplet p, ArrayList<PVector> pointsArray, float depth ) {
		drawExtruded2dPointList(p.g, pointsArray, depth);
	}
	
	public static void drawExtruded2dPointList( PGraphics p, ArrayList<PVector> pointsArray, float depth ) {
		
		p.beginShape();
		PVector v;
		
		// draw top
		p.beginShape();
		for (int i = 0; i < pointsArray.size(); i++) {
			v = pointsArray.get(i);
			p.vertex(v.x, v.y, depth);
		}
		p.endShape();

		// draw bottom
		p.beginShape();
		for (int i = 0; i < pointsArray.size(); i++) {
			v = pointsArray.get(i);
			p.vertex(v.x, v.y, -depth);
		}
		p.endShape();

		// draw walls between the 2 faces - close the 2 triangles
		p.beginShape(P.TRIANGLE_STRIP);
		for (int i = 0; i < pointsArray.size(); i++) {
			v = pointsArray.get(i);
			p.vertex(v.x, v.y, depth);
			p.vertex(v.x, v.y, -depth);
		}
		// connect the last to the first
		v = pointsArray.get(0);
		p.vertex(v.x, v.y, depth);
		p.vertex(v.x, v.y, -depth);

		p.endShape();

	}
	
}
