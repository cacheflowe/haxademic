package com.haxademic.core.draw.image;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.constants.PRenderers;

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

	public void pre() {
		if(moviesEventFrames.containsKey(movie) == false) return;
		if(moviesEventFrames.get(movie).intValue() == P.p.frameCount) {
			if(movie.width > 10) {
				if(buffer == null) buffer = P.p.createGraphics(movie.width, movie.height, PRenderers.P2D);
				else {
					buffer.beginDraw();
					buffer.image(movie, 0, 0);
					buffer.endDraw();
				}
			}
		}
	}
}
