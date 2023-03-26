package com.haxademic.demo.media.video;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PShaderHotSwap;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.file.FileUtil;

import processing.video.Movie;

public class Demo_Movie_Chroma 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie[] movies;
	protected float[] lastTime;
	protected PShaderHotSwap shaderNoiseCleanup;
	protected PShaderHotSwap shaderAlphaEdgeFade;
	
	protected void config() {
		Config.setAppSize(1080, 1080);
	}
	
	protected void firstFrame() {
		movies = new Movie[] {
			new Movie(P.p, "D:\\workspace\\afi-bernies-chalet\\data\\video\\attract\\BERNIE_08_JUMP_AROUND.mp4"),
		};
		lastTime = new float[movies.length];
		for (int i = 0; i < movies.length; i++) {
			movies[i].play();
			lastTime[i] = 0;
		}
		
		shaderNoiseCleanup = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/noise-alpha-cleanup.glsl"));
		shaderAlphaEdgeFade = new PShaderHotSwap(FileUtil.getPath("haxademic/shaders/filters/alpha-edge-fade.glsl"));
	}
	
	protected void drawApp() {
		p.background(80,0,0);
		
		
		pg.beginDraw();
		pg.background(0,0);
		for (int i = 0; i < movies.length; i++) {
			Movie movie = movies[i];
//			p.image(movie, i * 150, i * 150);
			pg.image(movie, pg.width/2 - movie.width/2, 0);
			DebugView.setTexture("movie ["+i+"] ", movie);
			
			// while loop() is broken
			boolean isFinished = movie.time() == lastTime[i];
			if(isFinished) {
//				movie.jump(0);
//				movie.play();
			}
				
			lastTime[i] = movie.time();
		}
		pg.endDraw();
		
		// run chroma!
		ChromaColorFilter.instance().presetWhiteKnockout();
		ChromaColorFilter.instance().setThresholdSensitivity(0.03f);
		ChromaColorFilter.instance().setSmoothing(0.03f);
		ChromaColorFilter.instance().applyTo(pg);

		// alpha cleanup
		shaderNoiseCleanup.update();
		shaderNoiseCleanup.shader().set("radiusCheck", 1.5f);
		shaderNoiseCleanup.shader().set("neighborAlphaCutoff", 0.5f);
		pg.filter(shaderNoiseCleanup.shader());
		
		// antialias..
		
		// alpha edge fade
		shaderAlphaEdgeFade.update();
		shaderAlphaEdgeFade.shader().set("radiusCheck", 1.5f);
		pg.filter(shaderAlphaEdgeFade.shader());

		GrainFilter.instance().setTime(p.frameCount * 0.01f);
		GrainFilter.instance().setCrossfade(0.1f);
//		GrainFilter.instance().setTime(p.millis());
//		GrainFilter.instance().applyTo(pg);
		
		// draw result to screen
		p.image(pg, 0, 0);
	}
	
}

