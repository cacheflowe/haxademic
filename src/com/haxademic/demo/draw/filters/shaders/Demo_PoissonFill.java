package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.PoissonFill;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;

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
		drawShapes();
//		drawSvg();
		pg.endDraw();
		
		// init & apply poisson fill
		if(Mouse.xNorm > 0.25f) {
			if(poisson == null) poisson = new PoissonFill(pg.width, pg.height);
			poisson.applyTo(pg);
			p.image(poisson.output(), 0, 0);
			
			// set to 
			DebugView.setTexture("output", poisson.output());
		} else {
			p.image(pg, 0, 0);
		}
	}

	protected void drawSvg() {
		pg.push();
		
		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		
		DemoAssets.shapeX().disableStyle();
		pg.rotate(FrameLoop.count(0.01f));
		pg.fill(255);
		pg.shape(DemoAssets.shapeX());
		pg.fill(255, 0, 0);
		pg.rotate(P.QUARTER_PI);
		pg.shape(DemoAssets.shapeX());
		
		pg.pop();
		PG.drawBorder(pg, 0xff000000, 1);
	}
	
	protected void drawShapes() {
		pg.push();

		PG.setCenterScreen(pg);
		PG.setDrawCenter(pg);
		pg.image(DemoAssets.justin(), 0, 0, 256, 256);

		// line
		pg.push();
		pg.stroke(255,0,255);
		pg.strokeWeight(14f);
		pg.line(50, 0, 500, FrameLoop.osc(0.1f,  -200,  200));
		pg.pop();

		// line 2
		pg.push();
		pg.fill(255);
		pg.rect(0, FrameLoop.osc(0.08f,  -400,  400), 200, 5);
		pg.pop();

		// circle
		pg.push();
		pg.fill(255,255,0);
		pg.stroke(0);
		pg.strokeWeight(10);
		pg.circle(-350, FrameLoop.osc(0.15f,  -100,  100), 100);
		pg.pop();
		
		pg.pop();
		PG.drawBorder(pg, 0xaa00ffff, 4);
	}
}
