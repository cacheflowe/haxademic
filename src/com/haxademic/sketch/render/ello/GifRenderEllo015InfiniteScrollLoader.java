package com.haxademic.sketch.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PShape;

public class GifRenderEllo015InfiniteScrollLoader
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	float _frames = 45;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "360" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "240" );

		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF, "false" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_QUALITY, "15" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		p.appConfig.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
	}

	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);

		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
		float scale = 0.95f;
		float oscSize = 0.1f;
		float elloSize = (float)(p.width * (scale - oscSize + oscSize * frameOsc));
//		float elloSize = (float)(40 * (scale - oscSize + oscSize * frameOsc));	// with padding
		
		DrawUtil.setDrawCenter(p);
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
//		p.rotate(easedPercent * PConstants.TWO_PI);
		p.fill(ColorUtil.colorFromHex("#DFDFDF"));
		p.stroke(0,0);
		p.ellipse(0, 0, elloSize, elloSize);
	}
}



