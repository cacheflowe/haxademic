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
	protected PGraphics sourceBuffer;
	protected PImage croppedText;
	
	public FitTextBuffer(PFont font, int color) {
		this.font = font;
		this.color = color;
		sourceBuffer = PG.newPG(2048, P.ceil(font.getSize() * 1.4f));
		croppedText = ImageUtil.newImage(16, 16);
	}
	
	public PGraphics source() {
		return sourceBuffer;
	}
	
	public PImage crop() {
		return croppedText;
	}
	
	public void updateText(String text) {
		// draw text to source buffer
		sourceBuffer.beginDraw();
		sourceBuffer.clear();
		sourceBuffer.noStroke();
		sourceBuffer.fill(color);
		sourceBuffer.textAlign(P.CENTER, P.TOP);
		sourceBuffer.textFont(font);
		sourceBuffer.text(text, 0, 0, sourceBuffer.width, sourceBuffer.height);
		sourceBuffer.endDraw();
		
		// analyze & crop - store results in new PImage
		ImageUtil.imageCroppedEmptySpace(sourceBuffer, croppedText, ImageUtil.EMPTY_INT, false);
	}
	
}