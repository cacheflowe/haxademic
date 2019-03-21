package com.haxademic.app.musicvideos;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

import processing.core.PImage;
import processing.video.Movie;

public class BrimDuels 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float w = 1280;
	protected float h = 720;
	protected int FRAMES = 1038; // (17.299s)
	protected PImage waveform;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE, false);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1);
		p.appConfig.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, FRAMES + 1);
		if(p.appConfig.getBoolean(AppSettings.RENDERING_MOVIE, false) == true) {
			w = 1920;
			h = 1080;
		}
		p.appConfig.setProperty(AppSettings.WIDTH, (int) w);
		p.appConfig.setProperty(AppSettings.HEIGHT, (int) h);
		p.appConfig.setProperty(AppSettings.FPS, 60);
		p.appConfig.setProperty(AppSettings.LOOP_FRAMES, FRAMES);
	}

	protected void setupFirstFrame() {
		waveform = P.getImage("audio/brim-duels/output-16k.png");
	}

	public void drawApp() {
		background(0);
		
		// draw to main buffer
		pg.beginDraw();
		pg.background(0);
		
		// draw waveform
		float waveformScale = 0.4f;
		float waveW = waveform.width * waveformScale;
		float waveH = waveform.height * waveformScale;
		pg.image(waveform, -waveW * p.loop.progress(), 0, waveW, waveH);
		pg.image(waveform, -waveW * p.loop.progress() + waveW, 0, waveW, waveH);
		
		
		pg.endDraw();
		
		// draw to screen
		p.image(pg, 0, 0);
	}

}

