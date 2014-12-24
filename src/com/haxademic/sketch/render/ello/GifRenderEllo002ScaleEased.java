package com.haxademic.sketch.render.ello;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.image.AnimatedGifEncoder;
import com.haxademic.core.math.easing.Penner;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class GifRenderEllo002ScaleEased
extends PAppletHax{
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	PImage _bread;
	float _frames = 60;
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "128" );
		_appConfig.setProperty( "height", "128" );

		_appConfig.setProperty( "rendering", "false" );
		_appConfig.setProperty( "rendering_gif", "false" );
		_appConfig.setProperty( "rendering_gif_framerate", "40" );
		_appConfig.setProperty( "rendering_gif_quality", "1" );
		_appConfig.setProperty( "rendering_gif_startframe", "2" );
		_appConfig.setProperty( "rendering_gif_stopframe", ""+Math.round(_frames+1) );
	}
	
	public void setup() {
		super.setup();
		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.getHaxademicDataPath()+"svg/ello-inverse.svg");
		_bread = p.loadImage(FileUtil.getHaxademicDataPath()+"images/bread.png");
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
		float elloSize = (float)((p.width - p.width*0.02) + p.width*0.02 * frameOsc);
//		float elloSize = (float)(p.width);
		
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
//		p.rotate(easedPercent * PConstants.TWO_PI);
		
		// Ello logo
//		DrawUtil.setDrawCorner(p);
//		p.shape(_logo, 0, 0, elloSize, elloSize);
		
		// Bread!
		DrawUtil.setDrawCenter(p);
		p.image(_bread, 0, 0, elloSize, elloSize);
	}
}



