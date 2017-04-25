package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;

import processing.core.PGraphics;
import processing.event.KeyEvent;

public class BrightnessBumper {
	
	float brightness = 1f;

	public BrightnessBumper() {
		if(P.p == null) DebugUtil.printErr("===========\n Please wait until setup() to init BrightnessBumper \n===========");
		P.p.registerMethod("keyEvent", this);
	}
	
	public void keyEvent(KeyEvent e) {
		if(e.getKey() == '-') brightness -= 0.01f;
		if(e.getKey() == '=') brightness += 0.01f;
		if(e.getKey() == '0') brightness = 1f;
	}
	
	public void update(PGraphics pg) {
		if(brightness == 1f) return;
		BrightnessFilter.instance(P.p).setBrightness(brightness);
		BrightnessFilter.instance(P.p).applyTo(pg);
	}

}
