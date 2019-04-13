package com.haxademic.app.interphase;


import com.haxademic.app.interphase.sequencing.Metronome;
import com.haxademic.app.interphase.sequencing.Sequencer;
import com.haxademic.app.interphase.sequencing.SequencerConfig;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.shared.InputTrigger;

import beads.Pitch;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;

public class Interphase
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	
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
	public static final String BPM = "BPM";
	public static final String BEAT_INTERVAL_MILLIS = "BEAT_INTERVAL_MILLIS";

	// input 
	
	public static final float TEMPO_EASE_FACTOR = 1.5f;
	public static boolean TEMPO_MOUSE_CONTROL = true;
	public static final int TRIGGER_TIMEOUT = 45000;
	public static final String INTERACTION_SPEED_MULT = "INTERACTION_SPEED_MULT";
	
	//////////////////////////////////////////
	
	protected PGraphics pgLedWall;
	protected PGraphics pgLedWashes;

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
	
	protected boolean mapTestMode = false;
	protected boolean upsideDownLED = true;
	
	protected InputTrigger trigger1 = new InputTrigger(new char[]{'1'}, null, new Integer[]{41}, null, null);
	protected InputTrigger trigger2 = new InputTrigger(new char[]{'2'}, null, new Integer[]{42}, null, null);
	protected InputTrigger trigger3 = new InputTrigger(new char[]{'3'}, null, new Integer[]{43}, null, null);
	protected InputTrigger trigger4 = new InputTrigger(new char[]{'4'}, null, new Integer[]{44}, null, null);
	protected InputTrigger trigger5 = new InputTrigger(new char[]{'5'}, null, new Integer[]{45}, null, null);
	protected InputTrigger trigger6 = new InputTrigger(new char[]{'6'}, null, new Integer[]{46}, null, null);
	protected InputTrigger trigger7 = new InputTrigger(new char[]{'7'}, null, new Integer[]{47}, null, null);
	protected InputTrigger trigger8 = new InputTrigger(new char[]{'8'}, null, new Integer[]{48}, null, null);

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 780 );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.INIT_MINIM_AUDIO, false );
		p.appConfig.setProperty( AppSettings.APP_NAME, "INTERPHASE" );
		p.appConfig.setProperty( AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
	}
	
	public void setupFirstFrame() {
		// init state
		P.store.setNumber(BEAT, 0);
		P.store.setNumber(BEAT_INTERVAL_MILLIS, 700f);
		P.store.setNumber(BPM, 0);
		P.store.setNumber(INTERACTION_SPEED_MULT, 0);
		P.store.setNumber(CUR_SCALE_INDEX, 0);

		// debug
		fontBig = p.createFont("Arial", FONT_BIG);
		fontSmall = p.createFont("Arial", FONT_SMALL);
		
		// LED communications
//		_network = new Network();
//		_network.init(p);
		pgLedWall = p.createGraphics( LED_WALL_SIZE_W, LED_WALL_SIZE_H, P.P2D );
		pgLedWashes = p.createGraphics( LED_WASHES_SIZE_W, 1, P.P2D );
		
		// global colors & graphics
		imageGradient = new ImageGradient(ImageGradient.PASTELS());
//		imageGradient.addTexturesFromPath(Interphase.BASE_PATH + "images/palettes/interphase/");
		
		// build music machine
		metronome = new Metronome();
		sequencers = new Sequencer[NUM_WALLS];
		for (int i = 0; i < sequencers.length; i++) {
			sequencers[i] = new Sequencer(SequencerConfig.interphaseChannels[i]);
		}
	}
	
	
	/////////////////////////////////
	// SHARED
	/////////////////////////////////
	
	public int getColorAtProgress(float progress) {
		return imageGradient.getColorAtProgress(progress);
	}
	
	public void newColorScheme() {
		imageGradient.randomGradientTexture();
	}
	
	public boolean systemMuted() {
		return systemMuted;
	}
	
	protected void setSystemMute(boolean muted) {
		systemMuteTime = p.millis();
		systemMuted = muted;
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		
		// App controls ---------------------------------------
		
		if(p.key == 'o') threeDSimulation = !threeDSimulation;
		if(p.key == 'g') TEMPO_MOUSE_CONTROL = !TEMPO_MOUSE_CONTROL;
		if(p.key == 'h') mapTestMode = !mapTestMode;
		if(p.key == ' ') newColorScheme();
		if (p.key == P.CODED && keyCode == P.DOWN) setSystemMute(true);
		if (p.key == P.CODED && keyCode == P.UP) setSystemMute(false);
		
		// Sequencer controls ---------------------------------
		
		if(p.key == 'q') sequencers[0].toggleMute();
		if(p.key == 'w') sequencers[1].toggleMute();
		if(p.key == 'e') sequencers[2].toggleMute();
		if(p.key == 'r') sequencers[3].toggleMute();
		if(p.key == 't') sequencers[4].toggleMute();
		if(p.key == 'y') sequencers[5].toggleMute();
		if(p.key == 'u') sequencers[6].toggleMute();
		if(p.key == 'i') sequencers[7].toggleMute();
		
		if(p.key == 'a') sequencers[0].loadNextSound();
		if(p.key == 's') sequencers[1].loadNextSound();
		if(p.key == 'd') sequencers[2].loadNextSound();
		if(p.key == 'f') sequencers[3].loadNextSound();
		if(p.key == 'g') sequencers[4].loadNextSound();
		if(p.key == 'h') sequencers[5].loadNextSound();
		if(p.key == 'j') sequencers[6].loadNextSound();
		if(p.key == 'k') sequencers[7].loadNextSound();
		
		if(p.key == 'z') sequencers[0].toggleEvloves();
		if(p.key == 'x') sequencers[1].toggleEvloves();
		if(p.key == 'c') sequencers[2].toggleEvloves();
		if(p.key == 'v') sequencers[3].toggleEvloves();
		if(p.key == 'b') sequencers[4].toggleEvloves();
		if(p.key == 'n') sequencers[5].toggleEvloves();
		if(p.key == 'm') sequencers[6].toggleEvloves();
		if(p.key == ',') sequencers[7].toggleEvloves();
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
	}
	
	public void drawApp() {
		p.background(0);
		p.noStroke();
		DrawUtil.setDrawCorner(p);

		// input & lighting buffers
		checkInputs();
		updateSequencers();

		// visualize
		drawBPM();
		
		// update debug values
		p.debugView.setValue("BEAT", P.store.getFloat(BEAT));
		p.debugView.setValue("INTERACTION_SPEED_MULT", P.store.getFloat(INTERACTION_SPEED_MULT));
	}
	
	protected void updateSequencers() {
		// update sequencers & draw to wall PG. also set overall user activity for tempo change
		float numWallsInteracted = 0;
		for (int i = 0; i < sequencers.length; i++) {
			if(sequencers[i].userInteracted()) numWallsInteracted++;
		}
		P.store.setNumber(INTERACTION_SPEED_MULT, numWallsInteracted);
	}
	
	protected void drawBPM() {
		p.fill(255);
		p.textAlign(P.LEFT, P.TOP);
		p.textFont(fontBig);
		p.textSize(fontBig.getSize());
		p.text( P.round(P.store.getFloat(BPM)) + "bpm" + " (" + P.store.getInt(BEAT_INTERVAL_MILLIS) + "ms)" + FileUtil.NEWLINE + 
				SCALE_NAMES[P.store.getInt(CUR_SCALE_INDEX)], 
				332, 
				10
				);
	}
}
