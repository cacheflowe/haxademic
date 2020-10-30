package com.haxademic.core.media.audio.playback;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.interphase.Scales;

public class DroneSampler {
	
	protected String audioDir;
	protected String[] audioFiles;
	protected WavPlayer activePlayer;
	protected HashMap<String, AmbientAudioLoop> droneLoops = new HashMap<String, AmbientAudioLoop>();
	protected int soundIndex = -1;
	protected int loopInterval;
	protected int loopLastStartTime;
	protected int activePlayers = 0;
	
	public DroneSampler(String audioDir, float loopIntervalSeconds) {
		this.audioDir = audioDir;
		this.loopInterval = P.round(loopIntervalSeconds * 1000); // ms
		loopLastStartTime = -loopInterval; // start immediately
		loadSounds();
	}
	
	protected void loadSounds() {
		// load audio directory
		P.out("DroneSampler loading sounds from:", this.audioDir);
		ArrayList<String> sounds = FileUtil.getFilesInDirOfTypes(FileUtil.getPath(audioDir), "wav,aif");
		audioFiles = new String[sounds.size()];
		for (int i = 0; i < sounds.size(); i++) {
			audioFiles[i] = sounds.get(i);
			P.out("Loading...", audioFiles[i]);
		}
	}
	
	protected void startPlayer(String id) {
		// lazy-init DroneSampler
		if(droneLoops.containsKey(id) == false) {
			droneLoops.put(id, new AmbientAudioLoop(id));
		}
		// get random pitch and play
		int newPitch = Scales.SCALES[0][MathUtil.randRange(0, Scales.SCALES[0].length - 1)];
		droneLoops.get(id).start(newPitch).setFadeSeconds(this.loopInterval / 2f / 1000f);
	}
	
	protected void startNextSound() {
		// go to next index & play next sound!
		soundIndex = (soundIndex < audioFiles.length - 1) ? soundIndex + 1 : 0;	
		String nextSoundId = audioFiles[soundIndex];
		startPlayer(nextSoundId);
	}
	
	protected void releaseOldPlayers() {
		// ramp down old players halfway through interval
		for (HashMap.Entry<String, AmbientAudioLoop> entry : droneLoops.entrySet()) {
			AmbientAudioLoop droneLoop = entry.getValue();
//			DebugView.setValue(droneLoop.id, droneLoop.active());
			if(droneLoop.active() && P.p.millis() > loopLastStartTime + loopInterval/2) {
				droneLoop.release();
			}
		}
	}
	
	protected void checkNextSoundInterval() {
		// is it time to start a new loop?
		if(P.p.millis() > loopLastStartTime + loopInterval) {
			loopLastStartTime = P.p.millis();
			startNextSound();
		}
	}
	
	protected void updateLoops() {
		// update volume lerping
		activePlayers = 0;
		for (HashMap.Entry<String, AmbientAudioLoop> entry : droneLoops.entrySet()) {
			AmbientAudioLoop droneLoop = entry.getValue();
			droneLoop.update();
			if(droneLoop.active()) activePlayers++;
		}
		DebugView.setValue("activePlayers", activePlayers);
	}
	
	public void update() {
		releaseOldPlayers();
		checkNextSoundInterval();
		updateLoops();
	}

	public int activePlayers() {
		return activePlayers;
	}
}