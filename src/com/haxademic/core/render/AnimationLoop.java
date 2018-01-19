package com.haxademic.core.render;

import com.haxademic.core.app.P;

public class AnimationLoop {

	protected float frames;
	protected float loopFrames;
	protected float progress;
	protected float progressRads;
	
	public AnimationLoop(float frames) {
		this.frames = frames;
		loopFrames = 0;
	}
	
	public float frames() {
		return frames;
	}
	
	public float loopFrames() {
		return loopFrames;
	}
	
	public float progress() {
		return progress;
	}
	
	public float progressRads() {
		return progressRads;
	}
	
	public void update() {
		loopFrames = (float) P.p.frameCount % frames;
		progress = loopFrames / frames;
		progressRads = progress * P.TWO_PI;
		if(P.p.debugView != null) P.p.debugView.setValue("AnimationLoop.progress()", progress);
	}
	
}
