package com.haxademic.demo.media.video;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.media.video.MovieBuffer;

public class Demo_MovieBuffer 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected MovieBuffer movieBuffer;
	
	protected void setupFirstFrame() {
		movieBuffer = new MovieBuffer(DemoAssets.movieFractalCube());
		movieBuffer.movie.loop();
	}

	public void drawApp() {
		p.background(0);
		if(movieBuffer.buffer != null) ImageUtil.cropFillCopyImage(movieBuffer.buffer, p.g, false);
	}
	
}
