package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;

import com.haxademic.core.app.P;
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
		* don't create new neighbors on top of others - do collision test with new vertices
		* search for close-enough points to close shapes if there are already 2 sides
		  * do this when creating new shapes
		* Add bounding box Rectangle to Polygon?
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
	protected PVector mouseVec = new PVector();
	protected StringBufferLog log = new StringBufferLog(10);

	
	protected void setupFirstFrame() {
		polygons = new ArrayList<Polygon>();
		polygons.add(Polygon.buildShape(p.width * 0.5f, p.height * 0.5f, 3, 100));
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
		
		// draw debug log
		log.printToScreen(p.g, 20, 20);
	}
	
	// add neighbors!
	
	protected void addNewNeighbor() {
		boolean createdNeighbor = false;
		Polygon randPoly = polygons.get(MathUtil.randRange(0, polygons.size() - 1));
		if(randPoly.needsNeighbors()) {
			Polygon newNeighbor = createNeighborTriangle(randPoly);
			// TODO: check to see if close points could close between other mesh polygons
			
			if(newNeighbor != null) {
				polygons.add(newNeighbor);
				createdNeighbor = true;
			}
		}
		P.out("createdNeighbor", createdNeighbor);
	}
	
	public Polygon createNeighborTriangle(Polygon parentPoly) {
		// get edge
		Edge edge = parentPoly.availableNeighborEdge();
		// find a reasonable new vertex for a neighbor 
		PVector newNeighborVertex = parentPoly.newNeighbor3rdVertex(edge, MathUtil.randRangeDecimal(3.14f, 5));
		
		// new triangle off the Edge, but lerp the shared edge away a tiny bit to prevent overlap check
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
			// if we're not overlapped, but close to another vertex, let's snap
			for (int i = 0; i < polygons.size(); i++) {
				for (int j = 0; j < polygons.get(i).vertices().size(); j++) {
					PVector vertex = polygons.get(i).vertices().get(j);
					if(newNeighborVertex.dist(vertex) < 100) {
						newNeighborVertex.set(vertex);
						overlappedPoly = polygons.get(i);	// ensures that the neighbors are connected below
						log.update("SNAP!");
					}
				}
			}
		}
		
		// new triangle off the Edge
		Polygon newNeighbor = new Polygon(new float[] {
				edge.v1().x, edge.v1().y, edge.v1().z,
				edge.v2().x, edge.v2().y, edge.v2().z,
				newNeighborVertex.x, newNeighborVertex.y, newNeighborVertex.z
		});
		
		// check to see if overlapped poly now has a shared edge with new poly, so undo overlapped flag
		if(overlappedPoly != null) {
			if(overlappedPoly.findSharedEdge(newNeighbor) != null) {
				// TODO: MAKE SURE EDGE DOESN"T ALREADY HAVE A NEIGHBOR! 
				// THESE ARE DOUBLING UP, WHICH IS A BUG
				overlappedPoly.setNeighbor(newNeighbor);
				newNeighbor.setNeighbor(overlappedPoly);
				overlappedPoly = null;
			}
		}
		
		// if not overlapping another, add to collection
		if(overlappedPoly == null) {
			// tell polys about their shared edges
			parentPoly.setNeighbor(newNeighbor);
			newNeighbor.setNeighbor(parentPoly);
			return newNeighbor;
		} else {
			// TODO: put this in an object pool for recycling
			return null;
		}
	}

	// user input
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			for (int i = 0; i < 10; i++) addNewNeighbor();
		}
	}
}
