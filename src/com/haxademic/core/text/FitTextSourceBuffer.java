package com.haxademic.core.text;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;

import processing.core.PFont;
import processing.core.PGraphics;

public class FitTextSourceBuffer {
	
	protected PFont font;
	protected int color;
	protected PGraphics buffer;
	
	// This is the large offscreen canvas we draw text into.
	// This text is then cropped and turned into a tiling texture
	
	public FitTextSourceBuffer(PFont font, int color) {
		this.font = font;
		this.color = color;
		buffer = P.p.createGraphics(4096, P.ceil(font.getSize() * 1.1f));
		buffer.smooth(8);
	}
	
	public PGraphics buffer() {
		return buffer;
	}
	
	public void updateText(String text) {
		// draw
		buffer.beginDraw();
		buffer.clear();
		buffer.background(0, 0);
		buffer.noStroke();
		buffer.background(0, 0);
		buffer.fill(color);
		buffer.textAlign(P.CENTER, P.CENTER);
		buffer.textFont(font);
		// buffer.textLeading(font.getSize() * 0.75f);
		buffer.text(text, 0, 0, buffer.width, buffer.height);
		buffer.endDraw();
	}
	
}