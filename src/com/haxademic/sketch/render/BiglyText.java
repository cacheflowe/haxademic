package com.haxademic.sketch.render;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.draw.image.DrawCommand.Command;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class BiglyText
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PImage biglyImg;
	float _frames = 80;
	PGraphics _pg;
	MotionBlurPGraphics _pgMotionBlur;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1920 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 840 );

		p.appConfig.setProperty( AppSettings.RENDERER, P.P2D );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, false );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, (int)_frames );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames * 2 );
	}
	
	public void setup() {
		super.setup();
		biglyImg = p.loadImage(FileUtil.getFile("images/bigly-trans.png"));
		buildMotionBlur();
	}
	
	protected void buildMotionBlur() {
		_pg = p.createGraphics( p.width, p.height, P.P2D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(11);
	}

	public void drawApp() {
		p.background(255);
		drawGraphics(_pg);
		_pgMotionBlur.updateToCanvas(_pg, p.g, 1);
//		DrawUtil.feedback(p.g, p.color(255), 0.2f, -1.0f);
//		drawFrame();
	}
	
	public void drawGraphics( PGraphics pg ) {
//		if(p.frameCount == 1) p.background(255);
		pg.beginDraw();
		pg.clear();

//		pg.background(255);
		pg.noStroke();
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float progressRads = percentComplete * P.TWO_PI;
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);

		
		// Bread!
		pg.translate(p.width/2, p.height/2);
		DrawUtil.setDrawCenter(pg);
		DrawUtil.setPImageAlpha(pg, 0.3f);
		pg.scale(0.9f + P.sin(progressRads) * 0.1f);
		pg.rotate(0.01f * P.sin(P.PI/2 + progressRads));
		pg.image(biglyImg, 0, 0);

		pg.endDraw();
	}
}



