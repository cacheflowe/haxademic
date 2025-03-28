package com.haxademic.core.media.audio.interphase;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.patterns.ISequencerPattern;
import com.haxademic.core.data.patterns.PatternUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.FloatBuffer;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.media.audio.analysis.AudioInputBeads;
import com.haxademic.core.media.audio.interphase.draw.ISequencerDrawable;
import com.haxademic.core.media.audio.playback.WavPlayer;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.system.SystemUtil;

import beads.AudioContext;
import beads.Compressor;
import beads.Envelope;
import beads.Gain;
import beads.Glide;
import beads.KillTrigger;
import beads.Reverb;
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
	protected boolean muted = false;
	protected SequencerConfig config;
	
	// beats & beat offset
	protected int index; // sequencer index
	protected int sequencesComplete = 0;			// keeps counting up
	protected int lastSequenceCountChangedSound = 0;
	protected int sequenceCountChangeSound = 16;
	protected int curStep = 0;						// 1-16
	// protected int manualTriggerQueuedIndex = -1;	// the next beat
	
	// step sequencer
	protected boolean steps[];
	protected ISequencerPattern[] sequencerPatterns;
	protected int curPatternGeneratorIndex = 0;
	
	// note selection
	protected float pitchShift = 0;
	protected int pitchIndex1 = 0;
	protected int pitchIndex2 = 0;
	protected boolean notesByStep = true;
	protected boolean upOctave = false;
	protected int noteOffset = 0;
	protected boolean chordMode = false;
	
	// trigger updates 
	protected int manualTriggerTime = 0;
	protected int sampleTriggerCount = 0;
	protected boolean evolves = false;
	protected int evolveCount = 0;
	
	// audio effects
	protected float sampleLength = 0;
	protected float attack = 0;
	protected float release = 650;
	protected int MAX_SAMPLE_LENGTH = 400;
	protected float velocity = 1;
	protected float reverbSize = 0;
	protected float reverbDamping = 0;

	// audio sample playback objects
	protected Sample curSample;
	protected int sampleIndex = 0;
	protected String sampleFileName = "";
	protected SamplePlayer player;
	protected SamplePlayer player2;
	protected Gain gain;
	protected boolean shouldPlay = false;
	protected boolean manuallyTriggered = false;
	protected String audioDir;
	protected Sample[] samples;
	protected String[] filenames;
	protected boolean useASDR = true;
	protected boolean sampleIsLoop;
	protected int loopDivisor;
	
	// draw waveform for current sample
	protected PGraphics sampleWaveformPG;
	protected boolean sampleWaveformDirty = false;

	// draw object
	public static int TRIGGER_DELAY = 130; // helps with syncing visual triggers with audio sample start time
	protected ISequencerDrawable drawable;
	
	// local audio analysis
	protected AudioInputBeads audioIn;
	protected boolean hasAudioTextures = false;
	protected LinearFloat triggerFalloff = new LinearFloat(0, 0.05f);
	protected FloatBuffer ampSmoothed = new FloatBuffer(8);
	
	public Sequencer(SequencerConfig config) {
		this.config = config;
		this.index = config.index;
		this.audioDir = config.audioPath;
		this.sequencerPatterns = config.patterns;
		getAudiofiles(audioDir);
		buildWaveformBuffer();
		loadNextSound();
		initStepValues();
		updateChangeSoundCount();
		P.store.addListener(this);
	}
	
	public void addAudioAnalysis() {
		audioIn = new AudioInputBeads(Metronome.ac);
	}
	
	public void addAudioTextures() {
		hasAudioTextures = true;
	}
	
	public String info() {
		String nl = FileUtil.NEWLINE;
		return 
				"index: " + index 
				+ nl + "SEQUENCE ------------------- " 
				+ nl + "curStep: " + curStep 
				+ nl + "sequencesComplete: " + sequencesComplete 
				+ nl + "triggerCount: " + (sampleTriggerCount % 4) 
				+ nl + "velocity: " + P.round(100f * velocity) 
				+ nl
				+ nl + "EVOLVE ------------------- " 
				+ nl + "evolves: " + evolves 
				+ nl + "changeSound: " + (sequencesComplete - lastSequenceCountChangedSound) + "/" + sequenceCountChangeSound 
				+ nl
				+ nl + "SAMPLE ------------------- " 
				// + nl + "file: " + filenames[sampleIndex] 
				+ nl + "file: " + sampleFileName
				+ nl + "sampleIndex: " + sampleIndex
				+ nl + "sampleLength: " + sampleLength 
				+ nl
				+ nl + "PLAYBACK ------------------- " 
				+ nl + "sampleProgress: " + sampleProgress() 
				+ nl + "volume: " + volume() 
				+ nl + "length: " + P.round(sampleLength) 
				+ nl + "attack: " + attack 
				+ nl + "release: " + release 
				+ nl + "reverb: " + reverbSize + "/" + reverbDamping
				+ nl
				+ nl + "NOTES ------------------- " 
				+ nl + "playsNotes: " + config.playsNotes 
				+ nl + "pitchShift: " + pitchShift 
				+ nl + "pitchIndex1: " + pitchIndex1 
				+ nl + "pitchIndex2: " + pitchIndex2 
				+ nl + "notesByStep: " + notesByStep 
				+ nl + "noteOffset: " + noteOffset 
				+ nl + "chordMode: " + chordMode 
				;
	}
	
	public JSONObject json() {
		// convert steps to ints array
		IntList stepsList =  new IntList(steps.length);
		for (int i = 0; i < steps.length; i++) {
			stepsList.append((steps[i]) ? 1 : 0);
		}
		JSONArray dataSteps = new JSONArray(stepsList);
		
		// build main json object
		JSONObject jsonConfig = new JSONObject();
		jsonConfig.setJSONArray("steps", dataSteps);
		jsonConfig.setInt("sampleIndex", sampleIndex);
		jsonConfig.setString("samplePath", curSample.getFileName());
		jsonConfig.setBoolean("notesByStep", notesByStep);
		jsonConfig.setInt("noteOffset", noteOffset);
		jsonConfig.setFloat("volume", config.volume);

		return jsonConfig;
	}
	
	public String jsonString() {
		return json().toString();
	}
	
	public void load(String jsonString) {
		load(JsonUtil.jsonFromString(jsonString));
	}
	
	public void load(JSONObject json) {
		// get data from JSON
		// setSampleByIndex(json.getInt("sampleIndex", 0));
		setSampleByPath(json.getString("samplePath", ""));
		noteOffset = json.getInt("noteOffset", 0);
		notesByStep = json.getBoolean("notesByStep", false);
		volume(json.getFloat("volume", 1));
		
		// set local objects
		JSONArray dataSteps = json.getJSONArray("steps");
		for (int i = 0; i < steps.length; i++) {
			steps[i] = dataSteps.getInt(i) == 1;
		}
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
	
	public void newRandomPattern() {
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
	
	public boolean stepActive(int i) {
		return steps[i];
	}
	
	public void stepActive(int i, boolean active) {
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
	
	public boolean manuallyTriggered() {
		return manuallyTriggered;
	}
	
	public int noteOffset() {
		return noteOffset;
	}
	
	public Sequencer noteOffset(int noteOffset) {
		this.noteOffset = noteOffset;
		return this;
	}
	
	public boolean evolves() { return this.evolves; }
	public Sequencer evolves(boolean evolves) { this.evolves = evolves; if(!evolves) { resetRandomizedProps(); } return this; } 
	public float volume() { return config.volume; }
	public Sequencer volume(float volume) { config.volume = volume; return this; } 
	public float attack() { return attack; }
	public Sequencer attack(float attack) { this.attack = attack; return this; } 
	public float release() { return release; }
	public Sequencer release(float release) { this.release = release; return this; }
	public float reverbSize() { return reverbSize; }
	public float reverbDamping() { return reverbDamping; }
	public Sequencer reverb(float reverbSize, float reverbDamping) { this.reverbSize = reverbSize; this.reverbDamping = reverbDamping; return this; }
	public int pitchIndex1() { return pitchIndex1; }
	public float pitchShift() { return pitchShift; }
	public Sequencer pitchShift(float pitchShift) { this.pitchShift = pitchShift; return this; }

	// audio data getters
	public float audioAmp() { return audioIn.audioData().amp(); }
	public PGraphics bufferWaveForm() { return audioIn.audioData().bufferWaveform; }
	public PGraphics bufferFFT() { return audioIn.audioData().bufferFFT; }
	public float triggerFalloff() { return triggerFalloff.value(); }
	public float ampSmoothed() { return ampSmoothed.averageOldHalf(); }

	
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
	
	public int sampleIndex() {
		return sampleIndex;
	}
	
	public float sampleProgress() {
		return WavPlayer.progress(player);
	}
	
	public PImage sampleWaveformPG() {
		return sampleWaveformPG;
	}
	
	public String stepsListString() {
		String stepsString = "";
		for (int i = 0; i < steps.length; i++) {
			stepsString += (steps[i]) ? 1 : 0;
			if(i < steps.length - 1) stepsString += ",";
		}
		return stepsString;
	}
	
	public Sequencer setSampleByIndex(int newIndex) {
		int safeIndex = newIndex % samples.length;
		if(newIndex != sampleIndex) P.store.setNumber(Interphase.SAMPLE_CHANGED, this.index);
		sampleIndex = newIndex;
		setSample(samples[safeIndex]); // safe access
		return this;
	}
	
	public void setSampleByPath(String samplePath) {
		for (int i = 0; i < samples.length; i++) {
			if(samplePath.equals(samples[i].getFileName())) {
				setSample(samples[i]); 
				sampleIndex = indexForSample(samplePath);
				return;
			}
		}
		// if none found...
		P.out("Index not found for file: " + samplePath + " in sequencers[" + index + "]");
		sampleIndex = 0;
	}
	
	public int indexForSample(String samplePath) {
		for (int i = 0; i < samples.length; i++) {
			String filename = samples[i].getFileName();
			if(filename.equals(samplePath)) return i;
		}
		return 0;
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
		updateSampleWaveform();
		updateAudioInput();
		triggerFalloff.update();
		triggerFalloff.setInc(0.1f);
		if(drawable != null) drawable.update(steps, curStep);
	}

	protected void updateSampleWaveform() {
		if (sampleWaveformDirty) {
			sampleWaveformDirty = false;
			WavPlayer.drawWav(sampleWaveformPG, curSample);
		}
	}

	protected void updateAudioInput() {
		if (audioIn == null) return;

		// update audio analysis: fft, waveform
		audioIn.update();

		// update audio buffers if we've set them active
		updateAudioAnalysisBuffer();
		updateAudioInputDebugBuffer();
		ampSmoothed.update(audioAmp());
	}
	
	protected void updateAudioAnalysisBuffer() {
		if (hasAudioTextures) {
			audioIn.drawDataBuffers();
			DebugView.setTexture("Audio FFT " + index, audioIn.audioData().bufferFFT);
			DebugView.setTexture("Audio Waveform " + index, audioIn.audioData().bufferWaveform);
		}
	}

	protected void updateAudioInputDebugBuffer() {
		if (DebugView.active()) {
			// draw audio debug view if debug panel is open
			audioIn.drawDebugBuffer();
			DebugView.setTexture("Audio Input " + index, audioIn.debugBuffer());
		}
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
		shouldPlay = false;
		manuallyTriggered = false;
	}
	
	public void setMute(boolean muted) {
		this.muted = muted;
	}
	
	public void triggerSample() {
		// keep track of manual input time
		manualTriggerTime = P.p.millis();
		sampleTriggerCount++;
		DebugView.setValue("sampleTriggerCount", sampleTriggerCount);
		// queue up for manual jamming
		// manualTriggerQueuedIndex = (curStep + 1) % Interphase.NUM_STEPS;
		shouldPlay = true;
		manuallyTriggered = true;
	}
	
	/////////////////////////////////////
	// EVOLVE
	/////////////////////////////////////
	
	public void evolvePattern() {
		// every 4 sample triggers, make a bigger evolving change
		// new pattern, note & note props
		// otherwise, do a small sequence pattern evolution
		// if(shouldEvolve == true) {
			if(evolveCount % 4 == 0) {
				newRandomPattern();
				newRandomNoteScheme();
				newRandomAttack();
				newRandomRelease();
			} else {
				evolvePatternSmall();
			}
			evolveCount++;
		// }
	}

	protected void evolvePatternSmall() {
		if(MathUtil.randBoolean()) {
			PatternUtil.nudgePatternForward(steps);
		} else {
			PatternUtil.morphPattern(steps);
		}
	}

	public void nudgePattern() {
		PatternUtil.nudgePatternForward(steps);
	}

	protected void newRandomNoteScheme() {
		noteOffset = MathUtil.randRange(0, Interphase.NUM_STEPS - 1);
		notesByStep = (index == 5) ? false : MathUtil.randBooleanWeighted(0.7f);
		P.out("notesByStep", notesByStep);
		upOctave = MathUtil.randBooleanWeighted(0.2f) && config.playsOctaveNotes; // don't octave on keys
		chordMode = (config.playsChords && MathUtil.randBooleanWeighted(0.5f));
	}

	protected void newRandomAttack() {
		if (MathUtil.randBooleanWeighted(0.2f) && config.hasAttack) {
			attack = MathUtil.randRange(30, 150);
		} else {
			attack = 0;
		}
	}

	protected void newRandomRelease() {
		if (MathUtil.randBooleanWeighted(0.2f) && config.hasRelease) {
			float loopInterval = Metronome.bpmToIntervalMS(P.store.getInt(Interphase.BPM));
			release = MathUtil.randRange(200, loopInterval);
		} else {
			release = 0;
		}
	}

	protected void resetRandomizedProps() {
		notesByStep = true;
		attack = 0;
		release = 0;
	}

	// go next sound / evolve sound

	public void loadNextSound() {
		sampleIndex++;
		if(sampleIndex >= samples.length) sampleIndex = 0;
		setSample(samples[sampleIndex]);
		sampleLength = (float) curSample.getLength();
	}
	
	protected void checkLoadNewSound() {
		if(!evolves) return;
		sequencesComplete++;
		if(sequencesComplete - lastSequenceCountChangedSound >= sequenceCountChangeSound) {
			lastSequenceCountChangedSound = sequencesComplete;
			updateChangeSoundCount();
			if(evolves == true) loadNextSound();
		}	
	}
	
	protected void updateChangeSoundCount() {
		sequenceCountChangeSound = Interphase.NUM_STEPS + 4 * MathUtil.randRange(0, 4);	
	}

	
	/////////////////////////////////////
	// AUDIO
	/////////////////////////////////////
	
	protected void buildWaveformBuffer() {
		sampleWaveformPG = PG.newPG2DFast(256, 32);
		DebugView.setTexture("Sequencer.waveformPG_"+index, sampleWaveformPG);
	}
	
	protected void getAudiofiles(String audioDir) {
		String absAudioPath = audioDir;// FileUtil.getFile(audioDir);
		P.out(absAudioPath);
		ArrayList<String> files = FileUtil.getFilesInDirOfTypes(absAudioPath, "wav,aif,mp3");
		samples = new Sample[files.size()];
		filenames = new String[files.size()];
		for (int i = 0; i < files.size(); i++) {
			samples[i] = SampleManager.sample(files.get(i));
			filenames[i] = fileNameForPath(files.get(i));
		}
		
		sampleIndex = MathUtil.randRange(0, samples.length - 1);
		setSample(samples[sampleIndex]);
	}
	
	protected void setSample(Sample newSample) {
		curSample = newSample;
		sampleFileName = FileUtil.fileNameFromPath(curSample.getFileName());
		DebugView.setValue("Sequencer.curSample_" + index, sampleFileName);
		sampleWaveformDirty = true;
		
		// add looping and get loop divisor from filename
		sampleIsLoop = sampleFileName.contains("_loop_");
		if(sampleIsLoop) {
			loopDivisor = 4;
			String[] parts = sampleFileName.split("_loop_");
			if(parts.length > 1) {
				String[] loopParts = parts[1].split("_");
				if(loopParts.length > 0) {
					loopDivisor = ConvertUtil.stringToInt(loopParts[0]);
				}
			}
		}
	}
	
	/////////////////////////////////////
	// PLAY SOUND
	/////////////////////////////////////

	protected void checkPlaySample() {
		if(!shouldPlay) return;
		if(curSample == null) return;
		if(muted && manuallyTriggered == false) return; // allow manually triggering even when channel is muted. good for jamming
		
		// get scale/pitch, play sound, send event out
		selectNewNote();
		playSound();
		sendTriggerEvent();
		shouldPlay = false;
		manuallyTriggered = false;
	}

	protected void sendTriggerEvent() {
		P.store.setNumber(Interphase.SEQUENCER_TRIGGER, index);
		if(TRIGGER_DELAY == 0) {
			triggerVisualHit();
		} else {
			SystemUtil.setTimeout(new ActionListener() { public void actionPerformed(ActionEvent e) {
				triggerVisualHit();
			}}, TRIGGER_DELAY);
		}
	}

	protected void triggerVisualHit() {
		P.store.setNumber(Interphase.SEQUENCER_TRIGGER_VISUAL, index);
		triggerFalloff.setCurrent(1).setTarget(0);
	}
	
	protected void selectNewNote() {
		if(config.playsNotes == false) {
			// if we don't play notes, use 0
			pitchIndex1 = pitchIndex2 = 0;	
		} else {			
			if(notesByStep) {
				// if notes by step mode, use cur step + offset to cycle through current scale notes 
				pitchIndex1 = Scales.CUR_SCALE[(noteOffset + curStep) % Scales.CUR_SCALE.length];
				// if(index == 5) P.out(pitchIndex1);
				int secondPitch = (MathUtil.randBoolean()) ? 5 : 7;	// 4ths & 5ths - thank you David Last :-D 
				pitchIndex2 = Scales.CUR_SCALE[(noteOffset + curStep + secondPitch) % Scales.CUR_SCALE.length];
			} else {
				// random pitch
				int randPitch = MathUtil.randRange(0, Scales.CUR_SCALE.length - 1);
				// if(index == 5) P.out(randPitch);
				int randPitch2 = randPitch + 2;
				pitchIndex1 = Scales.CUR_SCALE[randPitch];
				pitchIndex2 = Scales.CUR_SCALE[randPitch2 % Scales.CUR_SCALE.length];
			}
			
			// sometimes pitch up an octave 
			if(upOctave && MathUtil.randBooleanWeighted(0.2f)) pitchIndex1 += 12;
		}
	}
	
	protected float pitchRatioFromIndex(int pitchIndex) {
		float alteredPitch = pitchIndex + (pitchShift * 12f);	// pitch bending! 
		return P.pow(2, alteredPitch/12.0f);
	}

	protected void playSound() {
		player = playSampleWithNote(player, pitchRatioFromIndex(pitchIndex1));
		if (chordMode) {
			player2 = playSampleWithNote(player2, pitchRatioFromIndex(pitchIndex2));
		}
	}
	
	protected SamplePlayer playSampleWithNote(SamplePlayer curPlayer, float pitchRatio) {
		// stop playing previous sample instance 
		if(curPlayer != null) curPlayer.kill();	

		// re-init sample player
		AudioContext ac = Metronome.ac;
		curPlayer = new SamplePlayer(ac, curSample);
		curPlayer.setKillOnEnd(true);
//		curPlayer.pause(true);
		
		// set gain on audio input analyzer
		// todo: switch between gain or curPlayer, to take into account asdr
		if(audioIn != null) audioIn.addInput(curPlayer);
		
		// set pitch on all sequencers that play notes
//		if(config.playsNotes) {
			// change pitch
			if(sampleIsLoop) {
				// calcs from WavPlayer.pitchRatioFromIndex()
				float pitchIndex = Metronome.shiftPitchToMatchBpm(curPlayer, P.store.getInt(Interphase.BPM), loopDivisor);
				pitchRatio = WavPlayer.pitchRatioFromIndex(pitchIndex);
				useASDR = false;

				// randomize start position (sometimes)
				// TODO: add more playback modes for loops
				// if loop is attached to snare track, take kicks into account like the original demo
				if(MathUtil.randBooleanWeighted(0.2f) || manuallyTriggered) {
					// WavPlayer.seekToProgress(curPlayer, MathUtil.randRange(0, 3) * 0.25f);	
					WavPlayer.seekToProgress(curPlayer, MathUtil.randRange(0, loopDivisor) * 1f / loopDivisor);	
				}
				useASDR = false;
			} else {
				useASDR = true;
			}
			Glide glide = new Glide(ac, pitchRatio);
			// apply pitch shift to player
			curPlayer.setRate(glide);


			// reverse
			//			if(MathUtil.randBooleanWeighted(0.2f)) {
			//				curPlayer.setToEnd();
			//				glide.setValue(-1.0f);
			//				curPlayer.start();
			//			}
//		}

		// AudioContext ac = Metronome.ac;
		// // add some swing... move this!
		//// int delay = 0; // (index == 2 || index == 5) ? MathUtil.randRange(-10, 2) :
		// 0;
		// int delay = (index == 2 || index == 5) ? MathUtil.randRange(0, 20) : 0;
		// // play it, with delay or without
		// Bead myBead = new Bead() {
		// protected void messageReceived(Bead b) {
		// }
		// };
		//
		// DelayTrigger dt = new DelayTrigger(ac, delay, myBead, null);
		// ac.out.addDependent(dt);

		// apply attack/sustain
		if(useASDR) {
			// responsive release 
			float loopInterval = Metronome.bpmToIntervalMS(P.store.getInt(Interphase.BPM));
			MAX_SAMPLE_LENGTH = (int) loopInterval * 3; // but this doesn't make much sense, does it??
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
				DebugView.setValue("release", release);
				ampEnv.addSegment(endGain, P.min(sampleLength, MAX_SAMPLE_LENGTH), new KillTrigger(gain));	// no attack or release, but using Gain for volume. set killtrigger at end of sample. 
			}
		} else {
			gain = new Gain(ac, 2, config.volume); // standard pass-thru volume/gain
			curPlayer.setKillListener(new KillTrigger(gain));
		}
			
		// got reverb?
		boolean hasReverb = reverbSize > 0.1f;
		Reverb rb = null;
		Reverb rb2 = null;
		hasReverb = false;
		if(hasReverb) {
			// P.out("reverbSize", reverbSize);
			rb = new Reverb(ac, 2);
			rb.setSize(reverbSize);
			rb.setDamping(reverbDamping);
			rb.setValue(reverbSize);

			rb2 = new Reverb(ac, 2);
			rb2.setSize(reverbSize * 4f);
			rb2.setDamping(reverbDamping * 2.5f);
			rb2.setValue(reverbSize * 4f);
			// rb2.setLateReverbLevel(reverbSize * 0.001f);
			// rb2.setLateReverbLevel(reverbSize * 0.001f);
			rb2.setEarlyReflectionsLevel(reverbSize * 0.001f);
		}
		
		Compressor comp = null;
//			Compressor comp = new Compressor(ac, 2);
//			comp.setAttack(10);
//			comp.setDecay(100);
//			comp.setThreshold(0.75f);
//			comp.setRatio(7f);
//			comp.setKnee(3f);
		
		// build audio chain
				// any effects like reverb must be inserted before the gain in the audio chain ->
				// otherwise they won't get cleaned up?!
		if(hasReverb) {
			if(comp != null) {
				comp.addInput(curPlayer);
				rb.addInput(comp);
			} else {
				rb.addInput(curPlayer);
				rb2.addInput(curPlayer);
			}
			// mic both reverb and dry sample
			gain.addInput(rb);
			gain.addInput(rb2);
			gain.addInput(curPlayer);
		} else {
			gain.addInput(curPlayer);
		}
		ac.out.addInput(gain);


		// overall volume adjustment. we were red-lining
		// this should probably be elsewhere
		gain.setGain(gain.getGain() * 0.35f);
		
		// } else {
		// 	// add sample direct to AudioContext output
		// 	ac.out.addInput(curPlayer);
		// }
		
		// send player object back
		return curPlayer;
	}
	
		
	/////////////////////////////////////
	// AppStore callbacks & Beat updates
	/////////////////////////////////////
	
protected void checkBeatChanged(int newBeat) {
	int offset = 0; // index * 2; // 8 / 16
	// offset = index * 4;
	newBeat = (newBeat - offset + Interphase.NUM_STEPS) % Interphase.NUM_STEPS;
	if (newBeat != curStep) {
		curStep = newBeat;
		interphaseBeatChanged();
	}
}

	protected void interphaseBeatChanged() {
		// update timing
		if(curStep == 0) {
			checkLoadNewSound();
			{ // evolve on frist step?
				// next channel every cycle around the sequencer?
				// boolean shouldEvolve = P.round(sequencesComplete) % Interphase.NUM_CHANNELS == index;
				// or spaced out further:
				boolean shouldEvolve = P.round(sequencesComplete) % (Interphase.NUM_CHANNELS * 2) == index * 2;
				if(evolves && shouldEvolve) evolvePattern();
			}
		}
		checkActiveStepToTrigger();
		// checkManualTrigger();
		checkPlaySample();
	}

	protected void checkActiveStepToTrigger() {
		// trigger sound if this step is active!
		// queue up so Metronome can trigger it within main audio thread/context
		if (stepActive(curStep)) {
			shouldPlay = true;
		}
	}
	
	/*
	protected void checkManualTrigger() {
		// if step is quantized/queued from user interaction, play that!
		if(curStep == manualTriggerQueuedIndex) { 
			shouldPlay = true;
			manuallyTriggered = true;
			manualTriggerQueuedIndex = -1;
		}
	}
	*/
	
	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) checkBeatChanged(val.intValue());
	}

	public void updatedString(String key, String val) {}

	public void updatedBoolean(String key, Boolean val) {
		if(key.equals(Interphase.GLOBAL_PATTERNS_EVLOVE)) evolves(val);
	}

	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
