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
	protected float lastTime;
	protected float deltaTime;
	
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
		lastTime = P.p.millis();
		
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
	
	public static float oscRads(float rads, float low, float high) {
		float range = (high - low) * 0.5f;
		float mid = low + range;
		return mid + P.cos(rads) * range;
	}
	
	public static float noiseLoop(float zoom, float offset) {
		return noiseLoop(zoom, offset, 0);
	}
	
	public static float noiseLoop(float zoom, float offsetX, float offsetY) {
		// circular looped noise
		return P.p.noise(
			offsetX + zoom * P.cos(progressRads()),
			offsetY + zoom * P.sin(progressRads())
		);
	}

	public static float frameMod(int mod) {
		return FrameLoop.instance().frame % mod;
	}
	
	public static boolean frameModLooped(int mod) {
		return FrameLoop.instance().frame % mod == 0;
	}
	
	public static boolean frameModLoopedAt(int mod, int frameInLoop) {
		return FrameLoop.instance().frame % mod == frameInLoop;
	}
	
	public static boolean frameModLoopedAt(int frameInLoop) {
		return loopCurFrame() == frameInLoop;
	}
	
	// loop in terms of time, assuming 60fps. very inaccurate
	
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
	
	public static float progressOsc(float low, float high) {
		return progressOsc(low, high, 0, 1);
	}
	
	public static float progressOsc(float low, float high, float offsetNorm, float freqMult) {
		float range = (high - low) * 0.5f;
		float mid = low + range;
		float offsetRads = P.TWO_PI * offsetNorm;
		return mid + P.sin(offsetRads + progressRads() * freqMult) * range;
	}
	
	public static int curTick() {
		return FrameLoop.instance().curTick;
	}
	
	public static boolean isTick() {
		return FrameLoop.instance().isTick;
	}

	public static float deltaTime() {
		return FrameLoop.instance().deltaTime;
	}
	
	public static float timeAmp() {
		return timeAmp(60);
	}
	
	public static float timeAmp(float fps) {	// multiply speeds by this to make up for slow framerates
		float msPerFrame = 1000f / fps;
		return FrameLoop.instance().deltaTime / msPerFrame;
	}
	
	public static float curTime() {
		return FrameLoop.instance().lastTime;
	}
	
	// frame loop calculations 
	
	public void pre() {
		// make framecount available everywhere
		frame = P.p.frameCount;
		
		// check time
		float curTime = P.p.millis();
		deltaTime = curTime - lastTime;
		lastTime = curTime;
		
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
