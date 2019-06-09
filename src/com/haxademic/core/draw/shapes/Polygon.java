package com.haxademic.core.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class Polygon {

	protected PVector center = new PVector();
	protected ArrayList<PVector> vertices;
	protected ArrayList<Polygon> neighbors;
	protected PVector utilVec = new PVector(); 
	protected PVector newNeighborCenter = new PVector(); 
	
	public Polygon(float[] verticesXYZ) {
		vertices = new ArrayList<PVector>();
		neighbors = new ArrayList<Polygon>();
		for (int i = 0; i < verticesXYZ.length; i+=3) {
			PVector pVector = new PVector(verticesXYZ[i], verticesXYZ[i+1], verticesXYZ[i+2]);
			vertices.add(pVector);
			neighbors.add(null);
		}
		calcCenter();
	}
	
	protected void calcCenter() {
		center.set(0, 0, 0);
		for (int i = 0; i < vertices.size(); i++) {
			center.add(vertices.get(i));
		}
		center.div(vertices.size());
	}
	
	protected PVector midPoint(PVector v1, PVector v2) {
		utilVec.set(v1);
		utilVec.lerp(v2, 0.5f);
		return utilVec;
	}
	
	public void draw(PGraphics pg) {
		pg.beginShape();
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			pg.vertex(v.x, v.y, v.z);
		}
		pg.endShape(P.CLOSE);

		calcCenter();
		pg.circle(center.x, center.y, 3);
	}
	
	public Polygon createNeighbor() {
		int neighborSide = MathUtil.randRange(0, vertices.size() - 1);
		int numVertices = vertices.size();
		float ampOut = 3f;
		PVector midPoint = midPoint(vertices.get(neighborSide % numVertices), vertices.get((neighborSide + 1) % numVertices));
		newNeighborCenter.set(center);
		newNeighborCenter.lerp(midPoint, ampOut);
		
		PVector v1 = vertices.get(neighborSide);
		PVector v2 = vertices.get((neighborSide + 1) % numVertices);
		return new Polygon(new float[] { 
				v1.x, v1.y, v1.z,
				v2.x, v2.y, v2.z,
				newNeighborCenter.x, newNeighborCenter.y, newNeighborCenter.z
		});
	}
	
	public void mergeWithNeighbor() {
		
	}
}
