package com.haxademic.sketch.render.ello;

import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo020ElloTurntable
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	PImage _turntable;
	PImage _overlay;
	PImage _record;
	PGraphics _pg;
	float _frames = 30;
	boolean rendering = false;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "640" );
		_appConfig.setProperty( "height", "500" );


		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "true" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "15" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
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



