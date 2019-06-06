package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.image.ImageSequenceMovieClip;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.video.MovieToImageSequence;

public class Demo_MovieToImageSequence 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MovieToImageSequence movieToImageSequence;
	protected ImageSequenceMovieClip movieClip;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1400 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 700 );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
		
	public void setupFirstFrame () {
		movieToImageSequence = new MovieToImageSequence(DemoAssets.movieFractalCube(), 0.5f);
	}
	
	protected void convertToMoviePlayer() {
		movieClip = new ImageSequenceMovieClip(movieToImageSequence.imageSequence(), 30);
		movieClip.loop();
//		movieClip.pause();
	}
	
	public void drawApp() {
		movieToImageSequence.update();
		if(movieClip == null && movieToImageSequence.complete()) convertToMoviePlayer();
		p.debugView.setValue("movie convert progress", movieToImageSequence.progress());
		
		// show relatime comverstion
		if(movieToImageSequence.imageSequence() != null) {
			int sequenceFrame = p.frameCount % movieToImageSequence.imageSequence().size();
			p.image(movieToImageSequence.imageSequence().get(sequenceFrame), 0, 0);
		}
		
		// show looped movieclip after capture
		if(movieClip != null) {
			movieClip.update();
			movieClip.setFrameByProgress(p.mousePercentX() * 4f);
			p.image(movieClip.image(), 700, 0);
		}
	}
	
}

