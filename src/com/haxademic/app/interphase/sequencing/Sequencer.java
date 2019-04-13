package com.haxademic.app.interphase.sequencing;


import java.io.File;
import java.util.ArrayList;

import com.haxademic.app.interphase.Interphase;
import com.haxademic.core.app.P;
import com.haxademic.core.data.patterns.ISequencerPattern;
import com.haxademic.core.data.patterns.PatternUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.color.EasingColor;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.math.easing.LinearFloat;

import beads.AudioContext;
import beads.Envelope;
import beads.Gain;
import beads.Glide;
import beads.KillTrigger;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import processing.core.PGraphics;
import processing.core.PImage;

public class Sequencer
implements IAppStoreListener {
	
	// app & buffer
	protected Interphase p;
	protected PGraphics buffer;
	protected PGraphics bufferOver;
	protected boolean muted = false;
	protected SequencerConfig config;
	
	// beats & beat offset
	protected int index;						// wall index number
	protected int sequencesComplete = 0;		// keeps counting up
	protected int lastSequenceCountChangedSound = 0;
	protected int sequenceCountChangeSound = 16;
	protected int curStep = 0;				// 1-16
	protected int queuedBeat = -1;			// the next beat
	
	// note selection
	protected int pitchIndex1 = 0;
	protected int pitchIndex2 = 0;
	protected boolean notesByStep = true;
	protected boolean upOctave = false;
	protected int noteOffset = 0;
	protected boolean chordMode = false;
	
	// audio effects
	protected float sampleLength = 0;
	protected float attack = 0;
	protected float release = 650;
	protected int MAX_SAMPLE_LENGTH = 400;
	protected float velocity = 1;

	// step sequencer
	protected boolean steps[];
	protected ISequencerPattern[] sequencerPatterns;
	protected int curPatternGeneratorIndex = 0;
	
	// trigger updates 
	protected int manualTriggerTime = 0;
	protected int sampleTriggerCount = 0;
	protected boolean evolves = true;

	// colors
	protected static final int COLOR_INT_WHITE = 0xffffffff;
	protected static final int COLOR_INT_BLACK = 0xff000000;
	protected static final int COLOR_INT_CLEAR = 0x00ffffff;
	protected int ledColor;
	protected int wallIntColor;
	protected EasingFloat wallColorMultiplier = new EasingFloat(0, 5);				// used to shift lighter & darker on the beat
	protected EasingColor wallColorEasedBase = new EasingColor(COLOR_INT_WHITE, 14);	// wall color without white fade
	protected EasingColor wallEaseColorFaded = new EasingColor(COLOR_INT_WHITE, 8);	// wall color with white fade
	protected EasingColor stepColors[];
	protected EasingColor stepActiveColors[];
	protected LinearFloat stepCirclesProgress[];
	protected LinearFloat stepFlashProgress[];
	protected LinearFloat gradientWipeEase = new LinearFloat(0, 0.03f);

	// audio sample playback objects
	protected Sample curSample;
	protected int curSampleIndex = 0;
	protected SamplePlayer player;
	protected SamplePlayer player2;
	protected Gain gain;
	protected boolean shouldPlay = false;
	protected String audioDir;
	protected Sample[] samples;
	protected String[] filenames;
	protected boolean useASDR = true;
	
	public Sequencer(SequencerConfig config) {
		this.config = config;
		this.index = config.index;
		this.audioDir = config.audioPath;
		this.sequencerPatterns = config.patterns;
		p = (Interphase) P.p;
		// new Thread(new Runnable() { public void run() {
			getAudiofiles(audioDir);
		// }}).start();	
		loadNextSound();
		initStepValues();
		updateChangeSoundCount();
		P.store.addListener(this);
	}
	
	public String info() {
		return 
				  "index: " + index + FileUtil.NEWLINE
				+ "curStep: " + curStep + FileUtil.NEWLINE
				+ "file: " + filenames[curSampleIndex] + FileUtil.NEWLINE
				+ "evolves: " + evolves + FileUtil.NEWLINE
				+ "sequences: " + sequencesComplete + FileUtil.NEWLINE
				+ "triggerCount: " + (sampleTriggerCount % 4) + FileUtil.NEWLINE
				+ "changeSound: " + (sequencesComplete - lastSequenceCountChangedSound) + "/" + sequenceCountChangeSound + FileUtil.NEWLINE
				+ "velocity: " + P.round(100f * velocity) + FileUtil.NEWLINE
				+ "length: " + P.round(sampleLength) + FileUtil.NEWLINE
				+ "attack: " + attack + FileUtil.NEWLINE
				+ "release: " + release + FileUtil.NEWLINE
				+ "playsNotes: " + config.playsNotes + FileUtil.NEWLINE
				+ "notesByStep: " + notesByStep + FileUtil.NEWLINE
				+ "chordMode: " + chordMode + FileUtil.NEWLINE
				+ "gradientWipe: " + P.round(100f * gradientWipeEase.value()) + FileUtil.NEWLINE
				;
	}
	
	/////////////////////////////////////
	// Patterns
	/////////////////////////////////////
	
	protected void initStepValues() {
		steps = new boolean[Interphase.NUM_STEPS];
		stepColors = new EasingColor[Interphase.NUM_STEPS];
		stepActiveColors = new EasingColor[Interphase.NUM_STEPS];
		stepCirclesProgress = new LinearFloat[Interphase.NUM_STEPS];
		stepFlashProgress = new LinearFloat[Interphase.NUM_STEPS];
		for (int i = 0; i < Interphase.NUM_STEPS; i++) {
			steps[i] = false;
			stepColors[i] = new EasingColor(0xffffffff, 8);
			stepActiveColors[i] = new EasingColor(0xffffffff, 8);
			stepCirclesProgress[i] = new LinearFloat(0, 0.05f);
			stepFlashProgress[i] = new LinearFloat(0, 0.05f);
		}
		evolvePattern(false);
	}
	
	protected void newRandomPattern() {
		curPatternGeneratorIndex = MathUtil.randRange(0, sequencerPatterns.length - 1);
		sequencerPatterns[curPatternGeneratorIndex].newPattern(steps);
		PatternUtil.ensureOneStepActive(steps);
	}
	
	/////////////////////////////////////
	// GETTERS
	/////////////////////////////////////
	
	public PGraphics image() {
		return buffer;
	}

	public boolean muted() {
		return muted;
	}
	
	public int wallColorFaded() {
		return wallEaseColorFaded.colorInt(wallColorMultiplier.value());
	}
	
	public EasingColor wallColor() {
		return wallColorEasedBase;
	}
	
//	public int wallColor() {
//		return wallEaseColorFaded.colorInt(wallColorMultiplier.value());
//	}
	
	public EasingColor wallColorPrev() {
		return p.sequencers[(index + 7) % Interphase.NUM_WALLS].wallColor();
	}
	
	protected boolean stepActive(int i) {
		return steps[i];
	}
	
	public boolean shouldPlay() {
		return shouldPlay;
	}
	
	public boolean userInteracted() {
		return p.millis() < manualTriggerTime + Interphase.TRIGGER_TIMEOUT;
	}
	
	public boolean toggleEvloves() {
		return evolves = !evolves;
	}
	
	/////////////////////////////////////
	// INPUT
	/////////////////////////////////////
	
	public void toggleMute() {
		muted = !muted;
	}
	
	public void evolvePattern(boolean isManual) {
		// keep track of manual input time
		if(isManual) {
			manualTriggerTime = p.millis();
			sampleTriggerCount++;
			// queue up for manual jamming
			queuedBeat = (curStep + 1) % Interphase.NUM_STEPS;
		} else {
			// if user interaction timed out, advance trigger count for slower pattern switching & morphing below
			if(userInteracted() == false) {
				sampleTriggerCount++;
			}
		}
		
		if(evolves == true) {
			// every 4 sample triggers, make a bigger evoloving change
			// new pattern, note & note props
			if(sampleTriggerCount % 4 == 0) {
				newRandomPattern();
				
				// change note scheme
				noteOffset = MathUtil.randRange(0, Interphase.NUM_STEPS - 1);
				notesByStep = MathUtil.randBooleanWeighted(p, 0.7f);
				upOctave = MathUtil.randBooleanWeighted(p, 0.2f) && config.playsOctaveNotes;	// don't octave on keys
				chordMode = (config.playsChords && MathUtil.randBooleanWeighted(p, 0.5f));
				
				// change attack
				if(MathUtil.randBooleanWeighted(p, 0.2f) && config.hasAttack) {
					attack = MathUtil.randRange(30, 150);
				} else {
					attack = 0;
				}
				
				// change release
				if(MathUtil.randBooleanWeighted(p, 0.2f) && config.hasRelease) {
					release = MathUtil.randRange(200, P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS));
				} else {
					release = 0;
				}
			} else {
				// every sample trigger, slightly change sequence
				if(MathUtil.randBoolean(P.p)) {
					PatternUtil.nudgePatternForward(steps);
				} else {
					PatternUtil.morphPattern(steps);
				}
			}
		}
	}
	
	/////////////////////////////////////
	// AUDIO
	/////////////////////////////////////
	
	protected void getAudiofiles(String audioDir) {
		String absAudioPath = audioDir;// FileUtil.getFile(audioDir);
		P.out(absAudioPath);
		ArrayList<String> files = FileUtil.getFilesInDirOfTypes(absAudioPath, "wav,aif,mp3");
		samples = new Sample[files.size()];
		filenames = new String[files.size()];
		for (int i = 0; i < files.size(); i++) {
			samples[i] = SampleManager.sample(files.get(i));
			File file = new File(files.get(i));
			String simpleFileName = file.getName();
			filenames[i] = simpleFileName;
			// filenames[i] = this.audioDir + File.separator + simpleFileName;
			// P.println("loading:", filenames[i]);
		}
		
		curSampleIndex = MathUtil.randRange(0, samples.length - 1);
		curSample = samples[curSampleIndex];
	}
	
	public void playSample(AudioContext ac) {
		if(curSample == null) return;
		if(muted) return;
		
		// get scale/pitch
		selectNewNote();
		
		// recreate SamplePlayer objects
		player = playSampleWithNote(ac, player, pitchRatioFromIndex(pitchIndex1));
		if(chordMode) player2 = playSampleWithNote(ac, player2, pitchRatioFromIndex(pitchIndex2));
					
		// reset play trigger flag
		shouldPlay = false;
	}
	
	protected void selectNewNote() {
		if(config.playsNotes == false) {
			// if we don't play notes, use 0
			pitchIndex1 = pitchIndex2 = 0;	
		} else {			
			if(notesByStep) {
				// if notes by step mode, use cur step + offset to cycle through current scale notes 
				pitchIndex1 = Interphase.CUR_SCALE[(noteOffset + curStep) % Interphase.CUR_SCALE.length];
				pitchIndex2 = Interphase.CUR_SCALE[(noteOffset + curStep + 2) % Interphase.CUR_SCALE.length];
			} else {
				// random pitch
				int randPitch = MathUtil.randRange(0, Interphase.CUR_SCALE.length - 1);
				int randPitch2 = randPitch + 2;
				pitchIndex1 = Interphase.CUR_SCALE[randPitch];
				pitchIndex2 = Interphase.CUR_SCALE[randPitch2 % Interphase.CUR_SCALE.length];
			}
			
			// sometimes pitch up an octave 
			if(upOctave && MathUtil.randBooleanWeighted(p, 0.2f)) pitchIndex1 += 12;
		}

	}
	
	protected float pitchRatioFromIndex(int pitchIndex) {
		return P.pow(2, pitchIndex/12.0f);
	}
	
	protected SamplePlayer playSampleWithNote(AudioContext ac, SamplePlayer curPlayer, float pitchRatio) {
		// stop playing previous sample instance 
		if(curPlayer != null) curPlayer.kill();	

		// re-init sample player
		curPlayer = new SamplePlayer(ac, curSample);
		curPlayer.setKillOnEnd(false);
		
		// set pitch on all sequencers that play notes
		if(config.playsNotes) {
			// change pitch
			Glide glide = new Glide(ac, pitchRatio);
			curPlayer.setRate(glide);

			// reverse
			//			if(MathUtil.randBooleanWeighted(P.p, 0.2f)) {
			//				curPlayer.setToEnd();
			//				glide.setValue(-1.0f);
			//				curPlayer.start();
			//			}
		}

		// apply attack/sustain
		if(useASDR) {
			// responsive release 
			MAX_SAMPLE_LENGTH = P.store.getInt(Interphase.BEAT_INTERVAL_MILLIS);
			// set volume
			velocity = MathUtil.randRangeDecimal(0.8f, 1f) * config.volume;
			float fullGain = (chordMode) ? velocity * 0.7f : velocity;	// quieter on keys channel, when we're playing chords
			float startGain = (attack == 0) ? fullGain : 0f;
			float endGain = 0f;

			// add ASDR :: http://doc.gold.ac.uk/CreativeComputing/creativecomputation/?page_id=558
			Envelope ampEnv = new Envelope(ac, startGain);				// start volume at 0 or 1
			gain = new Gain(ac, 2, ampEnv); 								// ampEnv now controls the fader level. `2` for stereo

			if(attack > 0 && release > 0) {
				release = P.min(release, sampleLength - attack);						// release should be no longer than sample length minus attack
				ampEnv.addSegment(fullGain, attack);
				ampEnv.addSegment(endGain, release, new KillTrigger(gain));			// attack & release envelope segments
			} else if(attack > 0) {
				release = MAX_SAMPLE_LENGTH - attack;
				ampEnv.addSegment(fullGain, attack);															// just attack
				ampEnv.addSegment(fullGain, release, new KillTrigger(gain));									// set release to full sample time
			} else if(release > 0) {
				ampEnv.addSegment(endGain, P.min(sampleLength, release), new KillTrigger(gain));				// just release
			} else {
				ampEnv.addSegment(endGain, P.min(sampleLength, MAX_SAMPLE_LENGTH), new KillTrigger(gain));	// no attack or release, but using Gain for volume. set killtrigger at end of sample. 
			}
			
//	        Reverb rb = new Reverb(ac, 2);
//	        rb.setSize(0.01f);	
//	        rb.addInput(curPlayer);
//	        gain.addInput(rb);
			
			// build audio chain
			gain.addInput(curPlayer);
			ac.out.addInput(gain);
		} else {
			// add sample direct to AudioContext output
			ac.out.addInput(curPlayer);
		}

		curPlayer.start(0);
		return curPlayer;
	}
	
	// load next sound
	
	public void loadNextSound() {
		curSampleIndex++;
		if(curSampleIndex >= samples.length) curSampleIndex = 0;
		curSample = samples[curSampleIndex];
		sampleLength = (float) curSample.getLength();
	}
	
	protected void checkLoadNewSound() {
		if(sequencesComplete - lastSequenceCountChangedSound >= sequenceCountChangeSound) {
			lastSequenceCountChangedSound = sequencesComplete;
			updateChangeSoundCount();
			if(evolves == false) loadNextSound();
		}	
	}
	
	protected void updateChangeSoundCount() {
		sequenceCountChangeSound = 20 + 4 * MathUtil.randRange(0, 6);	
	}
	
		
	/////////////////////////////////////
	// AppStore callbacks & Beat updates
	/////////////////////////////////////
	
	protected void beatChanged() {
		// update timing
		if(curStep == 0) {
			// flash wall
//			wallColor.setCurrentInt(COLOR_INT_WHITE);
			// increment sequence
			sequencesComplete++;
			// load a new sound on a random interval
			checkLoadNewSound();
			// every 8 cycles, trigger
			if(sequencesComplete % Interphase.NUM_WALLS == index) evolvePattern(false);
		}
		
		// toggle wall color multiplier
		if(curStep % 2 == 0) {
			if(curStep % 4 == 0) {
				wallColorMultiplier.setTarget(1.1f);
			} else {
				wallColorMultiplier.setTarget(0.9f);
			}
		}
		
		// set 16 step colors
		for (int i = 0; i < Interphase.NUM_STEPS; i++) {
			
			///////////////////////////////////////
			// set playhead step color blocks
			///////////////////////////////////////
			if(curStep == i) {
				stepColors[i].setCurrentInt(COLOR_INT_WHITE);		// current beat
				if(stepActive(i)) {
					stepColors[i].setTargetInt(COLOR_INT_WHITE);		// active
				} else {
					stepColors[i].setTargetInt(COLOR_INT_WHITE);		// inactive
				}
			} else if(stepActive(i)) {
				stepColors[i].setTargetInt(COLOR_INT_BLACK); 		// active step
			} else {
				stepColors[i].setTargetInt(COLOR_INT_CLEAR);			// inactive step
			}
			
			///////////////////////////////////////
			// set active step color dots
			///////////////////////////////////////
			if(curStep == i && stepActive(i)) {
				stepActiveColors[i].setTargetInt(COLOR_INT_BLACK); 		// current & active step
			} else if(stepActive(i)) {
				stepActiveColors[i].setTargetInt(ledColor); 				// active step
			} else {
				stepActiveColors[i].setTargetInt(COLOR_INT_CLEAR);		// inactive step
			}
			
			///////////////////////////////////////
			// set circle splashes progress
			///////////////////////////////////////
			if(curStep == i && stepActive(i)) {
				stepCirclesProgress[i].setCurrent(0f); 					// current & active step
				stepCirclesProgress[i].setTarget(1f); 					// current & active step
			} else if(i == queuedBeat) {
				stepCirclesProgress[i].setCurrent(0f); 					// current & queued step
				stepCirclesProgress[i].setTarget(1f); 					// current & queued step
			} else if(stepActive(i)) {
				stepCirclesProgress[i].setTarget(1f); 					// current & active step
			} else {
				stepCirclesProgress[i].setTarget(1f); 					// current & active step
			}
			
		}
		
		// override current colors if user interaction should flash the wall
//		if(queuedBeat != -1) {
//			for (int i = 0; i < stepColors.length; i++) stepColors[i].setCurrentInt(COLOR_INT_WHITE);
//		}

		
		// trigger sound! queue up so Metronome can trigger it within main audio thread/context
		if(stepActive(curStep)) {
			shouldPlay = true;
		}
		
		// if step is quantized/queued from user interaction, play that!
		if(curStep == queuedBeat) { 
			flashLEDs();
			shouldPlay = true;
			queuedBeat = -1;
		}
		
		// playhead restarted, flash LEDs
//		if(curStep == 0) flashLEDs();
	}
	
	protected void flashLEDs() {
		for (int i = 0; i < stepColors.length; i++) {
			if(stepActive(i) == false) {
//				stepColors[i].setCurrentInt(COLOR_INT_WHITE);	// flash LEDS
				stepFlashProgress[i].setCurrent(0);
				stepFlashProgress[i].setTarget(1);
			}
		}
	}

	public void updatedAppStoreValue(String key, String val) {
	}

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) {
			int newBeat = val.intValue();
			int offset = 0; // index * 2; // 8 / 16
			newBeat = (newBeat - offset + Interphase.NUM_STEPS) % Interphase.NUM_STEPS; // do loop and offset per panel
			if(newBeat != curStep) {
				curStep = newBeat;
				beatChanged();
			}
		}
	}

	public void updatedString(String key, String val) {
	}

	public void updatedBoolean(String key, Boolean val) {
	}

	public void updatedImage(String key, PImage val) {
	}

	public void updatedBuffer(String key, PGraphics val) {
	}

}
