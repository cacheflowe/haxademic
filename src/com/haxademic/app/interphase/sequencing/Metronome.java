package com.haxademic.app.interphase.sequencing;

import com.haxademic.app.interphase.Interphase;
import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;

import beads.AudioContext;
import beads.Bead;
import beads.Clock;

public class Metronome {
	
	protected Interphase p;
	protected AudioContext ac;
	protected Clock clock;
	protected int tempoMin = 60;
	protected int tempoMax = 140;
	
	public static int DEFAULT_INTERVAL = 1000;
	public static int numTempos = Interphase.NUM_WALLS + 1; // (0-8 = 9 total)
	public static int[] TEMPOS = new int[numTempos];
	protected LinearFloat beatInterval = new LinearFloat(DEFAULT_INTERVAL, Interphase.TEMPO_EASE_FACTOR);

	
	public Metronome() {
		p = (Interphase) P.p;
		initTempos();
//		ac = new AudioContext();
		ac = new AudioContext(4096);
		init();
		p.registerMethod("pre", this);
	}
	
	public void pre() {
		// always lerp tempo on frameRate
		beatInterval.update(true);
	}
	
	public void initTempos() {
		float tempoSpread = tempoMax - tempoMin;
		float tempoIncrement = tempoSpread / Interphase.NUM_WALLS; 
		for (int i = 0; i < TEMPOS.length; i++) {
			TEMPOS[i] = P.round(60000f / (tempoMin + i * tempoIncrement)); 	// one minute divided by bpm
		}
	}
	
	public void init() {
		ac = new AudioContext();
		clock = new Clock(ac, DEFAULT_INTERVAL);
		clock.addMessageListener(
			new Bead() {
				@SuppressWarnings("deprecation")
				public void messageReceived(Bead message) {
					Clock c = (Clock) message;
					if(p.systemMuted()) return;
					
					// update tempo!
					if(Interphase.TEMPO_MOUSE_CONTROL) {
						// mouse control
						int beatIntervalMillisMouse = P.round(P.map(p.mousePercentX(), 0, 1, TEMPOS[0], TEMPOS[TEMPOS.length - 1]));
						P.store.setNumber(Interphase.BEAT_INTERVAL_MILLIS, beatIntervalMillisMouse);
					} else {
						// change tempo based on activity
						int interactionMultIndex = P.store.getInt(Interphase.INTERACTION_SPEED_MULT);
						beatInterval.setTarget(TEMPOS[interactionMultIndex]);
						
						// lerping through logarithmic tempo values 
						P.store.setNumber(Interphase.BEAT_INTERVAL_MILLIS, P.round(beatInterval.value()));
					}
					c.getIntervalEnvelope().setValue(P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS));
					
					// debug tempo values
					float bpm = (60*1000) / P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS);
					P.store.setNumber(Interphase.BPM, bpm);

					// set beat on sequencers, and play them if needed
					if(c.getCount() % 4 == 0) {
						// send beat to Sequencers
						int beat = P.round(c.getCount() / 4);
						P.store.setNumber(Interphase.BEAT, beat);
						
						// change scale (and color scheme) sometimes
						if(beat % Interphase.BEATS_PER_SCALE_CHANGE == 0) {
							int newScaleIndex = MathUtil.randRange(0, Interphase.SCALES.length - 1);
							P.store.setNumber(Interphase.CUR_SCALE_INDEX, newScaleIndex);
							Interphase.CUR_SCALE = Interphase.SCALES[newScaleIndex];
							p.newColorScheme();
						}
					}
					
					// trigger samples if sequence patterns need to
					for (int i = 0; i < p.sequencers.length; i++) {
						if(p.sequencers[i] != null && p.sequencers[i].shouldPlay()) {
							p.sequencers[i].playSample(ac);
						}
					}
					
					// set debug text
					p.debugView.setValue("numinputs", ac.out.getConnectedInputs().size());
					p.debugView.setValue("c.getCount()", ((c.getCount() / 4) % 8) + 1);
				}
			}
		);
		ac.out.addDependent(clock);
		ac.start();
	}
	
}
