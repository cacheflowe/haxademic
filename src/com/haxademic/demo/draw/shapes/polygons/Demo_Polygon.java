package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.shapes.polygons.CollisionUtil;
import com.haxademic.core.draw.shapes.polygons.Edge;
import com.haxademic.core.draw.shapes.polygons.Polygon;
import com.haxademic.core.math.MathUtil;

import processing.core.PVector;

public class Demo_Polygon 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	  * New neighbors!
   	    * log available polygons for neighbors - once all 3 neighbors are taken or polygons are offscreen, don't keep trying 
		* don't create new neighbors on top of others - do collision test with new vertices
		* check for too-small polygon area and bail if shape is too small
		* search for close-enough points to close shapes if there are already 2 sides
		  * do this when creating new shapes
		* Add bounding box Rectangle to Polygon?
		* Remove polygons & neighbors, etc 
      * Layout system
      * Growth vs. subdivision
	  * Polygon cells
	  * Inner-polygon draw styles
	  * Iterative distribution of styles
	  * Hand-drawn lines & imperfect grids/cells
	  * Color schemes
	  * Interaction
	  * Music
	*/
	
	protected ArrayList<Polygon> polygons;
	protected float baseShapeSize = 50;
	protected float snapRadius = baseShapeSize / 2f;
	protected PVector mouseVec = new PVector();
	protected StringBufferLog log = new StringBufferLog(30);
	
	// search for available connections
	protected ArrayList<PVector> availableVertices = new ArrayList<PVector>();
	protected PVector vClose1 = new PVector();
	protected PVector vClose2 = new PVector();
	protected PVector vCompare;
	
	protected void setupFirstFrame() {
		newSeedPolygon();
	}
	
	protected void newSeedPolygon() {
		polygons = new ArrayList<Polygon>();
		polygons.add(Polygon.buildShape(p.width * 0.5f, p.height * 0.5f, 4, baseShapeSize));
//		polygons.add((new Polygon(new float[] {300, 200, 0, 200, 300, 0, 400, 300, 0})));
	}
	
	public void drawApp() {
		background(0);
		p.stroke(255);
		p.noFill();
		
		// draw polygons
		mouseVec.set(p.mouseX, mouseY);
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).collided(CollisionUtil.polygonContainsPoint(polygons.get(i), mouseVec));
			polygons.get(i).draw(p.g);
		}
		
		// auto-create neighbors
		if(p.frameCount % 1 == 0) {
			addNewNeighbor();
		}
		closeNeighbors();

		// draw debug log
		log.printToScreen(p.g, 20, 20);
	}
	
	// add neighbors!
	
	protected Polygon randomPolygon() {
		return polygons.get(MathUtil.randRange(0, polygons.size() - 1));
	}
	
	protected void addNewNeighbor() {
		boolean createdNeighbor = false;
		
		// find a polygon that needs neighbors
		Polygon randPoly = randomPolygon();
		int attempts = 0;
		while(randPoly.needsNeighbors() == false && attempts < 100) {
			randPoly = randomPolygon();
			attempts++;
		}
		log.update("attempts: " + attempts);
		
		// try to add a neighbor
		if(randPoly.needsNeighbors()) {
			Polygon newNeighbor = createNeighborTriangle(randPoly);
			if(newNeighbor != null) {
				polygons.add(newNeighbor);
				createdNeighbor = true;
			}
		}
		log.update("createdNeighbor: " + createdNeighbor);
		
		// attempt to close connections
		closeNeighbors();
	}
	
	public Polygon createNeighborTriangle(Polygon parentPoly) {
		// get edge
		Edge edge = parentPoly.availableNeighborEdge();
		// find a reasonable new vertex for a neighbor 
		// TODO: this is where magic layout tweaks can happen by adjusting length of children, depending on location or iteration
		PVector newNeighborVertex = parentPoly.newNeighbor3rdVertex(edge, MathUtil.randRangeDecimal(1f, 1f));
		
		// new triangle off the Edge, but lerp the shared edge away a tiny bit to prevent overlap check
		// TODO: make temporary shrinkable 
		PVector edgeV1OffsetCheck = new PVector();
		edgeV1OffsetCheck.set(edge.v1());
		edgeV1OffsetCheck.lerp(newNeighborVertex, 0.01f);
		PVector edgeV2OffsetCheck = new PVector();
		edgeV2OffsetCheck.set(edge.v2());
		edgeV2OffsetCheck.lerp(newNeighborVertex, 0.01f);
		Polygon newNeighborCheckOverlap = new Polygon(new float[] {
				edgeV1OffsetCheck.x, edgeV1OffsetCheck.y, edgeV1OffsetCheck.z,
				edgeV2OffsetCheck.x, edgeV2OffsetCheck.y, edgeV2OffsetCheck.z,
				newNeighborVertex.x, newNeighborVertex.y, newNeighborVertex.z
		});

		
		// check to see if we're overlapping with another polygon
		Polygon overlappedPoly = null;
		for (int i = 0; i < polygons.size(); i++) {
			if(overlappedPoly == null && polygons.get(i) != parentPoly) {
				if(CollisionUtil.polygonsIntersect(polygons.get(i), newNeighborCheckOverlap)) {
					overlappedPoly = polygons.get(i);
				}
			}
		}
		
		// if we're overlapping another poly, try to move the new vertex to the closest vertex of the overlapped triangle, then see if the two triangles share an edge
		if(overlappedPoly != null) {
			PVector closestOverlappedVert = overlappedPoly.closestVertexToVertex(newNeighborVertex);
			newNeighborVertex.set(closestOverlappedVert);
			log.update("OVERLAP SNAP!");
		} else {
			// if we're not overlapped, but close to another vertex, let's try to snap
			boolean snapped = false;
			for (int i = 0; i < polygons.size(); i++) {
				for (int j = 0; j < polygons.get(i).vertices().size(); j++) {
					if(snapped == false) {
						PVector vertex = polygons.get(i).vertices().get(j);
						if(newNeighborVertex.dist(vertex) < snapRadius) {
							newNeighborVertex.set(vertex);
							overlappedPoly = polygons.get(i);	// ensures that the neighbors are connected below
							snapped = true;
							log.update("SNAP!");
						}
					}
				}
			}
		}
		
		// TODO: Do we need to check for overlap again, based on "SNAP" above??
		
		// new triangle off the Edge
		Polygon newNeighbor = new Polygon(new float[] {
				edge.v1().x, edge.v1().y, edge.v1().z,
				edge.v2().x, edge.v2().y, edge.v2().z,
				newNeighborVertex.x, newNeighborVertex.y, newNeighborVertex.z
		});
		
		// check area to see if we've created a garbage shape
		float newNeighborArea = CollisionUtil.polygonArea(newNeighbor);
		
		// check to see if overlapped poly now has a shared edge with new poly, so undo overlapped flag
		if(overlappedPoly != null && newNeighborArea > 100) {
			Edge sharedEdge = overlappedPoly.findSharedEdge(newNeighbor);
			if(sharedEdge != null) {
				boolean edgeAlreadyHasNeighbor = overlappedPoly.edgeHasNeighbor(sharedEdge);	// make sure snapped-to edge doesn't already have a neighbor!
				if(edgeAlreadyHasNeighbor == false) {
					// TODO: MAKE SURE EDGE DOESN"T ALREADY HAVE A NEIGHBOR! 
					// THESE ARE DOUBLING UP, WHICH IS A BUG
					overlappedPoly.setNeighbor(newNeighbor);
					newNeighbor.setNeighbor(overlappedPoly);
					overlappedPoly = null;
				}
			}
		}
		
		// if not overlapping another, add to collection
		if(overlappedPoly == null && CollisionUtil.polygonArea(newNeighbor) > 100) {
			// tell polys about their shared edges
			parentPoly.setNeighbor(newNeighbor);
			newNeighbor.setNeighbor(parentPoly);
			return newNeighbor;
		} else {
			// TODO: put this in an object pool for recycling
			return null;
		}
	}
	
	public Comparator<PVector> distanceComparator = new Comparator<PVector>() {         
		public int compare(PVector v1, PVector v2) {
			float dist1 = vCompare.dist(v1);
			float dist2 = vCompare.dist(v2);
			return (dist1 < dist2 ? -1 :                     
				   (dist1 == dist2 ? 0 : 1));           
		}     
	};       
	
	protected void closeNeighbors() {
		// find polygons that need neighbors and have a common vertex that connects to an edge without neighbors (on each polygon)
		boolean foundSharedVertex = false;
		int attemptsToFind = 0;
		
		// create array of vertices that can be connected with another
		availableVertices.clear();
		for (int i = 0; i < polygons.size(); i++) {
			Polygon poly = polygons.get(i);
			for (int j = 0; j < poly.edges().size(); j++) {
				Edge edge = poly.edges().get(j);
				if(poly.edgeHasNeighbor(edge) == false) {
					if(availableVertices.contains(edge.v1()) == false) availableVertices.add(edge.v1());
					if(availableVertices.contains(edge.v2()) == false) availableVertices.add(edge.v2());
				}
			}
		}
		p.debugView.setValue("availableVertices", availableVertices.size());
		
		// draw available vertices
		p.fill(0, 255, 0, 100);
		for (int i = 0; i < availableVertices.size(); i++) {
			p.circle(availableVertices.get(i).x, availableVertices.get(i).y, 10);
		}
		
		// find 3 closest vertices to a vertex, make a triangle, shrink it and see if it overlaps anything. if not, create it!
		for (int i = 0; i < availableVertices.size(); i++) {
			// store current vertex and sort by distance using comparator function
			vCompare = availableVertices.get(i);
			Collections.sort(availableVertices, distanceComparator);
			
			// check several combinations of closest vertices
			// TODO: refactor to create new shrinkable temporaray polygon
			/////////////
			if(availableVertices.size() >= 3) {
				// find triangle - first 3 vertices if there are that many!
				// draw debug triangle
				p.strokeWeight(2);
				p.stroke(255,0,0);
				PVector v1 = availableVertices.get(0);
				PVector v2 = availableVertices.get(1);
				PVector v3 = availableVertices.get(2);
				p.line(v1.x + 2, v1.y + 2, v2.x + 2, v2.y + 2);
				p.line(v1.x + 2, v1.y + 2, v3.x + 2, v3.y + 2);
				p.line(v3.x + 2, v3.y + 2, v2.x + 2, v2.y + 2);
				p.strokeWeight(1);
			}
			if(availableVertices.size() >= 4) {
				// find triangle - first 3 vertices if there are that many!
				// draw debug triangle
				p.strokeWeight(2);
				p.stroke(255,0,0);
				PVector v1 = availableVertices.get(0);
				PVector v2 = availableVertices.get(2);
				PVector v3 = availableVertices.get(3);
				p.line(v1.x + 2, v1.y + 2, v2.x + 2, v2.y + 2);
				p.line(v1.x + 2, v1.y + 2, v3.x + 2, v3.y + 2);
				p.line(v3.x + 2, v3.y + 2, v2.x + 2, v2.y + 2);
				p.strokeWeight(1);

				// find triangle - first 3 vertices if there are that many!
				// draw debug triangle
				p.strokeWeight(2);
				p.stroke(255,0,0);
				v1 = availableVertices.get(0);
				v2 = availableVertices.get(1);
				v3 = availableVertices.get(3);
				p.line(v1.x + 2, v1.y + 2, v2.x + 2, v2.y + 2);
				p.line(v1.x + 2, v1.y + 2, v3.x + 2, v3.y + 2);
				p.line(v3.x + 2, v3.y + 2, v2.x + 2, v2.y + 2);
				p.strokeWeight(1);
			}
			if(availableVertices.size() >= 5) {
				// find triangle - first 3 vertices if there are that many!
				// draw debug triangle
				p.strokeWeight(2);
				p.stroke(255,0,0);
				PVector v1 = availableVertices.get(0);
				PVector v2 = availableVertices.get(3);
				PVector v3 = availableVertices.get(4);
				p.line(v1.x + 2, v1.y + 2, v2.x + 2, v2.y + 2);
				p.line(v1.x + 2, v1.y + 2, v3.x + 2, v3.y + 2);
				p.line(v3.x + 2, v3.y + 2, v2.x + 2, v2.y + 2);
				p.strokeWeight(1);
			}
		}

		/*
		for (int i = 0; i < polygons.size(); i++) {
			for (int j = 0; j < polygons.size(); j++) {
//				if(foundSharedVertex == false) {
					Polygon poly1 = polygons.get(i);
					Polygon poly2 = polygons.get(j);
					if(poly1 != poly2) {	// don't compare to self
						if(poly1.needsNeighbors() && poly2.needsNeighbors()) {
							attemptsToFind++;
							// find shared point, then find each poly's edge connected to that point
							ArrayList<PVector> shareVerts = poly1.sharedVertex(poly2);
							for (int k = 0; k < shareVerts.size(); k++) {
								availableVertices.add(shareVerts.get(k));
								p.circle(shareVerts.get(k).x, shareVerts.get(k).y, 10);
							}
						}
					}
//				}
			}
		}
		*/
		// TODO: now find vertices that belong to a single polygon with 2 edges without neighbors
		
		// TODO: THEN!
		// attempt to create a new triangle with an edge between - might have to shrink towards center 
	}

	// user input
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
//			for (int i = 0; i < 10; i++) 
			addNewNeighbor();
		}
		if(p.key == 'r') {
			newSeedPolygon();
		}
	}

	public void mousePressed() {
		super.mousePressed();
		addNewNeighbor();
	}
}
