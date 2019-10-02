package com.haxademic.core.hardware.dmx.editor;

import processing.core.PGraphics;
import processing.core.PVector;

public interface ILight {
	public String toSaveString();
	public boolean isActive();
	public void setActive(PVector activePoint);
	public void sampleColorTexture(PGraphics pgUI, PGraphics textureMap);
	public int dmxChannel();
	public void setDmxChannel(int channel);
	public void update(PGraphics pg, int index);
}
