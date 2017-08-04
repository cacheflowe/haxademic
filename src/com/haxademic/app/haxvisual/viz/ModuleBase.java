package com.haxademic.app.haxvisual.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.AudioInputWrapper;
import com.haxademic.core.camera.common.ICamera;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.processing.ToxiclibsSupport;

public class ModuleBase {
	public PAppletHax p;
	public ToxiclibsSupport toxi;
	public AudioInputWrapper _audioData;
	public ICamera _curCamera;
	
	public ModuleBase() {
		p = P.p;
		toxi = Toxiclibs.instance(P.p).toxi;
		_audioData = p.getAudio();
	}
	
	public void dispose() {
		p = null;
		toxi = null;
		_audioData = null;
	}
}
