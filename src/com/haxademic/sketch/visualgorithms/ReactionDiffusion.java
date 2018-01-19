package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.SharpenFilter;

public class ReactionDiffusion
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int RD_ITERATIONS = 4;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void drawSeed(int color) {
		// seed
		DrawUtil.setDrawCenter(p);
		p.fill(color);
		p.noStroke();
		p.ellipse(p.width/2, p.height/2, 150, 150);
		
		// outer ring
		p.noFill();
		p.stroke(color);
		p.strokeWeight(100);
		p.ellipse(p.width/2, p.height/2, 450, 450);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') drawSeed(255);
		if(p.key == 'a') drawSeed(0);
		if(p.key == 's') { p.background(0); drawSeed(255); }
	}

	public void drawApp() {
		if(p.frameCount == 1) p.background(0);
		if(p.frameCount <= 20) {
			drawSeed(255);
		}
		
		// effect
		for (int i = 0; i < RD_ITERATIONS; i++) {			
			BlurHFilter.instance(p).setBlurByPercent(0.5f, p.width);
			BlurHFilter.instance(p).applyTo(p);
			BlurVFilter.instance(p).setBlurByPercent(0.5f, p.height);
			BlurVFilter.instance(p).applyTo(p);
			SharpenFilter.instance(p).applyTo(p);
		}
	}

}

