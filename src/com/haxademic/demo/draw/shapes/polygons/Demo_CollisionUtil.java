package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.polygons.CollisionUtil;
import com.haxademic.core.draw.shapes.polygons.Polygon;
import com.haxademic.core.draw.shapes.polygons.Rectangle;

import processing.core.PVector;

public class Demo_CollisionUtil 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<Polygon> polygons;
	protected ArrayList<PVector> points;
	protected Polygon movingPolygon;
	protected Rectangle rect1;
	protected Rectangle rect2;
	protected PVector mouseVec = new PVector();
	
	protected void setupFirstFrame() {
		// create polygons for collisions
		polygons = new ArrayList<Polygon>();
		polygons.add(Polygon.buildShape(p.width * 0.25f, p.height * 0.5f, 3, 100));
		polygons.get(0).translate(0, 30, 0);
		polygons.add(Polygon.buildShape(p.width * 0.5f, p.height * 0.5f, 4, 100, P.QUARTER_PI));
		polygons.add(Polygon.buildShape(p.width * 0.75f, p.height * 0.5f, 6, 100));
		
		movingPolygon = Polygon.buildShape(p.width * 0.5f, p.height * 0.15f, 12, 70);
		
		// create points for detection
		points = new ArrayList<PVector>();
		for (int i = 0; i < 10; i++) {
			points.add(new PVector(p.random(p.width), p.random(p.height), 0));
		}
		
		// create rectangles
		rect1 = new Rectangle(160, 60, 100, 80);
		rect2 = new Rectangle(210, 100, 100, 80);
	}
	
	public void drawApp() {
		mouseVec.set(p.mouseX, p.mouseY);
		
		background(0);
		p.stroke(255);
		p.noFill();
		
		/////////////////////////////////////////
		// move points
		/////////////////////////////////////////
		for (int i = 0; i < points.size(); i++) {
			PVector point = points.get(i); 
			point.add(i+1, 0, 0);
			if(point.x > p.width) point.set(0, p.random(p.height), point.z);
			p.fill(255);
			p.circle(point.x, point.y, 10);
		}
		
		/////////////////////////////////////////
		// draw/check polygons against points
		/////////////////////////////////////////
		for (int i = 0; i < polygons.size(); i++) {
			boolean polyCollided = false;
			for (int j = 0; j < points.size(); j++) {
				PVector point = points.get(j);
				if(polyCollided == false) {	// makes sure wE don't undo the colision with the next point
					polyCollided = CollisionUtil.polygonContainsPoint(polygons.get(i), point);
					polygons.get(i).collided(polyCollided);
				}
			}
			polygons.get(i).draw(p.g);
		}
		
		/////////////////////////////////////////
		// draw/check line segments
		/////////////////////////////////////////
		PVector line1Start = points.get(0);
		PVector line1End = points.get(1);
		PVector line2Start = points.get(2);
		PVector line2End = points.get(3);

		boolean linesIntersect = CollisionUtil.linesIntersect(line1Start, line1End, line2Start, line2End);
		p.stroke(255);
		if(linesIntersect) p.stroke(0, 255, 0);
		
		p.line(line1Start.x, line1Start.y, line1End.x, line1End.y);
		p.line(line2Start.x, line2Start.y, line2End.x, line2End.y);
		
		/////////////////////////////////////////
		// check polygons overlapping
		/////////////////////////////////////////
		movingPolygon.translate(0, 2, 0);
		// TODO: use bounding box calculation here
		if(movingPolygon.vertices().get(0).y > p.height) movingPolygon.translate(0, -p.height, 0);
		movingPolygon.collided(CollisionUtil.polygonsIntersect(movingPolygon, polygons.get(1)));
		movingPolygon.draw(p.g);
		
		/////////////////////////////////////////
		// draw rectangles
		/////////////////////////////////////////
		rect1.x(100 + 20f * P.sin(p.frameCount * 0.03f));
		rect2.y(100 + 50f * P.sin(p.frameCount * 0.02f));
		
		// rect points collision
		boolean rectCollided = false;
		for (int j = 0; j < points.size(); j++) {
			PVector point = points.get(j);
			if(rectCollided == false) {	// makes sure wE don't undo the colision with the next point
				rectCollided = rect1.contains(point);
				rect1.collided(rectCollided);
			}
		}
		
		// check rect overlap
		rect2.collided(rect2.intersects(rect1));
		
		// rect draw
		rect1.draw(p.g);
		rect2.draw(p.g);
		
		/////////////////////////////////////////
		// show bounding box
		/////////////////////////////////////////
		
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {}
	}
}
