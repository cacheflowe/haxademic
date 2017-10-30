package com.haxademic.sketch.shader;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class BlurImageTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public PImage img;
	public PGraphics pg;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.FPS, "60" );
	}


	public void setup() {
		super.setup();	
		p.smooth( OpenGLUtil.SMOOTH_HIGH );

		// load image and configure size
		img = p.loadImage(FileUtil.getFile("images/green-screen.png"));

		// transform to blurred img
		pg = ImageUtil.imageToGraphics(img);
//		BlurHFilter.instance(p).setBlur(0.05f);
//		BlurHFilter.instance(p).applyTo(pg);
//		BlurVFilter.instance(p).setBlur(1000f);
//		BlurVFilter.instance(p).applyTo(pg);
		
//		img = pg.get();
	}

	public void drawApp() {
//		img = p.loadImage(FileUtil.getFile("images/_the_grove_src_4.jpg"));
//		img = p.loadImage(FileUtil.getFile("images/green-screen.png"));

		// transform to blurred img
		pg.beginDraw();
		pg.image(img, 0, 0);
		pg.endDraw();
		
		float mousePercent = (float) p.mouseX / (float) p.width;
		P.println(mousePercent);
		BlurHFilter.instance(p).setBlurByPercent(mousePercent, img.width);
		BlurHFilter.instance(p).applyTo(pg);
		BlurVFilter.instance(p).setBlurByPercent(mousePercent, img.height);
		BlurVFilter.instance(p).applyTo(pg);
		
//		img = pg.get();

		DrawUtil.setDrawCorner(p);
		p.image(pg, 0, 0);

	}

}
