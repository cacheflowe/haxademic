package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;
import processing.core.PImage;

public class FrozenImageMonitor {
	
	protected PGraphics pg;
	protected float lastAnalysis = 0;

	public FrozenImageMonitor() {
		pg = PG.newPG(64, 64);
		P.outInit("FrozenImageMonitor() | Don't update at 60fps, ");
		P.outInit("(or faster than your video stream is updating),");
		P.outInit("or you'll get false positives.");
		P.outInit("Also, only call in drawPre()!");
		P.outInitLineBreak();
	}
	
	public PGraphics buffer() {
		return pg;
	}

	public float lastAnalysis() {
		return lastAnalysis;
	}

	public boolean isFrozen(PImage img) {
		float lastVal = lastAnalysis;
		float pixelDataCount = 0;
		if(img.width > 32) {
			pg.beginDraw();
			pg.background(0);
			ImageUtil.copyImage(img, pg);
			pg.loadPixels();
			pg.endDraw();
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.25f), P.round(pg.height * 0.25f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.25f), P.round(pg.height * 0.25f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.25f), P.round(pg.height * 0.25f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.75f), P.round(pg.height * 0.25f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.75f), P.round(pg.height * 0.25f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.75f), P.round(pg.height * 0.25f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.25f), P.round(pg.height * 0.75f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.25f), P.round(pg.height * 0.75f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.25f), P.round(pg.height * 0.75f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.75f), P.round(pg.height * 0.75f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.75f), P.round(pg.height * 0.75f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.75f), P.round(pg.height * 0.75f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.35f), P.round(pg.height * 0.35f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.35f), P.round(pg.height * 0.35f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.35f), P.round(pg.height * 0.35f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.65f), P.round(pg.height * 0.35f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.65f), P.round(pg.height * 0.35f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.65f), P.round(pg.height * 0.35f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.35f), P.round(pg.height * 0.65f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.35f), P.round(pg.height * 0.65f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.35f), P.round(pg.height * 0.65f)));
			pixelDataCount += P.p.red(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.65f), P.round(pg.height * 0.65f)));
			pixelDataCount += P.p.green(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.65f), P.round(pg.height * 0.65f)));
			pixelDataCount += P.p.blue(ImageUtil.getPixelColor(pg, P.round(pg.width * 0.65f), P.round(pg.height * 0.65f)));
			lastAnalysis = pixelDataCount;
		}
		return pixelDataCount == lastVal;
	}
	
}