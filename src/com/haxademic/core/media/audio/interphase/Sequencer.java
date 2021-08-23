package com.haxademic.core.media.audio.interphase;


import java.io.File;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.patterns.ISequencerPattern;
import com.haxademic.core.data.patterns.PatternUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.analysis.AudioStreamData;
import com.haxademic.core.ui.UI;

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
import processing.data.IntList;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Sequencer
implements IAppStoreListener {
	
	// app
	protected Interphase inter;
	protected boolean muted = false;
	protected SequencerConfig config;
	
	// beats & beat offset
	protected int index;							// wall index number
	protected int sequencesComplete = 0;			// keeps counting up
	protected int lastSequenceCountChangedSound = 0;
	protected int sequenceCountChangeSound = 16;
	protected int curStep = 0;						// 1-16
	protected int manualTriggerQueuedIndex = -1;	// the next beat
	
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

	// audio sample playback objects
	protected Sample curSample;
	protected int sampleIndex = 0;
	protected SamplePlayer player;
	protected SamplePlayer player2;
	protected Gain gain;
	protected boolean shouldPlay = false;
	protected String audioDir;
	protected Sample[] samples;
	protected String[] filenames;
	protected boolean useASDR = true;
	protected float triggerDelay = 0;

	// draw object
	protected ISequencerDrawable drawable;
	
	// local audio analysis
	protected AudioInputBeads audioIn;
	protected PGraphics audioInputBuffer;
	
	public Sequencer(Interphase inter, SequencerConfig config) {
		this(inter, config, false);
	}
	
	public Sequencer(Interphase inter, SequencerConfig config, boolean analyzeAudio) {
		this.config = config;
		this.index = config.index;
		this.audioDir = config.audioPath;
		this.sequencerPatterns = config.patterns;
		this.inter = inter;
		if(analyzeAudio) addAudioAnalysis();
		getAudiofiles(audioDir);
		loadNextSound();
		initStepValues();
		updateChangeSoundCount();
		P.store.addListener(this);
	}
	
	protected void addAudioAnalysis() {
		audioIn = new AudioInputBeads(Metronome.ac);
		audioInputBuffer = PG.newPG((int) AudioStreamData.debugW, (int) AudioStreamData.debugH);
		DebugView.setTexture("Audio Input " + index, audioInputBuffer);
	}
	
	public String info() {
		return 
				  "index: " + index + FileUtil.NEWLINE
				+ "curStep: " + curStep + FileUtil.NEWLINE
				+ "file: " + filenames[sampleIndex] + FileUtil.NEWLINE
				+ "sampleLength: " + sampleLength + FileUtil.NEWLINE
				+ "evolves: " + evolves + FileUtil.NEWLINE
				+ "sequencesComplete: " + sequencesComplete + FileUtil.NEWLINE
				+ "triggerCount: " + (sampleTriggerCount % 4) + FileUtil.NEWLINE
				+ "changeSound: " + (sequencesComplete - lastSequenceCountChangedSound) + "/" + sequenceCountChangeSound + FileUtil.NEWLINE
				+ "velocity: " + P.round(100f * velocity) + FileUtil.NEWLINE
				+ "length: " + P.round(sampleLength) + FileUtil.NEWLINE
				+ "attack: " + attack + FileUtil.NEWLINE
				+ "release: " + release + FileUtil.NEWLINE
				+ "playsNotes: " + config.playsNotes + FileUtil.NEWLINE
				+ "notesByStep: " + notesByStep + FileUtil.NEWLINE
				+ "noteOffset: " + noteOffset + FileUtil.NEWLINE
				+ "chordMode: " + chordMode + FileUtil.NEWLINE
				;
	}
	
	public String json() {
		// convert steps to ints
		IntList stepsList =  new IntList(steps.length);
		for (int i = 0; i < steps.length; i++) {
			stepsList.append((steps[i]) ? 1 : 0);
		}
		JSONArray dataSteps = new JSONArray(stepsList);
		JSONObject jsonConfig = new JSONObject();
		jsonConfig.setJSONArray("steps", dataSteps);
		jsonConfig.setInt("sampleIndex", sampleIndex);
		jsonConfig.setBoolean("notesByStep", notesByStep);
		jsonConfig.setInt("noteOffset", noteOffset);
		return jsonConfig.toString();
	}
	
	/////////////////////////////////////
	// Patterns
	/////////////////////////////////////
	
	protected void initStepValues() {
		steps = new boolean[Interphase.NUM_STEPS];
		for (int i = 0; i < Interphase.NUM_STEPS; i++) {
			steps[i] = false;
		}
		evolvePattern();
	}
	
	protected void newRandomPattern() {
		curPatternGeneratorIndex = MathUtil.randRange(0, sequencerPatterns.length - 1);
		sequencerPatterns[curPatternGeneratorIndex].newPattern(steps);
		PatternUtil.ensureOneStepActive(steps);
	}
	
	/////////////////////////////////////
	// GETTERS / SETTERS
	/////////////////////////////////////
	
	public boolean muted() {
		return muted;
	}
	
	protected boolean stepActive(int i) {
		return steps[i];
	}
	
	protected void stepActive(int i, boolean active) {
		steps[i] = active;
	}
	
	public Sequencer setPatternByInts(int[] pattern) {
		for (int i = 0; i < steps.length; i++) {
			steps[i] = (pattern[i] == 1);
		}
		return this;
	}
	
	public boolean shouldPlay() {
		return shouldPlay;
	}
	
	public boolean toggleEvloves() {
		evolves = !evolves;
		return evolves;
	}
	
	public boolean setEvolves(boolean doesEvolve) {
		return evolves = doesEvolve;
	}
	
	public int noteOffset() {
		return noteOffset;
	}
	
	public Sequencer noteOffset(int noteOffset) {
		this.noteOffset = noteOffset;
		return this;
	}
	
	public float attack() { return attack; }
	public Sequencer attack(float attack) { this.attack = attack; return this; } 
	public float release() { return release; }
	public Sequencer release(float release) { this.release = release; return this; }
	public int pitchIndex1() { return pitchIndex1; }
	
	public boolean notesByStep() {
		return notesByStep;
	}
	
	public Sequencer notesByStep(boolean notesByStep) {
		this.notesByStep = notesByStep;
		return this;
	}
	
	public int numSamples() {
		return filenames.length;
	}
	
	public Sequencer setSampleByIndex(int index) {
		sampleIndex = index;
		curSample = samples[sampleIndex];
		return this;
	}
	
	public int sampleIndex() {
		return sampleIndex;
	}
	
	public String stepsListString() {
		String stepsString = "";
		for (int i = 0; i < steps.length; i++) {
			stepsString += (steps[i]) ? 1 : 0;
			if(i < steps.length - 1) stepsString += ",";
		}
		return stepsString;
	}
	
	public void setSampleByPath(String samplePath) {
		curSample = SampleManager.sample(samplePath); // samples[curSampleIndex];
		// check if exists in current sample collection
		// otherwise, note that we loaded outside of collection
	}
	
	public String fileNameForPath(String samplePath) {
		File file = new File(samplePath);
		String simpleFileName = file.getName();
		return simpleFileName;
	}
	
	public boolean userInteracted() {
		return P.p.millis() < manualTriggerTime + Interphase.TRIGGER_TIMEOUT;
	}
	
	public void update() {
		if(audioIn != null) {
			if(audioInputBuffer != null && UI.active()) {
				audioInputBuffer.beginDraw();
				audioInputBuffer.background(0);
				audioIn.update(audioInputBuffer);
				audioInputBuffer.endDraw();
			} else {
				audioIn.update(null);
			}
		}
		if(drawable != null) drawable.update(steps, curStep); 
	}
	
	public void setDrawable(ISequencerDrawable drawable) {
		this.drawable = drawable;
	}
	
	public ISequencerDrawable getDrawable() {
		return drawable;
	}
	
	/////////////////////////////////////
	// INPUT
	/////////////////////////////////////
	
	public void toggleMute() {
		muted = !muted;
	}
	
	public void setMute(boolean muted) {
		this.muted = muted;
	}
	
	public void triggerSample() {
		// keep track of manual input time
//		if(isManual) {
			manualTriggerTime = P.p.millis();
			sampleTriggerCount++;
			// queue up for manual jamming
			manualTriggerQueuedIndex = (curStep + 1) % Interphase.NUM_STEPS;
//		} else {
//			// if user interaction timed out, advance trigger count for slower pattern switching & morphing below
//			if(userInteracted() == false) {
//				sampleTriggerCount++;
//			}
//		}
		
	}
	
	/////////////////////////////////////
	// EVOLVE
	/////////////////////////////////////
	
	public void evolvePattern() {
		if(!P.store.getBoolean(Interphase.GLOBAL_PATTERNS_EVLOVE)) return;
		if(evolves == true) {
			// every 4 sample triggers, make a bigger evoloving change
			// new pattern, note & note props
			if(sampleTriggerCount % 4 == 0) {
				newRandomPattern();
				
				// change note scheme
				noteOffset = MathUtil.randRange(0, Interphase.NUM_STEPS - 1);
				notesByStep = MathUtil.randBooleanWeighted(0.7f);
				upOctave = MathUtil.randBooleanWeighted(0.2f) && config.playsOctaveNotes;	// don't octave on keys
				chordMode = (config.playsChords && MathUtil.randBooleanWeighted(0.5f));
				
				// change attack
				if(MathUtil.randBooleanWeighted(0.2f) && config.hasAttack) {
					attack = MathUtil.randRange(30, 150);
				} else {
					attack = 0;
				}
				
				// change release
				if(MathUtil.randBooleanWeighted(0.2f) && config.hasRelease) {
					float loopInterval = Metronome.bpmToIntervalMS(P.store.getInt(Interphase.BPM));
					release = MathUtil.randRange(200, loopInterval);
				} else {
					release = 0;
				}
			} else {
				// every sample trigger, slightly change sequence
				if(MathUtil.randBoolean()) {
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
			filenames[i] = fileNameForPath(files.get(i));
			// filenames[i] = this.audioDir + File.separator + simpleFileName;
			// P.println("loading:", filenames[i]);
		}
		
		sampleIndex = MathUtil.randRange(0, samples.length - 1);
		curSample = samples[sampleIndex];
	}
	
	public void playSample() {
		if(curSample == null) return;
		if(muted) return;
		
		// get scale/pitch
		selectNewNote();
		
		// recreate SamplePlayer objects
		player = playSampleWithNote(player, pitchRatioFromIndex(pitchIndex1));
		if(chordMode) player2 = playSampleWithNote(player2, pitchRatioFromIndex(pitchIndex2));
		
		// let the app know
		P.store.setNumber(Interphase.SEQUENCER_TRIGGER, index);
					
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
				pitchIndex1 = Scales.CUR_SCALE[(noteOffset + curStep) % Scales.CUR_SCALE.length];
				int secondPitch = (MathUtil.randBoolean()) ? 5 : 7;	// 4ths & 5ths - thank you David Last :-D 
				pitchIndex2 = Scales.CUR_SCALE[(noteOffset + curStep + secondPitch) % Scales.CUR_SCALE.length];
			} else {
				// random pitch
				int randPitch = MathUtil.randRange(0, Scales.CUR_SCALE.length - 1);
				int randPitch2 = randPitch + 2;
				pitchIndex1 = Scales.CUR_SCALE[randPitch];
				pitchIndex2 = Scales.CUR_SCALE[randPitch2 % Scales.CUR_SCALE.length];
			}
			
			// sometimes pitch up an octave 
			if(upOctave && MathUtil.randBooleanWeighted(0.2f)) pitchIndex1 += 12;
		}

	}
	
	protected float pitchRatioFromIndex(int pitchIndex) {
		return P.pow(2, pitchIndex/12.0f);
	}
	
	protected SamplePlayer playSampleWithNote(SamplePlayer curPlayer, float pitchRatio) {
		// stop playing previous sample instance 
		if(curPlayer != null) curPlayer.kill();	

		// re-init sample player
		AudioContext ac = Metronome.ac;
		curPlayer = new SamplePlayer(ac, curSample);
		curPlayer.setKillOnEnd(false);
		
		// set pitch on all sequencers that play notes
		if(config.playsNotes) {
			// change pitch
			Glide glide = new Glide(ac, pitchRatio);
			curPlayer.setRate(glide);

			// reverse
			//			if(MathUtil.randBooleanWeighted(0.2f)) {
			//				curPlayer.setToEnd();
			//				glide.setValue(-1.0f);
			//				curPlayer.start();
			//			}
		}

		// apply attack/sustain
		if(useASDR) {
			// responsive release 
			float loopInterval = Metronome.bpmToIntervalMS(P.store.getInt(Interphase.BPM));
			MAX_SAMPLE_LENGTH = (int) loopInterval;
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
		
		// set gain on audio input analyzer
		// todo: switch between gain or curPlayer, to take into account asdr
		if(audioIn != null) audioIn.addInput(curPlayer);

		// add some swing... move this!
		int delay = (index == 2 || index == 5) ? MathUtil.randRange(-10, 2) : 0;
		
		// play!
		curPlayer.start(delay);
		return curPlayer;
	}
	
	// load next sound
	
	public void loadNextSound() {
		sampleIndex++;
		if(sampleIndex >= samples.length) sampleIndex = 0;
		curSample = samples[sampleIndex];
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
	
	protected void interphaseBeatChanged() {
		// update timing
		if(curStep == 0) {
			// increment sequence
			sequencesComplete++;
			// load a new sound on a random interval
			checkLoadNewSound();
			// every 8 cycles, trigger
			if(P.round(sequencesComplete) % Interphase.NUM_CHANNELS == index) evolvePattern();
		}
		
		// trigger sound! queue up so Metronome can trigger it within main audio thread/context
		if(stepActive(curStep)) {
			shouldPlay = true;
		}
		
		// if step is quantized/queued from user interaction, play that!
		if(curStep == manualTriggerQueuedIndex) { 
			shouldPlay = true;
			manualTriggerQueuedIndex = -1;
		}
		
		// playhead restarted, flash LEDs
//		if(curStep == 0) flashLEDs();
		
		if(shouldPlay) {
			playSample();
		}
	}
	
	public void updatedAppStoreValue(String key, String val) {
	}

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) {
			int newBeat = val.intValue();
			int offset = 0; // index * 2; // 8 / 16
//			offset = index * 4;
			newBeat = (newBeat - offset + Interphase.NUM_STEPS) % Interphase.NUM_STEPS; // do loop and offset per panel
			if(newBeat != curStep) {
				curStep = newBeat;
				interphaseBeatChanged();
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
