package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;

public class GifRenderEllo020ElloTurntable
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PImage _turntable;
	PImage _overlay;
	PImage _record;
	PGraphics _pg;
	float _frames = 30;
	boolean rendering = false;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "500" );


		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "true" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_turntable = p.loadImage(FileUtil.getHaxademicDataPath()+"images/ello/ello-turntable.png");
		_overlay = p.loadImage(FileUtil.getHaxademicDataPath()+"images/ello/ello-turntable-spindle.png");
		_record = p.loadImage(FileUtil.getHaxademicDataPath()+"images/ello/ello-turntable-record.png");
	}
	
	public void drawApp() {
//		p.background(255);
		drawFrame();
	}
		
	public void drawFrame() {
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);
		float easedPercentHard = Penner.easeInOutQuad(percentComplete, 0, 1, 1);
		
		DrawUtil.setDrawCorner(p);
		p.image(_turntable, 0, 0, _turntable.width, _turntable.height);

		DrawUtil.setDrawCenter(p);
		p.pushMatrix();
		p.translate(254, 249);
		p.rotate(percentComplete * P.TWO_PI);
		p.image(_record, 0, -0.8f);
		p.popMatrix();
	
		DrawUtil.setDrawCorner(p);
		p.image(_overlay, 0, 0);
	}
}



