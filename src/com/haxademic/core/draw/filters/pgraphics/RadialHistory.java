package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.ImageSequenceRecorder;
import com.haxademic.core.math.MathUtil;

import processing.core.PImage;

public class RadialHistory
extends BaseVideoFilter {
	
	protected ImageSequenceRecorder recorder;
	protected int numFrames = 20;
	protected int spacing = 15;

	public RadialHistory(int width, int height) {
		super(width, height);
		
		recorder = new ImageSequenceRecorder(width, height, numFrames);
	}

	public void newFrame(PImage frame) {
		super.newFrame(frame);
		
		// use copied sourceBuffer instead of frame
		recorder.addFrame(sourceBuffer);
	}
	
	public void update() {
		// set up context
		destBuffer.beginDraw();
		DrawUtil.setDrawCenter(destBuffer);
		DrawUtil.setCenterScreen(destBuffer);
		
		// draw tunnel
		for (int i = 0; i < recorder.images().length; i++) {
			int shaderFrame = (recorder.frameIndex() + 1 + i) % recorder.images().length;
			float imageScale = MathUtil.scaleToTarget(destBuffer.height, destBuffer.height - spacing * i);
			destBuffer.image(recorder.images()[shaderFrame], 0, 0, destBuffer.width * imageScale, destBuffer.height * imageScale);
		}
		
		// pop context
		DrawUtil.setDrawCorner(destBuffer);
		destBuffer.endDraw();
	}
	
}
