package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;

import processing.core.PGraphics;

public class Demo_BloomFilter
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected PGraphics glowBuffer;

	public void setupFirstFrame() {
		glowBuffer = p.createGraphics(pg.width, pg.height, PRenderers.P2D);
	}

	public void drawApp() {
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
		BloomFilter.instance(p).setStrength(p.mousePercentX() * 5f);
		BloomFilter.instance(p).setBlurIterations(P.round(p.mousePercentY() * 4f));
		BloomFilter.instance(p).setBlendMode(bloomBlendMode);
		BloomFilter.instance(p).applyTo(pg);
		p.debugView.setValue("Bloom blend mode", bloomBlendMode);
		
		// draw to screen
		p.image(pg, 0, 0);
		
		// post 
		GrainFilter.instance(p).setTime(p.frameCount * 0.01f);
		GrainFilter.instance(p).setCrossfade(0.03f);
		GrainFilter.instance(p).applyTo(p);
	}
}
