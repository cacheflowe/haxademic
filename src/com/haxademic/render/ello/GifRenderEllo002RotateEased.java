package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PShape;

public class GifRenderEllo002RotateEased
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	float _frames = 45;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "140" );
		Config.setProperty( AppSettings.HEIGHT, "140" );

		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames) );
	}
	
	public void firstFrame() {

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello-inverse.svg");
	}

	public void drawApp() {
		p.background(255);
//		p.fill(255, 40);
//		p.rect(0, 0, p.width, p.height);
		p.noStroke();
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);

		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
//		float elloSize = (float)(p.width/1.5f + 7f * frameOsc);
		float elloSize = (float)(p.width);
		
		PG.setDrawCorner(p);
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
		p.rotate(easedPercent * PConstants.TWO_PI);
		p.shape(_logo, 0, 0, elloSize, elloSize);
	}
}



