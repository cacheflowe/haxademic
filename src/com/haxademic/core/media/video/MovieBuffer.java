package com.haxademic.core.media.video;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PGraphics;
import processing.video.Movie;

public class MovieBuffer {
	
	public static HashMap<Movie, Number> moviesEventFrames = new HashMap<Movie, Number>();	// stores last frame that each movie was updated, to prevent storing frames more than necessary
	protected String moviePath;
	public PGraphics buffer;
	public Movie movie;

	public MovieBuffer(String moviePath) {
		movie = new Movie(P.p, moviePath);
		P.p.registerMethod("pre", this);
	}

	public MovieBuffer(Movie movie) {
		this.movie = movie;
		P.p.registerMethod("pre", this);
	}
	
	public void pre() {
		if(moviesEventFrames.containsKey(movie) == false) return;
		if(moviesEventFrames.get(movie).intValue() == P.p.frameCount) {
			if(movie.width > 10) {
				if(buffer == null) {
					buffer = PG.newPG(movie.width, movie.height, false, true);
				}
				else {
					ImageUtil.copyImage(movie, buffer);
				}
			}
		}
	}
}
