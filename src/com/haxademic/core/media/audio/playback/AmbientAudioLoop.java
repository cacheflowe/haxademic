package com.haxademic.core.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.LinearFloat;

public class AmbientAudioLoop {
	
	protected static WavPlayer player = null;	// shared static WavPlayer
	protected String id; 
	protected LinearFloat volume = new LinearFloat(0, 0.03f);
	protected int pitch = 0;
	protected int startTime = 0;

	public AmbientAudioLoop(String filePath) {
		if(AmbientAudioLoop.player == null) AmbientAudioLoop.player = new WavPlayer();
		id = filePath;
	}

	public boolean active() {
		return volume.value() > 0 || volume.target() > 0;
	}

	public LinearFloat volume() {
		return volume;
	}

	public AmbientAudioLoop start(int pitch) {
		return start(pitch, 1);
	}
	
	public AmbientAudioLoop start(int pitch, float targetVol) {
		this.pitch = pitch;
		startTime = P.p.millis();
		player.playWav(id, 0, WavPlayer.PAN_CENTER, true, this.pitch, 0);
		volume.setTarget(targetVol).setCurrent(0);
		return this;
	}
	
	public void setFadeSeconds(float seconds) {
		setFadeSeconds(seconds, 1f);
	}
	
	public void setFadeSeconds(float seconds, float maxVol) {
		// maxVol defaults to 1, for the idea that most loops would play at 1 volume...
		// but we can set this to 0.5f is the target volume is 0.5, to compensate the timing
		volume.setInc(maxVol / (seconds * 60f));
	}

	public AmbientAudioLoop release() {
		volume.setTarget(0);
		return this;
	}
	
	public void setVolume(float newVol) {
		volume.setTarget(newVol);
	}
	
	public void soundForceStop() {
		player.stop(id);
	}
	
	public int startTime() {
		return startTime;
	}
	
	public void update() {
		// update volume
		boolean wasActive = active();
		volume.update();
		if(wasActive && volume.value() == 0) soundForceStop();

		// update player props
		if(wasActive || volume.value() > 0 || volume.target() > 0) {
			player.setGlideTime(id, 1);
			player.setVolume(id, volume.value());
			player.setPitch(id, pitch);
		}
	}
	
}