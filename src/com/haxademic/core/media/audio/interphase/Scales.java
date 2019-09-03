package com.haxademic.core.media.audio.interphase;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.math.MathUtil;

import beads.Pitch;
import processing.core.PGraphics;
import processing.core.PImage;

public class Scales
implements IAppStoreListener {

	public static int[][] SCALES = new int[][] {
		// https://github.com/orsjb/beads/blob/master/src/beads_main/net/beadsproject/beads/data/Pitch.java
		// Pitch.circleOfFifths, 	// {0, 5, 10, 3, 8, 1, 6, 11, 4, 9, 2, 7} // not really cool
		Pitch.dorian,				// {0, 2, 3, 5, 7, 9, 10}
		Pitch.pentatonic,			// {0, 2, 4, 7, 9}
		Pitch.major,				// {0, 2, 4, 5, 7, 9, 11}
		Pitch.minor,				// {0, 2, 3, 5, 7, 8, 10}
		new int[] {0,3,5,7,10},
		// new int[] {0,5,10,15,19,24},
	};
	public static int scaleIndex = 0;
	public static int[] CUR_SCALE = SCALES[scaleIndex];
	
	public static String[] SCALE_NAMES = new String[] {
		"Dorian",
		"Pentatonic",
		"Major",
		"Minor",
		"Minor2",
	};
	
	public static int BEATS_PER_SCALE_CHANGE = 240;

	public Scales() {
		P.store.addListener(this);
	}
	
	public void setScaleIndex(int index) {
		scaleIndex = index;
		P.store.setNumber(Interphase.CUR_SCALE_INDEX, scaleIndex);
		CUR_SCALE = SCALES[scaleIndex % SCALES.length];
	}
	
	public void newBeat(int beat) {
		// change scale sometimes
		if(beat % BEATS_PER_SCALE_CHANGE == 0 && P.store.getBoolean(Interphase.PATTERNS_AUTO_MORPH)) {
			setScaleIndex(MathUtil.randRange(0, SCALES.length - 1));
		}
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(Interphase.BEAT)) newBeat(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
