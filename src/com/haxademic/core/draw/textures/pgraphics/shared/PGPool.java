package com.haxademic.core.draw.textures.pgraphics.shared;

import java.util.ArrayList;

import com.haxademic.core.app.P;

import processing.core.PGraphics;

public class PGPool {

	public static ArrayList<PGraphicsWrapper> pgWrappers = new ArrayList<PGraphicsWrapper>();
	
	public static PGraphics getPG(int w, int h) {
		// find an available PG
		for (int i = 0; i < pgWrappers.size(); i++) {
			PGraphicsWrapper wrapper = pgWrappers.get(i);
			if(wrapper.available(w, h)) {
				wrapper.setUpdated();
				return wrapper.pg;
			}
		}
		// if none found/returned, make a new one, add it to collection, and return
		PGraphicsWrapper newWrapper = new PGraphicsWrapper(w, h);
		newWrapper.setUpdated();
		pgWrappers.add(newWrapper);
		P.p.debugView.setValue("pgWrappers.size()", pgWrappers.size());
		return newWrapper.pg;
	}
	
	public static void updatePG(PGraphics pg) {
		for (int i = 0; i < pgWrappers.size(); i++) {
			PGraphicsWrapper wrapper = pgWrappers.get(i);
			if(pg == wrapper.pg) wrapper.setUpdated();
		}
	}
	
}
