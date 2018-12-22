package com.haxademic.app.haxvisual.viz;

import processing.core.PImage;

public interface IAudioTexture
extends IVizElement {
//	public void updateTexture( AudioInputWrapper audioInput );
	public PImage getTexture();
	public void dispose();
}
