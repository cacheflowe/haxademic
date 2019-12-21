package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.MotionBlurPGraphics;

import processing.core.PGraphics;

public class Demo_MotionBlurPGraphics
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "400" );
		Config.setProperty( AppSettings.HEIGHT, "400" );
	}

	public void firstFrame() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(20);
	}

	protected void drawGraphics( PGraphics pg ) {
		// redraw pgraphics grid
		PG.setDrawCenter(pg);
		pg.beginDraw();
		pg.clear();
		pg.stroke(0);
		pg.fill(255);
		pg.translate((p.frameCount * 3) % p.width, p.height/2);
		pg.rotate(p.frameCount * 0.1f);
		pg.rect(0, 0, 50, 50);
		pg.endDraw();
	}

	public void drawApp() {
		p.background(0);
		drawGraphics(_pg);
		_pgMotionBlur.updateToCanvas(_pg, p.g, 1);
	}

}
