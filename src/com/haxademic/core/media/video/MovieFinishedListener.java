package com.haxademic.core.media.video;

import org.freedesktop.gstreamer.elements.PlayBin;

import processing.video.Movie;

public class MovieFinishedListener {

	protected Movie movie;
	protected IMovieFinishedDelegate delegate;
	protected boolean connected = false;
	
	public MovieFinishedListener(Movie movie, IMovieFinishedDelegate delegate) {
		this.movie = movie;
		this.delegate = delegate;
		connect();
	}
	
	public void setMovie(Movie newMovie) {
		disconnect();
		movie = newMovie;
		connect();
	}
	
	public void connect() {
		if(connected) return;
		movie.playbin.connect(FinishCallback);
		connected = true;
	}
	
	public void disconnect() {
		if(!connected) return;
		movie.playbin.disconnect(FinishCallback);
		connected = false;
	}
	
	public void dispose() {
		disconnect();
		movie = null;
	}
	
	// GSTREAMER CALLBACK
	
	// for video library v1
	protected PlayBin.ABOUT_TO_FINISH FinishCallback = new PlayBin.ABOUT_TO_FINISH() {
		@Override
		public void aboutToFinish(PlayBin playbin) {
			delegate.videoFinished(movie);
		}
	};

	// for video library v2
//	protected PlayBin.ABOUT_TO_FINISH FinishCallback = new PlayBin.ABOUT_TO_FINISH() {
//		@Override
//		public void aboutToFinish(PlayBin playbin) {
//			delegate.videoFinished(movie);
//		}
//	};
	
	// PUBLIC CALLBACK INTERFACE
		
	public interface IMovieFinishedDelegate {
		public void videoFinished(Movie movie);
	}
}
