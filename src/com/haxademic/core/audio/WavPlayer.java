package com.haxademic.core.audio;

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
	protected SamplePlayer player;
	
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
		return playWav(filePath, PAN_CENTER);
	}
	
	public boolean playWav(String filePath, float panAmp) {
		boolean success = false;
		
		// load sound
		P.println("Playing:", filePath);
		Sample audioSample = SampleManager.sample(filePath);
		if(audioSample != null) {
			player = new SamplePlayer(audioContext, audioSample);
			player.start(0);
			
			// pan it!
			Panner pan = new Panner(audioContext, panAmp);
			pan.addInput(player);
			
			// play it!
			Gain gain = new Gain(audioContext, 2, 1f);		// 1 channel, 1f volume
			gain.addInput(pan);
			audioContext.out.addInput(gain);
			
			success = true;
		} else {
			DebugUtil.printErr("Bad audio file: " + filePath);
		}
		return success;
	}
	
	public void restart() {
		if(player != null) player.start(0);
	}
	
	public void stop() {
		if(player != null) player.pause(true);
		player = null;
	}
	
	public float progress() {
		return position() / duration();
	}
	
	public float position() {
		if(player == null) return 0;
		return (float) player.getPosition();
	}
	
	public float duration() {
		if(player == null) return 1;
		return (float) player.getSample().getLength();
	}
	
}
