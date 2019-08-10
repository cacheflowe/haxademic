package com.haxademic.core.draw.shapes.polygons;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Edge {
	
	protected PVector v1;
	protected PVector v2;
	protected PVector midPoint = new PVector();
	protected PVector launchPoint = new PVector();
	
	protected static PImage lineTexture = null;

	
	public Edge(PVector v1, PVector v2) {
		this.v1 = v1;
		this.v2 = v2;
		calcMidPoint();
	}
	
	public PVector v1() { return v1; }
	public PVector v2() { return v2; }
	public PVector midPoint() { return midPoint; }
	public Edge copy() { return new Edge(v2.copy(), v1.copy()); }
	public Edge copyRev() { return new Edge(v1.copy(), v2.copy()); }
	public Edge copy2() { return new Edge(v2.copy(), v2.copy()); }
	public Edge copy1() { return new Edge(v1.copy(), v1.copy()); }
	
	protected void calcMidPoint() {
		midPoint.set(v1);
		midPoint.lerp(v2, 0.5f);
	}
	
	public PVector launchPoint(float rangeLow, float rangeHigh) {
		launchPoint.set(v1);
		launchPoint.lerp(v2, MathUtil.randRangeDecimal(rangeLow, rangeHigh));
		return launchPoint;
	}
	
	public void set(Edge otherEdge) {
		v1.set(otherEdge.v1());
		v2.set(otherEdge.v2());
	}
	
	public void lerp(Edge otherEdge, float amp) {
		v1.lerp(otherEdge.v1(), amp);
		v2.lerp(otherEdge.v2(), amp);
	}
	
	public float length() {
		return v1.dist(v2);
	}
	
	public void update() {
		calcMidPoint();
	}
	
	public void draw(PGraphics pg) {
//		drawDebug(pg);
		drawHandDrawn(pg);
	}
	
	public boolean matchesEdge(Edge edge) {
		return (v1.dist(edge.v1()) < 0.01f && 
			    v2.dist(edge.v2()) < 0.01f) ||
			   (v1.dist(edge.v2()) < 0.01f && 
			    v2.dist(edge.v1()) < 0.01f);
	}
	
	public void drawDebug(PGraphics pg) {
		pg.stroke(0, 255, 0);
		pg.line(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	
	public void drawHandDrawn(PGraphics pg) {
		if(lineTexture == null) lineTexture = P.getImage("haxademic/images/hand-drawn-line.png");
		Shapes.drawTexturedLine(pg, lineTexture, v1.x, v1.y, v2.x, v2.y, 0xff000000, 24, P.p.noise(v1.x/100f + P.p.frameCount * 0.01f) * 1000f);
	}
	
	public String toString() {
		return v1 + " - " + v2;
	}
}
