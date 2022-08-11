package com.haxademic.core.draw.shapes;

import com.haxademic.core.app.P;

import processing.core.PGraphics;
import processing.core.PVector;

public class LineTrail {
	
	protected int size; 
	protected int NO_FILL = -99; 
	protected PVector[] trail; 
	
	public LineTrail(int size) {
		this.size = size;
	}
	
	public void reset(PVector newPos) {
		for (int i = 0; i < size; i++) trail[i].set(newPos);
	}
	
	public void update(PGraphics pg, PVector newPos) {
		update(pg, newPos, NO_FILL, NO_FILL);
	}
	
	public void update(PGraphics pg, PVector newPos, int colorStart, int colorEnd) {
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
			PVector curSegment = trail[i]; 
			PVector nexSegment = trail[i+1]; 
			if(curSegment.dist(nexSegment) != 0) {
				if(colorStart != NO_FILL) {
					float progress = (float) i / (float) size;
					pg.stroke(P.p.lerpColor(colorStart, colorEnd, progress));
				}
				pg.line(curSegment.x, curSegment.y,  curSegment.z, nexSegment.x, nexSegment.y, nexSegment.z);
			}
		}
	}
	
	protected PVector utilVec = new PVector(); 
	public void smoothLine() {
		for (int i = 1; i < size - 2; i++) {
			// get each point's prev/next segments
			PVector prevSegment = trail[i-1]; 
			PVector curSegment = trail[i];
			PVector nexSegment = trail[i+1];
			
			// find midpoint between segments
			utilVec.set(prevSegment);
			utilVec.lerp(nexSegment, 0.5f);
			
			// lerp towards midpoint by some factor
			float smoothAmp = 0.02f * (1f - (i / size)); // reduce smoothing towards end of line
			curSegment.lerp(utilVec, smoothAmp);
		}
	}
}
