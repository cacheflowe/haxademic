package com.haxademic.core.media.video;

import org.freedesktop.gstreamer.elements.PlayBin;

import com.haxademic.core.app.P;

import processing.video.Movie;

public class MovieFinishedListener {

	protected Movie movie;
	protected IMovieFinishedDelegate delegate;
	protected int lastFrameTriggered = -999;
	public static int TRIGGER_THROTTLE_FRAMES = 60;
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
	
    protected PlayBin.ABOUT_TO_FINISH FinishCallback = new PlayBin.ABOUT_TO_FINISH() {
        @Override
        public void aboutToFinish(PlayBin playbin) {
            int curFrame = P.p.frameCount; // solve for multiple triggers. this is not perfect...
            if(curFrame > lastFrameTriggered + TRIGGER_THROTTLE_FRAMES) {
                lastFrameTriggered = curFrame;
                delegate.videoFinished(movie);
            }
        }
    };

	// PUBLIC CALLBACK INTERFACE
		
	public interface IMovieFinishedDelegate {
		public void videoFinished(Movie movie);
	}
}
