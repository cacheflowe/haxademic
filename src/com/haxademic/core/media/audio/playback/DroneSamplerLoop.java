package com.haxademic.core.media.audio.playback;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.LinearFloat;

public class DroneSamplerLoop {
	
	protected static WavPlayer player = null;
	protected String id; 
	protected LinearFloat volume = new LinearFloat(0, 0.0003f);
	protected int pitch = 0;
	protected int startTime = 0;
	
	public DroneSamplerLoop(String filePath) {
		if(DroneSamplerLoop.player == null) DroneSamplerLoop.player = new WavPlayer();
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