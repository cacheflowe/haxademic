package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.video.ChromaMovie;

public class Demo_ChromaMovie
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected ChromaMovie chromaMovie;
	
	public void setup() {
		super.setup();
		chromaMovie = new ChromaMovie(FileUtil.getFile("video/fractal-cube.mp4"));
		chromaMovie.loop();
	}
	
	public void drawApp() {
		if(p.frameCount == 1) p.background(0);
		p.noStroke();
		
		DrawUtil.feedback(p.g, p.color(0), 0.1f, 1f);
		
		// draw buffer to screen
		chromaMovie.update();
		p.image(chromaMovie.image(), 0, 0);
	}	

}

