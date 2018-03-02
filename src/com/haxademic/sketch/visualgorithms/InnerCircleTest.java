package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class InnerCircleTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	PImage img;
	PGraphics pg;


	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void setup() {
		super.setup();
		img = p.loadImage(FileUtil.getFile("images/_sketch/bread-large.png"));
		pg = ImageUtil.imageToGraphics(img);
	}

	public void drawApp() {
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

