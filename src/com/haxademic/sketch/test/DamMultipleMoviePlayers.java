package com.haxademic.sketch.test;

import java.util.ArrayList;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.BrightnessFilter;
import com.haxademic.core.image.filters.shaders.SaturationFilter;
import com.haxademic.core.system.FileUtil;

import processing.video.Movie;

public class DamMultipleMoviePlayers
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ArrayList<Movie> movies;
	protected String movieLocation = "video/dancelab/";
	protected String[] movieFiles = {
		"_output-crop.mp4",
		"_output.mp4",
		"ghosting-example-flip-ben-fast-portrait.mp4",
		"ghosting-example-flip-ben-pingpong-portrait.mp4",
		"ghosting-example-justin-ghosted-portrait.mp4",
		"ghosting-example-matt-loop-portrait.mp4",
		"ghosting-example-matt-ping-pong-portrait.mp4"
	};
	protected int videoLoadIndex = 0;
	
	protected int MAX_MOVIES = 6;
		
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D ); // P.JAVA2D P.FX2D P.P2D P.P3D
		p.appConfig.setProperty( AppSettings.WIDTH, 1800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 534 );
		p.appConfig.setProperty( AppSettings.SHOW_STATS, true );
	}

	public void setup() {
		super.setup();
		
		// load movies
		movies = new ArrayList<Movie>();
		for(int i = 0; i < 1; i++) addNextMovie();
	}
	
	public void addNextMovie() {
		// add a new one into the mix
		P.println("Loading: ", movieLocation + movieFiles[videoLoadIndex]);
		Movie movie = new Movie(p, FileUtil.getFile(movieLocation + movieFiles[videoLoadIndex]));
		movie.play();
		movie.loop();
		movie.volume(0);
		movies.add(movie);
		
		// loop back on video library
		videoLoadIndex++;
		if(videoLoadIndex >= movieFiles.length) videoLoadIndex = 0;
	}
	
	public void removeOldestVideo() {
		// Dispose the oldest movie on a thread to prevent blocking
		// Also do some manual garbage collection to help keep the heap small
		final Movie oldestMovie = movies.remove(0);
		new Thread(new Runnable() {
	         public void run() {
	        	 oldestMovie.stop();
	        	 oldestMovie.dispose();
	        	 System.gc();
	         }
		}).start();
	}
	
	protected void restartAllMovies() {
		// restart all movies
		for (int i = 0; i < movies.size(); i++) {
			movies.get(i).jump(0);
		}
	}
	
	public int numLoadedMovies() {
		int loadedMovies = 0;
		for (int i = 0; i < movies.size(); i++) {
			if(movies.get(i).height > 0) loadedMovies++;
		}
		return loadedMovies;
	}

	public void drawApp() {	
		// load a new movie once in a while
		if(p.frameCount % 200 == 0) {
			loadMovieThread();
		}
	
		// draw movies
		if(movies.size() > 0) {
			if(numLoadedMovies() > MAX_MOVIES) removeOldestVideo();
			
			int vidW = p.width / MAX_MOVIES;
			for (int i = 0; i < movies.size(); i++) {
				int vidX = i * vidW;
				Movie movie = movies.get(i);
				if(movie.height > 0) {
					float drawScale = (float)vidW / (float)movie.width;
					p.image(movie, vidX, 0, movie.width * drawScale, movie.height * drawScale);
				}
			}
		}
		
		// special effects
		BrightnessFilter.instance(p).setBrightness(1.5f);
		BrightnessFilter.instance(p).applyTo(p);
		SaturationFilter.instance(p).setSaturation(0.3f);
		SaturationFilter.instance(p).applyTo(p);
	}
	
	
	////////////////////////////////
	// Load the next movie on a thread to prevent blocking
	////////////////////////////////
	protected UpdateAsync _updater;
	protected boolean _updateComplete = true;
	protected Thread _updateThread;

	class UpdateAsync implements Runnable {
		public UpdateAsync() {}    
		public void run() {
			addNextMovie();
			restartAllMovies();
			_updateComplete = true;
		} 
	}

	public void loadMovieThread() {
		if(_updateComplete == true) {
			_updateComplete = false;
			if(_updater == null) _updater = new UpdateAsync();
			_updateThread = new Thread( _updater );
			_updateThread.start();
		} else {
			P.println("loadMovieThread() not complete. failed to load next movie.");
		}
	}
	
}
