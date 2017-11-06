package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PerlinTexture {

	/**
	 * Borrowed from Daniel Shiffman @ https://processing.org/examples/noise2d.html
	 */

	protected PApplet p;
	protected PGraphics canvas;

	public PerlinTexture(PApplet p, int w, int h) {
		this.p = p;
		canvas = p.createGraphics(w, h, P.P3D);
	}

	public PGraphics texture() {
		return canvas;
	}

	public void update(float increment, float detail, float xStart, float yStart) {
		if(p == null) return;
		canvas.beginDraw();
		canvas.loadPixels();

		float xoff = 0.0f; // Start xoff at 0
		p.noiseDetail(8, detail);

		// For every x,y coordinate in a 2D space, calculate a noise value and produce a brightness value
		for (int x = 0; x < canvas.width; x++) {
			xoff += increment;   // Increment xoff 
			float yoff = 0.0f;   // For every xoff, start yoff at 0
			for (int y = 0; y < canvas.height; y++) {
				yoff += increment; // Increment yoff
				// Calculate noise and scale by 255
				float bright = p.noise(xoff + xStart, yoff + yStart) * 255;
				// Set each pixel onscreen to a grayscale value
				canvas.pixels[x+y*canvas.width] = canvas.color(bright);
			}
		}

		canvas.updatePixels();
		canvas.endDraw();
	}
}
