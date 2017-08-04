package com.haxademic.app.haxvisual.viz;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.draw.color.ColorGroup;

public class ElementBase {

	protected PApplet p;
	protected ToxiclibsSupport toxi;
	public AudioInputWrapper _audioData;

	public ElementBase( PApplet p5, ToxiclibsSupport toxiclibs, AudioInputWrapper audioData ) {
		p = p5;
		toxi = toxiclibs;
		_audioData = audioData;
	}
	
	public void dispose() {
		p = null;
		toxi = null;
		_audioData = null;
	}

	public void pause() {}
	public void updateColorSet( ColorGroup colors ){}
	public void updateLineMode(){}
	public void updateCamera(){}
	public void updateTiming(){};
	public void updateSection(){};
}
