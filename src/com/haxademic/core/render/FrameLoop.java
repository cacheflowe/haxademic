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

	public static FrameLoop instance(float frames) {
		if(instance != null) return instance;
		instance = new FrameLoop(frames);
		return instance;
	}
	
	public FrameLoop(float frames) {
		this(frames, 4);
	}
	
	public FrameLoop(float frames, float ticks) {
		this.loopFrames = frames;
		this.ticks = ticks;
		loopCurFrame = 1;
		frame = 1;
		curTick = -1;
		
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	// static frameCount getters
	
	public static float count() {
		return FrameLoop.instance().frame;
	}
	
	public static float count(float mult) {
		return FrameLoop.instance().frame * mult;
	}
	
	public static float osc(float mult, float low, float high) {
		float range = (high - low) * 0.5f;
		float mid = low + range;
		return mid + P.sin(count(mult)) * range;
	}
	
	public static boolean frameMod(int mod) {
		return FrameLoop.instance().frame % mod == 1;
	}
	
	public static boolean frameMod(int mod, int frameInLoop) {
		return FrameLoop.instance().frame % mod == frameInLoop;
	}
	
	public static boolean frameModSeconds(float seconds) {
		return FrameLoop.instance().frame % P.round(seconds * 60) == 1;   		// 60 frames per second
	}

	public static boolean frameModMinutes(float minutes) {
		return FrameLoop.instance().frame % P.round(minutes * 3600) == 1;		// 60 frames * 60 seconds
	}
	
	public static boolean frameModHours(float hours) {
		return FrameLoop.instance().frame % P.round(hours * 216000) == 1;		// 60 frames * 60 seconds * 60 minutes
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
			DebugView.setValue("FrameLoop.loopCurFrame ", loopCurFrame + " / " + loopFrames);
			DebugView.setValue("FrameLoop.progress ", progress);
			DebugView.setValue("FrameLoop.curTick ", curTick);
		}
	}
	
}
