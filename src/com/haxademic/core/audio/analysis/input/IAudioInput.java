package com.haxademic.core.audio.analysis.input;

import processing.core.PGraphics;

public interface IAudioInput {
	
	public void update(PGraphics pg);
	public AudioStreamData audioData();

}
