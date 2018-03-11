package com.haxademic.core.draw.image;

import java.util.ArrayList;
import java.util.Arrays;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Movie;

public class MovieToImageSequence {

	protected Movie movie;
	protected PGraphics movieBuffer;
	protected ArrayList<PImage> imageSequence;
	protected int[] pixels = new int[0];
	protected boolean capturing = true;
	
	public MovieToImageSequence(Movie movie) {
		this.movie = movie;
		movie.speed(0.7f);
		movie.loop();
	}
	
	public boolean complete() {
		return !capturing;
	}
	
	public float progress() {
		return (complete()) ? 1 : movie.time() / movie.duration();
	}
	
	public ArrayList<PImage> imageSequence() {
		return imageSequence;
	}
	
	protected void initCaptureBuffer() {
		imageSequence = new ArrayList<PImage>();
		movieBuffer = P.p.createGraphics(movie.width, movie.height, PRenderers.P2D);
		movie.jump(0);
		movie.play();
	}
	
	protected void captureFrame() {
		movieBuffer.copy(movie, 0, 0, movie.width, movie.height, 0, 0, movieBuffer.width, movieBuffer.height);
		movieBuffer.loadPixels();
		if(Arrays.equals(movieBuffer.pixels, pixels) == false) {
			imageSequence.add(movieBuffer.get());
			if(pixels.length != movieBuffer.pixels.length) {
				pixels = movieBuffer.pixels.clone();
			} else {
				System.arraycopy(movieBuffer.pixels, 0, pixels, 0, movieBuffer.pixels.length);
			}
		}
	}
	
	public void update() {
		if(movie == null) return;
		if(movie.width > 20) {
			if(movieBuffer == null) initCaptureBuffer();
			if(capturing) captureFrame();
			if(movie != null && movie.time() >= movie.duration() - 0.001f) finishMovieCapture();
		}
	}
	
	protected void finishMovieCapture() {
		capturing = false;
		movie.stop();
		movie.dispose();
		movie = null;
		movieBuffer = null;
	}
	
}
