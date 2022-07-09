package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.LeaveBlackFilter;
import com.haxademic.core.draw.filters.pshader.PoissonFill;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

import processing.video.Movie;

public class Demo_PoissonFill
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PoissonFill poisson;
	
	protected void config() {
		Config.setAppSize(1024, 1024);
	}

	protected void drawApp() {
		p.background(0);
		
		// set pg context, draw an image
		// if running on p.g, y-axis will be flipped!
		pg.beginDraw();
		pg.clear();
		pg.push();
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.image(DemoAssets.justin(), 0, 0, 256, 256);
		drawShapes();
		pg.pop();
		PG.drawBorder(pg, 0xaa00ffff, 4);
		pg.endDraw();
//		LeaveBlackFilter.instance(p).applyTo(pg);
		
		// init & apply poisson fill
		if(poisson == null) poisson = new PoissonFill(pg.width, pg.height);
		poisson.applyTo(pg);
		p.image(poisson.output(), 0, 0);
		
		// set to 
		DebugView.setTexture("output", poisson.output());
	}

	protected void drawShapes() {
		// line
		pg.push();
		pg.stroke(0,0,255);
		pg.strokeWeight(14f);
		pg.line(50, 0, 500, FrameLoop.osc(0.03f,  -200,  200));
		pg.pop();

		// line 2
		pg.push();
		pg.fill(255);
		pg.rect(0, FrameLoop.osc(0.03f,  -200,  200), 200, 5);
		pg.pop();

		// circle
		pg.push();
		pg.fill(255,0,0);
		pg.stroke(0);
		pg.strokeWeight(5);
		pg.circle(-150, FrameLoop.osc(0.04f,  -100,  100), 100);
		pg.pop();
	}
}
