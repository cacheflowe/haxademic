package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class TickerScroller {
	
	protected PImage scrolledImage;
	protected PGraphics tickerBuffer;
	protected int bgColor;
	protected float scrollSpeed = 1.4f;
	protected float scrollX = 0;

	float imgScale;
	float scrolledImageW;
	float scrolledImageH;
	
	public TickerScroller(PImage img, int bgColor, int w, int h, float speed) {
		this.scrolledImage = img;
		this.bgColor = bgColor;
		this.scrollSpeed = speed;
		tickerBuffer = P.p.createGraphics(w, h, P.P3D);
		tickerBuffer.smooth(8);
		// calculate scaled image size 
		imgScale = MathUtil.scaleToTarget(scrolledImage.height, tickerBuffer.height);
		scrolledImageW = (float) scrolledImage.width * imgScale;
		scrolledImageH = (float) scrolledImage.height * imgScale;
	}
	
	public PImage image() {
		return tickerBuffer;
	}
	
	public void speed(float speed) {
		scrollSpeed = speed;
	}
	
	public void update() {
		// draw scrolling graphics
		tickerBuffer.beginDraw();
		tickerBuffer.background(bgColor);
		
		// calc repeating image size & draw across buffer
		float curDrawX = scrollX;
		while(curDrawX < tickerBuffer.width) {
			tickerBuffer.image(scrolledImage, curDrawX, 0, scrolledImageW, scrolledImageH);
			curDrawX += scrolledImageW;
		}
		
		// scroll left with positive scroll speed
		scrollX -= scrollSpeed;
		if(scrollX < -scrolledImageW) scrollX += scrolledImageW;
		if(scrollX > 0) scrollX -= scrolledImageW;
		
		tickerBuffer.endDraw();
	}
}