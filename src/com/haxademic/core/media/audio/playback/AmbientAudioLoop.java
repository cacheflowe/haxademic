package com.haxademic.core.media.audio.playback;

import com.haxademic.core.app.P;

public class AmbientAudioLoop {
	
	public static WavPlayer player = null;	// shared static WavPlayer
	protected String id; 
	protected int pitch = 0;
	protected int startTime = 0;

	public AmbientAudioLoop(String filePath) {
		if(AmbientAudioLoop.player == null) AmbientAudioLoop.player = new WavPlayer();
		id = filePath;
	}

	public boolean active() {
		return volume() > 0;
	}

	public float volume() {
		return 0.1f; // player.getGain(id).getValue();
	}

	public AmbientAudioLoop start(int pitch) {
		return start(pitch, 1, 1000, 1000);
	}
	
	public AmbientAudioLoop start(int pitch, float targetVol, float attack, float release) {
		this.pitch = pitch;
		startTime = P.p.millis();
		player.playWav(id, targetVol, WavPlayer.PAN_CENTER, true, this.pitch, 0, attack, release, 0);
		return this;
	}
	
	public AmbientAudioLoop release() {
		P.error("AmbientAudioLoop.release() does nothing - fix this");
		return this;
	}
	
	public void soundForceStop() {
		player.stop(id);
	}
	
	public int startTime() {
		return startTime;
	}
		
}