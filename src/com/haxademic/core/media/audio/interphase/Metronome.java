package com.haxademic.core.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

import beads.AudioContext;
import beads.Bead;
import beads.Clock;
import beads.Glide;
import beads.SamplePlayer;

public class Metronome {
	
	protected PAppletHax p;
	public static AudioContext ac;
	protected AudioInputBeads audioInput;
	protected Clock clock;
	protected int tempoMin = 60;
	protected int tempoMax = 140;
	protected static int loopStartFrame = 0;	// since bpm intervals don't match ful frames, we need to track the start of the last loop
	protected boolean paused = true;
	
	public static int numTempos = Interphase.NUM_CHANNELS + 1; // (0-8 = 9 total)
	public static int[] TEMPOS = new int[numTempos];
	protected LinearFloat bpmEased = new LinearFloat(0, Interphase.TEMPO_EASE_FACTOR);

	public Metronome() {
	    this(false);
	}
	
	public Metronome(boolean autoPlay) {
		P.store.setBoolean(Interphase.SYSTEM_MUTED, false);
		initAudioContext();
		initTempos();
		initClock();
		P.p.registerMethod(PRegisterableMethods.pre, this);
		if(autoPlay) togglePlay();
	}
	
	public void pre() {
		// always lerp tempo on frameRate
		bpmEased.update(true);
	}
	
	protected void initAudioContext() {
		ac = AudioUtil.getBeadsContext();
		new AudioIn(new AudioInputBeads(ac));	// connects the audio input to the global audio frequencies getter & draws debug view
	}
	
	public void initTempos() {
		// helpers to calculate stopping points if user interaction with each wall "excites" the bpm
		float tempoSpread = tempoMax - tempoMin;
		float tempoIncrement = tempoSpread / Interphase.NUM_CHANNELS; 
		for (int i = 0; i < TEMPOS.length; i++) {
			float bpm = tempoMin + i * tempoIncrement;
			TEMPOS[i] = (int) bpm;
		}
		bpmEased.setCurrent(TEMPOS[0]).setTarget(TEMPOS[0]);
		P.store.setNumber(Interphase.BPM, TEMPOS[0]);
	}
	
	public void initClock() {
		clock = new Clock(ac, bpmEased.value());
		clock.addMessageListener(
			new Bead() {
				public void messageReceived(Bead message) {
					if(P.store.getBoolean(Interphase.SYSTEM_MUTED)) return;

					Clock c = (Clock) message;
					updateBpm(c);
					updateBeat(c);
					// set beat on sequencers, and play them if needed
					
					// set debug text
					DebugView.setValue("INTERPHASE :: numinputs", ac.out.getConnectedInputs().size());
					DebugView.setValue("INTERPHASE :: c.getCount()", ((c.getCount() / 4) % 8) + 1);
				}
			}
		);
		ac.out.addDependent(clock);
		ac.start();
		
		paused = true;
		clock.pause(true);
	}
	
	public void togglePlay() {
		paused = !paused;
		if(paused == false) clock.reset();
		clock.pause(paused);
	}
	
	protected void updateBpm(Clock c) {
		if(Interphase.TEMPO_MOUSE_CONTROL) {
			// mouse control
			P.store.setNumber(Interphase.BPM, P.map(Mouse.xNorm, 0, 1, TEMPOS[0], TEMPOS[TEMPOS.length - 1]));
		} else if(Interphase.TEMPO_MIDI_CONTROL) {
			// midi control now just adjusts BPM and this case allows it to stand
		} else {
			// change tempo based on activity
			int interactionMultIndex = P.store.getInt(Interphase.INTERACTION_SPEED_MULT);
			bpmEased.setTarget(TEMPOS[interactionMultIndex]);
			
			// lerping through logarithmic tempo values 
			P.store.setNumber(Interphase.BPM, bpmEased.value());
		}
		
		// update clock with bpm interval
		float bpm = P.store.getInt(Interphase.BPM);
		float bpmIntervalMS = bpmToIntervalMS(bpm * 1);
		c.getIntervalUGen().setValue(bpmIntervalMS);
		DebugView.setValue("INTERPHASE :: BPM", bpm);
	}
	
	protected void updateBeat(Clock c) {
		if(c.getCount() % 4 == 0) {
			// send beat to Sequencers
			int beat = P.round(c.getCount() / 4);
			P.store.setNumber(Interphase.BEAT, beat);
			P.store.setNumber(Interphase.CUR_STEP, beat % Interphase.NUM_STEPS);
			if(beat % 16 == 0) loopStartFrame = P.p.frameCount;	// reset loop to calc looped frames
		}
	}
	
	// Global tempo helpers
	
	public static float bpmToIntervalMS(float bpm) {
		return bpmToIntervalMS(bpm, 1);	// default to one bar length, four in a loop
	}
	
	public static float bpmToIntervalMS(float bpm, float measureDivider) {
		// one minute divided by bpm
		return 60000f / bpm * measureDivider;
	}
	
	public static void shiftPitchToMatchBpm(WavPlayer player, String id, float bpm, float measureDivider) {
		// formula from https://math.stackexchange.com/a/1205895
		float bpmToMs = Metronome.bpmToIntervalMS(bpm, measureDivider); // times 4 because they're only 1 bar loops
		float syncRatio = (bpmToMs) / player.duration(id);
		float pitchShift = P.log(syncRatio) / P.log(2f); // * 1.1f;
		player.setPitch(id, -pitchShift * 12f);
	}
	
	public static void shiftPitchToMatchBpm(SamplePlayer player, Glide glide, float bpm, float measureDivider) {
		// formula from https://math.stackexchange.com/a/1205895
		float bpmToMs = Metronome.bpmToIntervalMS(bpm, measureDivider); // times 4 because they're only 1 bar loops
		float syncRatio = (bpmToMs) / (float) player.getSample().getLength();
		float pitchShift = P.log(syncRatio) / P.log(2f);
		glide.setValueImmediately(WavPlayer.pitchRatioFromIndex(-pitchShift * 12f));
	}
	
	public static void shiftPitchToMatchOtherPlayer(WavPlayer player, String id, WavPlayer player2, String id2) {
		// formula from https://math.stackexchange.com/a/1205895
		float syncRatio = player.duration(id2) / player.duration(id);
		float pitchShift = P.log(syncRatio) / P.log(2f);
		player.setPitch(id, -pitchShift * 12f);
	}
	
	public static float loopProgress() {
		float bpm = P.store.getFloat(Interphase.BPM);
		float loopTime = Interphase.NUM_STEPS * Metronome.bpmToIntervalMS(bpm, 1) / 4f;	 // divided by 4 beats...
		float loopFrames = P.round(loopTime / 1000f * 60);
		float loopProgress = ((P.p.frameCount - loopStartFrame) % loopFrames) / loopFrames;
		return loopProgress;
	}
	
}
