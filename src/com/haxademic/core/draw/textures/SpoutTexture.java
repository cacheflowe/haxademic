package com.haxademic.core.draw.textures;

import com.haxademic.core.app.P;

import processing.core.PConstants;
import processing.core.PGraphics;
import spout.Spout;

public class SpoutTexture {

	protected Spout spout;
	protected PGraphics texture;

	public SpoutTexture(int w, int h) {
		spout = new Spout(P.p);
		texture = P.p.createGraphics(w, h, PConstants.P2D);
	}

	public PGraphics texture() {
		return texture;
	}

	public void update() {
		spout.receiveTexture(texture);
	}
}
