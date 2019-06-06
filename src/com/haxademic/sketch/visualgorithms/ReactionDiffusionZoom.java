package com.haxademic.sketch.visualgorithms;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;

public class ReactionDiffusionZoom
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int RD_ITERATIONS = 4;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 960 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 960 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 300 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 2100 );
	}

	public void drawSeed(int color) {
		// seed
		PG.setDrawCenter(p);
		p.noFill();
		p.stroke(color);
		p.strokeWeight(300);
		p.ellipse(p.width/2, p.height/2, 900, 900);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'm') drawSeed(255);
		if(p.key == ' ') drawSeed(255);
		if(p.key == 'a') drawSeed(0);
	}

	public void drawApp() {
		if(p.frameCount == 1) p.background(0);
		drawSeed(255);
		
		// TEST zoom
//		ImageUtil.cropFillCopyImage(DemoAssets.textureJupiter(), p.g, true);
//		RepeatFilter.instance(p).setZoom(1f + 0.01f * P.sin(p.frameCount * 0.1f));
//		RepeatFilter.instance(p).applyTo(p.g);
		
		// effect
		float blurAmp = P.map(p.mouseX, 0, p.width, 0.25f, 1.5f);
		float sharpAmp = P.map(p.mouseY, 0, p.height, 0.5f, 2f);
		// blurAmp = 0.5f;
		// sharpAmp = 1f;
		
		RD_ITERATIONS = 15;
		for (int i = 0; i < RD_ITERATIONS; i++) {			
			BlurHFilter.instance(p).setBlurByPercent(blurAmp, p.width);
			BlurHFilter.instance(p).applyTo(p);
			BlurVFilter.instance(p).setBlurByPercent(blurAmp, p.height);
			BlurVFilter.instance(p).applyTo(p);
			SharpenFilter.instance(p).setSharpness(sharpAmp);
			SharpenFilter.instance(p).applyTo(p);
			SaturationFilter.instance(p).setSaturation(0);
			SaturationFilter.instance(p).applyTo(p.g);
		}
		
		
		if(p.frameCount % 300 < 150) {
			PG.zoomReTexture(p.g, 1.03f);
//			RepeatFilter.instance(p).setZoom(1.01f);
//			RepeatFilter.instance(p).applyTo(p.g);
		} else {
//			RepeatFilter.instance(p).setZoom(0.99f);
//			RepeatFilter.instance(p).applyTo(p.g);
			PG.zoomReTexture(p.g, 0.97f);			
		}
		
	}

}

