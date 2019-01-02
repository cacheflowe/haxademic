package com.haxademic.core.render;

import com.haxademic.core.app.P;

public class AnimationLoop {

	protected float frames;
	protected float loopCurFrame;
	protected float progress;
	protected float progressRads;
	protected float ticks;
	protected int curTick;
	protected boolean isTick = false;
	
	public AnimationLoop(float frames) {
		this(frames, 4);
	}
	
	public AnimationLoop(float frames, float ticks) {
		this.frames = frames;
		loopCurFrame = 0;
		this.ticks = ticks;
		curTick = -1;
	}
	
	public float frames() {
		return frames;
	}
	
	public float loopCurFrame() {
		return loopCurFrame;
	}
	
	public float progress() {
		return progress;
	}
	
	public float progressRads() {
		return progressRads;
	}
	
	public int curTick() {
		return curTick;
	}
	
	public boolean isTick() {
		return isTick;
	}
	
	public void update() {
		// update progress
		loopCurFrame = (float) P.p.frameCount % frames;
		progress = loopCurFrame / frames;
		progressRads = progress * P.TWO_PI;
		if(P.p.debugView != null) P.p.debugView.setValue("AnimationLoop.progress()", progress);
		
		// update ticks
		int newTick = P.floor(ticks * progress);
		isTick = (curTick != newTick);
		curTick = newTick;
		if(P.p.debugView != null) P.p.debugView.setValue("AnimationLoop.curTick()", curTick);
	}
	
}
