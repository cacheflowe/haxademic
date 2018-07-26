package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class ImageCyclerBuffer {
	
	protected PGraphics pg;
	protected PGraphics[] imageFrames;
	protected PShader transitionShader;
	protected float frames;
	protected int imageIndex = 0;
	protected int nextIndex = 1;
	protected float transitionTime = 0.85f;

	public ImageCyclerBuffer(int w, int h, PImage[] images, int frames, float transitionTime) {
		pg = P.p.createGraphics(w, h, P.P2D);
		pg.smooth(8);
		this.frames = frames;
		this.transitionTime = transitionTime;
		
		// prep & crop fill images
		imageFrames = new PGraphics[images.length];
		for (int i = 0; i < imageFrames.length; i++) {
			imageFrames[i] = P.p.createGraphics(w, h, P.P2D);
			imageFrames[i].beginDraw();
			imageFrames[i].background(0);
			imageFrames[i].endDraw();
			ImageUtil.cropFillCopyImage(images[i], imageFrames[i], true);
		}

//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/dissolve.glsl"));
		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/directional-wipe.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/hsv-blend.glsl"));
//		transitionShader = P.p.loadShader(FileUtil.getFile("haxademic/shaders/transitions/colour-distance.glsl"));
		
		transitionShader.set("from", imageFrames[imageIndex]);
		transitionShader.set("to", imageFrames[nextIndex]);
	}
	
	public PImage image() {
		return pg;
	}
	
	public void update() {
		float progress = (P.p.frameCount % frames) / frames; 

		// switch to next image
		if(progress == 0f) {
			imageIndex = nextIndex;
			nextIndex++;
			if(nextIndex >= imageFrames.length) nextIndex = 0;
			transitionShader.set("from", imageFrames[imageIndex]);
			transitionShader.set("to", imageFrames[nextIndex]);
		}
		
		// map progress through transition
		float loopProgress = 0;
		if(progress >= transitionTime) loopProgress = P.map(progress, transitionTime, 1f, 0f, 1f);
		float easedProgress = Penner.easeInOutCubic(loopProgress, 0, 1, 1);
		transitionShader.set("progress", easedProgress);
		pg.filter(transitionShader);	
	}
	
}