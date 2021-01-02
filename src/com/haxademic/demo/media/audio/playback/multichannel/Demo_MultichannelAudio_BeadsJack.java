package com.haxademic.demo.media.audio.playback.multichannel;

import org.jaudiolibs.beads.AudioServerIO;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import beads.AudioContext;
import beads.Gain;
import beads.IOAudioFormat;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

public class Demo_MultichannelAudio_BeadsJack
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected SpatialSound[] sounds;
	protected SoundStage stage;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 700 );
		Config.setProperty( AppSettings.HEIGHT, 700 );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	protected void firstFrame() {
		// NOTES:
		// Jack app must be running!
		
		int outputs = 4;
		
		// beads
		// needs https://github.com/jaudiolibs/jnajack/releases
		// need to update a beads library to talk to JNAJack properly
		// - https://groups.google.com/d/msg/beadsproject/4E_73DZMTMg/31RJD02WWegJ
		// - https://code.google.com/archive/p/java-audio-utils/downloads
//		audioContext = new AudioContext();
		AudioContext audioContext = new AudioContext(new AudioServerIO.Jack(), 1024, new IOAudioFormat(44100, 16, 2, outputs));	// 2 inputs, 4 outputs. match sample & bit rates & buffer size to current soundcard settings
		audioContext.postAudioFormatInfo();
		audioContext.start();
		P.out("audioContext.out.getOuts()", audioContext.out.getOuts());
		
		// build sounds
		sounds = new SpatialSound[] {
			new SpatialSound(audioContext, FileUtil.getPath("audio/kit808/kick.wav"), outputs),
			new SpatialSound(audioContext, FileUtil.getPath("audio/communichords/cacheflowe/low-synth.wav"), outputs),
			new SpatialSound(audioContext, FileUtil.getPath("audio/communichords/cacheflowe/mid-synth.wav"), outputs),
		};
		
		// build mapped sound stage
		stage = new SoundStage(200, outputs);
	}

	protected void drawApp() {
		p.background(0);
		
		// auto trigger
		if (p.frameCount % 60 == 0) {
			// playAll();	
			sounds[0].play(false);
		}
		
		// update sounds' position
		for (int i = 0; i < sounds.length; i++) {
			float soundRads = p.frameCount * 0.05f * (i+1);
			sounds[i].updatePosition(P.cos(soundRads) * stage.radius() * (1-(i+1)*0.1f), P.sin(soundRads) * stage.radius() * (1-(i+1)*0.1f));
			if(i == 0) sounds[i].updatePosition(p.mouseX - p.width/2, p.mouseY - p.height/2); // mouse control
			sounds[i].update(p.g);
		}
		
		// draw stage
		stage.update(p.g);
		
		// set font
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 20);
		FontCacher.setFontOnContext(p.g, font, 255, 1, PTextAlign.LEFT, PTextAlign.TOP);
	}
	
	protected void startLoops() {
		for (int i = 1; i < sounds.length; i++) {
			sounds[i].play(true);
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') startLoops();
	}
	
	// aptial sound object
	
	public class SpatialSound {
		
		protected AudioContext audioContext;
		protected String audioFile;
		protected Sample audioSample;
		protected SamplePlayer player;
		protected int outputs;
		protected Gain[] gains;
		protected PVector position = new PVector();
		
		public SpatialSound(AudioContext audioContext, String audioFile, int outputs) {
			this.audioContext = audioContext;
			this.audioFile = audioFile;
			this.outputs = outputs;
			
			audioSample = SampleManager.sample(audioFile);
			
			gains = new Gain[outputs];
			for (int i = 0; i < outputs; i++) {
				gains[i] = new Gain(audioContext, 2, 1f);	// AudioContext context, int inouts, float gain
				audioContext.out.addInput(i, gains[i], 0);  // play first (left or mono) channel (3rd arg) out of specific soundcard/Jack output (1st arg)
			}
		}
		
		public void play(boolean loops) {
			if(audioSample == null) {
				DebugUtil.printErr("Bad audio file: " + this.audioFile);
				return;
			}

			// load sound
			player = new SamplePlayer(audioContext, audioSample);
			player.setKillOnEnd(true);
			
			// send SamplePlayer into all of the gains
			for (int i = 0; i < outputs; i++) {
				gains[i].addInput(player);
			}
			
			// play it!
			player.start(0);
			if(loops) player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
		}
		
		public void updatePosition(float x, float y) {
			position.set(x, y);
		}
		
		public void update(PGraphics pg) {
			// draw
			pg.push();
			pg.noFill();
			pg.stroke(0, 255, 0);
			PG.setDrawCenter(pg);
			PG.setCenterScreen(pg);
			pg.ellipse(position.x, position.y, 20, 20);
			pg.pop();
			
			// set gains
			for (int i = 0; i < outputs; i++) {
				// get dist to speaker
				PVector speakerPos = stage.getSpeaker(i).position();
				float dist = speakerPos.dist(position);
				float distToGain = P.map(dist, 0, stage.radius() * 2, 1, 0);
				distToGain = P.constrain(distToGain, 0, 1);
				gains[i].setGain(distToGain);
				
				// draw debug to speakers
				pg.push();
				pg.noFill();
				pg.stroke(0, 255 * distToGain, 0);
				PG.setDrawCenter(pg);
				PG.setCenterScreen(pg);
				pg.line(position.x, position.y, speakerPos.x, speakerPos.y);
				float midX = (position.x + speakerPos.x) / 2f;
				float midY = (position.y + speakerPos.y) / 2f;
				pg.text(distToGain, midX, midY);
				pg.pop();
			}
		}

	}
	
	public class SoundStage {
		
		protected float radius;
		protected int numSpeakers;
		protected Speaker[] speakers;
		
		public SoundStage(float radius, int numSpeakers) {
			this.radius = radius;
			this.numSpeakers = numSpeakers;
			
			// build speakers
			speakers = new Speaker[numSpeakers];
			float segmentRads = P.TWO_PI / numSpeakers;
			for (int i = 0; i < numSpeakers; i++) {
				float rads = segmentRads * i;
				float speakX = P.cos(rads) * radius;
				float speakY = P.sin(rads) * radius;
				speakers[i] = new Speaker(speakX, speakY);
			}
		}
		
		public float radius() {
			return radius;
		}
		
		public Speaker getSpeaker(int index) {
			return speakers[index];
		}
		
		public void update(PGraphics pg) {
			// draw stage
			pg.push();
			pg.noFill();
			pg.stroke(255);
			PG.setDrawCenter(pg);
			PG.setCenterScreen(pg);
			pg.ellipse(0, 0, radius * 2, radius * 2);
			pg.pop();
			
			// draw speakers
			for (int i = 0; i < numSpeakers; i++) {
				speakers[i].update(pg);
			}
		}
	}	
	public class Speaker {
		
		protected PVector position;
		
		public Speaker(float x, float y) {
			position = new PVector(x, y);
		}
		
		public PVector position() {
			return position;
		}
		
		public void update(PGraphics pg) {
			// draw stage
			pg.push();
			pg.noFill();
			pg.stroke(0, 0, 255);
			PG.setDrawCenter(pg);
			PG.setCenterScreen(pg);
			pg.ellipse(position.x, position.y, 30, 30);
			pg.pop();
		}
	}

}
