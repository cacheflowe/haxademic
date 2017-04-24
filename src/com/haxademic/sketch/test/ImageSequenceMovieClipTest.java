package com.haxademic.sketch.test;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.file.FileUtil;

import processing.core.PImage;

public class ImageSequenceMovieClipTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageSequenceMovieClip imageSequence;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1344 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RETINA, false );
	}

	public void setup() {
		super.setup();
		
		String imagePath = FileUtil.getFile("images/floaty-blob.anim/"); // File.separator
		imageSequence = new ImageSequenceMovieClip(imagePath, "png", 18);
	}
	
	public void drawApp() {
		p.background(0);
		
		imageSequence.preCacheImages();
		if(p.frameCount == 100) imageSequence.play();
		
		imageSequence.update();
		PImage frameImg = (imageSequence.isPlaying() == true) ? imageSequence.image() : imageSequence.getFrame(imageSequence.numImageFiles() - 1);
		p.image(frameImg, 0, 0);
		p.text(""+imageSequence.isFinished(), 20, 20);
	}
	
}
