package com.haxademic.core.draw.text;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class FitTextBuffer {
	
	protected PFont font;
	protected int color;
	protected PGraphics buffer;
	protected PImage croppedText;
	
	public FitTextBuffer(PFont font, int color) {
		this.font = font;
		this.color = color;
		buffer = PG.newPG(2048, P.ceil(font.getSize() * 1.4f));
		croppedText = P.p.createImage(16, 16, P.ARGB);
	}
	
	public PGraphics buffer() {
		return buffer;
	}
	
	public PImage crop() {
		return croppedText;
	}
	
	public void updateText(String text) {
		// set text size
//		buffer.beginDraw();
//		buffer.textFont(font);
//		int textW = P.ceil(buffer.textWidth(text) * 1.001f);
//		buffer.endDraw();
		
		// draw
		buffer.beginDraw();
		buffer.clear();
		buffer.noStroke();
		buffer.fill(color);
		buffer.textAlign(P.CENTER, P.TOP);
		buffer.textFont(font);
		buffer.text(text, 0, 0, buffer.width, buffer.height);
		buffer.endDraw();
		
		// analyze & crop
		ImageUtil.imageCroppedEmptySpace(buffer, croppedText, ImageUtil.EMPTY_INT, false);
	}
	
}