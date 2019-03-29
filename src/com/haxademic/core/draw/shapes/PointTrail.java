package com.haxademic.core.draw.shapes;

import processing.core.PGraphics;
import processing.core.PVector;

public class PointTrail {
	
	protected int size; 
	protected PVector[] trail; 
	
	public PointTrail(int size) {
		this.size = size;
	}
	
	public void update(PGraphics pg, PVector newPos) {
		// init points to start point
		if(trail == null) {
			trail = new PVector[size];
			for (int i = 0; i < size; i++) trail[i] = newPos.copy();
		}
		
		// copy all positions towards tail end each step
		for (int i = size - 1; i > 0; i--) {
			trail[i].set(trail[i-1]);
		}
		trail[0].set(newPos);
		
		// render
		for (int i = 0; i < size - 1; i++) {
			pg.line(trail[i].x, trail[i].y,  trail[i].z, trail[i+1].x, trail[i+1].y, trail[i+1].z);
		}
	}
	
}
