package com.haxademic.demo.media.video;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.media.DemoAssets;

import processing.video.Movie;

public class Demo_Movie_Vanilla 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie movie;
	protected float lastTime;
	
	protected void firstFrame () {
		movie = new Movie(P.p, P.path(DemoAssets.movieFractalCubePath));
		movie.play();
		lastTime = 0;
	}

	protected void forceVideoLoop() {
		boolean isFinished = movie.time() == lastTime && movie.time() > 0.6;
		if(isFinished) {
			movie.jump(0);
			movie.play();
		}
		lastTime = movie.time();
	}
	
	protected void drawApp() {
		setDebugValues();
		forceVideoLoop();
		p.background(0, 0, 0);
		p.image(movie, 0, 0);
	}

	protected void setDebugValues() {
		// video lib 1.x+ props
		DebugView.setValue("movie time", movie.time());
		DebugView.setValue("movie W", movie.width);
		DebugView.setValue("movie h", movie.height);
		DebugView.setValue("movie loaded", movie.loaded);
		DebugView.setValue("movie available", movie.available());
		DebugView.setValue("movie frameRate", movie.frameRate);
		DebugView.setValue("movie isLoaded", movie.isLoaded());
		DebugView.setValue("movie hasBufferSink", movie.hasBufferSink());
		// video lib 2.0+ props
		DebugView.setValue("movie sourceWidth", movie.sourceWidth);
		DebugView.setValue("movie sourceHeight", movie.sourceHeight);
		DebugView.setValue("movie duration", movie.duration());
		DebugView.setValue("movie time", movie.time());
		DebugView.setValue("movie isPlaying", movie.isPlaying());
		DebugView.setValue("movie isLooping", movie.isLooping());
		DebugView.setValue("movie isPaused", movie.isPaused());
	}
	
}

