package com.haxademic.core.media.audio.interphase;


import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPad.ILaunchpadCallback;
//import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPadMini;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.IUIControl;
import com.haxademic.core.ui.UI;

import beads.AudioContext;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import themidibus.MidiBus;

public class Interphase
implements IAppStoreListener, ILaunchpadCallback {
	
	////////////////////////////////////////
	
	// sizes
	
	public static int NUM_CHANNELS = 8;
	public static final int NUM_STEPS = 16;
	
	// events
	
	public static final String BEAT = "BEAT";
	public static final String CUR_STEP = "CUR_STEP";
	public static final String BPM = "BPM";
	public static final String SEQUENCER_TRIGGER_VISUAL = "SEQUENCER_TRIGGER";
	public static final String SEQUENCER_TRIGGER = "SEQUENCER_TRIGGER_IMMEDIATE";

	// state
	
	public static final String CUR_SCALE_INDEX = "CUR_SCALE_INDEX";
	public static final String GLOBAL_PATTERNS_EVLOVE = "GLOBAL_PATTERNS_EVLOVE";
	public static final String SYSTEM_MUTED = "SYSTEM_MUTED";

	// input 
	
	public static final float TEMPO_EASE_FACTOR = 1.5f;
	public static boolean TEMPO_MOUSE_CONTROL = false;
	public static boolean TEMPO_MIDI_CONTROL = true;
	public static final int TRIGGER_TIMEOUT = 45000;
	public static final String INTERACTION_SPEED_MULT = "INTERACTION_SPEED_MULT";
	
	// ui
	
	public static final String UI_GLOBAL_BPM = "UI_GLOBAL_BPM";
	public static final String UI_GLOBAL_EVOLVES = "UI_GLOBAL_EVOLVES";
	public static final String UI_CUR_SCALE = "UI_CUR_SCALE";
	public static final String UI_SAMPLE_ = "UI_SAMPLE_";
	public static final String UI_VOLUME_ = "UI_VOLUME_";
	public static final String UI_PITCH_ = "UI_PITCH_";
	
	public static final String UI_REVERB_ = "UI_REVERB_";
    public static final String UI_MUTE_ = "UI_MUTE_";
    public static final String UI_TRIGGER_ = "UI_MUTE_";

	
	//////////////////////////////////////////
	
	protected Scales scales;
	public Sequencer sequencers[];
	protected Metronome metronome;
	
	protected String SEQUENCES_PATH = "text/json/interphase/sequences/";
	protected ArrayList<String> configFiles = new ArrayList<String>();
	protected int curConfigIndex = 0;
	
	protected boolean hasUI = false;
	
//	protected InputTrigger trigger1 = new InputTrigger().addKeyCodes(new char[]{'1'}).addMidiNotes(new Integer[]{104, 41});
//	protected InputTrigger trigger2 = new InputTrigger().addKeyCodes(new char[]{'2'}).addMidiNotes(new Integer[]{105, 42});
//	protected InputTrigger trigger3 = new InputTrigger().addKeyCodes(new char[]{'3'}).addMidiNotes(new Integer[]{106, 43});
//	protected InputTrigger trigger4 = new InputTrigger().addKeyCodes(new char[]{'4'}).addMidiNotes(new Integer[]{107, 44});
//	protected InputTrigger trigger5 = new InputTrigger().addKeyCodes(new char[]{'5'}).addMidiNotes(new Integer[]{108, 45});
//	protected InputTrigger trigger6 = new InputTrigger().addKeyCodes(new char[]{'6'}).addMidiNotes(new Integer[]{109, 46});
//	protected InputTrigger trigger7 = new InputTrigger().addKeyCodes(new char[]{'7'}).addMidiNotes(new Integer[]{110, 47});
//	protected InputTrigger trigger8 = new InputTrigger().addKeyCodes(new char[]{'8'}).addMidiNotes(new Integer[]{111, 48});

	protected InputTrigger triggerDown = new InputTrigger().addKeyCodes(new char[]{'-'});
	protected InputTrigger triggerUp = new InputTrigger().addKeyCodes(new char[]{'='});
//	protected InputTrigger trigger9 = new InputTrigger().addKeyCodes(new char[]{'9'});

	protected LaunchPadMini launchpad1;
	protected LaunchPadMini launchpad2;

	
	public Interphase(SequencerConfig[] interphaseChannels) {
		this(interphaseChannels, AudioUtil.getAudioMixerIndex("Primary"));
	}
	
	public Interphase(SequencerConfig[] interphaseChannels, int audioOutDeviceIndex) {
		AudioUtil.DEFAULT_AUDIO_MIXER_INDEX = audioOutDeviceIndex;
		NUM_CHANNELS = interphaseChannels.length;
		
		// init state
		// override these after Interphase init
		P.store.setBoolean(SYSTEM_MUTED, false);
		P.store.setNumber(BEAT, 0);
		P.store.setNumber(CUR_STEP, 0);
		P.store.setNumber(BPM, 90);
		P.store.setNumber(INTERACTION_SPEED_MULT, 0);
		P.store.setNumber(CUR_SCALE_INDEX, 0);
		P.store.setNumber(SEQUENCER_TRIGGER_VISUAL, 0);
		P.store.setNumber(SEQUENCER_TRIGGER, 0);
		P.store.setBoolean(GLOBAL_PATTERNS_EVLOVE, false);
		P.store.addListener(this);
		
		// build music machine
		scales = new Scales();
		metronome = new Metronome();
		buildSequencers(interphaseChannels);
		addDebugHelpLines();
		loadConfigFiles();
	}
	
	protected void buildSequencers(SequencerConfig[] interphaseChannels) {
		sequencers = new Sequencer[NUM_CHANNELS];
		for (int i = 0; i < NUM_CHANNELS; i++) {
			sequencers[i] = new Sequencer(this, interphaseChannels[i]);
			sequencers[i].newRandomPattern();
		}
	}
	
	protected void addDebugHelpLines() {
		DebugView.setHelpLine(DebugView.TITLE_PREFIX + "Interphase Key Commands", "");
		DebugView.setHelpLine("[SPACE] |", "Play/Stop");
		DebugView.setHelpLine("[1234] |", "Trigger");
		DebugView.setHelpLine("[QWER] |", "Toggle mute");
		DebugView.setHelpLine("[ASDF] |", "New sound");
		DebugView.setHelpLine("[ZXCV] |", "Toggle Evloves");
		DebugView.setHelpLine("[+]    |", "BPM ++");
		DebugView.setHelpLine("[-]    |", "BPM --");
		DebugView.setHelpLine("[9-0]  |", "Load stored sequences");
		DebugView.setHelpLine("[o]    |", "Save stored sequences");
		DebugView.setHelpLine("[O]    |", "Overwrite cur sequence");
	}
	
	// init config
	
	public Interphase initUI() {
		hasUI = true;
		// add UI buttons grid
		// make dyanmic number of columns per number of sequencers
		for (int i = 0; i < NUM_STEPS; i++) {
			int numChannels = sequencers.length;
			String[] buttonIds = new String[numChannels];
			for(int j = 0; j < numChannels; j++) {
				int channel = j;
				int step = i;
				buttonIds[j] = "beatgrid-"+channel+"-"+step;
			}
			UI.addButtons(buttonIds, true);
		}
		UI.addWebInterface(false);
		return this;
	}
	
	public Interphase initGlobalControlsUI() {
		return initGlobalControlsUI(null, null, null, null);
	}
	
	public Interphase initGlobalControlsUI(int samplePickerMidiCC, int volumeCC, int pitchCC, int reverbCC) {
		// create sequential MIDI CC notes
		int[] samplePickerMidiCCArray = new int[sequencers.length];
		for (int i = 0; i < samplePickerMidiCCArray.length; i++) samplePickerMidiCCArray[i] = samplePickerMidiCC + i;
		int[] midiCCSequenceArray = new int[sequencers.length];
		for (int i = 0; i < midiCCSequenceArray.length; i++) midiCCSequenceArray[i] = volumeCC + i;
		int[] pitchCCSequenceArray = new int[sequencers.length];
		for (int i = 0; i < pitchCCSequenceArray.length; i++) pitchCCSequenceArray[i] = pitchCC + i;
		int[] reverbCCSequenceArray = new int[sequencers.length];
		for (int i = 0; i < reverbCCSequenceArray.length; i++) reverbCCSequenceArray[i] = reverbCC + i;
		// pass to constructor
		return initGlobalControlsUI(samplePickerMidiCCArray, midiCCSequenceArray, pitchCCSequenceArray, reverbCCSequenceArray);
	}
	
	public Interphase initGlobalControlsUI(int[] samplePickerMidiCC, int[] volumeMidiCC, int[] pitchMidiCC, int[] reverbMidiCC) {
		UI.addTitle("Interphase");
		UI.addSlider(UI_GLOBAL_BPM, P.store.getInt(BPM), 30, 200, 1, false);
		UI.addToggle(UI_GLOBAL_EVOLVES, false, false);
		UI.addSlider(UI_CUR_SCALE, 0, 0, Scales.SCALES.length-1, 1, false);
		UI.addTitle("Interphase | Sequencers");
		for (int i = 0; i < sequencers.length; i++) {
			// add optonal midi
			int midiCCSample = (samplePickerMidiCC != null) ? samplePickerMidiCC[i] : -1;
			int midiCCVolume = (volumeMidiCC != null) ? volumeMidiCC[i] : -1;
			int midiCCPitch = (pitchMidiCC != null) ? pitchMidiCC[i] : -1;
			int midiCCReverb = (reverbMidiCC != null) ? reverbMidiCC[i] : -1;
			// add sliders for each sequencer
			Sequencer seq = sequencerAt(i);
			UI.addSlider(UI_SAMPLE_+(i+1), 0, 0, seq.numSamples() - 1, 1, false, midiCCSample);
			UI.addSlider(UI_VOLUME_+(i+1), seq.volume(), 0, 3, 0.01f, false, midiCCVolume);
            UI.addSlider(UI_PITCH_+(i+1), 0, -1, 1, 0.01f, false, midiCCPitch);
            UI.addSlider(UI_REVERB_+(i+1), 0, 0, 1, 0.005f, false, midiCCReverb);
		}
		return this;
	}
	
	
	// init launchpad grids
	
	public Interphase initLaunchpads(int midiIn1, int midiOut1, int midiIn2, int midiOut2) {
		MidiBus.list();
		launchpad1 = new LaunchPadMini(midiIn1, midiOut1);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPadMini(midiIn2, midiOut2);
		launchpad2.setDelegate(this);
		return this;
	}
	
	public Interphase initLaunchpads(String deviceName1, String deviceName2) {
		MidiBus.list();
		launchpad1 = new LaunchPadMini(deviceName1);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPadMini(deviceName2);
		launchpad2.setDelegate(this);
		return this;
	}
	
	public Interphase initAudioAnalysisPerChannel() {
		for (int i = 0; i < sequencers.length; i++) {
			sequencers[i].addAudioAnalysis();
		}
		return this;
	}
	
	public Interphase initAudioTexturesPerChannel() {
		for (int i = 0; i < sequencers.length; i++) {
			sequencers[i].addAudioTextures();
		}
		return this;
	}
	
	public void autoPlay() {
		metronome.togglePlay();
	}
	
	
	/////////////////////////////////
	// GETTERS
	/////////////////////////////////
	
	public AudioContext ac() {
		return Metronome.ac;
	}
	
	public Sequencer[] sequencers() {
		return sequencers;
	}
	
	public Sequencer sequencerAt(int index) {
		return sequencers[index];
	}
	
	public int numChannels() {
		return sequencers.length;
	}
	
	/////////////////////////////////
	// SHARED
	/////////////////////////////////
	
	public void setSystemMute(boolean muted) {
		P.store.setBoolean(SYSTEM_MUTED, muted);
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	protected void keyPressed() {
		// App controls ---------------------------------------
		P.out("Interphase keyPressed:", P.p.key);
		if (P.p.key == ' ') metronome.togglePlay();
		if (P.p.key == 'g') TEMPO_MOUSE_CONTROL = !TEMPO_MOUSE_CONTROL;
		if (P.p.key == P.CODED && P.p.keyCode == P.DOWN) P.store.setBoolean(SYSTEM_MUTED, true);
		if (P.p.key == P.CODED && P.p.keyCode == P.UP) P.store.setBoolean(SYSTEM_MUTED, false);
		
		// Sequencer controls ---------------------------------
		
		if(P.p.key == 'Q') sequencers[0].toggleMute();
		if(P.p.key == 'W') sequencers[1].toggleMute();
		if(P.p.key == 'E') sequencers[2].toggleMute();
		if(P.p.key == 'R') sequencers[3].toggleMute();
		if(P.p.key == 'T') sequencers[4].toggleMute();
		if(P.p.key == 'Y') sequencers[5].toggleMute();
		if(P.p.key == 'U') sequencers[6].toggleMute();
		if(P.p.key == 'I') sequencers[7].toggleMute();
		
		if(P.p.key == 'A') sequencers[0].loadNextSound();
		if(P.p.key == 'S') sequencers[1].loadNextSound();
		if(P.p.key == 'D') sequencers[2].loadNextSound();
		if(P.p.key == 'F') sequencers[3].loadNextSound();
		if(P.p.key == 'G') sequencers[4].loadNextSound();
		if(P.p.key == 'H') sequencers[5].loadNextSound();
		if(P.p.key == 'J') sequencers[6].loadNextSound();
		if(P.p.key == 'K') sequencers[7].loadNextSound();
		
		if(P.p.key == 'Z') sequencers[0].evolves(!sequencers[0].evolves());
		if(P.p.key == 'X') sequencers[1].evolves(!sequencers[1].evolves());
		if(P.p.key == 'C') sequencers[2].evolves(!sequencers[2].evolves());
		if(P.p.key == 'V') sequencers[3].evolves(!sequencers[3].evolves());
		if(P.p.key == 'B') sequencers[4].evolves(!sequencers[4].evolves());
		if(P.p.key == 'N') sequencers[5].evolves(!sequencers[5].evolves());
		if(P.p.key == 'M') sequencers[6].evolves(!sequencers[6].evolves());
		if(P.p.key == '<') sequencers[7].evolves(!sequencers[7].evolves());

//		if(P.p.key == 'o') P.out(outputConfigSingleLine());
		if(P.p.key == 'o') saveJsonConfigToFile();
		if(P.p.key == 'O') rewriteCurJsonFile();
//		if(P.p.key == 'p') loadConfig("{\"sequencers\": [ { \"volume\": 1, \"sampleIndex\": 19, \"noteOffset\": 3, \"notesByStep\": true, \"steps\": [ 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1 ] }, { \"volume\": 1, \"sampleIndex\": 22, \"noteOffset\": 6, \"notesByStep\": false, \"steps\": [ 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 ] }, { \"volume\": 1, \"sampleIndex\": 29, \"noteOffset\": 13, \"notesByStep\": true, \"steps\": [ 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0 ] }, { \"volume\": 1, \"sampleIndex\": 35, \"noteOffset\": 7, \"notesByStep\": true, \"steps\": [ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ] } ]}");
//		if(P.p.key == 'p') loadConfigFromFile("2022-01-16-20-34-35.json");
		if(configFiles.size() > 0) {
			if(P.p.key == '9') { curConfigIndex--; curConfigIndex = P.max(curConfigIndex, 0); loadConfigFromFile(configFiles.get(curConfigIndex)); }
			if(P.p.key == '0') { curConfigIndex++; curConfigIndex = P.min(curConfigIndex, configFiles.size() - 1); loadConfigFromFile(configFiles.get(curConfigIndex)); }
		}
	}
	
	// JSON CONFIGS
	
	protected void loadConfigFiles() {
		if(FileUtil.fileOrPathExists(FileUtil.getPath(SEQUENCES_PATH))) {
			configFiles = FileUtil.getFilesInDirOfType(FileUtil.getPath(SEQUENCES_PATH), "json");
		}
	}
	
	public JSONObject outputConfig() {
		JSONObject interphaseConfig = new JSONObject();
		// add global props
		interphaseConfig.setFloat(BPM, P.store.getInt(BPM));
		// add sequencers' data
		JSONArray sequencersJSON = new JSONArray(); 
		interphaseConfig.setJSONArray("sequencers", sequencersJSON);
		for (int i = 0; i < numChannels(); i++) {
			Sequencer seq = sequencerAt(i);
			sequencersJSON.setJSONObject(i, seq.json());
		}
		return interphaseConfig;
	}
	
	public String outputConfigSingleLine() {
		return JsonUtil.jsonToSingleLine(outputConfig());
	}
		
	public void saveJsonConfigToFile() {
		FileUtil.createDir(FileUtil.getPath(SEQUENCES_PATH));
		String jsonFilename = SystemUtil.getTimestamp() + ".json";
		String jsonSavePath = FileUtil.getPath(SEQUENCES_PATH + jsonFilename);
		JsonUtil.jsonToFile(outputConfig(), jsonSavePath);
		configFiles.add(jsonFilename);
		curConfigIndex = configFiles.size() - 1;
	}
	
	public void rewriteCurJsonFile() {
		// when we want to update an existing json :) 
		FileUtil.createDir(FileUtil.getPath(SEQUENCES_PATH));
		String jsonFilename = configFiles.get(curConfigIndex);
		String jsonSavePath = FileUtil.getPath(SEQUENCES_PATH + jsonFilename);
		JsonUtil.jsonToFile(outputConfig(), jsonSavePath);
	}
	
	protected void loadConfig(String jsonStr) {
		JSONObject jsonConfig = JsonUtil.jsonFromString(jsonStr);
		loadConfig(jsonConfig);
	}
	
	protected void loadConfigFromFile(String filename) {
		JSONObject interphaseConfig = JsonUtil.jsonFromFile(FileUtil.getPath(SEQUENCES_PATH + filename));
		loadConfig(interphaseConfig);
	}
	
	protected void loadConfig(JSONObject interphaseConfig) {
		// load global props
		if(UI.has(UI_GLOBAL_BPM)) UI.setValue(UI_GLOBAL_BPM, interphaseConfig.getInt(BPM)); // just in case UI wasn't initialized
		P.store.setNumber(BPM, interphaseConfig.getInt(BPM));
			
		// add sequencers' data
		JSONArray sequencersDataArray = interphaseConfig.getJSONArray("sequencers");
		for (int i = 0; i < sequencersDataArray.size(); i++) {
			Sequencer seq = sequencerAt(i);
			JSONObject sequencerDataObj = sequencersDataArray.getJSONObject(i); 
			seq.load(sequencerDataObj);
			
			// make sure to update UI to keep in sync
			String uiSampleKey = UI_SAMPLE_+(i+1);
			String uiVolumeKey = UI_VOLUME_+(i+1);
			String uiPitchKey = UI_PITCH_+(i+1);
			String uiReverbKey = UI_REVERB_+(i+1);
			if(UI.has(uiSampleKey)) UI.setValue(UI_SAMPLE_+(i+1), seq.sampleIndex());
			if(UI.has(uiVolumeKey)) UI.setValue(UI_VOLUME_+(i+1), seq.volume());
			if(UI.has(uiPitchKey)) UI.setValue(UI_PITCH_+(i+1), seq.pitchShift());
			if(UI.has(uiReverbKey)) UI.setValue(UI_REVERB_+(i+1), seq.reverbSize());
		}
	}

	
	// LAUNCHPAD INTEGRATION
	
	protected void updateLaunchpads() {
		if(launchpad1 == null) return;
		// split across launchpads
		for (int i = 0; i < sequencers.length; i++) {
			for (int step = 0; step < NUM_STEPS; step++) {
				float value = (sequencers[i].stepActive(step)) ? 1 : 0; 
				float adjustedVal = value;
				if(value == 0 && step % 4 == 0) adjustedVal = 0.3f;	// show divisor by 4
				if(value == 0 && step == P.store.getInt(CUR_STEP)) adjustedVal = 0.65f;	// show playhead in row
				if(step <= 7) {
					launchpad1.setButton(i, step, adjustedVal);
				} else {
					launchpad2.setButton(i, step - 8, adjustedVal);
				}
			}
		}
		
		// update playhead row in play button column on 1st-gen Launchpad
		/*
		if(launchpad1 instanceof LaunchPadMini == false) {
			int lastColIndex = 8;
			for (int step = 0; step < NUM_STEPS; step++) {
				float value = (step == P.store.getInt(CUR_STEP)) ? 0.68f : 0; 
				if(step <= 7) {
					launchpad1.setButton(lastColIndex, step, value);
				} else {
					launchpad2.setButton(lastColIndex, step - 8, value);
				}
			}
		}
		*/
	}
	
	// Bridge to UIControls for mouse control
	
	protected void updateUIGridButtons() {
		if(!hasUI) return;
		
		// split across launchpads
		for (int i = 0; i < sequencers.length; i++) {
			int channel = i;
			for (int step = 0; step < NUM_STEPS; step++) {
				float value = (sequencers[i].stepActive(step)) ? 1 : 0;
				String buttonId = "beatgrid-"+channel+"-"+step;
				UI.get(buttonId).set(value);
				
				if(step % 4 == 0) {
					if(UI.active()) {
						P.p.fill(127);
						P.p.rect(0, 10 + IUIControl.controlSpacing * step, IUIControl.controlW + 20, IUIControl.controlH);
					}
				}
			}
		}
		
		// playhead
		if(UI.active()) {
			P.p.fill(255);
			P.p.rect(0, IUIControl.controlSpacing * P.store.getInt(CUR_STEP), IUIControl.controlW + 20, IUIControl.controlH);
		}
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	protected void checkInputs() {
		// sample triggers & evolve
//		if(trigger1.triggered()) { sequencers[0].evolvePattern(); sequencers[0].triggerSample(); }
//		if(trigger2.triggered()) { sequencers[1].evolvePattern(); sequencers[1].triggerSample(); }
//		if(trigger3.triggered()) { sequencers[2].evolvePattern(); sequencers[2].triggerSample(); }
//		if(trigger4.triggered()) { sequencers[3].evolvePattern(); sequencers[3].triggerSample(); }
//		if(trigger5.triggered()) { sequencers[4].evolvePattern(); sequencers[4].triggerSample(); }
//		if(trigger6.triggered()) { sequencers[5].evolvePattern(); sequencers[5].triggerSample(); }
//		if(trigger7.triggered()) { sequencers[6].evolvePattern(); sequencers[6].triggerSample(); }
//		if(trigger8.triggered()) { sequencers[7].evolvePattern(); sequencers[7].triggerSample(); }

		// bpm
		int curBmpMIDI = P.store.getInt(Interphase.BPM);
		if(triggerDown.triggered()) P.store.setNumber(Interphase.BPM, curBmpMIDI - 1);
		if(triggerUp.triggered())  P.store.setNumber(Interphase.BPM, curBmpMIDI + 1); 

		// global settings
//		if(trigger9.triggered()) P.store.setBoolean(GLOBAL_PATTERNS_EVLOVE, !P.store.getBoolean(GLOBAL_PATTERNS_EVLOVE));
	}
	
	public void update() {
		update(null);
	}
	
	public void update(PGraphics pg) {
		// check inputs & advance sequencers
		checkInputs();
		updateSequencers();
		updateUIGridButtons();
		updateDebugValues();
		if(pg != null) drawSequencer(pg);
	}
	
	protected void updateSequencers() {
		// update sequencers & draw to wall PG. also set overall user activity for tempo change
		float numWallsInteracted = 0;
		for (int i = 0; i < sequencers.length; i++) {
			sequencers[i].update();
			if(sequencers[i].userInteracted()) numWallsInteracted++;
		}
		P.store.setNumber(INTERACTION_SPEED_MULT, numWallsInteracted);
	}
	
	protected void updateDebugValues() {
		DebugView.setValue("INTERPHASE :: BPM", P.store.getFloat(BPM));
		DebugView.setValue("INTERPHASE :: BEAT", P.store.getFloat(BEAT));
		DebugView.setValue("INTERPHASE :: INTERACTION_SPEED_MULT", P.store.getFloat(INTERACTION_SPEED_MULT));
		DebugView.setValue("INTERPHASE :: GLOBAL_PATTERNS_EVLOVE", P.store.getBoolean(GLOBAL_PATTERNS_EVLOVE));
		DebugView.setValue("INTERPHASE :: SEQUENCER_TRIGGER", P.store.getInt(SEQUENCER_TRIGGER_VISUAL));
		DebugView.setValue("INTERPHASE :: CUR_SCALE", Scales.SCALE_NAMES[P.store.getInt(CUR_SCALE_INDEX)]);
	}
	
	protected void drawSequencer(PGraphics pg) {
		float boxSize = pg.width / NUM_STEPS;
		float drawW = (boxSize * sequencers.length);
		float startY =  drawW/ -2f - boxSize / 2;
		float startX = (boxSize * NUM_STEPS) / -2f;
		if(pg != P.p.g) pg.beginDraw();
		if(pg != P.p.g) pg.background(0);
		PG.setCenterScreen(pg);
		PG.setDrawCorner(pg);
		
		pg.translate(startX, startY);
		
		// draw grid
		pg.blendMode(PBlendModes.LIGHTEST);
		for (int y = 0; y < NUM_CHANNELS; y++) {
			for (int x = 0; x < NUM_STEPS; x++) {
				boolean isOn = (sequencers[y].stepActive(x)); 
				pg.push();
				pg.translate(x * boxSize, y * boxSize);
				int cellColor = 10;
				if(x % 4 == 0) cellColor = P.p.color(0, 87, 167);
				if(isOn) cellColor = P.p.color(0, 127, 0);
				pg.fill(cellColor);
				pg.stroke(100);
				pg.rect(0, 0, boxSize, boxSize);
				if(isOn) pg.image(sequencers[y].waveformPG, 0, 0, sequencers[y].waveformPG.width, boxSize);
				pg.pop();
			}
		}
		pg.blendMode(PBlendModes.BLEND);
		
		// track current beat
		int curBeat = P.store.getInt(BEAT) % NUM_STEPS;
		pg.push();
		pg.stroke(255);
		pg.stroke(0, 255, 0);
		pg.fill(255, 50);
		pg.rect(curBeat * boxSize, 0, boxSize, boxSize * NUM_CHANNELS);
		pg.popMatrix();	
		
		if(pg != P.p.g) pg.endDraw();
	}

	protected void drawSequencer3D(PGraphics pg) {
		float spacing = 40;
		float boxSize = 25;
		float startx = (spacing * sequencers.length) / -2f + boxSize/2;
		float startY = (spacing * NUM_STEPS) / -2f + boxSize/2;
		pg.beginDraw();
		PG.setCenterScreen(pg);
		PG.basicCameraFromMouse(pg, 0.1f);
		PG.setBetterLights(pg);
		PG.setDrawCenter(pg);
		
		// draw cubes
		for (int x = 0; x < sequencers.length; x++) {
			for (int y = 0; y < NUM_STEPS; y++) {
//				float value = (sequencers[x].stepActive(y)) ? 1 : 0; 
				boolean isOn = (sequencers[x].stepActive(y)); 
				pg.fill(isOn ? P.p.color(255) : 30);
				pg.pushMatrix();
				pg.translate(startx + x * spacing, startY + y * spacing);
				pg.box(20);
				pg.popMatrix();
			}
		}
		
		// show beat/4
		for (int y = 0; y < NUM_STEPS; y+=4) {
//			float value = (sequencers[x].stepActive(y)) ? 1 : 0; 
			pg.stroke(255);
			pg.noFill();
			pg.pushMatrix();
			pg.translate(-boxSize/2, startY + y * spacing);
			Shapes.drawDashedBox(pg, spacing * (sequencers.length + 1), boxSize, boxSize, 10, true);
			pg.popMatrix();
		}
		
		// track current beat
		int curBeat = P.store.getInt(BEAT) % NUM_STEPS;
		pg.stroke(255);
		pg.noFill();
		pg.pushMatrix();
		pg.translate(-boxSize/2, startY + curBeat * spacing);
		pg.box(spacing * (sequencers.length + 1), boxSize, boxSize);
		pg.popMatrix();	
		
		pg.endDraw();
	}
	
	/////////////////////////////////
	// LAUNCHPAD CALLBACK
	/////////////////////////////////
	
	public void cellUpdated(LaunchPad launchpad, int x, int y, float value) {
		// apply toggle button press
		int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
//		P.out(launchpadNumber, x, y, value);
		int step = (launchpadNumber == 1) ? y : 8 + y;
		boolean isActive = (value == 1f);
		if(x < NUM_CHANNELS) sequencers[x].stepActive(step, isActive);
	}
	
	public void noteOn(LaunchPad launchpad, int note, float value) {
		int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
//		P.out("Interphase.noteOn", launchpadNumber, note, value);
		// special functions on original paunchpad.
		// launchpad mini doesn't send MIDI notes from 9th row/col of buttons
		if(launchpad1 instanceof LaunchPadMini == false) {
			if(launchpadNumber == 1) {
				for (int i = 0; i < NUM_CHANNELS; i++) {
					if(note == LaunchPad.headerColMidiNote(i)) sequencers[i].evolvePattern(); 
				}
			} else {
				// change sample
				for (int i = 0; i < NUM_CHANNELS; i++) {
					if(note == LaunchPad.headerColMidiNote(i)) sequencers[i].loadNextSound(); 
				}
				// bpm up/down
				int curBmpMIDI = P.store.getInt(Interphase.BPM);
				if(note == LaunchPad.groupRowMidiNote(1)) P.store.setNumber(Interphase.BPM, curBmpMIDI - 1); 
				if(note == LaunchPad.groupRowMidiNote(0)) P.store.setNumber(Interphase.BPM, curBmpMIDI + 1); 
			}
		}
	}
	
	//////////////////////////
	// IAppStoreListener updates
	//////////////////////////
	
	public void updatedNumber(String key, Number val) {
		// connect to sequencer UI
		if(key.indexOf("beatgrid-") == 0) {
			String[] components = key.split("-");
			int gridX = ConvertUtil.stringToInt(components[1]);
			int gridY = ConvertUtil.stringToInt(components[2]);
			boolean isActive = (val.intValue() == 1);
			sequencers[gridX].stepActive(gridY, isActive);
		}
		// update launchpads with BEAT delay
		if(key.equals(BEAT)) updateLaunchpads();
		// connect local UI events to global settings
		if(key.equals(UI_GLOBAL_BPM)) P.store.setNumber(Interphase.BPM, val.floatValue());
		if(key.equals(UI_CUR_SCALE)) P.store.setNumber(Interphase.CUR_SCALE_INDEX, val.intValue());
		if(key.equals(UI_GLOBAL_EVOLVES)) P.store.setBoolean(Interphase.GLOBAL_PATTERNS_EVLOVE, val.intValue() == 1);	// toggle events are 0-1, not boolean !!!
		// set sample/volume props for specific channels
		if(key.indexOf(UI_SAMPLE_) == 0) {
			String sequencerNum = key.substring(UI_SAMPLE_.length(), key.length() - 0);	// used to break after 9 channels, should work for higher numbers 
			int sequencerIndex = ConvertUtil.stringToInt(sequencerNum) - 1;	// use key to grab sample index
			sequencerAt(sequencerIndex).setSampleByIndex(val.intValue());
		}
		if(key.indexOf(UI_VOLUME_) == 0) {
			String sequencerNum = key.substring(UI_VOLUME_.length(), key.length() - 0); 	// TODO: this will break after 9 channels!!!!
			int sequencerIndex = ConvertUtil.stringToInt(sequencerNum) - 1;
			sequencerAt(sequencerIndex).volume(val.floatValue());
		}
		if(key.indexOf(UI_PITCH_) == 0) {
			String sequencerNum = key.substring(UI_PITCH_.length(), key.length() - 0); 	// TODO: this will break after 9 channels!!!!
			int sequencerIndex = ConvertUtil.stringToInt(sequencerNum) - 1;
			sequencerAt(sequencerIndex).pitchShift(val.floatValue());
		}
		if(key.indexOf(UI_REVERB_) == 0) {
		    String sequencerNum = key.substring(UI_REVERB_.length(), key.length() - 0); 	// TODO: this will break after 9 channels!!!!
		    int sequencerIndex = ConvertUtil.stringToInt(sequencerNum) - 1;
		    float amp = val.floatValue();
		    if(amp < 0.1f) amp = 0;
		    float reverbSize = amp * 40f;
		    float reverbDampening = amp * 0.5f;
		    if(sequencerIndex == 0) {
		        reverbSize *= 0.5f;
		        reverbDampening *= 0.5f;
		    }
//		    reverbSize = 1.0f;
//		    reverbDampening = 1.5f;
		    P.out(reverbSize, reverbDampening);
		    sequencerAt(sequencerIndex).reverb(reverbSize, reverbDampening);
		}
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED)) keyPressed();
	}
	public void updatedBoolean(String key, Boolean val) {
	}	
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
