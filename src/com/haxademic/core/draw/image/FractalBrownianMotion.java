package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;

import processing.core.PApplet;

public class FractalBrownianMotion {

	/**
	 * Borrowed from @wblut @ http://pastebin.com/EiHbWr8t
	 */
	PApplet p;

	// Properties
	int octaves = 8;
	float lacunarity = 2.0f;
	float gain = 0.5f;

	public FractalBrownianMotion(PApplet p) {
		this.p = p;
	}

	protected float fbm(float x, float y) {
		// Initial values
		float value=0.0f;
		float amplitude = 0.8f;
		float frequency = 0.01f;

		// Loop of octaves
		float sa = P.sin(0.5f);
		float ca = P.cos(0.5f);
		float ox;
		for (int i = 0; i < octaves; i++) {
			ox=x;
			x=x*sa+y*ca;
			y=-ox*ca+y*sa;
			value += amplitude * p.noise(frequency*(x+0.4f*p.frameCount), frequency*y);
			frequency *= lacunarity;
			amplitude *= gain;
		}
		return value;
	}

	public float f(float x, float y) {
		float tmp = fbm(x, y);
		tmp = fbm(x+34*tmp, y+34*tmp);
		tmp = fbm(x+64*tmp, y+64*tmp);
		return tmp * tmp;
	}
}
