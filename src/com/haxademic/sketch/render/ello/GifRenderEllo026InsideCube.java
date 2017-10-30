package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;

public class GifRenderEllo026InsideCube 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 95;
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
		background(0);
		translate(width/2, height/2, 0);
		
		float percentComplete = 2f * ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete % 1, 0, 1, 1);
		float radsComplete = (easedPercent) * P.TWO_PI;

		if(percentComplete < 1f) {
			rotateX(P.PI); 
			rotateY(radsComplete * 0.25f);
		} else {
			rotateX(P.PI); 
			rotateZ(-radsComplete); 
		}

		Shapes.drawTexturedCube(p.g, 1200 + 0f * P.sin(P.PI + radsComplete), img);
		
		VignetteFilter.instance(p).applyTo(p);
	}
}
