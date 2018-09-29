package com.haxademic.core.audio;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

import beads.AudioContext;
import beads.Gain;
import beads.Panner;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;

public class WavPlayer {

	protected AudioContext audioContext;
	protected HashMap<String, SamplePlayer> players = new HashMap<String, SamplePlayer>();
	protected HashMap<String, Gain> gains = new HashMap<String, Gain>();
	
	public static int PAN_CENTER = 0;
	public static float PAN_LEFT = -1;
	public static float PAN_RIGHT = 1f;

	public WavPlayer() {		
		if(audioContext == null) {
			audioContext = new AudioContext();
			audioContext.start();
		}
	}
	
	public boolean playWav(String filePath) {
		return playWav(filePath, PAN_CENTER, false);
	}
	
	public boolean loopWav(String filePath) {
		return playWav(filePath, PAN_CENTER, true);
	}
	
	public boolean playWav(String filePath, float panAmp, boolean loops) {
		boolean success = false;
		
		// load sound
		P.println("Playing:", filePath);
		Sample audioSample = SampleManager.sample(filePath);
		if(audioSample != null) {
			if(players.containsKey(filePath) == false) {
				players.put(filePath, new SamplePlayer(audioContext, audioSample));
				getPlayer(filePath).setKillOnEnd(false);
				
				// pan it!
				Panner pan = new Panner(audioContext, panAmp);
				pan.addInput(getPlayer(filePath));
				
				// play it!
				gains.put(filePath, new Gain(audioContext, 2, 1f));		// 2 channel, 1f volume
				gains.get(filePath).addInput(pan);
				audioContext.out.addInput(gains.get(filePath));
			}
			
			// play it
			getPlayer(filePath).start(0);
			if(loops) getPlayer(filePath).setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
			
			success = true;
		} else {
			DebugUtil.printErr("Bad audio file: " + filePath);
		}
		return success;
	}
	
	public SamplePlayer getPlayer(String id) {
		return players.get(id);
	}
	
	public Gain getGain(String id) {
		return gains.get(id);
	}
	
	public void restart(String id) {
		if(getPlayer(id) != null) getPlayer(id).start(0);
	}
	
	public void stop(String id) {
		if(getPlayer(id) != null) getPlayer(id).pause(true);
	}
	
	public float progress(String id) {
		return position(id) / duration(id);
	}
	
	public float position(String id) {
		if(getPlayer(id) == null) return 0;
		return (float) getPlayer(id).getPosition();
	}
	
	public float duration(String id) {
		if(getPlayer(id) == null) return 1;
		return (float) getPlayer(id).getSample().getLength();
	}
	
	public void setVolume(String id, float gain) {
		if(gains.containsKey(id)) {
			gains.get(id).setGain(gain);
		}
	}
	
}
