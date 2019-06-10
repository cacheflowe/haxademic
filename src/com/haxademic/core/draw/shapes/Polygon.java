package com.haxademic.core.draw.shapes;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.math.CollisionUtil;

import processing.core.PGraphics;
import processing.core.PVector;

public class Polygon {

	protected PVector center = new PVector();
	protected ArrayList<PVector> vertices;
	protected HashMap<Edge, Polygon> neighbors;
	protected ArrayList<Edge> edges;
	protected int numVertices = 0;

	protected PVector utilVec = new PVector(); 
	protected PVector newNeighborCenter = new PVector(); 
	
	public Polygon(float[] verticesXYZ) {
		vertices = new ArrayList<PVector>();
		neighbors = new HashMap<Edge, Polygon>();
		edges = new ArrayList<Edge>();
		for (int i = 0; i < verticesXYZ.length; i+=3) {
			PVector pVector = new PVector(verticesXYZ[i], verticesXYZ[i+1], verticesXYZ[i+2]);
			vertices.add(pVector);
		}
		numVertices = vertices.size();
		buildEdges();
		calcCentroid();
	}
	
	public ArrayList<Edge> edges() {
		return edges;
	}
	
	protected void buildEdges() {
		for (int i = 0; i < vertices.size(); i++) {
			edges.add(new Edge(vertices.get(i), vertices.get((i+1) % numVertices)));
		}
	}
	
	protected void calcCentroid() {
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
	
	// draw
	
	public void draw(PGraphics pg) {
		updateEdges(pg);
		drawShapeOutline(pg);
		drawNeighborDebug(pg);
		calcCentroid();
		drawCentroid(pg);
		drawMouseOver(pg);
	}

	protected void updateEdges(PGraphics pg) {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).update(pg);
		}
	}
	
	protected void drawShapeOutline(PGraphics pg) {
		pg.noFill();
		pg.stroke(255);
		pg.beginShape();
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			pg.vertex(v.x, v.y, v.z);
		}
		pg.endShape(P.CLOSE);
	}
	
	protected void drawCentroid(PGraphics pg) {
		pg.fill(0, 255, 0);
		pg.noStroke();
		pg.circle(center.x, center.y, 4);
	}
	
	protected void drawMouseOver(PGraphics pg) {
		utilVec.set(P.p.mouseX, P.p.mouseY, 0);
//		if(numVertices == 3 && CollisionUtil.pointInsideTriangle(utilVec, vertices.get(0), vertices.get(1), vertices.get(2))) {
		if(numVertices == 3 && CollisionUtil.polygonContainsPoint(utilVec, vertices)) {
			pg.fill(0, 255, 0, 50);
			pg.noStroke();
			pg.beginShape();
			for (int i = 0; i < vertices.size(); i++) {
				PVector v = vertices.get(i);
				pg.vertex(v.x, v.y, v.z);
			}
			pg.endShape(P.CLOSE);
		}
	}
	
	/////////////////////////
	// neighbor stuff
	/////////////////////////
	
	protected void drawNeighborDebug(PGraphics pg) {
		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if(neighbors.containsKey(edge)) {
				pg.fill(0, 255, 0);
			} else {
				pg.fill(255, 0, 0);
			}
			pg.noStroke();
			PVector almostEdge = midPoint(center, edge.midPoint());
			pg.circle(almostEdge.x, almostEdge.y, 5);
		}
	}
	
	public boolean needsNeighbors() {
		P.out(neighbors.keySet().size(), edges.size());
		return (neighbors.keySet().size() < edges.size()); 
	}
	
	public Edge availableNeighborEdge() {
		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if(neighbors.containsKey(edge) == false) {
				return edge;
			}
		}
		return null;
	}
	
	public PVector newNeighbor3rdVertex(Edge edge, float ampOut) {
		newNeighborCenter.set(center);
		newNeighborCenter.lerp(edge.midPoint(), ampOut);	// lerp beyond edge midpoint from polygon center
		return newNeighborCenter;
	}
	
	public void setNeighbor(Polygon newNeighbor) {
		Edge sharedEdge = findSharedEdge(newNeighbor);
		neighbors.put(sharedEdge, newNeighbor);
	}
	
	public Edge findSharedEdge(Polygon newNeighbor) {
		ArrayList<Edge> otherPolyEdges = newNeighbor.edges();
		boolean matchedEdge = false;
		for (int i = 0; i < otherPolyEdges.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				Edge otherEdge = otherPolyEdges.get(i);
				Edge myEdge = edges.get(j);
				if(myEdge.matchesEdge(otherEdge)) {
					matchedEdge = true;
					P.out("matchedEdge", myEdge.toString(), otherEdge.toString());
					return myEdge;
				}
			}
		}
		P.out("matchedEdge", matchedEdge);
		return null;
	}
	
	public void mergeWithNeighbor() {
		
	}
	
}
