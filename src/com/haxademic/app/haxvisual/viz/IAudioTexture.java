package com.haxademic.app.haxvisual.viz;

import processing.core.PImage;

import com.haxademic.core.audio.AudioInputWrapper;

public interface IAudioTexture
extends IVizElement {
	public void updateTexture( AudioInputWrapper audioInput );
	public PImage getTexture();
	public void dispose();
}
