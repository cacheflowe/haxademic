package com.haxademic.core.media.audio.playback;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;

import beads.AudioContext;
import beads.Bead;
import beads.DelayTrigger;
import beads.Envelope;
import beads.Gain;
import beads.Glide;
import beads.KillTrigger;
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
	protected HashMap<String, Number> glideTimes = new HashMap<String, Number>();
	protected HashMap<String, Panner> panners = new HashMap<String, Panner>();
	
	public static int PAN_CENTER = 0;
	public static float PAN_LEFT = -1;
	public static float PAN_RIGHT = 1f;

	public static boolean RECYCLES_PLAYERS = false;	// could cause lots of active players at once... default off & revisit later

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
	
	public int activeConnections() {
		return curContext.out.getConnectedInputs().size();
	}
	
	// play triggers
	
	public boolean playWav(String filePath) {
		return playWav(filePath, 1, PAN_CENTER, false, 0, 0, 0, 0, 0);
	}
	
	public boolean loopWav(String filePath) {
		return playWav(filePath, 1, PAN_CENTER, true, 0, 0, 0, 0, 0);
	}
	
	public boolean playWav(String filePath, float volume, float panAmp, boolean loops, int pitch, int delay, float attackTime, float releaseTime, float sampleStart) {
		boolean success = false;
		
		// load sound
		String id = filePath;
		Sample audioSample = SampleManager.sample(filePath);
		
		if(audioSample != null) {
			// Audio chain: 
			// SamplePlayer -> Panner -> Gain (w/Envelope for ASDR)
			// we pause() in case there's a delay set - simply creating the player triggers the sample once
			final SamplePlayer player = new SamplePlayer(curContext, audioSample);
			player.pause(true);	
			players.put(id, player);
			player.setKillOnEnd(true);
			
			// pan it! 
			Panner panner = new Panner(curContext, panAmp);
			panner.setKillListener(player);
			panners.put(id, panner);
			panners.get(id).addInput(player);
			
			// set pitch if needed
			int glideTime = 0;
			float pitchOffset = pitchRatioFromIndex(pitch);
			Glide glide = new Glide(curContext, pitchOffset);
			glide.setKillListener(player);
			glides.put(id, glide);
			glideTimes.put(id, glideTime);
			
			// set pitch
			player.setRate(glide);
			glide.setGlideTime(glideTime);
			
//				P.error("Audioreactivity only works on the left channel");
			
			// Add Gain
			// add ASDR :: http://doc.gold.ac.uk/CreativeComputing/creativecomputation/?page_id=558
			float startGain = (attackTime == 0) ? volume : 0f;
			float endGain = 0f;

			Envelope ampEnv = new Envelope(curContext, startGain);				// start volume at 0 or 1
			Gain gain = new Gain(curContext, 2, ampEnv); 								// ampEnv now controls the fader level. `2` for stereo

			// apply ASDR or not!
			if(attackTime > 0 || releaseTime > 0) {
				float fadeCurve = 2f;	// 1f is default, but not very nice
//					release = P.min(release, sampleLength - attack);						// release should be no longer than sample length minus attack
				ampEnv.addSegment(volume, attackTime, fadeCurve);
				ampEnv.addSegment(endGain, releaseTime, 1f/fadeCurve, new KillTrigger(gain));			// attack & release envelope segments
			} else {
			}
			// need a KillTrigger if we're not using the on in the Envelope
			// otherwise players don't clear out!
			// clean up panner too!
			player.setKillListener(new KillTrigger(gain)); 	
			panner.setKillListener(new KillTrigger(gain)); 	
			
			// add Gain
			gains.put(id, gain);		
			gain.addInput(panner);
			
			// attach to main Beads output
			curContext.out.addInput(gain);		// to test SamplePlayer without audio chain: addInput(player);
				
			// play it, with delay or without
			Bead myBead = new Bead() {
				protected void messageReceived(Bead b) {
					// play from sample start - this un-pauses
					player.start(sampleStart);
					
					// trigger as loop or one-shot
					if(loops) {
						player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
//							player.setLoopCrossFade(200);
					}
					else player.setLoopType(SamplePlayer.LoopType.NO_LOOP_FORWARDS);
				}
			};
			DelayTrigger dt = new DelayTrigger(curContext, delay, myBead, null);
			curContext.out.addDependent(dt);
			
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
	
	public void seekToProgress(String id, float progress) {
		if(getPlayer(id) != null) {
//			getPlayer(id).start(duration(id) * progress);
			getPlayer(id).setPosition(duration(id) * progress);
		}
	}
	
	public WavPlayer pauseToggle(String id) {
		if(getPlayer(id) != null) getPlayer(id).pause(!isPaused(id));
		return this;
	}
	
	public boolean isPaused(String id) {
		if(getPlayer(id) != null) return getPlayer(id).isPaused();
		return false;
	}
	
	public WavPlayer stop(String id) {
		if(getPlayer(id) != null) {
			if(RECYCLES_PLAYERS) {
				getPlayer(id).pause(true);
			} else {
				getPlayer(id).kill();
			}
//			players.remove(id);
//			gains.remove(id);
//			glides.remove(id);
//			glideTimes.remove(id);
		}
		return this;
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
	
	public WavPlayer setVolume(String id, float gain) {
		if(gains.containsKey(id)) {
			gains.get(id).setGain(gain);
		}
		return this;
	}
	
	public int glideTime(String id) {
		if(getPlayer(id) == null) return 0;
		return glideTimes.get(id).intValue();
	}
	
	public WavPlayer setGlideTime(String id, int glide) {
		if(gains.containsKey(id)) {
			glideTimes.put(id, glide);
			glides.get(id).setGlideTime(glide);
		}
		return this;
	}
	
	public WavPlayer setPitch(String id, float pitchIndex) {
		if(glides.containsKey(id)) {
			// remove glide completely or use portamento if glide has been set
			if(glideTime(id) == 0) {
				glides.get(id).setValueImmediately(pitchRatioFromIndex(pitchIndex));
			} else {
				glides.get(id).setValue(pitchRatioFromIndex(pitchIndex));
			}
			// apply pitch adjustment to sample
			getPlayer(id).setRate(glides.get(id));
		}
		return this;
	}
	
}
