package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.filters.shaders.HalftoneLinesFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;

public class GifRenderEllo027HalftoneLines 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 160;
	PImage img;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	public void setup() {
		super.setup();
		img = loadImage(FileUtil.getHaxademicDataPath() + "images/ello.png");
		noStroke();
	}

	public void drawApp() {
		background(210);
		translate(width/2, height/2, 0);
		DrawUtil.setDrawCenter(p);
		
		float percentComplete = (float)(p.frameCount%_frames)/(float)_frames;
		float easedPercent = Penner.easeInOutQuart(percentComplete % 1, 0, 1, 1);
		float radsComplete = (percentComplete) * P.TWO_PI;

		DrawUtil.setPImageAlpha(p, 0.4f + 0.4f * P.sin(radsComplete));
		p.image(img, 0, 0, width * 0.75f, height * 0.75f);
		
		HalftoneLinesFilter.instance(p).setSampleDistX(100f);
		HalftoneLinesFilter.instance(p).setSampleDistY(100f);
		HalftoneLinesFilter.instance(p).setAntiAlias(0.3f);
		HalftoneLinesFilter.instance(p).setRows(80f);
		HalftoneLinesFilter.instance(p).setRotation(0);
		HalftoneLinesFilter.instance(p).applyTo(p);
	}
}
