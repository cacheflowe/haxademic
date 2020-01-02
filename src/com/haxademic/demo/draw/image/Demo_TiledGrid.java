package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.TiledGrid;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.render.FrameLoop;

public class Demo_TiledGrid
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int FRAMES = 300;
	protected TiledGrid tiledImg1;
	protected TiledGrid tiledImg2;
	protected TiledGrid tiledImg3;
	
	protected LinearFloat cols = new LinearFloat(1, 0.025f);
	protected LinearFloat rows = new LinearFloat(1, 0.025f);
	protected LinearFloat offsetX = new LinearFloat(0, 0.025f);
	protected LinearFloat offsetY = new LinearFloat(0, 0.025f);
	
	public void config() {
		Config.setProperty(AppSettings.RENDERING_MOVIE, false );
		Config.setProperty(AppSettings.RENDERING_MOVIE_START_FRAME, 1 + FRAMES * 2);
		Config.setProperty(AppSettings.RENDERING_MOVIE_STOP_FRAME, 1 + FRAMES * 3);
	}
	
	public void firstFrame() {
		// frame loop
		FrameLoop.instance(FRAMES);
		
		// build tiling object
		float strokeW = 2;
		tiledImg1 = new TiledGrid(64, 0xff000000, 0xffffffff, strokeW);
		tiledImg2 = new TiledGrid(32, 0xff000000, 0xffffffff, strokeW);
		tiledImg3 = new TiledGrid(16, 0xff000000, 0xffffffff, strokeW);
	}
	
	public void drawApp() {
		// set context
		p.background(0);
		p.noStroke();
		p.blendMode(PBlendModes.ADD);

		// draw tiling. center screen
		p.push();
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);

		// draw concentric cells for testing
		/*
		tiledImg1.offsetY(FrameLoop.progress() * -1).draw(p.g, 8, 6, true);
		tiledImg2.offsetX(FrameLoop.progress() * 0).offsetY(FrameLoop.progress() * 2).draw(p.g, 8, 6, true);
		tiledImg2.offsetX(FrameLoop.progress() * 0).offsetY(FrameLoop.progress() * -2).draw(p.g, 8, 8, true);
		tiledImg3.offsetX(FrameLoop.progress() * 0).offsetY(FrameLoop.progress() * -4).draw(p.g, 8, 8, true);
		tiledImg3.offsetX(FrameLoop.progress() * 0).offsetY(FrameLoop.progress() * 8).draw(p.g, 8, 8, true);
		*/
		
		// lerped properties
		// grid size
		if(FrameLoop.isTick()) {
			if(FrameLoop.curTick() == 0) cols.setTarget(0);
			if(FrameLoop.curTick() == 2) cols.setTarget(1);
			if(FrameLoop.curTick() == 1) rows.setCurrent(0).setTarget(1);
			if(FrameLoop.curTick() == 3) rows.setCurrent(1).setTarget(0);
		}
		cols.update();
		rows.update();
		float easedCols = 2f + 8f * Penner.easeInOutQuad(cols.value());
		float easedRows = 2f + 6f * Penner.easeInOutQuad(rows.value());
		
		// scroll offsets
		if(FrameLoop.isTick()) {
			if(FrameLoop.curTick() == 1) offsetX.setCurrent(1).setTarget(0);
			if(FrameLoop.curTick() == 3) offsetX.setCurrent(0).setTarget(0);
			if(FrameLoop.curTick() == 0) offsetY.setCurrent(0).setTarget(1);
			if(FrameLoop.curTick() == 2) offsetY.setCurrent(0).setTarget(1);
		}
		offsetX.update();
		offsetY.update();
		float easedOffsetX = -easedCols * Penner.easeInOutQuad(offsetX.value());
		float easedOffsetY = -easedRows * Penner.easeInOutQuad(offsetY.value());

		// draw!
		tiledImg2.offsetX(easedOffsetX).offsetY(easedOffsetY).draw(p.g, easedCols, easedRows, true);
		
		// reset context
		p.pop();
	}	

}