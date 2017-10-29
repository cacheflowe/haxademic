package com.haxademic.sketch.net;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.net.WebServerRequestHandler;
import com.haxademic.core.system.SystemUtil;

import processing.sound.SoundFile;

public class MultiUserDrumMachine
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	
	protected float sliderVal = 0;
	protected float bpm = 120;
	
	protected StepInstrument kickLane;
	protected StepInstrument hatLane;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.FPS, 90);
	}

	public void setup() {
		super.setup();	
		buildWebServer();
		loadAudio();
	}
	
	protected void buildWebServer() {
		server = new WebServer(new CustomWebRequestHandler(), true);
	}
	
	protected void loadAudio() {
		kickLane = new StepInstrument("audio/kit808/kick.wav");
		hatLane = new StepInstrument("audio/kit808/hi-hat.wav");
	}
	
	public void drawApp() {
		background(0);
		// if(p.frameCount == 200) SystemUtil.openWebPage("http://localhost:8080/web-server-demo/index.html");
		// draw slider val
		p.fill(255);
		p.rect(0, 0, P.map(sliderVal, 0, 1, 0, p.width), p.height);
		drawPlayhead();
	}
	
	protected void drawPlayhead() {
		// calculate timing
		float bpm = 80f + sliderVal * 50f;
		float beatTime = 60f / bpm; // seconds / bpm
		float beatSlots = 8f;
		float loopLength = (beatSlots * beatTime) * 1000f;
		float curMillis = p.millis() % loopLength;
		float playheadProgress = curMillis / loopLength;
		float progressRads = playheadProgress * P.TWO_PI;
		
		// draw playhead
		p.translate(p.width/2, p.height/2);
		float radius = P.min(p.width/2, p.height/2) * 0.9f;
		p.noFill();
		p.strokeWeight(2);
		p.stroke(127);
		p.line(0, 0, P.cos(progressRads) * radius, P.sin(progressRads) * radius);
		
		// steps management
		if(p.frameCount > 60) {
			kickLane.checkProgress(playheadProgress);
			hatLane.checkProgress(playheadProgress);
		}
		
		// draw kicks
		float offsetRads = -P.HALF_PI;

		float kicks = 8;
		float kickRads = P.TWO_PI / kicks;
		for (float i = 0; i < kicks; i++) {
			p.noFill();
			p.stroke(255);
			p.strokeWeight(20);
			p.point(P.cos(offsetRads + kickRads * i) * radius/2, P.sin(offsetRads + kickRads * i) * radius/2);
		}
		
		// draw hat
		float hats = 8;
		float hatsRads = P.TWO_PI / hats;
		float offsetHatRads = P.QUARTER_PI/2f;

		for (float i = 0; i < hats; i++) {
			p.noFill();
			p.stroke(255);
			p.strokeWeight(10);
			p.point(P.cos(offsetRads + offsetHatRads + hatsRads * i) * radius*0.75f, P.sin(offsetRads + offsetHatRads + hatsRads * i) * radius*0.75f);
		}
	}
	
	public class StepInstrument {
		
		protected SoundFile sound;
		protected Step[] steps;
		protected float numSteps = 32f;
		protected float curStep = 0f;
		protected float stepSize = 1f / numSteps;
		
		protected float radius = 0;
		
		public StepInstrument(String soundPath) {
			sound = new SoundFile(P.p, FileUtil.getFile(soundPath));
			steps = new Step[8];
			for (int i = 0; i < steps.length; i++) {
				steps[i] = new Step();
			}
		}
				
		public void checkProgress(float progress) {
			boolean changed = false;
			if(progress > curStep + stepSize) {
				curStep += stepSize;
				changed = true;
			} else if(progress < curStep - 0.5f) {
				curStep = 0;
				changed = true;
			}
			if(changed && MathUtil.randBoolean(p)) {
				sound.stop();
				sound.play();
			}
		}
		
		
		
	}
	
	public class Step {
		
		protected boolean played = false;
		protected int time = 0;
		
		public Step() {
			
		}
		
		public int time() {
			return time;
		}
		
		public void time(int time) {
			this.time = time;
		}
		
	}
	
	// Example 
	
	public class CustomWebRequestHandler extends WebServerRequestHandler {
		
		@Override
		protected String handleCustomPaths(String path, String[] pathComponents) {
			P.println(path, path.indexOf("button"));
			if(path.indexOf("button") != -1) {
				int buttonIndex = ConvertUtil.stringToInt(pathComponents[1]);
				return "{\"log\": \"Button Number: "+buttonIndex+"\"}";
			} else if(path.indexOf("slider") != -1) {
				sliderVal = ConvertUtil.stringToFloat(pathComponents[1]);
				return "{\"log\": \"Slider Val: "+sliderVal+"\"}";
			} else {
				return null;
			}
		}
	}

}
