package com.haxademic.sketch.robbie;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;
import processing.video.Movie;

public class VideoMask 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected Movie testMovie;
	protected PImage maskImage;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1080 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1080 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
//		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.APP_NAME, "VideoMask" );
		p.appConfig.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
//		p.appConfig.setProperty( AppSettings.FPS, 12 );
	}
	
	public void setupFirstFrame() {
		p.background(0);
		p.noStroke();
		
		testMovie = new Movie(p, FileUtil.getFile("images/Test_1080x1080_24fps.mp4/"));
		testMovie.loop();
		
		maskImage = p.loadImage(FileUtil.getFile("images/joyrideSF_mask_1080x1080.png"));
		testMovie.mask(maskImage);
	}

	public void drawApp() {
		p.background(100);
		
		if(testMovie.width > 10) {
			testMovie.read();
			testMovie.mask(maskImage);
			p.image(testMovie, 0, 0);
		}	
//		if (testMovie.available()) {
//			testMovie.read();
//			testMovie.mask(maskImage);
//			P.out(p.frameCount);
//		}
//		p.image(testMovie, 0, 0);
	}
	
}
