package com.haxademic.demo.draw.shapes.polygons;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
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
	}
	
	// add neighbors!
	
	protected void addNewNeighbor() {
		boolean createdNeighbor = false;
		Polygon randPoly = polygons.get(MathUtil.randRange(0, polygons.size() - 1));
		if(randPoly.needsNeighbors()) {
			Polygon newNeighbor = createNeighborTriangle(randPoly);
			// TODO: check to see if close points could close between other mesh polygons
			polygons.add(newNeighbor);
			createdNeighbor = true;
		}
		P.out("createdNeighbor", createdNeighbor);
	}
	
	public Polygon createNeighborTriangle(Polygon polygon) {
		// get edge
		Edge edge = polygon.availableNeighborEdge();
		// find a reasonable new vertex for a neighbor 
		PVector newNeighborVertex = polygon.newNeighbor3rdVertex(edge, MathUtil.randRangeDecimal(2, 6));
		
		// new triangle off the Edge
		Polygon newNeighbor = new Polygon(new float[] { 
				edge.v1().x, edge.v1().y, edge.v1().z,
				edge.v2().x, edge.v2().y, edge.v2().z,
				newNeighborVertex.x, newNeighborVertex.y, newNeighborVertex.z
		});
		
		// tell polys about their shared edges
		polygon.setNeighbor(newNeighbor);
		newNeighbor.setNeighbor(polygon);
		
		return newNeighbor;
	}

	// user input
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			addNewNeighbor();
		}
	}
}
