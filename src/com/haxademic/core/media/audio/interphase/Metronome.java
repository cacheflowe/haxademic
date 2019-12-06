package com.haxademic.core.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.analysis.AudioIn;

import beads.AudioContext;
import beads.Bead;
import beads.Clock;

public class Metronome {
	
	protected Interphase p;
	protected AudioContext ac;
	protected AudioInputBeads audioInput;
	protected Clock clock;
	protected int tempoMin = 60;
	protected int tempoMax = 140;
	
	public static int DEFAULT_INTERVAL = 1000;
	public static int numTempos = Interphase.NUM_WALLS + 1; // (0-8 = 9 total)
	public static int[] TEMPOS = new int[numTempos];
	protected LinearFloat beatInterval = new LinearFloat(DEFAULT_INTERVAL, Interphase.TEMPO_EASE_FACTOR);

	public Metronome(Interphase i) {
		p = i;
		initTempos();
		init();
		P.p.registerMethod(PRegisterableMethods.pre, this);
	}
	
	public void pre() {
		// always lerp tempo on frameRate
		beatInterval.update(true);
	}
	
	public void initTempos() {
		// helpers to calculat stopping points if user interaction with each wall "excites" the bpm
		float tempoSpread = tempoMax - tempoMin;
		float tempoIncrement = tempoSpread / Interphase.NUM_WALLS; 
		for (int i = 0; i < TEMPOS.length; i++) {
			TEMPOS[i] = P.round(60000f / (tempoMin + i * tempoIncrement)); 	// one minute divided by bpm
		}
		// inits user bpm
		// start tempo in the middle
		P.store.setNumber(Interphase.BPM_MIDI, tempoMin + (tempoMax - tempoMin) / 2); 
	}
	
	public void init() {
		ac = new AudioContext();
		new AudioIn(new AudioInputBeads(ac));	// connects the audio input to the global audio frequencies getter & draws debug view

		clock = new Clock(ac, DEFAULT_INTERVAL);
		clock.addMessageListener(
			new Bead() {
				public void messageReceived(Bead message) {
					Clock c = (Clock) message;
					if(p.systemMuted()) return;
					
					// update tempo!
					if(Interphase.TEMPO_MOUSE_CONTROL) {
						// mouse control
						int beatIntervalMillisMouse = P.round(P.map(P.p.mousePercentX(), 0, 1, TEMPOS[0], TEMPOS[TEMPOS.length - 1]));
						P.store.setNumber(Interphase.BEAT_INTERVAL_MILLIS, beatIntervalMillisMouse);
					} else if(Interphase.TEMPO_MIDI_CONTROL) {
						// mouse control
						int beatIntervalMillisMidi = P.round(P.map(P.store.getInt(Interphase.BPM_MIDI), tempoMin, tempoMax, TEMPOS[0], TEMPOS[TEMPOS.length - 1]));
						P.store.setNumber(Interphase.BEAT_INTERVAL_MILLIS, beatIntervalMillisMidi);
					} else {
						// change tempo based on activity
						int interactionMultIndex = P.store.getInt(Interphase.INTERACTION_SPEED_MULT);
						beatInterval.setTarget(TEMPOS[interactionMultIndex]);
						
						// lerping through logarithmic tempo values 
						P.store.setNumber(Interphase.BEAT_INTERVAL_MILLIS, P.round(beatInterval.value()));
					}
					c.getIntervalUGen().setValue(P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS));
//					c.getIntervalEnvelope().setValue(P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS));
					
					// debug tempo values
					float bpm = (60*1000) / P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS);
					P.store.setNumber(Interphase.BPM, bpm);

					// set beat on sequencers, and play them if needed
					if(c.getCount() % 4 == 0) {
						// send beat to Sequencers
						int beat = P.round(c.getCount() / 4);
						P.store.setNumber(Interphase.BEAT, beat);
						P.store.setNumber(Interphase.CUR_STEP, beat % Interphase.NUM_STEPS);
					}
					
					// trigger samples if sequence patterns need to
					for (int i = 0; i < p.sequencers.length; i++) {
						if(p.sequencers[i] != null && p.sequencers[i].shouldPlay()) {
							p.sequencers[i].playSample(ac);
						}
					}
					
					// set debug text
					P.p.debugView.setValue("INTERPHASE :: numinputs", ac.out.getConnectedInputs().size());
					P.p.debugView.setValue("INTERPHASE :: c.getCount()", ((c.getCount() / 4) % 8) + 1);
				}
			}
		);
		ac.out.addDependent(clock);
		ac.start();
	}
	
}
