package com.haxademic.demo.draw.shapes;

import java.util.ArrayList;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.shapes.Polygon;
import com.haxademic.core.math.MathUtil;

public class Demo_Polygon 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/*
	  * New neighbors!
	    * Make sure neighbors are stored
		* don't try to create new neighbors on top of others
		* search for close-enough points to close shapes if there are already 2 sides 
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
	
	protected void setupFirstFrame() {
		polygons = new ArrayList<Polygon>();
		polygons.add((new Polygon(new float[] {200, 200, 0, 200, 300, 0, 00, 150, 0})));
	}
	
	public void drawApp() {
		background(0);
		p.stroke(255);
		p.noFill();
		
		// draw polygons
		for (int i = 0; i < polygons.size(); i++) {
			polygons.get(i).draw(p.g);
		}
	}
	
	protected void addNewNeighbor() {
		Polygon randPoly = polygons.get(MathUtil.randRange(0, polygons.size() - 1));
		polygons.add(randPoly.createNeighbor());
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			addNewNeighbor();
		}
	}
}
