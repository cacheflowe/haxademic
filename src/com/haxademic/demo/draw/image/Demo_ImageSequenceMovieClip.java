package com.haxademic.demo.draw.image;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.file.FileUtil;

public class Demo_ImageSequenceMovieClip
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageSequenceMovieClip imageSequence;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1344 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
		String imagePath = FileUtil.getFile("haxademic/images/floaty-blob.anim/");
		imageSequence = new ImageSequenceMovieClip(imagePath, "png", 18);
	}
	
	public void drawApp() {
		p.background(0);
		
		if(p.frameCount % 100 == 50) {
			if(p.mousePercentX() < 0.5f)
				imageSequence.play();
			else 
				imageSequence.loop();
		}
		imageSequence.preCacheImages();
		imageSequence.update();
		
		p.image(imageSequence.image(), 0, 0);
		p.debugView.setValue("imageSequence.isFinished()", imageSequence.isFinished());
	}
	
}
