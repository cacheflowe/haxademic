package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.file.FileUtil;

public class Demo_ImageSequenceMovieClip
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ImageSequenceMovieClip imageSequence;
	protected ImageSequenceMovieClip imageSequenceCopy;
	protected ImageSequenceMovieClip imageSequenceManual;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1344 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 700 );
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

		// create a copy & play it offset
		if(imageSequenceCopy == null && imageSequence.numImageFiles() > 0) {
			imageSequenceCopy = imageSequence.copy();
			imageSequenceManual = imageSequence.copy();
		}
		if(imageSequenceCopy != null) {
			if(p.frameCount % 100 == 70) {
				imageSequenceCopy.play();
			}
			imageSequenceCopy.update();
			p.image(imageSequenceCopy.image(), 0, 50);
			p.debugView.setValue("imageSequenceCopy.isFinished()", imageSequenceCopy.isFinished());
			
			// manual copy
			p.image(imageSequenceManual.getFrameByProgress(p.mousePercentX()), 0, 100);
		}
		
	}
	
}
