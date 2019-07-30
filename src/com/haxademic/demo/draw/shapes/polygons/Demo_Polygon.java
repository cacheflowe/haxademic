package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
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
		* Sometimes subdivide a poly
		* New point should snap to line if very close, not just snap to other points
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
	
	// mesh / growth
	protected ArrayList<Polygon> polygons;
	protected float baseShapeSize = 100;
	protected float baseShapeArea = 0;
	protected float snapRadius = baseShapeSize / 2f;
	protected Polygon tempTriangle;
	
	// search for available connections
	protected ArrayList<PVector> availableVertices = new ArrayList<PVector>();
	protected PVector vClose1 = new PVector();
	protected PVector vClose2 = new PVector();
	protected PVector vCompare;
	protected int[] closeIndexes = new int[3];
	
	// debug
	protected PVector mouseVec = new PVector();
	protected StringBufferLog log = new StringBufferLog(30);
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 720 );
	}

	protected void setupFirstFrame() {
		tempTriangle = Polygon.buildShape(0, 0, 3, 100);
		newSeedPolygon();
	}
	
	protected void newSeedPolygon() {
		polygons = new ArrayList<Polygon>();
		Polygon firstPoly = Polygon.buildShape(p.width * 0.5f, p.height * 0.5f, 3, baseShapeSize);
		polygons.add(firstPoly);
		baseShapeArea = CollisionUtil.polygonArea(firstPoly);
//		polygons.add((new Polygon(new float[] {300, 200, 0, 200, 300, 0, 400, 300, 0})));
	}
	
	public void drawApp() {
		background(0);
		p.stroke(255, 255, 255, 100);
		p.noFill();
		
		// draw polygons
		mouseVec.set(p.mouseX, mouseY);
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).collided(CollisionUtil.polygonContainsPoint(polygons.get(i), mouseVec));
			polygons.get(i).draw(p.g);
		}
		
		// auto-create neighbors
		if(p.frameCount % 1 == 0) {
			for(int i=0; i < 20; i++) addNewNeighbor();
		}
		if(p.frameCount % 1 == 0) {
			closeNeighbors();
		}
		ensureNeighborsConnect();
			
		// draw debug log
		log.printToScreen(p.g, 20, 20);
	}
	
	// add neighbors!
	
	protected Polygon randomPolygon() {
		return polygons.get(MathUtil.randRange(0, polygons.size() - 1));
	}
	
	protected void addNewNeighbor() {
		// find a polygon that needs neighbors
		Polygon randPoly = randomPolygon();
		int attempts = 0;
		while(randPoly.needsNeighbors() == false && attempts < 100) {
			randPoly = randomPolygon();
			attempts++;
		}
//		log.update("attempts: " + attempts);
		
		// try to add a neighbor
		if(randPoly.needsNeighbors()) {
			Polygon newNeighbor = createNeighborTriangle(randPoly);
			if(newNeighbor != null) {
				polygons.add(newNeighbor);
				ensureNeighborsConnect();
				log.update("createdNeighbor!");
			}
		}
		
		// attempt to close connections
//		closeNeighbors();
	}
	
	public Polygon createNeighborTriangle(Polygon parentPoly) {
		// get edge
		Edge edge = parentPoly.availableNeighborEdge();
		// find a reasonable new vertex for a neighbor 
		// TODO: this is where magic layout tweaks can happen by adjusting length of children, depending on location or iteration
		// TODO: should this happen outside  of the polygon? How else can we find a new vertex?
		PVector newNeighborVertex = parentPoly.newNeighbor3rdVertex(edge, MathUtil.randRangeDecimal(0.5f, 1.8f));
		
		// new triangle off the Edge, but lerp the shared edge away a tiny bit to prevent overlap check
		tempTriangle.setVertex(0, edge.v1());
		tempTriangle.setVertex(1, edge.v2());
		tempTriangle.setVertex(2, newNeighborVertex);
		tempTriangle.shrink(0.001f);

		
		// check to see if we're overlapping with another polygon
		Polygon overlappedPoly = null;
		for (int i = 0; i < polygons.size(); i++) {
			if(overlappedPoly == null) {
				if(CollisionUtil.polygonsIntersect(polygons.get(i), tempTriangle)) {
					overlappedPoly = polygons.get(i);
//					log.update("overlappedPoly");
				}
			}
		}
		
		// if we're overlapping another poly, try to move the new vertex to the closest vertex of the overlapped triangle, then see if the two triangles share an edge
		if(overlappedPoly != null) {
			PVector closestOverlappedVert = overlappedPoly.closestVertexToVertex(newNeighborVertex);
			newNeighborVertex.set(closestOverlappedVert);
//			log.update("OVERLAP SNAP!");
		} else {
			// if we're not overlapped, but close to another vertex, let's try to snap
			boolean snapped = false;
			for (int i = 0; i < polygons.size(); i++) {
				for (int j = 0; j < polygons.get(i).vertices().size(); j++) {
					if(snapped == false && polygons.get(i) != parentPoly) {		// don't snap to parent, or we get overlaps that don't get cleaned up below
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
		if(overlappedPoly != null) {
			
		}
		
		// new triangle to attach
		Polygon newNeighbor = new Polygon(new float[] {
				edge.v1().x, edge.v1().y, edge.v1().z,
				edge.v2().x, edge.v2().y, edge.v2().z,
				newNeighborVertex.x, newNeighborVertex.y, newNeighborVertex.z
		});
		
		// check area to see if we've created a garbage shape
		float newNeighborArea = CollisionUtil.polygonArea(newNeighbor);
//		log.update("newNeighborArea: " + (int) newNeighborArea);
		
		// check to see if overlapped poly now has a shared edge with new poly, so undo overlapped flag
//		if(overlappedPoly != null && newNeighborArea > 100 && overlappedPoly != parentPoly) {
//			Edge sharedEdge = overlappedPoly.findSharedEdge(newNeighbor);
//			if(sharedEdge != null) {
//				boolean edgeAlreadyHasNeighbor = overlappedPoly.edgeHasNeighbor(sharedEdge);	// make sure snapped-to edge doesn't already have a neighbor!
//				if(edgeAlreadyHasNeighbor == false) {
//					// TODO: MAKE SURE EDGE DOESN"T ALREADY HAVE A NEIGHBOR! 
//					// THESE ARE DOUBLING UP, WHICH IS A BUG
//					overlappedPoly.findNeighbor(newNeighbor);
//					newNeighbor.findNeighbor(overlappedPoly);
//					overlappedPoly = null;
//					log.update("OVERLAP FIXED?!");
//				}
//			}
//		}
		
		// if not overlapping another, add to collection
		newNeighborArea = CollisionUtil.polygonArea(newNeighbor);
		if(overlappedPoly == null && newNeighborArea > 800 && newNeighborArea < 20000) {
			// tell polys about their shared edges
			parentPoly.findNeighbor(newNeighbor);
			newNeighbor.findNeighbor(parentPoly);
			return newNeighbor;
		} else {
			// TODO: put this in an object pool for recycling
			return null;
		}
	}
	
	protected void ensureNeighborsConnect() {
		for (int i = 0; i < polygons.size(); i++) {
			for (int j = 0; j < polygons.size(); j++) {
				Polygon poly1 = polygons.get(i);
				Polygon poly2 = polygons.get(i);
				if(poly1 != poly2) {
					if(poly1.findNeighbor(poly2)) {	// if we find a neighbor, make it mutual
						poly2.findNeighbor(poly1);
					}
				}
			}
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
	
	protected void fillArrayWithUniqueIndexes(int[] arr, int maxIndex) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = MathUtil.randRange(0, maxIndex);
			while(valueExistsInArr(arr, arr[i], i)) arr[i] = MathUtil.randRange(0, maxIndex);
		}
	}
	
	protected boolean valueExistsInArr(int[] arr, int val, int curIndex) {
		for (int i = 0; i < curIndex; i++) {
			if(arr[i] == val) return true;
		}
		return false;
	}
	
	protected void closeNeighbors() {
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
		p.fill(0, 255, 0);
		for (int i = 0; i < availableVertices.size(); i++) {
			p.circle(availableVertices.get(i).x, availableVertices.get(i).y, 10);
		}
		
		// find 3 closest vertices to a vertex, make a triangle, shrink it and see if it overlaps anything. if not, create it!
		for (int i = 0; i < availableVertices.size(); i++) {
			// store current vertex and sort by distance using comparator function
			vCompare = availableVertices.get(i);
			Collections.sort(availableVertices, distanceComparator);
			
			// try random vertices instead of sorting every frame and trying the same set - this might succeed more often
			// only use the 10 closest to the current in the loop through all available
			if(availableVertices.size() >= 3) {
				fillArrayWithUniqueIndexes(closeIndexes, P.min(20, availableVertices.size()-1));
				attemptCloseTriangle(
						availableVertices.get(closeIndexes[0]), 
						availableVertices.get(closeIndexes[1]),
						availableVertices.get(closeIndexes[2])
				);
			}
		}
	}

	protected boolean attemptCloseTriangle(PVector v1, PVector v2, PVector v3) {
		float tooFarThresh = baseShapeSize * 2;
		if(v1.dist(v2) > tooFarThresh || v1.dist(v3) > tooFarThresh || v2.dist(v3) > tooFarThresh) {
//			log.update("VERTICES TOO FAR");
			return false;
		}
		
		// new to check for overlaps
		tempTriangle.setVertex(0, v1);
		tempTriangle.setVertex(1, v2);
		tempTriangle.setVertex(2, v3);
		tempTriangle.shrink(0.001f);
		
		// bail if the triangle is too big
		float newTriArea = CollisionUtil.polygonArea(tempTriangle);
		if(newTriArea > baseShapeArea) { //  || newTriArea < baseShapeArea/4) {
//			log.update("TRIANGLE TOO BIG/SMALL");
			return false;
		}

		// debug draw??
		p.strokeWeight(2);
		p.stroke(255,0,0);
		p.line(v1.x + 2, v1.y + 2, v2.x + 2, v2.y + 2);
		p.line(v1.x + 2, v1.y + 2, v3.x + 2, v3.y + 2);
		p.line(v3.x + 2, v3.y + 2, v2.x + 2, v2.y + 2);
		p.strokeWeight(1);

		// check to see if we're overlapping with another polygon
		Polygon overlappedPoly = null;
		for (int i = 0; i < polygons.size(); i++) {
			if(overlappedPoly == null) {
				if(CollisionUtil.polygonsIntersect(polygons.get(i), tempTriangle)) {
					overlappedPoly = polygons.get(i);
				}
			}
		}
		
		if(overlappedPoly == null) {
			Polygon newNeighbor = new Polygon(new float[] {
					v1.x, v1.y, v1.z,
					v2.x, v2.y, v2.z,
					v3.x, v3.y, v3.z
			});
			polygons.add(newNeighbor);
			log.update("CLOSED TRIANGLE!!!!!");
			
			// find any matching edges and add neighbor Polygons.
			// This is super important to stop edges without neighbors from being in `availableVertices`
			ensureNeighborsConnect();
			
			return true;
		} else {
			return false;
		}

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
