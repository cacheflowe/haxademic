package com.haxademic.core.media.audio.playback;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.media.audio.AudioUtil;

import beads.AudioContext;
import beads.Envelope;
import beads.Gain;
import beads.Glide;
import beads.KillTrigger;
import beads.Panner;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import processing.core.PGraphics;

public class WavPlayer {

	public static AudioContext sharedContext;
	protected AudioContext curContext;
	protected HashMap<String, SamplePlayer> players = new HashMap<String, SamplePlayer>();
	protected HashMap<String, Envelope> ampEnvs = new HashMap<String, Envelope>();
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
		AudioContext ctx = AudioUtil.getBeadsContext();
		ctx.start();
		return ctx;
	}
	
	public AudioContext context() {
		return curContext;
	}
	
	// getters
	
	public int activeConnections() {
		return curContext.out.getConnectedInputs().size();
	}
	
	public void setGlobalVolume(float vol) {
		curContext.out.setGain(vol);
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
			
			
			// experimenting with pan LFO
			Panner panner;
			boolean autoPan = false;
			if(autoPan) {
				// Initialize the LFO at a frequency of 0.33Hz.
				// create a custom frequency modulation function
//				Function frequencyModulation = new Function(new WavePlayer(curContext, 0.33f, Buffer.SINE)) {
//					public float calculate() {
//						// return x[0], scaled into an appropriate frequency
//						// range
//						return (x[0] * 100.0f) + Mouse.y;
//					}
//				};
//				WavePlayer panLFO = new WavePlayer(curContext, frequencyModulation, Buffer.SINE);
//				WavePlayer panLFO = new WavePlayer(curContext, 0.33f, Buffer.SINE);
				Envelope panEnvelope = new Envelope(curContext, 0.0f);
				for(int i=0; i < 10; i++) {
					panEnvelope.addSegment(1f, 150);
					panEnvelope.addSegment(-1f, 150);
				}
				panEnvelope.addSegment(0, 150);
				panner = new Panner(curContext, panEnvelope);	// panLFO  // panEnvelope
			} else {
				panner = new Panner(curContext, panAmp);
			}
			// pan it! 
			panner.setKillListener(player);
			panner.addInput(player);
			panners.put(id, panner);
			
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
				ampEnv.addSegment(volume, 0, 1f);
			}
			// need a KillTrigger if we're not using the on in the Envelope
			// otherwise players don't clear out!
			// clean up panner too!
			player.setKillListener(new KillTrigger(gain)); 	
			panner.setKillListener(new KillTrigger(gain));
			
			// add Gain
			ampEnvs.put(id, ampEnv);		
			gains.put(id, gain);		
			gain.addInput(panner);
			
			// attach to main Beads output
			curContext.out.addInput(gain);		// to test SamplePlayer without audio chain: addInput(player);
				
			// play it, with delay or without
//			Bead myBead = new Bead() {
//				protected void messageReceived(Bead b) {
//					// play from sample start - this un-pauses
//					player.start(sampleStart);
//					
//					// trigger as loop or one-shot
//					if(loops) {
//						player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
////							player.setLoopCrossFade(200);
//					}
//					else player.setLoopType(SamplePlayer.LoopType.NO_LOOP_FORWARDS);
//				}
//			};
//			
//			// attempt to delay... having issues, so just call .start();
//			DelayTrigger dt = new DelayTrigger(curContext, delay, myBead, null);
//			curContext.out.addDependent(dt);
			

			// PLAY NORMALLY - no delay
			// play from sample start - this un-pauses
			player.start(sampleStart);
			
			// trigger as loop or one-shot
			if(loops) {
				player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
//					player.setLoopCrossFade(200);
			}
			else player.setLoopType(SamplePlayer.LoopType.NO_LOOP_FORWARDS);

			
			success = true;
		} else {
			DebugUtil.printErr("Bad audio file: " + filePath);
		}
		return success;
	}
	
	// pitch shift
	
	public static float pitchRatioFromIndex(float pitchIndex) {
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
			float playTimeMS = duration(id) * progress;
			P.out(playTimeMS);
			getPlayer(id).setPosition(playTimeMS);
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
	
	public WavPlayer fadeOut(String id) {
		if(getPlayer(id) != null) {
			P.out("FADE", ampEnvs.get(id));
			float fadeCurve = 2f;
//			ampEnvs.get(id).addSegment(1, 0, fadeCurve);
//			ampEnvs.get(id).addSegment(fadeCurve, fadeCurve, fadeCurve, null)
//			ampEnvs.get(id).addSegment(0, 1000, 1f/fadeCurve, new KillTrigger(gains.get(id)));			// attack & release envelope segments
//			Envelope ampEnv = new Envelope(curContext, 1);				// start volume at 0 or 1
			ampEnvs.get(id).addSegment(0, 1000, 1f, new KillTrigger(gains.get(id)));			// attack & release envelope segments
			gains.get(id).addDependent(ampEnvs.get(id));
//			ampEnvs.get(id).start();
//			gains.get(id).setGain(ampEnvs.get(id));
//			gains.get(id).setGainEnvelope(ampEnvs.get(id));
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
	
	public void drawWav(PGraphics pg, String id) {
		SamplePlayer sp = getPlayer(id);
		if(sp == null) return;
		Sample sample = sp.getSample();
		if(sample == null) return;
		
		WavPlayer.drawWav(pg, sample);
	}
	
	public static void drawWav(PGraphics pg, Sample sample) {
		if(sample == null) return;
		
		long sampleFrames = sample.getNumFrames();
		if(sampleFrames == 0 || sample.getLength() == 0) return;
		
		int numChannels = sample.getNumChannels();
		float[][] SampleFrames = new float[numChannels][(int)sampleFrames]; // (int)sampleFrames
		sample.getFrames(0, SampleFrames);
		float skipFrames = SampleFrames[0].length / 512f;
		int x = 0;
		int h = 512;
		for (float i = 0; i < SampleFrames[0].length; i+=skipFrames) {
			pg.fill(255);
			pg.rect(x, 0, 1, h * SampleFrames[0][P.floor(i)]);
			x++;
		}
	}
	
}
