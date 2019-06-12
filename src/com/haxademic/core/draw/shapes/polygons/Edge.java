package com.haxademic.core.draw.shapes.polygons;

import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class Edge {
	
	protected PVector v1;
	protected PVector v2;
	protected PVector midPoint = new PVector();
	protected PVector launchPoint = new PVector();
	
	public Edge(PVector v1, PVector v2) {
		this.v1 = v1;
		this.v2 = v2;
		calcMidPoint();
	}
	
	public PVector v1() { return v1; }
	public PVector v2() { return v2; }
	public PVector midPoint() { return midPoint; }
	
	protected void calcMidPoint() {
		midPoint.set(v1);
		midPoint.lerp(v2, 0.5f);
	}
	
	public PVector launchPoint() {
		launchPoint.set(v1);
		launchPoint.lerp(v2, MathUtil.randRangeDecimal(0.45f, 0.55f));
		return launchPoint;
	}
	
	public float length() {
		return v1.dist(v2);
	}
	
	public void update() {
		calcMidPoint();
	}
	
	public void draw(PGraphics pg) {
		drawDebug(pg);
	}
	
	public boolean matchesEdge(Edge edge) {
		return (v1.dist(edge.v1()) < 0.01f && 
			    v2.dist(edge.v2()) < 0.01f) ||
			   (v1.dist(edge.v2()) < 0.01f && 
			    v2.dist(edge.v1()) < 0.01f);
	}
	
	protected void drawDebug(PGraphics pg) {
		pg.stroke(0, 0, 255);
		pg.line(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	
	public String toString() {
		return v1 + " - " + v2;
	}
}
