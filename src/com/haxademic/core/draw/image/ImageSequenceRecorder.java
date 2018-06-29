package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;

import processing.core.PGraphics;
import processing.core.PImage;

public class ImageSequenceRecorder {

	protected int width;
	protected int height;
	protected int numFrames;
	protected int frameIndex = 0;
	protected PGraphics[] images;
	
	public ImageSequenceRecorder(int width, int height, int frames) {
		this.width = width;
		this.height = height;
		this.numFrames = frames;
		buildFrames();
	}
	
	protected void buildFrames() {
		images = new PGraphics[numFrames];
		for (int i = 0; i < numFrames; i++) {
			images[i] = P.p.createGraphics(width, height, PRenderers.P2D);
			images[i].noSmooth();
		}
	}
	
	public PGraphics[] images() {
		return images;
	}
	
	public int frameIndex() {
		return frameIndex;
	}
	
	public int addFrame(PImage img) {
		ImageUtil.cropFillCopyImage(img, getNextBuffer(), true);
		return frameIndex;
	}
	
	public PGraphics getCurFrame() {
		return images[frameIndex];
	}
	
	protected PImage getNextBuffer() {
		frameIndex = (frameIndex < numFrames-1) ? frameIndex + 1 : 0;
		return images[frameIndex];
	}
	
	public void drawDebug(PGraphics pg) {
		pg.beginDraw();
		float frameW = (float) pg.width / (float) numFrames;
		float frameH = frameW * ((float) height / (float) width);
		for (int i = 0; i < numFrames; i++) {
			float x = frameW * i;
			int curIndex = (frameIndex - i) % numFrames;
			while(curIndex < 0) curIndex += numFrames; 
			pg.image(images[curIndex], x, 0, frameW, frameH);
		}
		pg.endDraw();
	}
}
