package com.haxademic.sketch.buffer;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.image.TickerScroller;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class TickerScrollerTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected TickerScroller ticker;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 640 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 182 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.FPS, 30 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, 311 );
	}

	public void setup() {
		super.setup();
		PImage img = p.loadImage(FileUtil.getFile("images/hbd-hazen.png"));
		ticker = new TickerScroller(img, p.color(255), 640, 182, 4.f);
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		// draw buffer to screen
		DrawUtil.setDrawCenter(p);
		p.translate(p.width/2, p.height/2);
//		p.rotate(0.1f);
		ticker.update();
		p.image(ticker.image(), 0, 0);
		BrightnessFilter.instance(p).setBrightness(1.35f);
		BrightnessFilter.instance(p).applyTo(p);
		VignetteFilter.instance(p).applyTo(p);
//		VignetteFilter.instance(p).applyTo(p);
	}	

}

