package com.haxademic.demo.media.audio.playback;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.interphase.Scales;
import com.haxademic.core.media.audio.playback.WavPlayer;

public class Demo_WavPlayer_synthLoopEvolve
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Visualize the progress and pitch of currently-active players
	// - Draw lerped waveform
	//   - Use lerped waveform as distortion?
	// - How to prevent clicking? Is there a Gain function to lerp volume?
	// - Global pitch & volume multiplier for interactivity response?
	// - Add a 2nd layer of loops w/higher pitches, like Communichords
	
	protected WavPlayer player;
	protected WavPlayer activePlayer;
	protected String[] soundFiles;
	protected HashMap<String, SynthLoop> synths = new HashMap<String, SynthLoop>();
	protected int soundIndex = -1;
	protected int nextSynthInterval = 15000;
	protected int lastSynthStarted = -nextSynthInterval;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 400 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		// create looping players
		player = new WavPlayer();
		
		// send Beads audio player analyzer to PAppletHax
		AudioIn.instance(new AudioInputBeads(WavPlayer.sharedContext()));
		
		// load audio directory
		ArrayList<String> sounds = FileUtil.getFilesInDirOfTypes(FileUtil.getPath("audio/communichords/bass"), "wav,aif");
		soundFiles = new String[sounds.size()];
		for (int i = 0; i < sounds.size(); i++) {
			soundFiles[i] = sounds.get(i);
			P.out("Loading...", soundFiles[i]);
		}
	}
	
	protected void startNextSound() {
		// kill old players
		killOldPlayers();
		// go to next index & play next sound!
		soundIndex = (soundIndex < soundFiles.length - 1) ? soundIndex + 1 : 0;	
		String nextSoundId = soundFiles[soundIndex];
		startPlayer(nextSoundId);
		
		
		// set player properties
//		player1.setGlideTime(soundId1, 200);
//		player2.setGlideTime(soundId2, 200);
		
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
			// do something with the key/value
			synthLoop.update();
		}
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
