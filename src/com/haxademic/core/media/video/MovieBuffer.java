package com.haxademic.core.media.video;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;

import processing.core.PGraphics;
import processing.video.Movie;

public class MovieBuffer {
	
	public static HashMap<Movie, Number> moviesEventFrames = new HashMap<Movie, Number>();	// stores last frame that each movie was updated, to prevent storing frames more than necessary
	protected String moviePath;
	public PGraphics buffer;
	public Movie movie;
	public boolean hasNewFrame = false;

	public MovieBuffer(String moviePath) {
		this(new Movie(P.p, moviePath));
	}

	public MovieBuffer(Movie movie) {
		this.movie = movie;
		P.p.registerMethod("pre", this);
	}
	
	public void pre() {
		hasNewFrame = false;
		if(moviesEventFrames.containsKey(movie) == false) return;
		if(moviesEventFrames.get(movie).intValue() == P.p.frameCount) {
			hasNewFrame = true;
			if(movie.width > 10) {
				if(buffer == null) {
					buffer = PG.newPG2DFast(movie.width, movie.height);
				}
				else {
					// copyImage() is about 2x slower
					// ImageUtil.copyImage(movie, buffer);

					buffer.beginDraw();
					buffer.image(movie, 0, 0);
					buffer.endDraw();
				}
			}
		}
	}
}
