package com.haxademic.core.draw.image;

import org.gstreamer.elements.PlayBin2;

import processing.video.Movie;

public class MovieFinishedListener {

	protected Movie movie;
	protected IMovieFinishedDelegate delegate;
	protected boolean connected = true;
	
	public MovieFinishedListener(Movie movie, IMovieFinishedDelegate delegate) {
		this.movie = movie;
		this.movie.playbin.connect(FINISHING);
		this.delegate = delegate;
	}
	
	public void connect() {
		if(connected) return;
		movie.playbin.connect(FINISHING);
		connected = true;
	}
	
	public void disconnect() {
		if(!connected) return;
		movie.playbin.disconnect(FINISHING);
		connected = false;
	}
	
	public void dispose() {
		disconnect();
		movie = null;
	}
	
	// GSTREAMER CALLBACK
	
	protected PlayBin2.ABOUT_TO_FINISH FINISHING = new PlayBin2.ABOUT_TO_FINISH() {
		@Override
		public void aboutToFinish(PlayBin2 playbin) {
			delegate.videoFinished(movie);
		}
	};
	
	// PUBLIC CALLBACK INTERFACE
		
	public interface IMovieFinishedDelegate {
		public void videoFinished(Movie movie);
	}
}
