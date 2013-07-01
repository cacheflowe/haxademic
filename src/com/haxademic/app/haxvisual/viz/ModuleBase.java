package com.haxademic.app.haxvisual.viz;

import toxi.processing.ToxiclibsSupport;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.cameras.common.ICamera;

public class ModuleBase {
	public PAppletHax p;
	public ToxiclibsSupport toxi;
	public AudioInputWrapper _audioData;
	public ICamera _curCamera;
	
	public ModuleBase() {
		p = PAppletHax.getInstance();
		toxi = p.getToxi();
		_audioData = p.getAudio();
	}
	
	public void dispose() {
		p = null;
		toxi = null;
		_audioData = null;
	}
}
