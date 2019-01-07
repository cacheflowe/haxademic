package com.haxademic.app.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.store.AppStore;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.sound.SoundFile;

public class Interphase
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	
	// timing
	protected int NUM_STEPS = 8;
	protected float bpm = 120f;
	protected float beatTime = 60f / bpm; // seconds / bpm
	protected float beatSlots = 8f;
	protected float loopLength = (beatSlots * beatTime) * 1000f;
	protected float curMillis = 0;
	protected float playheadProgress = 0;
	
	// state keys
	public static String PROGRESS = "PROGRESS";
	public static String LAST_PROGRESS = "LAST_PROGRESS";
	public static String DRAW = "DRAW";
	
	// objects
	protected AudioStep[] steps = new AudioStep[8];
	
	////////////////////////////////////////////////
	// INIT
	////////////////////////////////////////////////
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 400 );
	}

	public void setupFirstFrame() {
		buildState();
		buildSteps();
	}
	
	protected void buildState() {
		AppStore.instance().setNumber(Interphase.PROGRESS, 0);
		AppStore.instance().setNumber(Interphase.LAST_PROGRESS, 0);
	}
	
	protected void buildSteps() {
		steps = new AudioStep[NUM_STEPS];
		float stepTriggerProgress = 1f / (float) NUM_STEPS;
		for (int i = 0; i < NUM_STEPS; i++) steps[i] = new AudioStep((float) i * stepTriggerProgress);
	}
	
	////////////////////////////////////////////////
	// UPDATE
	////////////////////////////////////////////////
	
	protected void updateTiming() {
		loopLength = (beatSlots * beatTime) * 1000f;
		beatTime = 60f / bpm;
		loopLength = (beatSlots * beatTime) * 1000f;
		curMillis = p.millis() % loopLength;
		playheadProgress = curMillis / loopLength;
		
		// dispatch timing
		AppStore.instance().setNumber(Interphase.LAST_PROGRESS, AppStore.instance().getFloat(Interphase.PROGRESS));
		AppStore.instance().setNumber(Interphase.PROGRESS, playheadProgress);
	}
	
	public void drawApp() {
		updateTiming();
		p.background(0);
		AppStore.instance().setNumber(Interphase.DRAW, p.frameCount);
		drawProgress();
	}
	
	protected void drawProgress() {
		DrawUtil.setDrawCorner(p);
		p.noStroke();
		p.fill(255);
		p.rect(0, p.height - 3, p.width * playheadProgress, 3);
	}
	
	////////////////////////////////////////////////
	// OBJECTS
	////////////////////////////////////////////////
	
	public class AudioStep implements IAppStoreListener {
		
		protected int width = 50;
		protected int height = 200;
		protected float triggerProgress = 0;
		protected boolean trigger = false;
		protected int curColor = 0xff000000;
		protected SoundFile sound;
		
		public AudioStep(float triggerProgress) {
			AppStore.instance().addListener(this);
			this.triggerProgress = triggerProgress;
			sound = new SoundFile(P.p, FileUtil.getFile("audio/kit808/kick.wav"));
		}
		
		protected void checkTrigger() {
			if((AppStore.instance().getFloat(Interphase.LAST_PROGRESS) < triggerProgress || AppStore.instance().getFloat(Interphase.LAST_PROGRESS) > AppStore.instance().getFloat(Interphase.PROGRESS)) && 
			   AppStore.instance().getFloat(Interphase.PROGRESS) >= triggerProgress) {
				trigger();
			}
		}
		
		protected void trigger() {
			curColor = p.color(p.random(0,255), p.random(0,255), p.random(0,255));
			sound.stop();
			sound.play();
		}
		
		protected void draw() {
			p.fill(curColor);
			p.rect(p.width * triggerProgress, 0, width, height);
		}
		
		// events

		@Override public void updatedNumber(String key, Number val) {
			if(key == Interphase.PROGRESS) checkTrigger();
			if(key == Interphase.DRAW) draw();
		}

		public void updatedString(String key, String val) {}
		public void updatedBoolean(String key, Boolean val) {}
		public void updatedImage(String key, PImage val) {}
		public void updatedBuffer(String key, PGraphics val) {}
		
	}
	
}
