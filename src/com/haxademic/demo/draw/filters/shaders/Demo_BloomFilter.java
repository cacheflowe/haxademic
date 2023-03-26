package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PGraphics;

public class Demo_BloomFilter
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics glowBuffer;

	protected void firstFrame() {
		glowBuffer = PG.newPG(pg.width, pg.height);
	}

	protected void drawApp() {
		// draw cube to buffer
		pg.beginDraw();
		pg.clear();
//		pg.background(0);
		PG.setCenterScreen(pg);
		PG.setBetterLights(pg);
		pg.fill(180 + 55f * P.sin(p.frameCount * 0.02f), 180 + 55f * P.sin(p.frameCount * 0.03f), 180 + 55f * P.sin(p.frameCount * 0.04f), 255);
		pg.stroke(0);
		pg.rotateX(p.frameCount * 0.01f);
		pg.rotateY(p.frameCount * 0.02f);
		pg.box(200 + 170f * P.sin(p.frameCount * 0.04f), 200 + 50f * P.sin(p.frameCount * 0.01f), 200 + 50f * P.sin(p.frameCount * 0.02f));
		pg.endDraw();

		// run bloom on off-screen buffer
		int bloomBlendMode = P.round(p.frameCount / 200f) % 3;
		BloomFilter.instance().setStrength(Mouse.xNorm * 5f);
		BloomFilter.instance().setBlurIterations(P.round(Mouse.yNorm * 4f));
		BloomFilter.instance().setBlendMode(bloomBlendMode);
		BloomFilter.instance().applyTo(pg);
		DebugView.setValue("Bloom blend mode", bloomBlendMode);
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// post 
		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.03f);
		GrainFilter.instance().applyTo(p);
	}
}
