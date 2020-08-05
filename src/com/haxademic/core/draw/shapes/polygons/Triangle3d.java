package com.haxademic.core.draw.shapes.polygons;

import processing.core.PVector;

public class Triangle3d {

	public PVector v1 = new PVector(); 
	public PVector v2 = new PVector(); 
	public PVector v3 = new PVector(); 
	
	public Triangle3d(PVector v1, PVector v2, PVector v3) {
		this.v1.set(v1);
		this.v2.set(v2);
		this.v3.set(v3);
	}
	
	public String toString() {
		return 
				"["+v1.x+","+v1.y+","+v1.z+"], " + 
				"["+v2.x+","+v2.y+","+v2.z+"], " + 
				"["+v3.x+","+v3.y+","+v3.z+"]";
	}
}
