package com.haxademic.core.draw.filters.pgraphics;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.ImageFramesHistory;
import com.haxademic.core.math.MathUtil;

import processing.core.PImage;

public class RadialHistory
extends BaseVideoFilter {
	
	protected ImageFramesHistory recorder;
	protected int numFrames = 20;
	protected int spacing = 15;

	public RadialHistory(int width, int height) {
		super(width, height);
		
		recorder = new ImageFramesHistory(width, height, numFrames);
	}

	public void newFrame(PImage frame) {
		super.newFrame(frame);
		
		// use copied sourceBuffer instead of frame
		recorder.addFrame(sourceBuffer);
	}
	
	public void update() {
		// set up context
		destBuffer.beginDraw();
		PG.setDrawCenter(destBuffer);
		PG.setCenterScreen(destBuffer);
		
		// draw tunnel
		for (int i = 0; i < recorder.images().length; i++) {
			float imageScale = MathUtil.scaleToTarget(destBuffer.height, destBuffer.height - spacing * i);
			destBuffer.image(recorder.getSortedFrame(i), 0, 0, destBuffer.width * imageScale, destBuffer.height * imageScale);
		}
		
		// pop context
		PG.setDrawCorner(destBuffer);
		destBuffer.endDraw();
	}
	
}
