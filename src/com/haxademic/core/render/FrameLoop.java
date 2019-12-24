package com.haxademic.core.render;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;

public class FrameLoop {

	protected float frame;
	protected float loopFrames;
	protected float loopCurFrame;
	protected float progress;
	protected float progressRads;
	protected float ticks;
	protected int curTick;
	protected boolean isTick = false;
	
	// Singleton instance
	
	public static FrameLoop instance;
	
	public static FrameLoop instance() {
		if(instance != null) return instance;
		instance = new FrameLoop(0, 0);
		return instance;
	}
	
	public static FrameLoop instance(float frames, float ticks) {
		if(instance != null) return instance;
		instance = new FrameLoop(frames, ticks);
		return instance;
	}

	public FrameLoop(float frames) {
		this(frames, 4);
	}
	
	public FrameLoop(float frames, float ticks) {
		this.loopFrames = frames;
		loopCurFrame = 0;
		this.ticks = ticks;
		curTick = -1;
		
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	// static frameCount getters
	
	public static float count() {
		return FrameLoop.instance().frame;
	}
	
	public static boolean frameMod(int mod) {
		return FrameLoop.instance().frame % mod == 1;
	}
	
	public static boolean frameMod(int mod, int frameInLoop) {
		return FrameLoop.instance().frame % mod == frameInLoop;
	}
	
	// static loop getters
	
	public static float loopFrames() {
		return FrameLoop.instance().loopFrames;
	}
	
	public static float loopCurFrame() {
		return FrameLoop.instance().loopCurFrame;
	}
	
	public static float progress() {
		return FrameLoop.instance().progress;
	}
	
	public static float progressRads() {
		return FrameLoop.instance().progressRads;
	}
	
	public static int curTick() {
		return FrameLoop.instance().curTick;
	}
	
	public static boolean isTick() {
		return FrameLoop.instance().isTick;
	}

	// frame loop calculations 
	
	public void pre() {
		// make framecount available everywhere
		frame = P.p.frameCount;
		
		// update progress
		if(loopFrames > 0) {
			loopCurFrame = frame % loopFrames;
			progress = loopCurFrame / loopFrames;
			progressRads = progress * P.TWO_PI;
			
			// update ticks
			int newTick = P.floor(ticks * progress);
			isTick = (curTick != newTick);
			curTick = newTick;
			
			// set on DebugView
			DebugView.setValue("AnimationLoop.loopCurFrame ", loopCurFrame + " / " + loopFrames);
			DebugView.setValue("AnimationLoop.progress ", progress);
			DebugView.setValue("AnimationLoop.curTick ", curTick);
		}
	}
	
}
