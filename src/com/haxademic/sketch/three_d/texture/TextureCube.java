package com.haxademic.sketch.three_d.texture;

import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.system.FileUtil;

public class TextureCube 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage img;

	public void setup() {
		super.setup();
		img = loadImage(FileUtil.getHaxademicDataPath() + "images/snowblinded-beach.jpg");
		noStroke();
	}

	public void drawApp() {
		background(0);

		translate(width/2, height/2, -30); 

		rotateX(p.frameCount/10f); 
		rotateY(p.frameCount/20f); 

		scale(100);
		beginShape(P.QUADS);
		texture(img);

		vertex(-1,  1,  1, 		0, 0);
		vertex( 1,  1,  1, 		img.width, 0);
		vertex( 1, -1,  1,		img.width, img.height);
		vertex(-1, -1,  1,		0, img.height);

		vertex( 1,  1,  1, 		0, 0);
		vertex( 1,  1, -1, 		img.width, 0);
		vertex( 1, -1, -1,		img.width, img.height);
		vertex( 1, -1,  1,		0, img.height);

		vertex( 1,  1, -1, 		0, 0);
		vertex(-1,  1, -1, 		img.width, 0);
		vertex(-1, -1, -1,		img.width, img.height);
		vertex( 1, -1, -1,		0, img.height);

		vertex(-1,  1, -1, 		0, 0);
		vertex(-1,  1,  1, 		img.width, 0);
		vertex(-1, -1,  1,		img.width, img.height);
		vertex(-1, -1, -1,		0, img.height);

		vertex(-1,  1, -1, 		0, 0);
		vertex( 1,  1, -1, 		img.width, 0);
		vertex( 1,  1,  1,		img.width, img.height);
		vertex(-1,  1,  1,		0, img.height);

		vertex(-1, -1, -1, 		0, 0);
		vertex( 1, -1, -1, 		img.width, 0);
		vertex( 1, -1,  1,		img.width, img.height);
		vertex(-1, -1,  1,		0, img.height);

		endShape();
	}
}
