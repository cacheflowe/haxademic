package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BlurByTextureTransfer 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PImage sourceImg;
	protected PGraphics copySmallBuffer;
	protected PGraphics copyBackBuffer;

	public void setupFirstFrame() {
		// prep buffers
		sourceImg = DemoAssets.smallTexture();
		float scaleDown = 0.2f;
		copySmallBuffer = p.createGraphics(P.round(sourceImg.width * scaleDown), P.round(sourceImg.height * scaleDown), P.P3D);
		copyBackBuffer = p.createGraphics(sourceImg.width, sourceImg.height, P.P3D);

		// copy image down and back up for blur
		ImageUtil.copyImage(sourceImg, copySmallBuffer);
		ImageUtil.copyImage(copySmallBuffer, copyBackBuffer);
		
	}

	public void drawApp() {
		// set up context
		p.background(100);
		p.noStroke();

		// draw steps/results to screen
		p.image(sourceImg, 0, 0);
		p.image(copySmallBuffer, sourceImg.width, 0);
		p.image(copyBackBuffer, sourceImg.width + copySmallBuffer.width, 0);
	}
}
