package com.haxademic.core.draw.text;

import com.haxademic.core.app.P;

import processing.core.PGraphics;

public class StrokeText {

	public static void draw(PGraphics pg, String str, float x, float y, int strokeColor, int fillColor, float thickness, int resolution) {
		draw(pg, str, x, y, -1, -1, strokeColor, fillColor, thickness, resolution);
	}
	
	public static void draw(PGraphics pg, String str, float x, float y, float w, float h, int strokeColor, int fillColor, float thickness, int resolution) {
		// translate
		pg.pushMatrix();
		pg.translate(x, y);
		
		// draw text around in a circle 
		pg.fill(strokeColor);
		float segmentRads = P.TWO_PI / resolution;
		for (int i = 0; i < resolution; i++) {
			float outlineX = P.cos(segmentRads * i);
			float outlineY = P.sin(segmentRads * i);
			if(w == -1) {
				pg.text(str, thickness * outlineX, thickness * outlineY);
			} else {
				pg.text(str, thickness * outlineX, thickness * outlineY, w, h);
			}
		}
		
		// fill in center
		pg.fill(fillColor);
		if(w == -1) {
			pg.text(str, 0, 0);
		} else {
			pg.text(str, 0, 0, w, h);
		}
		
		// pop
		pg.popMatrix();
	}

}
