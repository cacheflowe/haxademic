package com.haxademic.core.draw.shapes.polygons;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

public class Polygon {

	protected PVector center = new PVector();
	protected ArrayList<PVector> vertices;
	protected HashMap<Edge, Polygon> neighbors;
	protected ArrayList<Edge> edges;
	protected int numVertices = 0;
	protected float area = 0;

	protected PVector collideVec = new PVector(); 
	protected PVector utilVec = new PVector(); 
	protected PVector utilVec2 = new PVector(); 
	protected PVector newNeighborCenter = new PVector(); 
	
	protected ArrayList<Edge> availableNeighborEdges = new ArrayList<Edge>();
	
	protected boolean collided = false;
	
	/////////////////////////////////////
	// INIT
	/////////////////////////////////////

	public Polygon(float[] verticesXYZ) {
		// turn int of 3-component coordinates into PVectors
		ArrayList<PVector> verticesPVector = new ArrayList<PVector>();
		for (int i = 0; i < verticesXYZ.length; i+=3) {
			PVector pVector = new PVector(verticesXYZ[i], verticesXYZ[i+1], verticesXYZ[i+2]);
			verticesPVector.add(pVector);
		}
		init(verticesPVector);
	}
	
	public Polygon(ArrayList<PVector> vertices) {
		init(vertices);
	}
	
	protected void init(ArrayList<PVector> vertices) {
		this.vertices = vertices;
		numVertices = vertices.size();
		neighbors = new HashMap<Edge, Polygon>();
		edges = new ArrayList<Edge>();
		buildEdges();
		calcCentroid();
		calcArea();
	}
	
	/////////////////////////////////////
	// GENERATORS
	/////////////////////////////////////

	public static Polygon buildShape(float x, float y, float vertices, float radius) {
		return buildShape(x, y, vertices, radius, -P.HALF_PI);
	}
	
	public static Polygon buildShape(float x, float y, float vertices, float radius, float radsOffset) {
		float vertexRads = P.TWO_PI / vertices;
		ArrayList<PVector> verticesPVector = new ArrayList<PVector>();
		
		for (int i = 0; i < vertices; i++) {
			verticesPVector.add(new PVector(
					x + radius * P.cos(radsOffset + vertexRads * i),
					y + radius * P.sin(radsOffset + vertexRads * i),
					0
			));
		}
		return new Polygon(verticesPVector);
	}

	/////////////////////////////////////
	// GETTERS / SETTERS
	/////////////////////////////////////
	
	public ArrayList<PVector> vertices() {
		return vertices;
	}
	
	public ArrayList<Edge> edges() {
		return edges;
	}
	
	public boolean collided() { return collided; }
	public void collided(boolean collided) { this.collided = collided; }
	

	/////////////////////////////////////
	// CALCULATE POSITIONS
	/////////////////////////////////////

	public void translate(PVector move) {
		translate(move.x, move.y, move.z);
	}
	
	public void translate(float x, float y, float z) {
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			v.add(x, y, z);
		}
		calcCentroid();
	}
	
	public void setPosition(PVector pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	
	public void setPosition(float x, float y, float z) {
		// get offset from current position, based on center, and add to vertices
		utilVec.set(x, y, z);
		utilVec.sub(center);
		translate(utilVec);
	}
	
	public void setVertex(int index, PVector v) {
		vertices.get(index).set(v);
		calcCentroid();
		calcArea();
	}
	
	public void setVertices(ArrayList<PVector> newVertices) {
		for (int i = 0; i < vertices.size(); i++) {
			if (i < newVertices.size()) {
				vertices.get(i).set(newVertices.get(i));
			}
		}
		calcCentroid();
		calcArea();
	}
	
	protected void calcCentroid() {
		center.set(0, 0, 0);
		for (int i = 0; i < vertices.size(); i++) {
			center.add(vertices.get(i));
		}
		center.div(vertices.size());
	}
	
	public void shrink(float amp) {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).lerp(center, amp);
		}
	}
	
	protected void calcArea() {
		area = CollisionUtil.polygonArea(this);
	}
	
	protected PVector midPoint(PVector v1, PVector v2) {
		utilVec.set(v1);
		utilVec.lerp(v2, 0.5f);
		return utilVec;
	}
	
	public boolean offscreen() {
		// find any vertices on screen
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			if(v.x > 0 && v.x < P.p.width && v.y > 0 && v.y < P.p.height) return false;
		}
		return true;
	}
	
	/////////////////////////////////////
	// DRAW
	/////////////////////////////////////
	
	public void draw(PGraphics pg) {
		updateEdges();
		drawEdges(pg);
		drawShapeBg(pg);
		drawShapeOutline(pg);
		drawNeighborDebug(pg);
		drawCentroid(pg);
//		drawMouseOver(pg);
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
	
	protected void drawShapeBg(PGraphics pg) {
		pg.fill(0);
		if(collided) pg.fill(0, 255, 0, 50);
		pg.noStroke();
		pg.beginShape();
		for (int i = 0; i < vertices.size(); i++) {
			PVector v = vertices.get(i);
			pg.vertex(v.x, v.y, v.z);
		}
		pg.endShape(P.CLOSE);
	}
		
	/////////////////////////////////////
	// EDGES
	/////////////////////////////////////

	protected void buildEdges() {
		for (int i = 0; i < vertices.size(); i++) {
			edges.add(new Edge(vertices.get(i), vertices.get((i+1) % numVertices)));
		}
	}
	
	protected void updateEdges() {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).update();
		}
	}
	
	protected void drawEdges(PGraphics pg) {
		for (int i = 0; i < edges.size(); i++) {
			edges.get(i).draw(pg);
		}
	}
	
	/////////////////////////
	// NEIGHBORS
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
		// draw area calc
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 10);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.text(P.round(area), center.x, center.y);
	}
	
	public boolean needsNeighbors() {
		if(offscreen()) return false;
		return (neighbors.keySet().size() < edges.size()); 
	}
	
	public Edge availableNeighborEdge() {
		availableNeighborEdges.clear();
		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			if(neighbors.containsKey(edge) == false) {
				availableNeighborEdges.add(edge);
			}
		}
		if(availableNeighborEdges.size() > 0) {
			return availableNeighborEdges.get(MathUtil.randRange(0, availableNeighborEdges.size() - 1)); // return a random available edge
		} else {
			return null;
		}
	}
	
	public PVector newNeighbor3rdVertex(Edge edge, float ampOut) {
		PVector edgeLaunchPoint = edge.launchPoint();
		float edgeLength = edge.length();
		float edgeDistFromCenter = center.dist(edgeLaunchPoint);
		ampOut += (edgeLength / edgeDistFromCenter);
		newNeighborCenter.set(center);
		newNeighborCenter.lerp(edgeLaunchPoint, ampOut);	// lerp beyond edge midpoint from polygon center
		return newNeighborCenter;
	}
	
	public void setNeighbor(Polygon newNeighbor) {
		Edge sharedEdge = findSharedEdge(newNeighbor);
		neighbors.put(sharedEdge, newNeighbor);
	}
	
	public ArrayList<PVector> sharedVertex(Polygon poly) {
		ArrayList<PVector> sharedVertices = new ArrayList<PVector>();
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < poly.vertices().size(); j++) {
				if(vertices.get(i).dist(poly.vertices.get(j)) < 0.001f) {
					sharedVertices.add(vertices.get(i));
//					return vertices.get(i);
				}
			}
		}
		return sharedVertices;
	}
	
	public boolean edgeHasNeighbor(Edge edge) {
		return neighbors.containsKey(edge);
	}
	
	public boolean hasEdge(Edge edge) {
		for (int j = 0; j < edges.size(); j++) {
			Edge myEdge = edges.get(j);
			if(myEdge.matchesEdge(edge)) {
				return true;
			}
		}
		return false;
	}
	
	public Edge findSharedEdge(Polygon newNeighbor) {
		ArrayList<Edge> otherPolyEdges = newNeighbor.edges();
//		boolean matchedEdge = false;
		for (int i = 0; i < otherPolyEdges.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				Edge otherEdge = otherPolyEdges.get(i);
				Edge myEdge = edges.get(j);
				if(myEdge.matchesEdge(otherEdge)) {
//					matchedEdge = true;
					return myEdge;
				}
			}
		}
//		P.out("matchedEdge", matchedEdge);
		return null;
	}
	
	public int numSharedVertices(Polygon poly) {
		int numShared = 0;
		for (int i = 0; i < poly.vertices().size(); i++) {
			for (int j = 0; j < numVertices; j++) {
				if(poly.vertices.get(i).dist(vertices.get(j)) < 0.01f ) numShared++;
			}
		}
		return numShared;
	}
	
	public PVector closestVertexToVertex(PVector vertex) {
		float closestVertDist = 999999;
		PVector closestVert = null;
		for (int i = 0; i < vertices().size(); i++) {
			float checkDist = vertices.get(i).dist(vertex);
			if(checkDist < closestVertDist) {
				closestVertDist = checkDist; 
				closestVert = vertices.get(i);
			}
		}
		return closestVert;
	}
	
	public void mergeWithNeighbor() {
		
	}
	
}
