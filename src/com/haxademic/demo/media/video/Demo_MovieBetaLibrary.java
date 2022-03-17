package com.haxademic.demo.media.video;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;

import processing.video.Movie;

public class Demo_MovieBetaLibrary 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie[] movies;
	protected float[] lastTime;
	
	protected void firstFrame () {
		movies = new Movie[] {
			new Movie(P.p, "C:\\Users\\cacheflowe\\Downloads\\BERNIE_02B_WAVE_L.mov"),
//			new Movie(P.p, "C:\\Users\\cacheflowe\\Downloads\\SamplePackOneHap1080p\\Movies\\Suho-Hap-HD.mov"),
//			new Movie(P.p, "D:\\workspace\\media-utility-scripts\\_saved_files\\Using the Ello iOS App-135605909.mp4"),
//			new Movie(P.p, "D:\\workspace\\media-utility-scripts\\_saved_files\\Exploring CTD - Design Studio-341609057.mp4"),
//			new Movie(P.p, "D:\\workspace\\media-utility-scripts\\_saved_files\\Using the Ello iOS App-135605909.mp4"),
		};
		lastTime = new float[movies.length];
		for (int i = 0; i < movies.length; i++) {
			movies[i].play();
			lastTime[i] = 0;
		}
	}
	
	protected void drawApp() {
		PG.feedback(p.g, 10);
		
		for (int i = 0; i < movies.length; i++) {
			Movie movie = movies[i];
			p.image(movie, i * 150, i * 150);

			DebugView.setValue("movie ["+i+"] time", movie.time());
			DebugView.setValue("movie ["+i+"] W", movie.width);
			DebugView.setValue("movie ["+i+"] h", movie.height);
			DebugView.setValue("movie ["+i+"] loaded", movie.loaded);
			DebugView.setValue("movie ["+i+"] available", movie.available());
			DebugView.setValue("movie ["+i+"] frameRate", movie.frameRate);
			DebugView.setValue("movie ["+i+"] isLoaded", movie.isLoaded());
			DebugView.setValue("movie ["+i+"] hasBufferSink", movie.hasBufferSink());
			// video lib 2.0 props
			DebugView.setValue("movie ["+i+"] sourceWidth", movie.sourceWidth);
			DebugView.setValue("movie ["+i+"] sourceHeight", movie.sourceHeight);
			DebugView.setValue("movie ["+i+"] duration", movie.duration());
			DebugView.setValue("movie ["+i+"] time", movie.time());
			DebugView.setValue("movie ["+i+"] isPlaying", movie.isPlaying());
			DebugView.setValue("movie ["+i+"] isLooping", movie.isLooping());
			DebugView.setValue("movie ["+i+"] isPaused", movie.isPaused());
			
			// while loop() is broken
//			if(movie.isLooping()) {
				boolean isFinished = movie.time() == lastTime[i];
				if(isFinished) {
					movie.jump(0);
					movie.play();
				}
//			}
				
			lastTime[i] = movie.time();
		}
	}
	
}

