package com.haxademic.core.media.audio.playback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.interphase.Scales;

public class DroneSampler {
	
	protected String audioDir;
	protected String[] audioFiles;
	protected HashMap<String, AmbientAudioLoop> droneLoops = new HashMap<String, AmbientAudioLoop>();
	protected AmbientAudioLoop curLoop;
	protected String curLoopId;
	protected int soundIndex = -1;
	protected int loopInterval;
	protected int loopLastStartTime;
	protected float loopVol = 1;
	
	public DroneSampler(String audioDir, float loopIntervalSeconds, float loopVol) {
		this.audioDir = audioDir;
		this.loopInterval = P.round(loopIntervalSeconds * 1000); // ms
		loopLastStartTime = -loopInterval; // start immediately
		this.loopVol = loopVol;
		loadSounds();
	}
	
	protected void loadSounds() {
		// load audio directory
		P.out("DroneSampler loading sounds from:", this.audioDir);
		ArrayList<String> sounds = FileUtil.getFilesInDirOfTypes(FileUtil.getPath(audioDir), "wav,aif,mp3");
		Collections.shuffle(sounds);
		audioFiles = new String[sounds.size()];
		for (int i = 0; i < sounds.size(); i++) {
			audioFiles[i] = sounds.get(i);
			P.out("Loading...", audioFiles[i]);
		}
	}
	
	protected void startNextPlayer() {
		// log the current id
		// go to next index & play next sound!
		soundIndex = (soundIndex < audioFiles.length - 1) ? soundIndex + 1 : 0;	
		curLoopId = audioFiles[soundIndex];
		DebugView.setValue("CUR_LOOP @ " + audioDir, FileUtil.fileNameFromPath(curLoopId));

		// lazy-init DroneSampler
		if(droneLoops.containsKey(curLoopId) == false) {
			droneLoops.put(curLoopId, new AmbientAudioLoop(curLoopId));
		}
		
		// get random pitch and play
		int newPitch = Scales.SCALES[0][MathUtil.randRange(0, Scales.SCALES[0].length - 1)];
		float loopFadeTime = this.loopInterval / 2f;
		curLoop = droneLoops.get(curLoopId); 
		curLoop.start(newPitch, loopVol, loopFadeTime, loopFadeTime);
	}
	
//	protected void releaseOldPlayers() {
//		// ramp down old players halfway through interval
//		for (HashMap.Entry<String, AmbientAudioLoop> entry : droneLoops.entrySet()) {
//			AmbientAudioLoop droneLoop = entry.getValue();
//			if(droneLoop.active() && P.p.millis() > loopLastStartTime + loopInterval/2) {
//				droneLoop.release();
//			}
//		}
//	}
	
	protected void checkNextSoundInterval() {
		// is it time to start a new loop?
		if(P.p.millis() > loopLastStartTime + loopInterval) {
			loopLastStartTime = P.p.millis();
			startNextPlayer();
		}
	}
	
	protected void updateLoops() {
		// log loop progress
		float curProgress = AmbientAudioLoop.player.progress(curLoopId);
//		float curPosition = AmbientAudioLoop.player.position(curLoopId);
//		float curDuration = AmbientAudioLoop.player.duration(curLoopId);
		DebugView.setValue("AmbientAudioLoop progress "+audioDir, MathUtil.roundToPrecision(curProgress, 3));
//		DebugView.setValue("AmbientAudioLoop position "+audioDir, MathUtil.roundToPrecision(curPosition, 3));
//		DebugView.setValue("AmbientAudioLoop duration "+audioDir, MathUtil.roundToPrecision(curDuration, 3));
	}
	
	public void update() {
		checkNextSoundInterval();
		updateLoops();
	}

}