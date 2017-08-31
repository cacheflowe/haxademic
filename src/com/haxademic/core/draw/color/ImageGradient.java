package com.haxademic.core.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class ImageGradient {
	
	protected PImage gradientImg;
	protected float gradientProgress;
	protected int sampleX;
	protected int sampleY;
	
	public ImageGradient(PImage texture) {
		gradientImg = texture;
		gradientImg.loadPixels();
		sampleY = P.round(gradientImg.height * 0.5f);
	}
	
	public PImage texture() {
		return gradientImg;
	}
	
	public int getColorAtProgress(float progress) {
		sampleX = P.round(gradientImg.width * progress);
		sampleX = P.constrain(sampleX, 0, gradientImg.width - 1);
		return ImageUtil.getPixelColor(gradientImg, sampleX, sampleY);
	}
	
	public void drawDebug(PGraphics pg) {
		pg.image(gradientImg, 0, 0);
		pg.stroke(255, 0, 0);
		pg.noFill();
		pg.rect(sampleX - 1, 0, 3, gradientImg.height);
		pg.fill(255);
		pg.noStroke();
	}
	
}