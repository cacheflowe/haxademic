package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_BlurHFilter_BlurVFilter_setBlurByPercent
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public PImage img;
	public PGraphics pg;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		// load image and configure size
		img = DemoAssets.justin();

		// transform to blurred img
		pg = ImageUtil.imageToGraphics( DemoAssets.justin());
	}

	protected void drawApp() {
		p.background(0);
		
		// redraw img to pg
		pg.beginDraw();
		pg.image(img, 0, 0);
		pg.endDraw();
		
		// apply blur
		BlurHFilter.instance().setBlurByPercent(Mouse.xNorm * 2f, img.width);
		BlurHFilter.instance().applyTo(pg);
		BlurVFilter.instance().setBlurByPercent(Mouse.yNorm * 2f, img.height);
		BlurVFilter.instance().applyTo(pg);
		
		// draw result to screen
		PG.setDrawCorner(p);
		p.image(pg, 0, 0);

	}

}
