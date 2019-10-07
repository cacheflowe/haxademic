package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.math.MathUtil;

public class ReactionDiffusion
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int RD_ITERATIONS = 4;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 640 );
	}

	public void drawSeed(int color) {
		// seed
		PG.setDrawCenter(p);
		p.fill(color);
		p.noStroke();
		p.ellipse(p.width/2, p.height/2, 150, 150);
		
		// outer ring
		p.noFill();
		p.stroke(color);
		p.strokeWeight(100);
		p.ellipse(p.width/2, p.height/2, 450, 450);
	}
	
	public void drawShape() {
		PG.setDrawCenter(p);
		p.pushMatrix();
		p.noStroke();
		int color = (MathUtil.randBoolean() == true) ? 0 : 255;
		p.fill(color);
		p.fill(255);
		// p.fill(ColorUtil.colorFromHex(ColorUtil.randomHex()));
		p.translate(p.width/2, p.height/2);
		// p.rotate(MathUtil.randRangeDecimal(0, P.TWO_PI));
		p.rotate(p.frameCount * 0.01f);
		// p.rect(0, 0, MathUtil.randRangeDecimal(30, 800), MathUtil.randRangeDecimal(30, 80));
		p.rect(0, 0, 1000, 30);
		p.popMatrix();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'm') drawSeed(255);
		if(p.key == ' ') drawSeed(255);
		if(p.key == 'a') drawSeed(0);
		if(p.key == 's') drawShape();
	}

	public void drawApp() {
		if(p.frameCount == 1) p.background(0);
		// if(p.frameCount <= 20) drawSeed(255);
		drawShape();
		
		// effect
		float blurAmp = P.map(p.mouseX, 0, p.width, 0.25f, 1.5f);
		float sharpAmp = P.map(p.mouseY, 0, p.height, 0.5f, 2f);
		// blurAmp = 0.5f;
		// sharpAmp = 1f;
		for (int i = 0; i < RD_ITERATIONS; i++) {			
			BlurHFilter.instance(p).setBlurByPercent(blurAmp, p.width);
			BlurHFilter.instance(p).applyTo(p);
			BlurVFilter.instance(p).setBlurByPercent(blurAmp, p.height);
			BlurVFilter.instance(p).applyTo(p);
			SharpenFilter.instance(p).setSharpness(sharpAmp);
			SharpenFilter.instance(p).applyTo(p);
		}
	}

}

