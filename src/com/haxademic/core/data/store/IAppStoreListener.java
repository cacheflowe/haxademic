package com.haxademic.core.data.store;

import processing.core.PGraphics;
import processing.core.PImage;

public interface IAppStoreListener {
	public void updatedNumber(String key, Number val);
	public void updatedString(String key, String val);
	public void updatedBoolean(String key, Boolean val);
	public void updatedImage(String key, PImage val);
	public void updatedBuffer(String key, PGraphics val);
}