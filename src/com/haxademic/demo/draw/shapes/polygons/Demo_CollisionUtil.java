package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.polygons.Polygon;

import processing.core.PVector;

public class Demo_CollisionUtil 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected ArrayList<Polygon> polygons;
	protected ArrayList<PVector> points;
	
	protected void setupFirstFrame() {
		polygons = new ArrayList<Polygon>();
		polygons.add(Polygon.buildShape(p.width * 0.25f, p.height * 0.5f, 3, 100));
		polygons.add(Polygon.buildShape(p.width * 0.5f, p.height * 0.5f, 4, 100));
		polygons.add(Polygon.buildShape(p.width * 0.75f, p.height * 0.5f, 6, 100));
		
		points = new ArrayList<PVector>();
		for (int i = 0; i < 10; i++) {
			points.add(new PVector(p.random(p.width), p.random(p.height), 0));
		}
	}
	
	public void drawApp() {
		background(0);
		p.stroke(255);
		p.noFill();
		
		// move points
		for (int i = 0; i < points.size(); i++) {
			PVector point = points.get(i); 
			point.add(i+1, 0, 0);
			if(point.x > p.width) point.set(0, p.random(p.height), point.z);
			p.fill(255);
			p.circle(point.x, point.y, 10);
		}
		
		// draw polygons
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).draw(p.g);
			for (int j = 0; j < points.size(); j++) {
				PVector point = points.get(j); 
				polygons.get(i).drawCollision(p.g, point.x, point.y);
			}
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {}
	}
}
