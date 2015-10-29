package com.haxademic.core.vendor;

import processing.core.PApplet;
import toxi.processing.ToxiclibsSupport;

public class Toxiclibs {
	public ToxiclibsSupport toxi;
	public static Toxiclibs instance;

	public Toxiclibs(PApplet p) {
		toxi = new ToxiclibsSupport(p);
	}

	public static Toxiclibs instance(PApplet p) {
		if(instance != null) return instance;
		instance = new Toxiclibs(p);
		return instance;
	}
}
