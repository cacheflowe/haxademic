package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class InnerCircleTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage img;
	PGraphics pg;


	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
	}

	protected void firstFrame() {

		img = p.loadImage(FileUtil.getPath("images/_sketch/bread-large.png"));
		pg = ImageUtil.imageToGraphics(img);
	}

	protected void drawApp() {
		background(100,100,255);
	
		p.fill(255);
		p.noStroke();
		beginShape();
		// Exterior part of shape, clockwise winding
		vertex(0, 0);
		vertex(p.width, 0);
		vertex(p.width, p.height);
		vertex(0, p.height);
		// Interior part of shape, counter-clockwise winding
		beginContour();
		float segments = 360f;
		float segmentRads = P.TWO_PI / segments;
		float radius = 100f;
		for(float i = 0; i < segments; i++) {
			vertex(p.width * 0.5f + radius * P.cos(-i * segmentRads), p.height * 0.5f + radius * P.sin(-i * segmentRads));
		}
		endContour();
		endShape(CLOSE);
	}

}

