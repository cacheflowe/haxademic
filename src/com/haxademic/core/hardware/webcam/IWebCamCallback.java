package com.haxademic.core.hardware.webcam;

import processing.core.PImage;

public interface IWebCamCallback {
	public void newFrame(PImage frame);
}
