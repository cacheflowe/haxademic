package com.haxademic.sketch.test;

import processing.core.PApplet;

public class PAppletTest
extends PApplet {
	public static void main(String args[]) { PApplet.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	public void settings() {
		super.settings();
		size(400, 400, P3D);
	}
	
	public void setupFirstFrame() {
	
	}

	public void draw() {
		super.draw();
		background(0);
		translate(width/2, height/2);
		fill(255, 0, 255);
		stroke(255);
		
		for(int z = 0; z > -20000; z -= 100) {
			translate(40, -40, -100);
			box(100 + -z/100);
		}
	}
}
