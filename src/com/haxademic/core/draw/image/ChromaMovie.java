package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class ChromaMovie {

	static public PImage BLANK_IMAGE;

	Movie movie;
	PGraphics buffer;
	
	String videoToLoad;
	int loadStartTime;
	int numFrames = 0;
	boolean debug = false;
	
	float fps;
	static float DEFAULT_FPS = 30;
	
	boolean isPlaying = false;
	boolean isLooping = false;
	boolean isResetting = false;
	
	protected float chromaThreshold = 0.08f;
	protected float chromaSmoothing = 0.75f;
	

	/*
	 * 	Usage:
	 * 	VideoChromaMovieClip clip = new VideoChromaMovieClip("video/test/blast_hole_no_alpha.mov"); 
	 **/

	public ChromaMovie(String videoPath, float fps, boolean loadAsync) {
		this.fps = fps;
		if(BLANK_IMAGE == null) BLANK_IMAGE = P.p.createImage(32, 32, P.ARGB);
		loadVideo(videoPath, loadAsync);
	}
	
	public ChromaMovie(String videoPath) {
		this(videoPath, DEFAULT_FPS, false);
	}

	public Movie movie() {
		return movie;
	}
	
	public float curTime() {
		return movie.time();
	}
	
	public float duration() {
		return movie.duration();
	}
	
	public boolean isLoaded() {
		return buffer != null;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void loop() {
		if(movie != null) {
			movie.jump(0);
			movie.loop();
		}
		isPlaying = true;
		isLooping = true;
	}
	
	public void play() {
		if(movie != null) {
			movie.play();
		}
		isPlaying = true;
		isLooping = false;
	}
	
	public void playFromStart() {
		if(movie != null) movie.jump(0);
		play();
		isResetting = false;
	}
	
	public void pause() {
		if(movie != null) {
			movie.pause();
		}
	}
	
	public void reset() {
		if(movie != null) {
			isPlaying = false;
			isLooping = false;
//			movie.stop();
			movie.jump(0);
			play();
			isResetting = true;
		}
	}
	
	public PImage image() {
		if(isLoaded() == false || isPlaying == false) {
			return BLANK_IMAGE;
		} else {
			return buffer;
		}
	}
	
	public void setChromaThreshold(float chromaThreshold) {
		this.chromaThreshold = chromaThreshold;
	}

	public void setChromaSmoothing(float chromaSmoothing) {
		this.chromaSmoothing = chromaSmoothing;
	}
	
	///////////////////////////////////
	// Load video
	///////////////////////////////////
	
	public void loadVideo(String videoPath, boolean loadAsync) {
		loadStartTime = P.p.millis();
		if(loadAsync == true) {
			new Thread(new Runnable() { public void run() {
				loadMovie(videoPath);
			}}).start();	
		} else {
			loadMovie(videoPath);
		}
	}
	
	protected void loadMovie(String videoPath) {
		movie = new Movie(P.p, videoPath);
		if(fps != DEFAULT_FPS) movie.speed(fps/DEFAULT_FPS);
		if(debug == true) P.println("Movie Load Time: "+((P.p.millis() - loadStartTime)/1000) + " seconds");
		// if started before loaded, get it going
		if(isLooping == true) loop();
		else if(isPlaying == true) play();
	}
	
	protected void lazyCreateBuffer() {
		if(buffer == null && movie != null && movie.width > 0) {
			buffer = P.p.createGraphics(movie.width, movie.height, P.P2D);
			if(debug == true) P.println("Movie buffer created");
		}
	}
		
	///////////////////////////////////
	// Cleanup
	///////////////////////////////////
	
	public void dispose() {
		movie = null;
	}
	
	///////////////////////////////////
	// Animation
	///////////////////////////////////
	
	public void update() {
		lazyCreateBuffer();
		if(isLoaded() == true) {
			// copy movie to buffer & run chromakey on black
			buffer.beginDraw();
			buffer.clear();
			buffer.image(movie, 0, 0);
			buffer.endDraw();
			ChromaColorFilter.instance(P.p).presetWhiteKnockout();
			ChromaColorFilter.instance(P.p).setThresholdSensitivity(chromaThreshold);
			ChromaColorFilter.instance(P.p).setSmoothing(chromaSmoothing);
			ChromaColorFilter.instance(P.p).applyTo(buffer);
			
			// check for end
			if(isPlaying == true && isLooping == false && movie.time() >= movie.duration() - 0.1f) {
				movie.pause();
				isPlaying = false;
				buffer.beginDraw();
				buffer.clear();
				buffer.endDraw();
			}
		}
		
		// helper for resetting the movie back to the first frame. not precise, but makes sure video plays a couple of frames to the buffer before pausing. bah!
		if(isResetting == true) {
			if(debug == true) P.println("movie.time()", movie.time());
			if(movie.time() > 0.4f) {
				movie.pause();
				movie.jump(0);
				isResetting = false;
			}
		}
	}

}
