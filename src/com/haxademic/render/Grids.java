package com.haxademic.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.TiledGrid;
import com.haxademic.core.render.FrameLoop;

public class Grids
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int FRAMES = 300;
	protected TiledGrid tiledImg1;
	protected TiledGrid tiledImg2;
	protected TiledGrid tiledImg3;

	protected void config() {
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}
	
	protected void firstFrame() {
		// frame loop
		FrameLoop.instance(FRAMES);
		
		// build tiling object
		float strokeW = 2;
		tiledImg1 = new TiledGrid(64, 0xff000000, 0xffffffff, strokeW);
		tiledImg2 = new TiledGrid(32, 0xff000000, 0xffffffff, strokeW);
		tiledImg3 = new TiledGrid(16, 0xff000000, 0xffffffff, strokeW);
	}
	
	protected void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		p.blendMode(PBlendModes.ADD);

		// draw tiling. center screen
		p.push();
		PG.setCenterScreen(p);
		p.translate(-8/2 * tiledImg1.tileSize(), -6/2 * tiledImg1.tileSize());

		// draw cells
		tiledImg1.draw(p.g, 8, 6);
		
		p.translate(tiledImg1.tileSize(), tiledImg1.tileSize());
		tiledImg2.offsetX(FrameLoop.progress() * 2).offsetY(FrameLoop.progress() * 0).draw(p.g, 6, 4);

		p.translate(tiledImg1.tileSize() * 4, tiledImg1.tileSize() * 0);
		tiledImg2.offsetX(FrameLoop.progress() * 0).offsetY(FrameLoop.progress() * 1).draw(p.g, 4, 8);
		
		p.translate(tiledImg1.tileSize() * -4, tiledImg1.tileSize() * 3);
		tiledImg3.offsetX(FrameLoop.progress() * 0).offsetY(FrameLoop.progress() * -4).draw(p.g, 4, 4);
		
		p.translate(tiledImg1.tileSize() * 2, tiledImg1.tileSize() * 0);
		tiledImg3.offsetX(FrameLoop.progress() * -8).offsetY(FrameLoop.progress() * 0).draw(p.g, 4, 4);
		
		// reset context
		p.pop();
	}	

}