package com.haxademic.app.interphase.sequencing;


import com.haxademic.core.app.P;
import com.haxademic.core.data.ConvertUtil;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.LaunchPad.ILaunchpadCallback;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.ui.UIButton;
import com.haxademic.core.ui.UIControlPanel;

import beads.Pitch;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;

public class Interphase
implements ILaunchpadCallback {
	
	////////////////////////////////////////
	
	public static String BASE_PATH = "D:\\workspace\\interphase\\data\\";
	
	// sizes
	
	public static final int NUM_WALLS = 8;
	public static final int NUM_STEPS = 16;
	public static final int WALL_BUFFER_LED_SPACING = 12;
	
	public static final int PANEL_SIZE = 3;
	public static final int LED_STRIP_W = 3;
	public static final int LED_STRIP_H = 48;
	public static final int LED_WALL_SIZE_W = 99;
	public static final int LED_WALL_SIZE_H = 48;
	public static final int LED_WASHES_SIZE_W = 8;
	
	// music
	
	public static int[][] SCALES = new int[][] {
		// https://github.com/orsjb/beads/blob/master/src/beads_main/net/beadsproject/beads/data/Pitch.java
		// Pitch.circleOfFifths, 	// {0, 5, 10, 3, 8, 1, 6, 11, 4, 9, 2, 7} // not really cool
		Pitch.dorian,				// {0, 2, 3, 5, 7, 9, 10}
		Pitch.pentatonic,			// {0, 2, 4, 7, 9}
		Pitch.major,				// {0, 2, 4, 5, 7, 9, 11}
		Pitch.minor,				// {0, 2, 3, 5, 7, 8, 10}
									//		new int[] {0,5,10,15,19,24},
		new int[] {0,3,5,7,10},
	};
	public static int[] CUR_SCALE = SCALES[0];
	public static String CUR_SCALE_INDEX = "CUR_SCALE_INDEX";
	
	public static String[] SCALE_NAMES = new String[] {
		"Dorian",
		"Pentatonic",
		"Major",
		"Minor",
		"Minor2",
	};
	
	public static int BEATS_PER_SCALE_CHANGE = 240;
	
	// events
	
	public static final String BEAT = "BEAT";
	public static final String CUR_STEP = "BEAT_MOD";
	public static final String BPM = "BPM";
	public static final String BEAT_INTERVAL_MILLIS = "BEAT_INTERVAL_MILLIS";
	public static final String BPM_MIDI = "BPM_MIDI";
	public static final String SEQUENCER_TRIGGER = "SEQUENCER_TRIGGER";

	// state
	
	public static final String PATTERNS_AUTO_MORPH = "PATTERNS_AUTO_MORPH";

	// input 
	
	public static final float TEMPO_EASE_FACTOR = 1.5f;
	public static boolean TEMPO_MOUSE_CONTROL = false;
	public static boolean TEMPO_MIDI_CONTROL = true;
	public static final int TRIGGER_TIMEOUT = 45000;
	public static final String INTERACTION_SPEED_MULT = "INTERACTION_SPEED_MULT";
	
	//////////////////////////////////////////
	
	public Sequencer sequencers[];
	protected Metronome metronome;
	protected ImageGradient imageGradient;
	
	protected boolean threeDSimulation = false;
	protected PShape human;
	
	protected int FONT_BIG = 18;
	protected int FONT_SMALL = 11;
	protected PFont fontBig; 
	protected PFont fontSmall; 
	
	protected boolean systemMuted = false;
	protected int systemMuteTime = -1;
	
	protected boolean upsideDownLED = true;
	
	protected InputTrigger trigger1 = new InputTrigger(new char[]{'1'}, null, new Integer[]{104, 41}, null, null);
	protected InputTrigger trigger2 = new InputTrigger(new char[]{'2'}, null, new Integer[]{105, 42}, null, null);
	protected InputTrigger trigger3 = new InputTrigger(new char[]{'3'}, null, new Integer[]{106, 43}, null, null);
	protected InputTrigger trigger4 = new InputTrigger(new char[]{'4'}, null, new Integer[]{107, 44}, null, null);
	protected InputTrigger trigger5 = new InputTrigger(new char[]{'5'}, null, new Integer[]{108, 45}, null, null);
	protected InputTrigger trigger6 = new InputTrigger(new char[]{'6'}, null, new Integer[]{109, 46}, null, null);
	protected InputTrigger trigger7 = new InputTrigger(new char[]{'7'}, null, new Integer[]{110, 47}, null, null);
	protected InputTrigger trigger8 = new InputTrigger(new char[]{'8'}, null, new Integer[]{111, 48}, null, null);

	protected InputTrigger trigger9 = new InputTrigger().addKeyCodes(new char[]{'9'});

	protected LaunchPad launchpad1;
	protected LaunchPad launchpad2;

	
	public Interphase() {
		// init state
		P.store.setNumber(BEAT, 0);
		P.store.setNumber(CUR_STEP, 0);
		P.store.setNumber(BEAT_INTERVAL_MILLIS, 700f);
		P.store.setNumber(BPM, 0);
		P.store.setNumber(BPM_MIDI, 0);
		P.store.setNumber(INTERACTION_SPEED_MULT, 0);
		P.store.setNumber(CUR_SCALE_INDEX, 0);
		P.store.setBoolean(PATTERNS_AUTO_MORPH, true);

		// debug
		fontBig = P.p.createFont("Arial", FONT_BIG);
		fontSmall = P.p.createFont("Arial", FONT_SMALL);
		
		// global colors & graphics
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
//		imageGradient.addTexturesFromPath(Interphase.BASE_PATH + "images/palettes/interphase/");
		
		// build music machine
		metronome = new Metronome(this);
		sequencers = new Sequencer[NUM_WALLS];
		for (int i = 0; i < sequencers.length; i++) {
			sequencers[i] = new Sequencer(this, SequencerConfig.interphaseChannels[i]);
		}
		
		// build launchpad
		launchpad1 = new LaunchPad(0, 3);
		launchpad1.setDelegate(this);
		launchpad2 = new LaunchPad(1, 4);
		launchpad2.setDelegate(this);
		
		// alternate UI buttons
		for (int i = 0; i < 16; i++) {
			P.p.ui.addButtons(new String[] {"beatgrid-0-"+i, "beatgrid-1-"+i, "beatgrid-2-"+i, "beatgrid-3-"+i, "beatgrid-4-"+i, "beatgrid-5-"+i, "beatgrid-6-"+i, "beatgrid-7-"+i}, true);
		}
		P.p.ui.addWebInterface(false);
	}
	
	
	/////////////////////////////////
	// SHARED
	/////////////////////////////////
	
	public int getColorAtProgress(float progress) {
		return imageGradient.getColorAtProgress(progress);
	}
	
	public boolean systemMuted() {
		return systemMuted;
	}
	
	protected void setSystemMute(boolean muted) {
		systemMuteTime = P.p.millis();
		systemMuted = muted;
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		// App controls ---------------------------------------
		
		if(P.p.key == 'g') TEMPO_MOUSE_CONTROL = !TEMPO_MOUSE_CONTROL;
		if (P.p.key == P.CODED && P.p.keyCode == P.DOWN) setSystemMute(true);
		if (P.p.key == P.CODED && P.p.keyCode == P.UP) setSystemMute(false);
		
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
	
	protected void updateUIButtons() {
		// split across launchpads
		for (int i = 0; i < sequencers.length; i++) {
			for (int step = 0; step < NUM_STEPS; step++) {
				float value = (sequencers[i].stepActive(step)) ? 1 : 0; 
				P.p.ui.get("beatgrid-"+i+"-"+step).set(value);
				
				if(step % 4 == 0) {
					if(P.p.ui.active()) {
						P.p.fill(127);
						P.p.rect(0, 10 + UIControlPanel.controlSpacing * step, UIControlPanel.controlW + 20, UIControlPanel.controlH);
					}
				}
			}
		}
		
		// playhead
		if(P.p.ui.active()) {
			P.p.fill(255);
			P.p.rect(0, 10 + UIControlPanel.controlSpacing * P.store.getInt(CUR_STEP), UIControlPanel.controlW + 20, UIControlPanel.controlH);
		}
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	protected void checkInputs() {
		if(trigger1.triggered()) sequencers[0].evolvePattern(true);
		if(trigger2.triggered()) sequencers[1].evolvePattern(true);
		if(trigger3.triggered()) sequencers[2].evolvePattern(true);
		if(trigger4.triggered()) sequencers[3].evolvePattern(true);
		if(trigger5.triggered()) sequencers[4].evolvePattern(true);
		if(trigger6.triggered()) sequencers[5].evolvePattern(true);
		if(trigger7.triggered()) sequencers[6].evolvePattern(true);
		if(trigger8.triggered()) sequencers[7].evolvePattern(true);
		
		if(trigger9.triggered()) P.store.setBoolean(PATTERNS_AUTO_MORPH, !P.store.getBoolean(PATTERNS_AUTO_MORPH));
	}
	
	public void update(PGraphics pg) {
		// check inputs & advance sequencers
		checkInputs();
		updateSequencers();
		updateLaunchpads();
		updateUIButtons();
		if(pg != null) drawSequencer(pg);
		
		// update debug values
		P.p.debugView.setValue("INTERPHASE :: BPM", P.store.getFloat(BPM));
		P.p.debugView.setValue("INTERPHASE :: BPM interval", P.store.getInt(BEAT_INTERVAL_MILLIS) + "ms");
		P.p.debugView.setValue("INTERPHASE :: BEAT", P.store.getFloat(BEAT));
		P.p.debugView.setValue("INTERPHASE :: INTERACTION_SPEED_MULT", P.store.getFloat(INTERACTION_SPEED_MULT));
	}
	
	protected void updateSequencers() {
		// update sequencers & draw to wall PG. also set overall user activity for tempo change
		float numWallsInteracted = 0;
		for (int i = 0; i < sequencers.length; i++) {
			if(sequencers[i].userInteracted()) numWallsInteracted++;
		}
		P.store.setNumber(INTERACTION_SPEED_MULT, numWallsInteracted);
	}
	
	protected void drawSequencer(PGraphics pg) {
		float spacing = 40;
		float boxSize = 25;
		float startx = (spacing * sequencers.length) / -2f + boxSize/2;
		float startY = (spacing * NUM_STEPS) / -2f + boxSize/2;
		pg.beginDraw();
		DrawUtil.setCenterScreen(pg);
		DrawUtil.basicCameraFromMouse(pg, 0.1f);
		DrawUtil.setBetterLights(pg);
		DrawUtil.setDrawCenter(pg);
		
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
		if(x < 8) sequencers[x].stepActive(step, isActive);
	}
	
	public void noteOn(LaunchPad launchpad, int note, float value) {
		int launchpadNumber = (launchpad == launchpad1) ? 1 : 2;
//		P.out("Interphase.noteOn", launchpadNumber, note, value);
		if(launchpadNumber == 1) {
			for (int i = 0; i < 8; i++) {
				if(note == LaunchPad.headerColMidiNote(i)) sequencers[i].evolvePattern(true); 
			}
		} else {
			// change sample
			for (int i = 0; i < 8; i++) {
				if(note == LaunchPad.headerColMidiNote(i)) sequencers[i].loadNextSound(); 
			}
			// bpm up/down
			int curBmpMIDI = P.store.getInt(Interphase.BPM_MIDI);
			if(note == LaunchPad.groupRowMidiNote(1)) P.store.setNumber(Interphase.BPM_MIDI, curBmpMIDI - 1); 
			if(note == LaunchPad.groupRowMidiNote(0)) P.store.setNumber(Interphase.BPM_MIDI, curBmpMIDI + 1); 
		}
	}
	
	public void uiButtonClicked(UIButton button) {
		if(button.id().indexOf("beatgrid-") == 0) {
			String[] components = button.id().split("-");
			int gridX = ConvertUtil.stringToInt(components[1]);
			int gridY = ConvertUtil.stringToInt(components[2]);
			boolean isActive = (button.value() == 1f);
			sequencers[gridX].stepActive(gridY, isActive);
		}
	}
}
