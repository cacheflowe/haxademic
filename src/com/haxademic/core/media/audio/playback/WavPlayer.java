package com.haxademic.core.media.audio.playback;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

import beads.AudioContext;
import beads.Gain;
import beads.Glide;
import beads.Panner;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;

public class WavPlayer {

	public static AudioContext sharedContext;
	protected AudioContext curContext;
	protected HashMap<String, SamplePlayer> players = new HashMap<String, SamplePlayer>();
	protected HashMap<String, Gain> gains = new HashMap<String, Gain>();
	protected HashMap<String, Glide> glides = new HashMap<String, Glide>();
	
	public static int PAN_CENTER = 0;
	public static float PAN_LEFT = -1;
	public static float PAN_RIGHT = 1f;

	// contructors
	
	public WavPlayer(AudioContext context) {
		// set local context - could be shared or custom
		curContext = (context != null) ? context : WavPlayer.sharedContext();
	}
	
	public WavPlayer(boolean sharedContext) {
		this((sharedContext) ? WavPlayer.sharedContext() : WavPlayer.newAudioContext());
	}
	
	public WavPlayer() {
		this(null);
	}
	
	// context init
	
	public static AudioContext sharedContext() {
		if(sharedContext == null) sharedContext = newAudioContext();
		return sharedContext;
	}
	
	public static AudioContext newAudioContext() {
		AudioContext ctx = new AudioContext();
		ctx.start();
		return ctx;
	}
	
	public AudioContext context() {
		return curContext;
	}
	
	// play triggers
	
	public boolean playWav(String filePath) {
		return playWav(filePath, PAN_CENTER, false, 0);
	}
	
	public boolean loopWav(String filePath) {
		return playWav(filePath, PAN_CENTER, true, 0);
	}
	
	public boolean playWav(String filePath, float panAmp, boolean loops, int pitch) {
		boolean success = false;
		
		// load sound
		// P.println("Playing:", filePath);
		Sample audioSample = SampleManager.sample(filePath);
		if(audioSample != null) {
			SamplePlayer player = null;
			if(players.containsKey(filePath) == false) {
				players.put(filePath, new SamplePlayer(curContext, audioSample));
				player = getPlayer(filePath);
				player.setKillOnEnd(false);
				
				// pan it! only add panner if actually panned
				Panner pan = null;
				if(panAmp != PAN_CENTER) {
					pan = new Panner(curContext, panAmp);
					pan.addInput(player);
				}
				
				// set pitch if needed
				glides.put(filePath, new Glide(curContext, pitchRatioFromIndex(pitch)));
				if(pitch != 0) {
					// change pitch
					player.setRate(glides.get(filePath));
				}
				
//				P.error("Panning only works once, not a second time");
//				P.error("Audioreactivity only works on the left channel");
				
				// play it! 
				gains.put(filePath, new Gain(curContext, 2, 1f));		// 2 channel, 1f volume
				if(pan != null) {
					gains.get(filePath).addInput(pan);
				} else {
					gains.get(filePath).addInput(player);
				}
				curContext.out.addInput(gains.get(filePath));
			}
			
			// play it
			if(player != null) {
				player.start(0);
				if(loops) player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
			}
			
			success = true;
		} else {
			DebugUtil.printErr("Bad audio file: " + filePath);
		}
		return success;
	}
	
	// pitch shift
	
	protected float pitchRatioFromIndex(float pitchIndex) {
		return P.pow(2, pitchIndex/12.0f);
	}
	
	// property access
	
	public SamplePlayer getPlayer(String id) {
		return players.get(id);
	}
	
	public Gain getGain(String id) {
		return gains.get(id);
	}
	
	public Glide getGlide(String id) {
		return glides.get(id);
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
	
	public void setPitch(String id, float pitchIndex) {
		if(glides.containsKey(id)) {
//			glides.get(id).setValue(pitchRatioFromIndex(pitchIndex));
			glides.get(id).setValueImmediately(pitchRatioFromIndex(pitchIndex));
			getPlayer(id).setRate(glides.get(id));
		}
	}
	
}
