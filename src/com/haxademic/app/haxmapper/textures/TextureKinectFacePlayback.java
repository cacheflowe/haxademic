package com.haxademic.app.haxmapper.textures;

import com.haxademic.sketch.hardware.kinect_openni.KinectFaceRecorder;

import processing.core.PGraphics;

public class TextureKinectFacePlayback 
extends BaseTexture {

	protected int _eqIndex;
	protected float bright = 0;
	
	public TextureKinectFacePlayback( int width, int height ) {
		super();
	}
	
	public PGraphics texture() {
		return KinectFaceRecorder.playbackTexture;
	}

	// overridden!!!!
	public void update() {
		resetUseCount(); // this should be the last thing that happens in a frame, to help with texture pool optimization
		postProcess();
	}
}
