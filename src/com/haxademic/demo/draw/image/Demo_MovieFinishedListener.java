package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.video.MovieFinishedListener;
import com.haxademic.core.media.video.MovieFinishedListener.IMovieFinishedDelegate;

import processing.video.Movie;

public class Demo_MovieFinishedListener
extends PAppletHax
implements IMovieFinishedDelegate {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie video; 
	protected MovieFinishedListener movieFinished; 

	protected void overridePropsFile() {
	}

	public void setupFirstFrame() {
		video = DemoAssets.movieFractalCube();
		video.loop();
		movieFinished = new MovieFinishedListener(video, this);
	}
	
	public void drawApp() {
		p.background(0);
		p.image(video, 0, 0);
		// movieFinished.disconnect();
	}

	@Override
	public void videoFinished(Movie movie) {
		P.out("Movie finished!");
	}
	
}

