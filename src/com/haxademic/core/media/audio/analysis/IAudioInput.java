package com.haxademic.core.media.audio.analysis;

import beads.AudioContext;
import processing.core.PGraphics;

public interface IAudioInput {
	
	// interface methods 
	
	public void update(PGraphics pg);
	public AudioStreamData audioData();
	
	// factory methods
	
	public static IAudioInput initBeadsInput() {
		return new AudioInputBeads();
	}
	public static IAudioInput initBeadsInput(AudioContext exsitingAc) {
		return new AudioInputBeads(exsitingAc);
	}

}
