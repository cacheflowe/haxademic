package com.haxademic.core.media.audio.interphase;


import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPad.ILaunchpadCallback;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.media.audio.AudioUtil;
import com.haxademic.core.ui.IUIControl;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
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
	public static final String SEQUENCER_TRIGGER = "SEQUENCER_TRIGGER";

	// state
	
	public static final String CUR_SCALE_INDEX = "CUR_SCALE_INDEX";
	public static final String GLOBAL_PATTERNS_EVLOVE = "GLOBAL_PATTERNS_EVLOVE";
	public static boolean SYSTEM_MUTED = false;

	// input 
	
	public static final float TEMPO_EASE_FACTOR = 1.5f;
	public static boolean TEMPO_MOUSE_CONTROL = false;
	public static boolean TEMPO_MIDI_CONTROL = true;
	public static final int TRIGGER_TIMEOUT = 45000;
	public static final String INTERACTION_SPEED_MULT = "INTERACTION_SPEED_MULT";
	
	// ui
	
	public static final String GLOBAL_BPM = "GLOBAL_BPM";
	public static final String GLOBAL_EVOLVES = "GLOBAL_EVOLVES";
	public static final String CUR_SCALE = "CUR_SCALE";
	public static final String SAMPLE_ = "SAMPLE_";
	public static final String VOLUME_ = "VOLUME_";

	
	//////////////////////////////////////////
	
	protected Scales scales;
	public Sequencer sequencers[];
	protected Metronome metronome;
	
	protected boolean hasUI = false;
	
	protected InputTrigger trigger1 = new InputTrigger().addKeyCodes(new char[]{'1'}).addMidiNotes(new Integer[]{104, 41});
	protected InputTrigger trigger2 = new InputTrigger().addKeyCodes(new char[]{'2'}).addMidiNotes(new Integer[]{105, 42});
	protected InputTrigger trigger3 = new InputTrigger().addKeyCodes(new char[]{'3'}).addMidiNotes(new Integer[]{106, 43});
	protected InputTrigger trigger4 = new InputTrigger().addKeyCodes(new char[]{'4'}).addMidiNotes(new Integer[]{107, 44});
	protected InputTrigger trigger5 = new InputTrigger().addKeyCodes(new char[]{'5'}).addMidiNotes(new Integer[]{108, 45});
	protected InputTrigger trigger6 = new InputTrigger().addKeyCodes(new char[]{'6'}).addMidiNotes(new Integer[]{109, 46});
	protected InputTrigger trigger7 = new InputTrigger().addKeyCodes(new char[]{'7'}).addMidiNotes(new Integer[]{110, 47});
	protected InputTrigger trigger8 = new InputTrigger().addKeyCodes(new char[]{'8'}).addMidiNotes(new Integer[]{111, 48});

	protected InputTrigger triggerDown = new InputTrigger().addKeyCodes(new char[]{'-'});
	protected InputTrigger triggerUp = new InputTrigger().addKeyCodes(new char[]{'='});
	protected InputTrigger trigger9 = new InputTrigger().addKeyCodes(new char[]{'9'});

	protected LaunchPad launchpad1;
	protected LaunchPad launchpad2;

	
	public Interphase(SequencerConfig[] interphaseChannels) {
		this(interphaseChannels, AudioUtil.getAudioMixerIndex("Primary"));
	}
	
	public Interphase(SequencerConfig[] interphaseChannels, int audioOutDeviceIndex) {
		AudioUtil.DEFAULT_AUDIO_MIXER_INDEX = audioOutDeviceIndex;
		NUM_CHANNELS = interphaseChannels.length;
		
		// init state
		P.store.setNumber(BEAT, 0);
		P.store.setNumber(CUR_STEP, 0);
		P.store.setNumber(BPM, 90);
		P.store.setNumber(INTERACTION_SPEED_MULT, 0);
		P.store.setNumber(CUR_SCALE_INDEX, 0);
		P.store.setNumber(SEQUENCER_TRIGGER, 0);
		P.store.setBoolean(GLOBAL_PATTERNS_EVLOVE, true);
		P.store.addListener(this);
		
		// build music machine
		scales = new Scales();
		metronome = new Metronome();
		buildSequencers(interphaseChannels);
		addDebugHelpLines();
	}
	
	protected void buildSequencers(SequencerConfig[] interphaseChannels) {
		sequencers = new Sequencer[NUM_CHANNELS];
		for (int i = 0; i < NUM_CHANNELS; i++) {
			sequencers[i] = new Sequencer(this, interphaseChannels[i]);
		}
	}
	
	protected void addDebugHelpLines() {
		DebugView.setHelpLine("\n" + DebugView.TITLE_PREFIX + "Interphase Key Commands", "");
		DebugView.setHelpLine("[1234] |", "Trigger");
		DebugView.setHelpLine("[QWER] |", "Toggle on/off");
		DebugView.setHelpLine("[ASDF] |", "New sound");
		DebugView.setHelpLine("[9] |", "Toggle auto morph");
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
		return initGlobalControlsUI(-1, -1);
	}
	
	public Interphase initGlobalControlsUI(int samplePickerMidiCC, int volumeMidiCC) {
		UI.addTitle("Interphase");
		UI.addSlider(GLOBAL_BPM, 105, 60, 170, 1, false);
		UI.addToggle(GLOBAL_EVOLVES, false, false);
		UI.addSlider(CUR_SCALE, 0, 0, Scales.SCALES.length-1, 1, false);
		for (int i = 0; i < sequencers.length; i++) {
			Sequencer seq = sequencerAt(i);
			int midiCCSample = (samplePickerMidiCC > -1) ? samplePickerMidiCC + i : -1;
			int midiCCVolume = (volumeMidiCC > -1) ? volumeMidiCC + i : -1;
			UI.addSlider(SAMPLE_+(i+1), 0, 0, seq.numSamples() - 1, 1, false, midiCCSample);
			UI.addSlider(VOLUME_+(i+1), seq.volume(), 0, 3, 0.01f, false, midiCCVolume);
		}
		return this;
	}
	
	
	// init launchpad grids
	
	public Interphase initLaunchpads(int midiIn1, int midiOut1, int midiIn2, int midiOut2) {
		MidiBus.list();
		launchpad1 = new LaunchPad(midiIn1, midiOut1);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPad(midiIn2, midiOut2);
		launchpad2.setDelegate(this);
		return this;
	}
	
	public Interphase initLaunchpads(String deviceName1, String deviceName2) {
		MidiBus.list();
		launchpad1 = new LaunchPad(deviceName1);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPad(deviceName2);
		launchpad2.setDelegate(this);
		return this;
	}
	
	public Interphase initAudioAnalysisPerChannel() {
		for (int i = 0; i < sequencers.length; i++) {
			sequencers[i].addAudioAnalysis();
		}
		return this;
	}
	
	
	/////////////////////////////////
	// GETTERS
	/////////////////////////////////
	
	public Sequencer[] sequencers() {
		return sequencers;
	}
	
	public Sequencer sequencerAt(int index) {
		return sequencers[index];
	}
	
	/////////////////////////////////
	// SHARED
	/////////////////////////////////
	
	public void setSystemMute(boolean muted) {
		SYSTEM_MUTED = muted;
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		// App controls ---------------------------------------
		
		if (P.p.key == 'g') TEMPO_MOUSE_CONTROL = !TEMPO_MOUSE_CONTROL;
		if (P.p.key == P.CODED && P.p.keyCode == P.DOWN) SYSTEM_MUTED = true;
		if (P.p.key == P.CODED && P.p.keyCode == P.UP) SYSTEM_MUTED = false;
		
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
		
		if(P.p.key == 'Z') sequencers[0].toggleEvloves();
		if(P.p.key == 'X') sequencers[1].toggleEvloves();
		if(P.p.key == 'C') sequencers[2].toggleEvloves();
		if(P.p.key == 'V') sequencers[3].toggleEvloves();
		if(P.p.key == 'B') sequencers[4].toggleEvloves();
		if(P.p.key == 'N') sequencers[5].toggleEvloves();
		if(P.p.key == 'M') sequencers[6].toggleEvloves();
		if(P.p.key == '<') sequencers[7].toggleEvloves();
	}
	
	// LAUNCHPAD INTEGRATION
	
	protected void updateLaunchpads() {
		if(launchpad1 == null) return;
		
		// split across launchpads
		for (int i = 0; i < sequencers.length; i++) {
			for (int step = 0; step < NUM_STEPS; step++) {
				float value = (sequencers[i].stepActive(step)) ? 1 : 0; 
				float adjustedVal = value;
				if(value == 0 && step % 4 == 0) adjustedVal = 0.15f;	// show divisor by 4
				if(value == 0 && step == P.store.getInt(CUR_STEP)) adjustedVal = 0.65f;	// show playhead in row
				if(step <= 7) {
					launchpad1.setButton(i, step, adjustedVal);
				} else {
					launchpad2.setButton(i, step - 8, adjustedVal);
				}
			}
		}
		
		// update playhead
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
	
	protected void updateFromGlobalUI() {
		// set interphase props
		P.store.setNumber(Interphase.BPM, UI.value(GLOBAL_BPM));
		P.store.setBoolean(Interphase.GLOBAL_PATTERNS_EVLOVE, UI.valueToggle(GLOBAL_EVOLVES));
		P.store.setNumber(Interphase.CUR_SCALE_INDEX, UI.valueInt(CUR_SCALE));
		
		// set current sample, volume by UI sliders
		for (int i = 0; i < sequencers.length; i++) {
			Sequencer seq = sequencerAt(i);
			seq.setSampleByIndex(UI.valueInt(SAMPLE_+(i+1)));
			seq.volume(UI.value(VOLUME_+(i+1)));
		}
	}
	
	protected void checkInputs() {
		// sample triggers & evolve
		if(trigger1.triggered()) { sequencers[0].evolvePattern(); sequencers[0].triggerSample(); }
		if(trigger2.triggered()) { sequencers[1].evolvePattern(); sequencers[1].triggerSample(); }
		if(trigger3.triggered()) { sequencers[2].evolvePattern(); sequencers[2].triggerSample(); }
		if(trigger4.triggered()) { sequencers[3].evolvePattern(); sequencers[3].triggerSample(); }
		if(trigger5.triggered()) { sequencers[4].evolvePattern(); sequencers[4].triggerSample(); }
		if(trigger6.triggered()) { sequencers[5].evolvePattern(); sequencers[5].triggerSample(); }
		if(trigger7.triggered()) { sequencers[6].evolvePattern(); sequencers[6].triggerSample(); }
		if(trigger8.triggered()) { sequencers[7].evolvePattern(); sequencers[7].triggerSample(); }

		// bpm
		int curBmpMIDI = P.store.getInt(Interphase.BPM);
		if(triggerDown.triggered()) P.store.setNumber(Interphase.BPM, curBmpMIDI - 1);
		if(triggerUp.triggered())  P.store.setNumber(Interphase.BPM, curBmpMIDI + 1); 

		// global settings
		if(trigger9.triggered()) P.store.setBoolean(GLOBAL_PATTERNS_EVLOVE, !P.store.getBoolean(GLOBAL_PATTERNS_EVLOVE));
	}
	
	public void update(PGraphics pg) {
		// check inputs & advance sequencers
		updateFromGlobalUI();
		checkInputs();
		updateSequencers();
		updateLaunchpads();
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
		DebugView.setValue("INTERPHASE :: PATTERNS_AUTO_MORPH", P.store.getBoolean(GLOBAL_PATTERNS_EVLOVE));
		DebugView.setValue("INTERPHASE :: SEQUENCER_TRIGGER", P.store.getInt(SEQUENCER_TRIGGER));
		DebugView.setValue("INTERPHASE :: CUR_SCALE", Scales.SCALE_NAMES[P.store.getInt(CUR_SCALE_INDEX)]);
	}
	
	protected void drawSequencer(PGraphics pg) {
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
	
	//////////////////////////
	// IAppStoreListener updates
	//////////////////////////
	
	public void updatedNumber(String key, Number val) {
		if(key.indexOf("beatgrid-") == 0) {
			String[] components = key.split("-");
			int gridX = ConvertUtil.stringToInt(components[1]);
			int gridY = ConvertUtil.stringToInt(components[2]);
			boolean isActive = (val.intValue() == 1);
			sequencers[gridX].stepActive(gridY, isActive);
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}	
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
