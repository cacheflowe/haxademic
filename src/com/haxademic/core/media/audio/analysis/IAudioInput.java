package com.haxademic.core.media.audio.analysis;

import processing.core.PGraphics;

public interface IAudioInput {
	
	public void update(PGraphics pg);
	public AudioStreamData audioData();

}
