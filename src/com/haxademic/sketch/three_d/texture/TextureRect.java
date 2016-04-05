package com.haxademic.sketch.three_d.texture;

import processing.core.PImage;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.FileUtil;

public class TextureRect 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage img;
	
	public void setup() {
		super.setup();
		img = loadImage(FileUtil.getHaxademicDataPath() + "images/justin-tiny-color1.png");
		noStroke();
	}
	
	public void drawApp() {
		background(0);
		float f = (float) frameCount;
		beginShape(QUADS);
		texture(img);
		vertex(50+ 90f*sin(f/10f), 70+ 50f*sin(f/9f),  150f*sin(f/6f), 			0, 0);
		vertex(450+ 90f*sin(f/7f), 100+ 50f*sin(f/7f), 150f*sin(f/7f), 			img.width, 0);
		vertex(500+ 90f*sin(f/3f), 550+ 50f*sin(f/4f), 150f*sin(f/3f), 			img.width, img.height);
		vertex(90+ 90f*sin(f/9f),  600+ 60f*sin(f/6f), 150f*sin(f/8f), 			0, img.height);
		endShape();
	}
}
