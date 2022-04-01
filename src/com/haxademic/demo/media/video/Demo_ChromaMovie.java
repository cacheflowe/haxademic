package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.video.ChromaMovie;

public class Demo_ChromaMovie
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ChromaMovie chromaMovie;
	
	protected void firstFrame() {

		chromaMovie = new ChromaMovie(FileUtil.getPath(DemoAssets.movieFractalCubePath));
		chromaMovie.loop();
	}
	
	protected void drawApp() {
		if(p.frameCount == 1) p.background(0);
		p.noStroke();
		
		PG.feedback(p.g, p.color(0), 0.1f, 1f);
		
		// draw buffer to screen
		chromaMovie.update();
		p.image(chromaMovie.image(), 0, 0);
	}	

}

