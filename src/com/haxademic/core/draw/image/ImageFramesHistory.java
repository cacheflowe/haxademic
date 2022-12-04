package com.haxademic.core.draw.image;

import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;
import processing.core.PImage;

public class ImageFramesHistory {

	protected int width;
	protected int height;
	protected int numFrames;
	protected int frameIndex = 0;
	protected PGraphics[] images;
	protected PGraphics[] imagesSorted;
	
	// TODO: 
	// - Update other classes that use frames like this
	// - Make the slitscan app use 15-16 images, and try to not pass in `texture`
	
	public ImageFramesHistory(int width, int height, int frames) {
		this.width = width;
		this.height = height;
		this.numFrames = frames;
		buildFrames();
	}
	
	protected void buildFrames() {
		images = new PGraphics[numFrames];
		imagesSorted = new PGraphics[numFrames];
		for (int i = 0; i < numFrames; i++) {
			images[i] = PG.newPG2DFast(width, height);
			imagesSorted[i] = images[i];
		}
	}
	
	public PGraphics[] images() {
		return images;
	}
	
	public int frameIndex() {
		return frameIndex;
	}
	
	public void reset() {
	    frameIndex = 0;
	}
	
	public int addFrame(PImage img) {
	    ImageUtil.cropFillCopyImage(img, getNextBuffer(), true);
		return frameIndex;
	}
	
	public PGraphics getCurFrame() {
		return images[frameIndex];
	}
	
	public PGraphics getSortedFrame(int index) {
		return imagesSorted[index];
	}
	
	protected PImage getNextBuffer() {
		frameIndex = (frameIndex < numFrames-1) ? frameIndex + 1 : 0;
		updateSortedImages();
		return images[frameIndex];
	}
	
	protected void updateSortedImages() {
		for (int i = 0; i < numFrames; i++) {
			int curIndex = (frameIndex - i) % numFrames;
			while(curIndex < 0) curIndex += numFrames;
			imagesSorted[i] = images[curIndex];
		}
	}
	
	public void drawDebug(PGraphics pg) {
		drawDebug(pg, false);
	}
	
	public void drawDebug(PGraphics pg, boolean openContext) {
		if(openContext) pg.beginDraw();
		float frameW = (float) pg.width / (float) numFrames;
		float frameH = frameW * ((float) height / (float) width);
		for (int i = 0; i < numFrames; i++) {
			float x = frameW * i;
			pg.image(imagesSorted[i], x, 0, frameW, frameH);
		}
		if(openContext) pg.endDraw();
	}
}
