package com.haxademic.core.media.audio.analysis;

import beads.AudioContext;

public interface IAudioInput {
	
	// interface methods 
	
	public AudioStreamData audioData();
	public void update();
	public void drawDebugBuffer();
	public void drawDataBuffers();
	
	// factory methods
	
	public static IAudioInput initBeadsInput() {
		return new AudioInputBeads();
	}
	
	public static IAudioInput initBeadsInput(AudioContext exsitingAc) {
		return new AudioInputBeads(exsitingAc);
	}

}
