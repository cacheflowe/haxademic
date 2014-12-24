package com.haxademic.core.render;

import java.awt.image.BufferedImage;

import processing.core.PApplet;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.system.FileUtil;
import com.haxademic.core.system.SystemUtil;

public class GifRenderer {

	public AnimatedGifEncoder encoder;
	protected int _frameRate = 30;
	protected int _quality = 10;
	protected int _framesRendered = 0;

	public GifRenderer(int frameRate, int quality) {
		_frameRate = frameRate;
		_quality = quality;
	}
	
	public void startGifRender(PApplet p) {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.getHaxademicOutputPath() + SystemUtil.getTimestamp(p) + "-export.gif" );
		encoder.setFrameRate( _frameRate );
		encoder.setQuality( _quality );
		encoder.setRepeat( 0 );
		P.println("== started rendering gif ==");
	}
		
	public void renderGifFrame(PApplet p) {
		if(encoder == null) return; // no-op if we're starting after frameCount == 1
		_framesRendered++;
		P.println("== rendering gif frame: "+_framesRendered+" ==");
		PImage screenshot = p.get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);
	}

	public void finish() {
		P.println("== finished rendering gif ==");
		encoder.finish();
		encoder = null;
	}
}
