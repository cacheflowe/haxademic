package com.haxademic.core.draw.image;

import java.util.ArrayList;

import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;
import processing.core.PImage;

public class MotionBlurPGraphics {
	
	int _blurFrames = 1;
	protected ArrayList<PImage> _pastFrames;

	public MotionBlurPGraphics(int frames) {
		_blurFrames = frames;
		_pastFrames = new ArrayList<PImage>();
	}
	
	public void updateToCanvas(PImage img, PGraphics canvas, float maxAlpha) {
		// save current frame to buffer
		_pastFrames.add(img.copy());
		if(_pastFrames.size() > _blurFrames) {
			_pastFrames.remove(0);
		}
		
		// draw all frames to screen
		for (int f=0; f < _pastFrames.size(); f++) {
			float alpha = (f+1f) * maxAlpha / _pastFrames.size();
			PImage pastFrame = _pastFrames.get(f);
			PG.setPImageAlpha(canvas, alpha);
			canvas.image(pastFrame, 0, 0);
		}
	}
	
	public void clearFrames() {
		_pastFrames.clear();
	}
}