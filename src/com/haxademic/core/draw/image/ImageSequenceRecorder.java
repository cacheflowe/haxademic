package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;

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
			images[i] = PG.newPG(width, height);
		}
	}
	
	public PGraphics[] images() {
		return images;
	}
	
	public PGraphics imageAtFrame(int frame) {
	    return images[frame % numFrames];
	}
	
	public int frameIndex() {
		return safeIndex();
	}
	
	protected int safeIndex() {
	    return P.constrain(frameIndex, 0, numFrames - 1);
	}
	
	public void reset() {
	    frameIndex = -1;
	}
	
	public int addFrame(PImage img) {
	    if(!isComplete()) {
	        frameIndex++;
	        ImageUtil.cropFillCopyImage(img, images[frameIndex], true);
	    }
		return frameIndex;
	}
	
	public boolean isComplete() {
	    return (frameIndex < numFrames - 1) ? false : true;
	}
	
	public boolean isRecording() {
	    return !isComplete();
	}
	
	public float recordProgress() {
	    return (float) safeIndex() / numFrames;
	}
	
	public PGraphics getCurFrame() {
		return images[safeIndex()];
	}
	
	public void drawDebug(PGraphics pg) {
		drawDebug(pg, false);
	}
	
	public void drawDebug(PGraphics pg, boolean openContext) {
		if(openContext) pg.beginDraw();
		float frameW = (float) pg.width / (float) numFrames;
		float frameH = frameW * ((float) height / (float) width);
		for (int i = 0; i < safeIndex(); i++) {
			float x = frameW * i;
			pg.image(images[i], x, 0, frameW, frameH);
		}
		if(openContext) pg.endDraw();
	}
}
