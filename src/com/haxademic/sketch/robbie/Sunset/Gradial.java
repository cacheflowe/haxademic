package com.haxademic.sketch.robbie.Sunset;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;

import processing.core.PGraphics;

public class Gradial {
	
	protected PAppletHax p;
	protected PGraphics pg;
	protected PGraphics GradialBuffer;
	
	protected ArrayList<GradialParticle> gradialParticles = new ArrayList<GradialParticle>();
	protected int MAX_GRADIALS = 30;

	public Gradial(PGraphics _pg) {
		p = P.p;
		pg = _pg;
		GradialBuffer = PG.newPG(pg.width, pg.height);
		PG.setTextureRepeat(GradialBuffer, false);
		
		// init gradial particles
		for (int i = 0; i < MAX_GRADIALS; i++) {
			GradialParticle gradialParticle = new GradialParticle(GradialBuffer, i);
			gradialParticles.add(gradialParticle);
		}

	}
	
	public void draw() {
		drawGradials();
	}

	public void drawGradials() {
		GradialBuffer.beginDraw();
		GradialBuffer.background(0);
		for (int i = 0; i < gradialParticles.size(); i++) {
			gradialParticles.get(i).display();
		}

		GradialBuffer.endDraw();
		BlurHFilter.instance(p).setBlurByPercent(7f, p.width);
		BlurHFilter.instance(p).applyTo(GradialBuffer);
		BlurVFilter.instance(p).setBlurByPercent(7f, p.height);
		BlurVFilter.instance(p).applyTo(GradialBuffer);
		
		GradialBuffer.loadPixels();

		p.debugView.setTexture("gradialTexture", GradialBuffer);
	}

}
