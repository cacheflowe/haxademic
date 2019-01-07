package com.haxademic.app.haxvisual.viz;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.audio.analysis.input.AudioStreamData;
import com.haxademic.core.draw.camera.common.ICamera;
import com.haxademic.core.vendor.Toxiclibs;

import toxi.processing.ToxiclibsSupport;

public class ModuleBase {
	public PAppletHax p;
	public ToxiclibsSupport toxi;
	public AudioStreamData audioData;
	public ICamera _curCamera;
	
	public ModuleBase() {
		p = P.p;
		toxi = Toxiclibs.instance(P.p).toxi;
		audioData = p.audioData;
	}
	
	public void dispose() {
		p = null;
		toxi = null;
		audioData = null;
	}
}
