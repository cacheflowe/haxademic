package com.haxademic.core.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.analysis.AudioIn;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.playback.WavPlayer;

import beads.AudioContext;
import beads.Bead;
import beads.Clock;

public class Metronome {
	
	protected PAppletHax p;
	public static AudioContext ac;
	protected AudioInputBeads audioInput;
	protected Clock clock;
	protected int tempoMin = 60;
	protected int tempoMax = 140;
	
	public static int numTempos = Interphase.NUM_WALLS + 1; // (0-8 = 9 total)
	public static int[] TEMPOS = new int[numTempos];
	protected LinearFloat bpmEased = new LinearFloat(0, Interphase.TEMPO_EASE_FACTOR);

	public Metronome() {
		initAudioContext();
		initTempos();
		initClock();
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
		// always lerp tempo on frameRate
		bpmEased.update(true);
	}
	
	protected void initAudioContext() {
		ac = new AudioContext();
		new AudioIn(new AudioInputBeads(ac));	// connects the audio input to the global audio frequencies getter & draws debug view
	}
	
	public void initTempos() {
		// helpers to calculate stopping points if user interaction with each wall "excites" the bpm
		float tempoSpread = tempoMax - tempoMin;
		float tempoIncrement = tempoSpread / Interphase.NUM_WALLS; 
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
					if(Interphase.SYSTEM_MUTED) return;

					Clock c = (Clock) message;
					updateBpm(c);
					updateBeat(c);
					// set beat on sequencers, and play them if needed
					
					// set debug text
					P.p.debugView.setValue("INTERPHASE :: numinputs", ac.out.getConnectedInputs().size());
					P.p.debugView.setValue("INTERPHASE :: c.getCount()", ((c.getCount() / 4) % 8) + 1);
				}
			}
		);
		ac.out.addDependent(clock);
		ac.start();
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
		P.p.debugView.setValue("INTERPHASE :: BPM", bpm);
	}
	
	protected void updateBeat(Clock c) {
		if(c.getCount() % 4 == 0) {
			// send beat to Sequencers
			int beat = P.round(c.getCount() / 4);
			P.store.setNumber(Interphase.BEAT, beat);
			P.store.setNumber(Interphase.CUR_STEP, beat % Interphase.NUM_STEPS);
		}
	}
	
	// Global tempo helpers
	
	public static float bpmToIntervalMS(float bpm) {
		return bpmToIntervalMS(bpm, 1);	// default to one bar length, four in a loop
	}
	
	public static float bpmToIntervalMS(float bpm, int measureDivider) {
		// one minute divided by bpm
		return 60000f / bpm * measureDivider;
	}
	
	public static void shiftPitchToMatchBpm(WavPlayer player, String id, float bpm, int measureDivider) {
		// formula from https://math.stackexchange.com/a/1205895
		float bpmToMs = Metronome.bpmToIntervalMS(bpm, measureDivider); // times 4 because they're only 1 bar loops
		float syncRatio = (bpmToMs) / player.duration(id);
		float pitchShift = P.log(syncRatio) / P.log(2f);
		player.setPitch(id, -pitchShift * 12f);
	}
	
	public static void shiftPitchToMatchOtherPlayer(WavPlayer player, String id, WavPlayer player2, String id2) {
		// formula from https://math.stackexchange.com/a/1205895
		float syncRatio = player.duration(id2) / player.duration(id);
		float pitchShift = P.log(syncRatio) / P.log(2f);
		player.setPitch(id, -pitchShift * 12f);
	}
	

	
}
