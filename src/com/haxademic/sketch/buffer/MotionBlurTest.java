package com.haxademic.sketch.buffer;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;

import processing.core.PGraphics;

public class MotionBlurTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "400" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "400" );
	}

	public void setup() {
		super.setup();	
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(20);
	}

	protected void drawGraphics( PGraphics pg ) {
		// redraw pgraphics grid
		DrawUtil.setDrawCenter(pg);
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
