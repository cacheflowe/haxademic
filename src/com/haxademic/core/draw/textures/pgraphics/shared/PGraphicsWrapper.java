package com.haxademic.core.draw.textures.pgraphics.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;

public class PGraphicsWrapper {
	
	public int width;
	public int height;
	public PGraphics pg;
	public int lastUpdated;
	
	public PGraphicsWrapper(int w, int h) {
		width = w;
		height = h;
		pg = PG.newPG(w, h);
		lastUpdated = -999;
	}
	
	public boolean available(int matchW, int matchH) {
		return P.p.frameCount - lastUpdated > 0 && 		// lastUpdated > 10
				width == matchW && 
				height == matchH;
	}
	
	public void setUpdated() {
		lastUpdated = P.p.frameCount;
	}
	
}

