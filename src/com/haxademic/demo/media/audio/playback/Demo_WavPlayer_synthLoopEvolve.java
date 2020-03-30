package com.haxademic.demo.media.audio.playback;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.ArrayUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.render.FrameLoop;

import processing.core.PGraphics;

public class Demo_WavPlayer_synthLoopEvolve
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Visualize the progress and pitch of currently-active players
	// - Lerp texture to further smooth out the displacement map
	// - Use lerped waveform as distortion texture map?
	//   - Need to normalize waveform value between negative and positive max extents
	// - Add global pitch & volume multiplier for interactivity response
	// - Add a 2nd & 3rd layer of loops w/higher pitches & field recordings
	// - How to prevent clicking? Is there a Gain function to lerp volume?
	
	protected WavPlayer player;
	protected WavPlayer activePlayer;
	protected String[] soundFiles;
	protected HashMap<String, SynthLoop> synths = new HashMap<String, SynthLoop>();
	protected int soundIndex = -1;
	protected int nextSynthInterval = 15000;
	protected int lastSynthStarted = -nextSynthInterval;
	
	protected EasingFloat waveformMaxVal;
	protected FloatBuffer[] waveformLerped;
	protected float[] waveformLerpValues;
	protected PGraphics waveformLerpImg;
	protected PGraphics waveformTexture;
	protected PGraphics waveformTextureSmoothed;
	protected PGraphics waveformShaderTexture;
	protected PShaderHotSwap waveformShader;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 512 );
		Config.setProperty( AppSettings.HEIGHT, 520 );
		Config.setProperty( AppSettings.SHOW_DEBUG, false );
	}

	protected void firstFrame() {
		// create looping players
		player = new WavPlayer();
		
		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
		
		// prepare to draw lerped waveform
		waveformMaxVal = new EasingFloat(0, 0.01f);
		waveformLerped = new FloatBuffer[AudioIn.waveform.length];
		waveformLerpValues = new float[AudioIn.waveform.length];
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformLerped[i] = new FloatBuffer(90);
			waveformLerpValues[i] = 0;
		}
		waveformLerpImg = PG.newPG2DFast(AudioIn.waveform.length, 256);
		
		waveformTexture = PG.newPG(AudioIn.waveform.length, 8);
		waveformTextureSmoothed = PG.newPG(AudioIn.waveform.length, 8);
		DebugView.setTexture("waveformLerpImg", waveformLerpImg);
		DebugView.setTexture("waveformTexture", waveformTexture);
		DebugView.setTexture("waveformTextureSmoothed", waveformTextureSmoothed);
		
		// load audio directory
		ArrayList<String> sounds = FileUtil.getFilesInDirOfTypes(FileUtil.getPath("audio/communichords/bass"), "wav,aif");
		soundFiles = new String[sounds.size()];
		for (int i = 0; i < sounds.size(); i++) {
			soundFiles[i] = sounds.get(i);
			P.out("Loading...", soundFiles[i]);
		}
		
		// shader to draw stripes
//		waveformShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/cacheflowe-audio-stripes.glsl"));
		waveformShaderTexture = PG.newPG(512, 256);
		waveformShader = new PShaderHotSwap(
				FileUtil.getPath("haxademic/shaders/textures/cacheflowe-audio-stripes.glsl") 
			);
	}
	
	protected void startNextSound() {
		// kill old players
		killOldPlayers();
		// go to next index & play next sound!
		soundIndex = (soundIndex < soundFiles.length - 1) ? soundIndex + 1 : 0;	
		String nextSoundId = soundFiles[soundIndex];
		startPlayer(nextSoundId);
	}
	
	protected void killOldPlayers() {
		for (HashMap.Entry<String, SynthLoop> entry : synths.entrySet()) {
			String id = entry.getKey();
			SynthLoop synthLoop = entry.getValue();
			// do something with the key/value
			// kill old players
			if(synthLoop.active() && P.p.millis() - synthLoop.startTime() > nextSynthInterval/2) {
				synthLoop.stop();
			}
		}
	}
	
	protected void startPlayer(String id) {
		// lazy-init SynthLoop
		if(synths.containsKey(id) == false) {
			synths.put(id, new SynthLoop(id));
		}
		// get pitch
		int newPitch = Scales.SCALES[0][MathUtil.randRange(0, Scales.SCALES[0].length - 1)];
		// play!
		synths.get(id).start(newPitch);
	}
	
	protected void drawApp() {
		p.background(0);
		startNextSoundInterval();
		updateSynthLoops();
		updateWaveform();
		drawShader();
		p.image(waveformLerpImg, 0, 0);
		p.image(waveformTexture, 0, waveformLerpImg.height);
		p.image(waveformShaderTexture, 0, waveformLerpImg.height + waveformTexture.height);
	}
	
	protected void startNextSoundInterval() {
		if(p.millis() > lastSynthStarted + nextSynthInterval) {
			lastSynthStarted = p.millis();
			startNextSound();
		}
	}
	
	protected void updateSynthLoops() {
		for (HashMap.Entry<String, SynthLoop> entry : synths.entrySet()) {
//			String id = entry.getKey();
			SynthLoop synthLoop = entry.getValue();
			synthLoop.update();
		}
	}
	
	protected void updateWaveform() {
		// lerp waveform normalization amp
		float absMaxVal = 0;	// normalize visual waveform
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformLerped[i] = waveformLerped[i].update(AudioIn.waveform[i]);
			if(P.abs(waveformLerped[i].average()) > absMaxVal) absMaxVal = P.abs(waveformLerped[i].average()); 
		}
		waveformMaxVal.setTarget(absMaxVal);
		waveformMaxVal.update(true);
		float addAmp = 1f / waveformMaxVal.value();
		DebugView.setValue("addAmp", addAmp);
		DebugView.setValue("waveformMaxVal.value()", waveformMaxVal.value());
		
		// double lerp into final float[] arrayt and crossfade ends to loop
		for (int i = 0; i < waveformLerpValues.length; i++) {
			waveformLerpValues[i] = MathUtil.easeTo(waveformLerpValues[i], waveformLerped[i].average(), 5);
		}
		ArrayUtil.crossfadeEnds(waveformLerpValues, 0.075f);
		
		// draw waveform
		waveformLerpImg.beginDraw();
		waveformLerpImg.background(0);
		waveformLerpImg.noFill();
		waveformLerpImg.stroke(255);
		waveformLerpImg.strokeWeight(1);
		waveformLerpImg.push();
		waveformLerpImg.translate(0, waveformLerpImg.height / 2);
		waveformLerpImg.beginShape();
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformLerpImg.vertex(i, waveformLerpValues[i] * 10f * waveformLerpImg.height/2);// * addAmp); // waveformLerpImg.height
		}
		waveformLerpImg.endShape();
		waveformLerpImg.pop();
		waveformLerpImg.endDraw();
		
		// draw waveform
		waveformTexture.beginDraw();
		waveformTexture.background(0);
		waveformTexture.noStroke();
		for (int i = 0; i < waveformLerped.length; i++) {
			waveformTexture.fill(127 + 127f * waveformLerpValues[i] * 10f, 255);
			waveformTexture.rect(i, 0, 1, waveformTexture.height); // waveformTexture.height
		}
		waveformTexture.endDraw();
		
		// lerp towards 2nd texture to further smooth displacement
		BlendTowardsTexture.instance(p).setSourceTexture(waveformTexture);
		BlendTowardsTexture.instance(p).setBlendLerp(0.2f);
		BlendTowardsTexture.instance(p).applyTo(waveformTextureSmoothed);
	}

	protected void drawShader() {
		// update shader
		waveformShader.update();
		waveformShader.shader().set("waveformTex", waveformTextureSmoothed);
		waveformShader.shader().set("color1", 1f, 1f, 1f);
		waveformShader.shader().set("color2", 0f, 0f, 0f);
		waveformShader.shader().set("zoom", 60f);
		waveformShader.shader().set("offset", FrameLoop.count(0.005f), FrameLoop.count(0.002f));
		waveformShader.shader().set("rotate", Mouse.xEasedNorm * P.TWO_PI);
		waveformShader.shader().set("fade", 0.6f);
		waveformShader.shader().set("amp", 0.7f);
		
		// draw!
		waveformShaderTexture.filter(waveformShader.shader());
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') startNextSound();
//		if(p.key == ' ') player2.pauseToggle(soundId2);
//		if(p.key == 'c') player2.stop(soundId2);
//		if(p.key == 's') player2.loopWav(soundId2);
	}
	
	/////////////////////////////
	// Synth loop object
	/////////////////////////////
	
	public class SynthLoop {
		
		protected String id; 
		protected LinearFloat volume = new LinearFloat(0, 0.0003f);
		protected int pitch = 0;
		protected int startTime = 0;
		
		public SynthLoop(String filePath) {
			id = filePath;
		}
		
		public boolean active() {
			return volume.value() > 0;
		}
		
		public void start(int pitch) {
			P.out("Started!", id);
			this.pitch = pitch;
			startTime = P.p.millis();
			player.playWav(id, 0, WavPlayer.PAN_CENTER, true, this.pitch);
			volume.setTarget(1);
		}
		
		public void stop() {
			volume.setDelay(200).setTarget(0);
		}
		
		public void setVolume(float newVol) {
			volume.setTarget(newVol);
		}
		
		public void soundStopped() {
			P.out("Stopped!", id);
			player.stop(id);
		}
		
		public int startTime() {
			return startTime;
		}
		
		public void update() {
			// update volume
			boolean wasActive = active();
			volume.update();
			if(wasActive && volume.target() == 0 & volume.value() == 0) soundStopped();
			if(!active()) return;
			
			// update player props
			player.setGlideTime(id, 200);
			player.setVolume(id, volume.value());
			player.setPitch(id, pitch);
		}
		
	}
}
